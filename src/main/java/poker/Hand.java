package poker;

import lombok.Getter;
import lombok.Setter;
import poker.comprators.CardComparator;
import poker.comprators.CardComparatorAceAsOne;
import poker.comprators.CardComparatorColor;
import poker.comprators.CardComparatorColorAceAsOne;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@Getter
@Setter
public class Hand {
    private int points;
    private String name, nameShort;
    private int maxSuit;				//Max count of cards with the same suit -> Royal Flush, Straight Flush, Flush
    private int suit;				//Suit of maxSuit cards -> Royal Flush, Straight Flush, Flush
    private int maxValue1;				//Max count of cards with the same value -> Quads, Trips, Full House, Two Pair, One Pair
    private int value1;				//Value of cards with value of maxValue1 -> Quads, Trips, Full House, Two Pair, One Pair
    private int maxValue2;			//Second max count of cards with the same value -> Full House, Two Pair
    private int value2;				//Value of cards with value of maxValue2 -> Full House, Two Pair
    private ArrayList<Card> bestHand;	//best 5-card hand
    private ArrayList<Card> allCards;	//all cards which can build a hand

    public Hand() {
//        System.out.println("Hand()");
        points = 0;
        name = "";
        nameShort = "";
        maxSuit = 0;
        suit = 0;
        maxValue1 = 0;
        value1 = 0;
        maxValue2 = 0;
        value2 = 0;
        bestHand = new ArrayList<>(5);
        allCards = new ArrayList<>(7);
    }

    @SuppressWarnings("unused")
    public Hand(Player player, Board board) {
        this();
//        System.out.println("Player: " + player.getNickname());
        try {
            allCards.add(player.getCard1());
            allCards.add(player.getCard2());
            allCards.add(board.getFlop1());
            allCards.add(board.getFlop2());
            allCards.add(board.getFlop3());
            allCards.add(board.getTurn());
            allCards.add(board.getRiver());
        } catch(NullPointerException e) {
            System.out.println(e.getMessage());
        }
//        System.out.println(allCards);


        howManySuited();
        maxValuePower1();
        maxValuePower2();

        checkHand();
    }

    /* ==== HELPFUL METHODS TO CHECK HAND ==== */

    // ONLY FOR TESTS //
    public void suitsAndValues() {
        howManySuited();
        maxValuePower1();
        maxValuePower2();
    }


    private void howManySuited() {
//        System.out.println("howManySuited()");
        for(int i = 0; i < 4; i++) {
            int count = 0;
            for(int j = 0; j < allCards.size(); j++)
                if(allCards.get(j).getSuit() == i) count++;
            if(count > maxSuit) {
                maxSuit = count;
                suit = i;
            }
        }
//        System.out.println("maxSuit: " + maxSuit);
    }

    private int suitValue() {
        if(maxSuit < 5)
            return 0;
        int value = 0;
        Collections.sort(allCards, new CardComparatorColor());
        int index = 0;
        while(allCards.get(index).getSuit() != suit)
            index++;
        for(int i = 0; i < 5; i++)
            value += allCards.get(index + maxSuit-1 - i).getValue();
        return value;
    }

    @SuppressWarnings({"unused", "Duplicates"})
    private boolean isStraightFlush() {
        if(maxSuit < 5) return false;
        Collections.sort(allCards, new CardComparatorColorAceAsOne());
        int index = 0;
        while(allCards.get(index).getSuit() != suit)
            index++;
        for(int i = 0; i < maxSuit-4; i++) {
            if((allCards.get(index + i).getValue() % 13) - (allCards.get(index + 4 + i).getValue() % 13) == 4)
                return true;
        }
        return false;
    }

    private void maxValuePower1() {
        int max = 0;
        int nr = 0;
        for(int i = 1; i <= 13; i++) {
            int count = 0;
            for(int j = 0; j < allCards.size(); j++)
                if(allCards.get(j).getValue() == i) count++;
            if(count >= max) {
                max = count;
                nr = i;
            }
        }
        maxValue1 = max;
        value1 = nr;
    }

