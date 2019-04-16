package poker.comprators;

import poker.Card;

import java.util.Comparator;

public class CardComparatorAceAsOne implements Comparator<Card> {
    public int compare(Card o1, Card o2) {
        if((o1.getValue() % 13) < (o2.getValue() % 13)) return 1;
        return -1;
    }
}
