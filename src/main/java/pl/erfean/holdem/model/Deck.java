package pl.erfean.holdem.model;

import lombok.Getter;
import lombok.Setter;
import pl.erfean.holdem.model.interfaces.DeckI;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
public class Deck implements DeckI {
    private List<Card> cards;

    public Deck() {
        cards = IntStream.range(0, DECKSIZE)
                .mapToObj(i -> new Card(i/13, i%13 + 1))
                .collect(Collectors.toList());
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        var card = cards.get(0);
        cards.remove(0);
        return card;
    }
    public Card drawCard(Card card) {
        this.cards.remove(card);
        return card;
    }
    public Card drawCard(int hashCode) {
        return drawCard(new Card(hashCode/13, hashCode%13 + 1));
    }
}