package ee.anu.server;

import java.util.Comparator;

public class StringLengthComparator implements Comparator<Tuple> {
    @Override
    public int compare(Tuple o1, Tuple o2) {
        return -Integer.compare(
                o1.getAttribute().length(),
                o2.getAttribute().length());
    }

}
