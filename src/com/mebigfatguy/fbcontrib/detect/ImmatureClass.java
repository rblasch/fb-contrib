package com.mebigfatguy.fbcontrib.detect;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;

import com.mebigfatguy.fbcontrib.collect.MethodInfo;
import com.mebigfatguy.fbcontrib.collect.Statistics;
import com.mebigfatguy.fbcontrib.utils.BugType;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.visitclass.PreorderVisitor;

/**
 * looks for classes that aren't fully flushed out to be easily usable for various reasons.
 * While the class will most likely work fine, it is more difficult to use than necessary.
 */
public class ImmatureClass extends PreorderVisitor implements Detector {

    private BugReporter bugReporter;
    private boolean hasToString;
    
    public ImmatureClass(BugReporter reporter) {
        bugReporter = reporter;
    }

    /**
     * overrides the visitor to report on classes without toStrings that have fields
     * 
     * @param classContext the context object of the currently parsed class
     */
    @Override
    public void visitClassContext(ClassContext classContext) {
        JavaClass cls = classContext.getJavaClass();
        if ((!cls.isAbstract()) && !cls.getClassName().contains("$")) {
        
            try {
                boolean clsHasRuntimeAnnotation = classHasRuntimeVisibleAnnotation(cls);

                for (Field f : cls.getFields()) {
                    if (!f.isStatic() && !f.isSynthetic()) {
                        
                        boolean fieldHasRuntimeAnnotation = fieldHasRuntimeVisibleAnnotation(f);
                        if (!fieldHasRuntimeAnnotation) {
                            /* only report one of these, so as not to flood the report */
                            if (!hasMethodInHierarchy(cls, "toString", "()Ljava/lang/String;")) {
                                bugReporter.reportBug(new BugInstance(this, BugType.IMC_IMMATURE_CLASS_NO_TOSTRING.name(), LOW_PRIORITY)
                                                    .addClass(cls));
                                return;    
                            } else if (!clsHasRuntimeAnnotation) {
                                if (!hasMethodInHierarchy(cls, "equals", "(Ljava/lang/Object;)Z;")) {
                                    bugReporter.reportBug(new BugInstance(this, BugType.IMC_IMMATURE_CLASS_NO_EQUALS.name(), LOW_PRIORITY)
                                        .addClass(cls));
                                    return;
                                } else if (!hasMethodInHierarchy(cls, "hashCode", "()I")) {
                                    bugReporter.reportBug(new BugInstance(this, BugType.IMC_IMMATURE_CLASS_NO_HASHCODE.name(), LOW_PRIORITY)
                                            .addClass(cls));
                                    return;
                                }
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException cnfe) {
                bugReporter.reportMissingClass(cnfe);
            }
        }
    }

    @Override
    public void report() { 
        // required by the interface but not needed
    }
    
    /**
     * looks to see if this class (or some class in its hierarchy (besides Object) has implemented
     * the specified method.
     * 
     * @param cls the class to look in
     * @param methodName the method name to look for
     * @param methodSig the method signature to look for
     * 
     * @return when toString is found
     * 
     * @throws ClassNotFoundException if a super class can't be found
     */
    private static boolean hasMethodInHierarchy(JavaClass cls, String methodName, String methodSig) throws ClassNotFoundException {
        MethodInfo mi = null;
        
        do {
            String clsName = cls.getClassName();
            if ("java.lang.Object".equals(clsName)) {
                return false;
            }
            
            mi = Statistics.getStatistics().getMethodStatistics(clsName, methodName, methodSig);
            cls = cls.getSuperClass();
        } while ((mi != null) && !mi.hasToString());
        
        return true;
    }
    
    /**
     * determins if class has a runtime annotation. If it does it is likely to be a singleton, or
     * handled specially where hashCode/equals isn't of importance.
     * 
     * @param cls the class to check
     * 
     * @return if runtime annotations are found
     */
    private static boolean classHasRuntimeVisibleAnnotation(JavaClass cls) {
        AnnotationEntry[] annotations = cls.getAnnotationEntries();
        if (annotations != null) {
            for (AnnotationEntry annotation : annotations) {
                if (annotation.isRuntimeVisible()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * looks to see the field has a runtime visible annotation, if it does it might be autowired
     * or someother mechanism attached that makes them less interesting for a tostring call.
     * 
     * @param f the field to check
     * @return if the field has a runtime visible annotation
     */
    private static boolean fieldHasRuntimeVisibleAnnotation(Field f) {
        AnnotationEntry[] annotations = f.getAnnotationEntries();
        if (annotations != null) {
            for (AnnotationEntry annotation : annotations) {
                if (annotation.isRuntimeVisible()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    
}