    private void maxValuePower2() {
        int max = 0;
        int nr = 0;
        for(int i = 1; i <= 13; i++)
            if(i != value1) {
                int count = 0;
                for(int j = 0; j < allCards.size(); j++)
                    if(allCards.get(j).getValue() == i) count++;
                if(count >= max) {
                    max = count;
                    nr = i;
                }
            }
        maxValue2 = max;
        value2 = nr;
    }

    @SuppressWarnings("Duplicates")
    private boolean isStraight() {
        Collections.sort(allCards, new CardComparatorAceAsOne());
        int[] values = {allCards.get(0).getValue() % 13,20,20,20,20,20,20};
        int index = 1;
        for(int i = 1; i < allCards.size(); i++) {
            Card card = allCards.get(i);
            if(card.getValue() != values[index-1])
                values[index++] = card.getValue() % 13;
        }
        return (values[6] - values[2] == 4) && (values[5] - values[3] == 2)
                || (values[5] - values[1]) == 4 && (values[4] - values[2] == 2)
                || (values[4] - values[0]) == 4 && (values[3] - values[1] == 2);
    }

    private boolean isStraightAceHigh() {
        Collections.sort(allCards, new CardComparator());
        int max[] = {0,0,0,0,0};
        int maxIndex = 4;
        for (int i = 0; i < allCards.size(); i++) {
            Card card = allCards.get(i);
            if (maxIndex >= 0) {
                if(i == 0)
                    max[maxIndex--] = card.getValue();
                else if(card.getValue() != allCards.get(i-1).getValue())
                    max[maxIndex--] = card.getValue();
            }
        }
        return max[0] == 9 && max[4] == 13;
    }


    /* ==== MAIN FUNCTION WHICH CHECK HAND ==== */

