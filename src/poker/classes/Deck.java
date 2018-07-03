package poker.classes;

import java.util.HashMap;
import java.util.Random;

public class Deck {
    private HashMap<Integer, Card> cards;

    Deck() {
        cards = new HashMap<>();
        int key = 0;
        for(int suit = 0; suit < 4; suit++)
            for(int value = 1; value <= 13; value++) {
                Card card = new Card(suit, value);
                cards.put(key++, card);
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

    /* ==== GETTERS AND SETTERS ==== */

    @SuppressWarnings("unused")
    public HashMap<Integer, Card> getCards() {
        return cards;
    }

    @SuppressWarnings("unused")
    public void setCards(HashMap<Integer, Card> Karty) {
        this.cards = Karty;
    }
}
