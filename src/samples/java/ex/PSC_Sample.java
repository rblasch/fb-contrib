package ex;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PSC_Sample {

    public void testPSC(List<PSC_Sample> samples) {
        Set<String> names = new HashSet<String>();
        for (PSC_Sample s : samples) {
            names.add(s.toString());
        }
    }

    public void testPSCMaps(Map<String, String> input) {
        Map<String, String> output = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : input.entrySet()) {
            output.put(entry.getKey().intern(), entry.getValue());
        }
    }

    public void testPSCEnumerated() {
        Set<String> commonWords = new HashSet<String>();
        commonWords.add("a");
        commonWords.add("an");
        commonWords.add("the");
        commonWords.add("by");
        commonWords.add("of");
        commonWords.add("and");
        commonWords.add("or");
        commonWords.add("in");
        commonWords.add("with");
        commonWords.add("my");
        commonWords.add("I");
        commonWords.add("on");
        commonWords.add("over");
        commonWords.add("under");
        commonWords.add("it");
        commonWords.add("they");
        commonWords.add("them");
    }

    public List<String> testAddAllToCtor(List<String> l) {
        List<String> ll = new ArrayList<String>();
        ll.addAll(l);

        ll.add("FooBar");
        return ll;
    }

    public void fpDontHaveCollectionForSizing(Iterator<Long> it) {
        Deque<Long> ad = new ArrayDeque<Long>();
        while (it.hasNext()) {
            ad.add(it.next());
        }
    }

    public void fpConditionalInLoop(Set<String> source) {
        List<String> dest = new ArrayList<String>();
        for (String s : source) {
            if (s.length() > 0) {
                dest.add(s);
            }
        }
    }

    public List<String> fpAddSubCollection(Map<String, Set<String>> s) {
        List<String> l = new ArrayList<String>();
        for (Map.Entry<String, Set<String>> entry : s.entrySet()) {
            l.add(entry.getKey());
            l.addAll(entry.getValue());
        }
        return l;
    }

    public void fpSwitchInLoop(Set<Integer> source) {
        List<Integer> dest = new ArrayList<Integer>();
        for (Integer s : source) {
            switch (s.intValue()) {
            case 0:
                dest.add(s);
                break;
            case 1:
                dest.remove(s);
                break;
            }
        }
    }

    public void fpAllocationInLoop(Map<String, String> source) {
        Map<String, List<String>> dest = new HashMap<String, List<String>>();

        for (Map.Entry<String, String> entry : source.entrySet()) {

            List<String> l = new ArrayList<String>();
            l.add(entry.getValue());
            dest.put(entry.getKey(), l);
        }
    }

    public List<String> fpUnknownSrcSize(BufferedReader br) throws IOException {
        List<String> l = new ArrayList<String>();
        String line;
        while ((line = br.readLine()) != null) {
            l.add(line);
        }

        return l;
    }

    public List<Exception> fpPSCInCatchBlock(List<String> src) {
        List<Exception> exceptions = new ArrayList<Exception>();

        for (String s : src) {
            try {
                s = s.substring(1000, 1001);

            } catch (IndexOutOfBoundsException e) {
                exceptions.add(e);
            }
        }

        List<Exception> exceptions2 = new ArrayList<Exception>();

        for (String s : src) {
            try {
                s = s.substring(1000, 1001);
                if (s == null)
                    return null;
            } catch (IndexOutOfBoundsException e) {
                exceptions2.add(e);
            }
        }

        return exceptions;
    }
}
