package classes;

import java.util.Comparator;

public class CardComparatorColor implements Comparator<Card> {
    public int compare(Card o1, Card o2) {
        if(o1.getSuit() < o2.getSuit()) return 1;
        if(o1.getValue() > o2.getValue()) return 1;
        return -1;
    }
}
