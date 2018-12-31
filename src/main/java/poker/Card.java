package poker;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Card {
    private int value; //2 -> 1, ..., T -> 9, J -> 10, Q -> 11, K -> 12, A -> 13// (2 -> 1 because in some cases ace could have a count '0')
    private int suit; //heart -> 0, diamond -> 1, spade -> 2, club -> 3//
    private boolean isUsed;
    private String valueName;
    private String valueNameLong;
    private String suitName;
    private String suitNameLong;
    private String name;
    private String nameLong;

    public Card() {
        this(-1, 0);
    }

    public Card(int suit, int value) {
        this.value = value;
        this.suit = suit;
        isUsed = false;
        setValue();
        setSuit();
        setName();
    }

    private void setValue() {
        if(value < 9) valueName = "" + (value + 1);
        else if(value == 9) {
            valueName = "T";
            valueNameLong = "Ten";
        }
        else if(value == 10) {
            valueName = "J";
            valueNameLong = "Jack";
        }
        else if(value == 11) {
            valueName = "Q";
            valueNameLong = "Queen";
        }
        else if(value == 12) {
            valueName = "K";
            valueNameLong = "King";
        }
        else if(value == 13) {
            valueName = "A";
            valueNameLong = "Ace";
        }
        else valueName = "";
        if(value == 1) valueNameLong = "Two";
        else if(value == 2) valueNameLong = "Three";
        else if(value == 3) valueNameLong = "Four";
        else if(value == 4) valueNameLong = "Five";
        else if(value == 5) valueNameLong = "Six";
        else if(value == 6) valueNameLong = "Seven";
        else if(value == 7) valueNameLong = "Eight";
        else if(value == 8) valueNameLong = "Nine";
    }

    private void setSuit() {
        if(suit == 0) {
            suitName = "h";
            suitNameLong = "of hearts";
        }
        else if(suit == 1) {
            suitName = "d";
            suitNameLong = "of diamonds";
        }
        else if(suit == 2) {
            suitName = "s";
            suitNameLong = "of spades";
        }
        else if(suit == 3) {
            suitName = "c";
            suitNameLong = "of clubs";
        }
        else suitName = "";
    }

    private void setName() {
        if(valueName.equals("") || suitName.equals("")) name = nameLong = "";
        else {
            name = valueName + suitName;
            nameLong = valueNameLong + " " + suitNameLong;
        }
    }

    @Override
    public String toString() {
        return nameLong;
    }
}

