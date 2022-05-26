package uj.java.w3;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class ListMerger {
    public static List<Object> mergeLists(List<?> l1, List<?> l2) {
        List<Object> output = new LinkedList<>();

        if (l1 == null && l2 == null)
            return Collections.unmodifiableList(output);

        if (l2 == null) {
            output.addAll(l1);
            return Collections.unmodifiableList(output);
        }

        if (l1 == null) {
            output.addAll(l2);
            return Collections.unmodifiableList(output);
        }

        Iterator<?> iter1 = l1.iterator();
        Iterator<?> iter2 = l2.iterator();

        while (iter1.hasNext() || iter2.hasNext()) {
            if (iter1.hasNext()) output.add(iter1.next());
            if (iter2.hasNext()) output.add(iter2.next());
        }

        return Collections.unmodifiableList(output);
    }

}
