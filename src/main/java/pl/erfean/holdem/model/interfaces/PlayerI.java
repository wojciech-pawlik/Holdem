package pl.erfean.holdem.model.interfaces;

import pl.erfean.holdem.model.Card;

import java.util.List;

public interface PlayerI {
    // Operating with bets
    void bet();
    int addedToPot(int round);
    void addBets(int round, int value);
    void setBets(int round, int value);
    int getBets();
    int getBets(int round);
    // Operating with chips
    void addChips();
    void addChips(int chips);
    void subtractChips(int chips);
    // Setting up hand
    void setHand(List<Card> boardCards);
}
