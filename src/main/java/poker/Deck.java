package poker;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Random;

@Getter
@Setter
public class Deck {
    private ArrayList<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        for(int suit = 0; suit < 4; suit++)
            for(int value = 1; value <= 13; value++) {
                cards.add(new Card(suit, value));
            }
    }

    public void shuffle() {
        for(Card card : cards) card.setUsed(false);
    }

    public Card drawCard(Random random) {
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

    public Card drawCard(int number) {
        if(!cards.get(number).isUsed()) {
            cards.get(number).setUsed(true);
            return cards.get(number);
        }
        return null;
    }

    int usedCount() {
        return (int)cards.stream().filter(Card::isUsed).count();
    }
}