    @SuppressWarnings({"Duplicates", "unused"})
    public int checkHand() {
        //ROYAL FLUSH////////////
        if(suitValue() == 55) {
            points = 1200000;
            name = nameShort = "Royal flush";
            for(int i = 0; i < allCards.size(); i++) {
                if(allCards.get(i).getValue() == 13 && allCards.get(i).getSuit() == suit) bestHand.add(allCards.get(i));
                if(allCards.get(i).getValue() == 12 && allCards.get(i).getSuit() == suit) bestHand.add(allCards.get(i));
                if(allCards.get(i).getValue() == 11 && allCards.get(i).getSuit() == suit) bestHand.add(allCards.get(i));
                if(allCards.get(i).getValue() == 10 && allCards.get(i).getSuit() == suit) bestHand.add(allCards.get(i));
                if(allCards.get(i).getValue() == 9 && allCards.get(i).getSuit() == suit) bestHand.add(allCards.get(i));
            }
        }

        //STRAIGHT FLUSH/////////
        else if(isStraightFlush()) {
            name = "Straight flush";
            Collections.sort(allCards, new CardComparatorColorAceAsOne());
            int index = 0;
            while(allCards.get(index).getSuit() != suit) {
                index++;
            }
            if(allCards.get(index).getValue() - allCards.get(index + 1).getValue() == 1
                    && allCards.get(index+1).getValue() - allCards.get(index + 2).getValue() == 1)
                for (int i = 0; i < 5; i++) bestHand.add(allCards.get(index + i));
            else if(allCards.get(index+1).getValue() - allCards.get(index + 2).getValue() == 1)
                for (int i = 0; i < 5; i++) bestHand.add(allCards.get(index + 1 + i));
            else for (int i = 0; i < 5; i++) bestHand.add(allCards.get(index + 2 + i));

            name += " - " + bestHand.get(0).getValueNameLong() + " high";
            nameShort = name;
            points = 1180000 + bestHand.get(0).getValue();
        }

        //FOUR OF A KIND///////////////////////
        else if(maxValue1 == 4) {
            nameShort = "Four of a kind";
            int key = 0;
            for(int i = 0; i < allCards.size(); i++) {
                if(allCards.get(i).getValue() == value1) {
                    bestHand.add(key, allCards.get(i));
                    key++;
                }
            }
            int max = 0; //Kicker
            int index = 0;
            for(int j = 0; j < allCards.size(); j++)
                if(allCards.get(j).getValue() != value1)
                    if(allCards.get(j).getValue() > max) {
                        max = allCards.get(j).getValue();
                        index = j;
                    }
            bestHand.add(4, allCards.get(index));
            points = 1150000 + 14* bestHand.get(0).getValue() + bestHand.get(4).getValue();
            nameShort += ", " + bestHand.get(0).getValueNameLong() + "s";
            name = nameShort + " with " + bestHand.get(4).getValueNameLong() + " kicker";
        }

        //FULL HOUSE//////////////////////////////////
        else if(maxValue1 == 3 && maxValue2 >= 2) {
            nameShort = "Full house";
            int key = 0;
            for(int i = 0; i < allCards.size(); i++) {
                if(allCards.get(i).getValue() == value1) {
                    bestHand.add(key, allCards.get(i));
                    key++;
                }
            }
            int k = 0;
            while(key < 5) {
                if(allCards.get(k).getValue() == value2) {
                    bestHand.add(key, allCards.get(k));
                    key++;
                }
                k++;
            }
            nameShort += ", " + bestHand.get(0).getValueNameLong() + "s full of "
                    + bestHand.get(3).getValueNameLong() + "s";
            name = nameShort;
            points = 1140000 + 14* bestHand.get(0).getValue() + bestHand.get(3).getValue();
        }

        //FLUSH///////////////////////////////////////////
        else if(maxSuit >= 5) {
            nameShort = "Flush";
            Collections.sort(allCards, new CardComparatorColor());
            int index = 0;
            while(allCards.get(index).getSuit() != suit)
                index++;
            for(int i = 0; i < 5; i++)
                bestHand.add(allCards.get(index + maxSuit-1 - i));

            nameShort += ", " + bestHand.get(0).getValueNameLong() + " high";
            name = nameShort;
            points = 600000 + 38416* bestHand.get(0).getValue() + 2744* bestHand.get(1).getValue() + 196* bestHand.get(2).getValue()
                    + 14* bestHand.get(3).getValue() + bestHand.get(4).getValue();
        }

        //STRAIGHT/////////////////////////////////////////
        else if(isStraightAceHigh()) {
            nameShort = "Straight";
            name = "Straight, Ace high";
            points = 600000;
            bestHand.add(allCards.get(0));
            int card = 1;
            while(card < allCards.size() && bestHand.size() < 5) {
                if(allCards.get(card).getValue() != allCards.get(card-1).getValue())
                    bestHand.add(allCards.get(card));
                card++;
            }
        }

        else if(isStraight()) {
            nameShort = "Straight";
            bestHand.add(allCards.get(allCards.size()-1));
            int index = 1;
            while(bestHand.size() < 5) {
                if(allCards.get(allCards.size()-1 - index).getValue() == bestHand.get(bestHand.size()-1).getValue())
                    index++;
                else {
                    if(bestHand.get(bestHand.size()-1).getValue() - allCards.get(allCards.size()-1 - index).getValue() > 1)
                        bestHand.clear();
                    bestHand.add(allCards.get(allCards.size()-1-index++));
                }
            }

            nameShort += ", " + bestHand.get(0).getValueNameLong() + " high";
            name = nameShort;
            points = 590000 + bestHand.get(0).getValue();
        }

        //THREE OF A KIND///////////////////////////
        else if(maxValue1 == 3) {
            nameShort = "Three of a kind";
            int key = 0;
            for(int i = 0; i < allCards.size(); i++)
                if(allCards.get(i).getValue() == value1) {
                    bestHand.add(key, allCards.get(i));
                    key++;
                }
            int i1 = 0;
            int i2 = 0;
            int[] max = {0,-1};
            for(int j = 0; j < allCards.size(); j++)
                if(allCards.get(j).getValue() != value1) {
                    if(allCards.get(j).getValue() > max[0]) {
                        max[1] = max[0];
                        max[0] = allCards.get(j).getValue();
                        i2 = i1;
                        i1 = j;
                    }
                    else if(allCards.get(j).getValue() > max[1]) {
                        max[1] = allCards.get(j).getValue();
                        i2 = j;
                    }
                }
            bestHand.add(3, allCards.get(i1));
            bestHand.add(4, allCards.get(i2));

            nameShort += ", " + bestHand.get(0).getValueNameLong() + "s";
            name = nameShort + " with " + bestHand.get(3).getValueNameLong() + "-" + bestHand.get(4).getValueNameLong() + " kicker";
            points = 583000 + 196* bestHand.get(1).getValue() + 14* bestHand.get(3).getValue() + bestHand.get(4).getValue();
        }

        //TWO PAIR///////////////////////////////////
        else if(maxValue2 == 2) {
            nameShort = "Two pair";
            int key = 0;
            for(int i = 0; i < allCards.size(); i++) {
                if(allCards.get(i).getValue() == value1) {
                    bestHand.add(key, allCards.get(i));
                    key++;
                }
            }
            for(int j = 0; j < allCards.size(); j++) {
                if(allCards.get(j).getValue() == value2) {
                    bestHand.add(key, allCards.get(j));
                    key++;
                }
            }
            int max = 0;
            int f = 0;
            for(int k = 0; k < allCards.size(); k++)
                if(allCards.get(k).getValue() != value1 && allCards.get(k).getValue() != value2)
                    if(allCards.get(k).getValue() > max) {
                        max = allCards.get(k).getValue();
                        f = k;
                    }
            bestHand.add(4, allCards.get(f));

            nameShort += ", " + bestHand.get(0).getValueNameLong() + "s and " + bestHand.get(2).getValueNameLong() + "s";
            name = nameShort + " with " + bestHand.get(4).getValueNameLong() + " kicker";
            points = 580000 + 196* bestHand.get(0).getValue() + 14* bestHand.get(2).getValue() + bestHand.get(4).getValue();
        }

        //ONE PAIR////////////////////////////////////
        else if(maxValue1 == 2) {
            nameShort = "One pair";
            for (Card card : allCards)
                if (card.getValue() == value1) {
                    bestHand.add(card);
                }
            int[] max = {0,-1,-2};
            int i1, i2, i3;
            i1 = i2 = i3 = 0;
            for(int j = 0; j < allCards.size(); j++)
                if(allCards.get(j).getValue() != value1) {
                    if(allCards.get(j).getValue() > max[0]) {
                        max[2] = max[1];
                        max[1] = max[0];
                        max[0] = allCards.get(j).getValue();
                        i3 = i2;
                        i2 = i1;
                        i1 = j;
                    }
                    else if(allCards.get(j).getValue() > max[1]) {
                        max[2] = max[1];
                        max[1] = allCards.get(j).getValue();
                        i3 = i2;
                        i2 = j;
                    }
                    else if(allCards.get(j).getValue() > max[2]) {
                        max[2] = allCards.get(j).getValue();
                        i3 = j;
                    }
                }
            bestHand.add(2, allCards.get(i1));
            bestHand.add(3, allCards.get(i2));
            bestHand.add(4, allCards.get(i3));

            nameShort += ", " + bestHand.get(0).getValueNameLong() + "s";
            name = nameShort + " with " + bestHand.get(2).getValueNameLong() + "-"
                    + bestHand.get(3).getValueNameLong() + "-" + bestHand.get(4).getValueNameLong() + " kicker";
            points = 540000 + 2744* bestHand.get(0).getValue() + 196* bestHand.get(2).getValue() + 14* bestHand.get(3).getValue()
                    + bestHand.get(4).getValue();
        }

        //HIGH CARD//////////////////////////////////////
        else {
            nameShort = "High card";
            Collections.sort(allCards, new CardComparator());
            for(int i = 0; i < 5; i++)
                bestHand.add(allCards.get(i));

            nameShort += " " + bestHand.get(0).getValueNameLong();
            name = nameShort + " with " + bestHand.get(1).getValueNameLong() + "-"
                    + bestHand.get(2).getValueNameLong() + "-" + bestHand.get(3).getValueNameLong() + "-"
                    + bestHand.get(4).getValueNameLong() + " kicker";
            points = 38416* bestHand.get(0).getValue() + 2744* bestHand.get(1).getValue() + 196* bestHand.get(2).getValue()
                    + 14* bestHand.get(3).getValue() + bestHand.get(4).getValue();
        }
        return points;
    }

    public void destroyHand() {
        points = 0;
        name = "";
        nameShort = "";
        maxSuit = 0;
        suit = 0;
        maxValue1 = 0;
        value1 = 0;
        maxValue2 = 0;
        value2 = 0;
        bestHand.clear();
        allCards.clear();
    }
}