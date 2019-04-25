package pl.erfean.holdem.model.interfaces;

import pl.erfean.holdem.model.Card;

public interface DeckI {
    int DECKSIZE = 52;
    void shuffle();
    Card drawCard();
    Card drawCard(Card card);
    Card drawCard(int hashCode);
}
