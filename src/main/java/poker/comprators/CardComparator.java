package poker.comprators;

import poker.Card;

import java.util.Comparator;

public class CardComparator implements Comparator<Card> {
    public int compare(Card o1, Card o2) {
        if(o1.getValue() <= o2.getValue()) return 1;
        return -1;
    }
}
