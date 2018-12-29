package poker;

import java.util.ArrayList;
import java.util.Random;

public class Deck {
    private ArrayList<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        for(int suit = 0; suit < 4; suit++)
            for(int value = 1; value <= 13; value++) {
                cards.add(new Card(suit, value));
            }
    }

    void shuffle() {
        for(int i = 0; i < 52; i++) cards.get(i).setUsed(false);
    }

    Card drawCard(Random random) {
        int number;
        while(true) {
            number = random.nextInt(52);
            if(!cards.get(number).isUsed()) {
                cards.get(number).setUsed(true);
                break;
            }
        }
        return cards.get(number);
    }

    Card drawCard(int number) {
        if(!cards.get(number).isUsed())
            cards.get(number).setUsed(true);
        return cards.get(number);
    }

    int usedCount() {
        int count = 0;
        for(Card card : cards)
            if(card.isUsed()) count++;
        return count;
    }

    /* ==== GETTERS AND SETTERS ==== */

    public ArrayList<Card> getCards() {
        return cards;
    }
}
