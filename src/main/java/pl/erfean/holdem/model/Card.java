package pl.erfean.holdem.model;

import lombok.Getter;
import lombok.Setter;
import pl.erfean.holdem.model.interfaces.CardI;

@Getter
@Setter
public class Card implements CardI {
    private int value; //2 -> 1, ..., T -> 9, J -> 10, Q -> 11, K -> 12, A -> 13// (2 -> 1 because in some cases ace could have a count '0')
    private int suit; //heart -> 0, diamond -> 1, spade -> 2, club -> 3//

    public Card() {
        this(-1,-1);
    }

    public Card(int suit, int value) {
        this.value = value;
        this.suit = suit;
    }

    // Getters for string representation

    public String getValueSymbol() {
        if(value < 9) return ""+(value + 1);
        else if(value == 9)
            return "T";
        else if(value == 10)
            return "J";
        else if(value == 11)
            return "Q";
        else if(value == 12)
            return "K";
        else if(value == 13)
            return "A";
        return "";
    }
    public String getValueName() {
        switch(value) {
            case 1:
                return "Two";
            case 2:
                return "Three";
            case 3:
                return "Four";
            case 4:
                return "Five";
            case 5:
                return "Six";
            case 6:
                return "Seven";
            case 7:
                return "Eight";
            case 8:
                return "Nine";
            case 9:
                return "Ten";
            case 10:
                return "Jack";
            case 11:
                return "Queen";
            case 12:
                return "King";
            case 13:
                return "Ace";
            default:
                return "";
        }
    }

    public String getSuitSymbol() {
        switch (suit) {
            case 0:
                return "h";
            case 1:
                return "d";
            case 2:
                return "s";
            case 3:
                return "c";
            default:
                return "";
        }
    }
    public String getSuitName() {
        switch (suit) {
            case 0:
                return "of hearts";
            case 1:
                return "of diamonds";
            case 2:
                return "of spades";
            case 3:
                return "of clubs";
            default:
                return "";
        }
    }

    public String getSymbol() {
        return getValueSymbol() + getSuitSymbol();
    }
    public String getName() {
        return getValueName() + " " + getSuitName();
    }

    @Override
    public String toString() {
        return getSymbol();
    }

    @Override
    public boolean equals(Object o) {
        return o.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return (value - 1) + suit*13;
    }
}