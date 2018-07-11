package poker.classes;

import java.util.HashMap;

public class Hand {
    private int points;
    private String name, nameShort;
    private int maxSuit;				//Royal Flush, Straight Flush, Flush
    private int suit;				//Royal Flush, Straight Flush, Flush
    private int maxValue1;				//Quads, Trips, Full House, Two Pair, One Pair
    private int value1;				//Quads, Trips, Full House, Two Pair, One Pair
    private int maxValue2;			//Full House, Two Pair
    private int value2;				//Full House, Two Pair
    private HashMap<Integer, Card> bestHand;	//best 5-card hand
    private HashMap<Integer, Card> allCards;	//all cards which can build a hand

    Hand() {
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
        bestHand = new HashMap<>();
        allCards = new HashMap<>();
    }

    @SuppressWarnings("unused")
    Hand(Player player, Board board) {
        this();
//        System.out.println("Player: " + player.getNickname());
        try {
            allCards.put(0, player.getCard1());
            allCards.put(1, player.getCard2());
            allCards.put(2, board.getFlop1());
            allCards.put(3, board.getFlop2());
            allCards.put(4, board.getFlop3());
            allCards.put(5, board.getTurn());
            allCards.put(6, board.getRiver());
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


    private void howManySuited() {
//        System.out.println("howManySuited()");
        for(int i = 0; i < 4; i++) {
            int licznik = 0;
            for(int j = 0; j < allCards.size(); j++)
                if(allCards.get(j).getSuit() == i) licznik++;
            if(licznik > maxSuit) {
                maxSuit = licznik;
                suit = i;
            }
        }
//        System.out.println("maxSuit: " + maxSuit);
    }

    private int suitValue() {
        int value = 0;
        if(maxSuit == 5) {
            for(int i = 0; i < allCards.size(); i++)
                if(allCards.get(i).getSuit() == suit)
                    value += allCards.get(i).getValue();
        }

        else if(maxSuit == 6) {
            int index = 0;
            int min = 14;
            for(int j = 0; j < allCards.size(); j++)
                if(allCards.get(j).getSuit() == suit) {
                    value += allCards.get(j).getValue();
                    if(allCards.get(j).getValue() < min) {
                        min = allCards.get(j).getValue();
                        index = j;
                    }
                }
            value -= allCards.get(index).getValue();
        }

        else if(maxSuit == 7) {
            int i1 = 0;
            int i2 = 0;
            int min1 = 14;
            int min2 = 15;
            for(int k = 0; k < allCards.size(); k++) {
                value += allCards.get(k).getValue();
                if(allCards.get(k).getValue() < min1) {
                    min2 = min1;
                    i2 = i1;
                    min1 = allCards.get(k).getValue();
                    i1 = k;
                }
                else if(allCards.get(k).getValue() < min2) {
                    min2 = allCards.get(k).getValue();
                    i2 = k;
                }
            }
            value -= allCards.get(i1).getValue() + allCards.get(i2).getValue();
        }
        return value;
    }

    @SuppressWarnings({"unused", "Duplicates"})
    private boolean isStraightFlush() {
        if(maxSuit == 5) {
            int min = 14;
            int max = 0;
            for(int i = 0; i < allCards.size(); i++)
                if(allCards.get(i).getSuit() == suit) {
                    if(allCards.get(i).getValue() % 13 < min) min = allCards.get(i).getValue() % 13;
                    if(allCards.get(i).getValue() % 13 > max) max = allCards.get(i).getValue() % 13;
                }
            return max - min == 4;
        }
        else if(maxSuit == 6) {
            int min1 = 14;
            int min2 = 15;
            int max1 = 0;
            int max2 = -1;
            for(int i = 0; i < allCards.size(); i++)
                if(allCards.get(i).getSuit() == suit) {
                    if(allCards.get(i).getValue() % 13 < min1) {
                        min2 = min1;
                        min1 = allCards.get(i).getValue() % 13;
                    }
                    else if(allCards.get(i).getValue() % 13 < min2)
                        min2 = allCards.get(i).getValue() % 13;
                    if(allCards.get(i).getValue() % 13 > max1) {
                        max2 = max1;
                        max1 = allCards.get(i).getValue() % 13;
                    }
                    else if(allCards.get(i).getValue() % 13 > max2)
                        max2 = allCards.get(i).getValue() % 13;
                }
            return max1 - min2 == 4 || max2 - min1 == 4;
        }
        else if(maxSuit == 7) {
            int mini1 = 14;
            int mini2 = 15;
            int mini3 = 16;
            int maxi1 = 0;
            int maxi2 = -1;
            int maxi3 = -2;
            for(int i = 0; i < allCards.size(); i++) {
                if(allCards.get(i).getValue() % 13 < mini1) {
                    mini3 = mini2;
                    mini2 = mini1;
                    mini1 = allCards.get(i).getValue() % 13;
                }
                else if(allCards.get(i).getValue() % 13 < mini2) {
                    mini3 = mini2;
                    mini2 = allCards.get(i).getValue() % 13;
                }
                else if(allCards.get(i).getValue() % 13 < mini3)
                    mini3 = allCards.get(i).getValue() % 13;

                if(allCards.get(i).getValue() % 13 > maxi1) {
                    maxi3 = maxi2;
                    maxi2 = maxi1;
                    maxi1 = allCards.get(i).getValue() % 13;
                }
                else if(allCards.get(i).getValue() % 13 > maxi2) {
                    maxi3 = maxi2;
                    maxi2 = allCards.get(i).getValue() % 13;
                }
                else if(allCards.get(i).getValue() % 13 > maxi3)
                    maxi3 = allCards.get(i).getValue() % 13;
            }
            return maxi1 - mini3 == 4 || maxi2 - mini2 == 4 || maxi3 - mini1 == 4;
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
                int licznik = 0;
                for(int j = 0; j < allCards.size(); j++)
                    if(allCards.get(j).getValue() == i) licznik++;
                if(licznik >= max) {
                    max = licznik;
                    nr = i;
                }
            }
        maxValue2 = max;
        value2 = nr;
    }

    @SuppressWarnings("Duplicates")
    private boolean isStraight() {
        int min1, min2, min3, min4, min5, min6, min7;
        min1 = 14; min2 = 15; min3 = 16; min4 = 17; min5 = 18; min6 = 19; min7 = 20;
        for(int i = 0; i < allCards.size(); i++) {
            if(allCards.get(i).getValue() % 13 < min1) {
                min7 = min6;
                min6 = min5;
                min5 = min4;
                min4 = min3;
                min3 = min2;
                min2 = min1;
                min1 = allCards.get(i).getValue() % 13;
            }
            else if(allCards.get(i).getValue() % 13 < min2 && allCards.get(i).getValue() % 13 > min1) {
                min7 = min6;
                min6 = min5;
                min5 = min4;
                min4 = min3;
                min3 = min2;
                min2 = allCards.get(i).getValue() % 13;
            }
            else if(allCards.get(i).getValue() % 13 < min3 && allCards.get(i).getValue() % 13 > min2) {
                min7 = min6;
                min6 = min5;
                min5 = min4;
                min4 = min3;
                min3 = allCards.get(i).getValue() % 13;
            }
            else if(allCards.get(i).getValue() % 13 < min4 && allCards.get(i).getValue() % 13 > min3) {
                min7 = min6;
                min6 = min5;
                min5 = min4;
                min4 = allCards.get(i).getValue() % 13;
            }
            else if(allCards.get(i).getValue() % 13 < min5 && allCards.get(i).getValue() % 13 > min4) {
                min7 = min6;
                min6 = min5;
                min5 = allCards.get(i).getValue() % 13;
            }
            else if(allCards.get(i).getValue() % 13 < min6 && allCards.get(i).getValue() % 13 > min5) {
                min7 = min6;
                min6 = allCards.get(i).getValue() % 13;
            }
            else if(allCards.get(i).getValue() % 13 < min7 && allCards.get(i).getValue() % 13 > min6) {
                min7 = allCards.get(i).getValue() % 13;
            }
        }
        return (min7 - min3 == 4 || min6 - min2 == 4 || min5 - min1 == 4);
    }

    @SuppressWarnings({"Duplicates", "unused"})
    private boolean isStraightAceHigh() {
        int max1 = 0;
        int max2 = -1;
        int max3 = -2;
        int max4 = -3;
        int max5 = -4;
        for(int k = 0; k < allCards.size(); k++) {
            if(allCards.get(k).getValue() > max1) {
                max5 = max4;
                max4 = max3;
                max3 = max2;
                max2 = max1;
                max1 = allCards.get(k).getValue();
            }
            else if(allCards.get(k).getValue() > max2 && allCards.get(k).getValue() < max1) {
                max5 = max4;
                max4 = max3;
                max3 = max2;
                max2 = allCards.get(k).getValue();
            }
            else if(allCards.get(k).getValue() > max3 && allCards.get(k).getValue() < max2) {
                max5 = max4;
                max4 = max3;
                max3 = allCards.get(k).getValue();
            }
            else if(allCards.get(k).getValue() > max4 && allCards.get(k).getValue() < max3) {
                max5 = max4;
                max4 = allCards.get(k).getValue();
            }
            else if(allCards.get(k).getValue() > max5 && allCards.get(k).getValue() < max4)
                max5 = allCards.get(k).getValue();
        }
        return max1 == 13 && max5 == 9;
    }


    /* ==== MAIN FUNCTION WHICH CHECK HAND ==== */

    @SuppressWarnings({"Duplicates", "unused"})
    void checkHand() {
        //ROYAL FLUSH////////////
        if(suitValue() == 55) {
            points = 1200000;
            name = nameShort = "Royal flush";
            for(int i = 0; i < allCards.size(); i++) {
                if(allCards.get(i).getValue() == 13 && allCards.get(i).getSuit() == suit) bestHand.put(0, allCards.get(i));
                if(allCards.get(i).getValue() == 12 && allCards.get(i).getSuit() == suit) bestHand.put(1, allCards.get(i));
                if(allCards.get(i).getValue() == 11 && allCards.get(i).getSuit() == suit) bestHand.put(2, allCards.get(i));
                if(allCards.get(i).getValue() == 10 && allCards.get(i).getSuit() == suit) bestHand.put(3, allCards.get(i));
                if(allCards.get(i).getValue() == 9 && allCards.get(i).getSuit() == suit) bestHand.put(4, allCards.get(i));
            }
        }

        //STRAIGHT FLUSH/////////
        else if(isStraightFlush()) {
            nameShort = "Straight flush";
            if(maxSuit == 5) {
                int max1 = 0;
                int max2 = -1;
                int max3 = -2;
                int max4 = -3;
                int max5 = -4;
                int q1, q2, q3, q4, q5;
                q1 = q2 = q3 = q4 = q5 = 0;
                for(int i = 0; i < allCards.size(); i++)
                    if(allCards.get(i).getSuit() == suit) {
                        if(allCards.get(i).getValue() % 13 > max1) {
                            max5 = max4;
                            max4 = max3;
                            max3 = max2;
                            max2 = max1;
                            max1 = allCards.get(i).getValue() % 13;
                            q5 = q4;
                            q4 = q3;
                            q3 = q2;
                            q2 = q1;
                            q1 = i;
                        }
                        else if(allCards.get(i).getValue() % 13 > max2) {
                            max5 = max4;
                            max4 = max3;
                            max3 = max2;
                            max2 = allCards.get(i).getValue() % 13;
                            q5 = q4;
                            q4 = q3;
                            q3 = q2;
                            q2 = i;
                        }
                        else if(allCards.get(i).getValue() % 13 > max3) {
                            max5 = max4;
                            max4 = max3;
                            max3 = allCards.get(i).getValue() % 13;
                            q5 = q4;
                            q4 = q3;
                            q3 = i;
                        }
                        else if(allCards.get(i).getValue() % 13 > max4) {
                            max5 = max4;
                            max4 = allCards.get(i).getValue() % 13;
                            q5 = q4;
                            q4 = i;
                        }
                        else if(allCards.get(i).getValue() % 13 > max5) {
                            max5 = allCards.get(i).getValue() % 13;
                            q5 = i;
                        }
                    }
                bestHand.put(0, allCards.get(q1));
                bestHand.put(1, allCards.get(q2));
                bestHand.put(2, allCards.get(q3));
                bestHand.put(3, allCards.get(q4));
                bestHand.put(4, allCards.get(q5));
            }
            else if(maxSuit == 6) {
                int min1 = 14;
                int min2 = 15;
                int max1 = 0;
                int max2 = -1;
                int max3 = -2;
                int max4 = -3;
                int g1, g2, h1, h2, h3, h4;
                g1 = g2 = h1 = h2 = h3 = h4 = 0;
                for(int j = 0; j < allCards.size(); j++) {
                    if(allCards.get(j).getSuit() == suit) {
                        if(allCards.get(j).getValue() % 13 < min1) {
                            min2 = min1;
                            min1 = allCards.get(j).getValue() % 13;
                            g2 = g1;
                            g1 = j;
                        }
                        else if(allCards.get(j).getValue() % 13 < min2) {
                            min2 = allCards.get(j).getValue() % 13;
                            g2 = j;
                        }

                        if(allCards.get(j).getValue() % 13 > max1) {
                            max4 = max3;
                            max3 = max2;
                            max2 = max1;
                            max1 = allCards.get(j).getValue() % 13;
                            h4 = h3;
                            h3 = h2;
                            h2 = h1;
                            h1 = j;
                        }
                        else if(allCards.get(j).getValue() % 13 > max2) {
                            max4 = max3;
                            max3 = max2;
                            max2 = allCards.get(j).getValue() % 13;
                            h4 = h3;
                            h3 = h2;
                            h2 = j;
                        }
                        else if(allCards.get(j).getValue() % 13 > max3) {
                            max4 = max3;
                            max3 = allCards.get(j).getValue() % 13;
                            h4 = h3;
                            h3 = j;
                        }
                        else if(allCards.get(j).getValue() % 13 > max4) {
                            max4 = allCards.get(j).getValue() % 13;
                            h4 = j;
                        }
                    }
                }
                if(max1 - min2 == 4) {
                    bestHand.put(0, allCards.get(h1));
                    bestHand.put(1, allCards.get(h2));
                    bestHand.put(2, allCards.get(h3));
                    bestHand.put(3, allCards.get(h4));
                    bestHand.put(4, allCards.get(g2));
                }
                else {
                    bestHand.put(0, allCards.get(h2));
                    bestHand.put(1, allCards.get(h3));
                    bestHand.put(2, allCards.get(h4));
                    bestHand.put(3, allCards.get(g2));
                    bestHand.put(4, allCards.get(g1));
                }
            }
            else {
                int min1 = 14;
                int min2 = 15;
                int min3 = 16;
                int max1 = 0;
                int max2 = -1;
                int max3 = -2;
                int max4 = -3;
                int r1, r2, r3, t1, t2, t3, t4;
                r1 = r2 = r3 = t1 = t2 = t3 = t4 = 0;
                for(int k = 0; k < allCards.size(); k++) {
                    if(allCards.get(k).getValue() % 13 < min1) {
                        min3 = min2;
                        min2 = min1;
                        min1 = allCards.get(k).getValue() % 13;
                        r3 = r2;
                        r2 = r1;
                        r1 = k;
                    }
                    else if(allCards.get(k).getValue() % 13 < min2) {
                        min3 = min2;
                        min2 = allCards.get(k).getValue() % 13;
                        r3 = r2;
                        r2 = k;
                    }
                    else if(allCards.get(k).getValue() % 13 < min3) {
                        min3 = allCards.get(k).getValue() % 13;
                        r3 = k;
                    }
                    if(allCards.get(k).getValue() % 13 > max1) {
                        max4 = max3;
                        max3 = max2;
                        max2 = max1;
                        max1 = allCards.get(k).getValue() % 13;
                        t4 = t3;
                        t3 = t2;
                        t2 = t1;
                        t1 = k;
                    }
                    else if(allCards.get(k).getValue() % 13 > max2) {
                        max4 = max3;
                        max3 = max2;
                        max2 = allCards.get(k).getValue() % 13;
                        t4 = t3;
                        t3 = t2;
                        t2 = k;
                    }
                    else if(allCards.get(k).getValue() % 13 > max3) {
                        max4 = max3;
                        max3 = allCards.get(k).getValue() % 13;
                        t4 = t3;
                        t3 = k;
                    }
                    else if(allCards.get(k).getValue() % 13 > max4) {
                        max4 = allCards.get(k).getValue() % 13;
                        t4 = k;
                    }
                }
                if(max1 - min3 == 4) {
                    bestHand.put(0, allCards.get(t1));
                    bestHand.put(1, allCards.get(t2));
                    bestHand.put(2, allCards.get(t3));
                    bestHand.put(3, allCards.get(t4));
                    bestHand.put(4, allCards.get(r3));
                }
                else if(max2 - min2 == 4) {
                    bestHand.put(0, allCards.get(t2));
                    bestHand.put(1, allCards.get(t3));
                    bestHand.put(2, allCards.get(t4));
                    bestHand.put(3, allCards.get(r3));
                    bestHand.put(4, allCards.get(r2));
                }
                else {
                    bestHand.put(0, allCards.get(t3));
                    bestHand.put(1, allCards.get(t4));
                    bestHand.put(2, allCards.get(r3));
                    bestHand.put(3, allCards.get(r2));
                    bestHand.put(4, allCards.get(r1));
                }
            }

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
                    bestHand.put(key, allCards.get(i));
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
            bestHand.put(4, allCards.get(index));
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
                    bestHand.put(key, allCards.get(i));
                    key++;
                }
            }
            int k = 0;
            while(key < 5) {
                if(allCards.get(k).getValue() == value2) {
                    bestHand.put(key, allCards.get(k));
                    key++;
                }
                k++;
            }
            nameShort += ", " + bestHand.get(0).getValueNameLong() + "s full of "
                    + bestHand.get(3).getValueNameLong() + "s";
            name = nameShort;
            points = 1140000 + 196* bestHand.get(0).getValue() + 14* bestHand.get(3).getValue();
        }

        //FLUSH///////////////////////////////////////////
        else if(maxSuit >= 5) {
            nameShort = "Flush";
            int max1 = 0;
            int max2 = -1;
            int max3 = -2;
            int max4 = -3;
            int max5 = -4;
            int i1, i2, i3, i4, i5;
            i1 = i2 = i3 = i4 = i5 = 0;
            for(int i = 0; i < allCards.size(); i++) {
                if(allCards.get(i).getSuit() == suit) {
                    if(allCards.get(i).getValue() > max1) {
                        max5 = max4;
                        max4 = max3;
                        max3 = max2;
                        max2 = max1;
                        max1 = allCards.get(i).getValue();
                        i5 = i4;
                        i4 = i3;
                        i3 = i2;
                        i2 = i1;
                        i1 = i;
                    }
                    else if(allCards.get(i).getValue() > max2) {
                        max5 = max4;
                        max4 = max3;
                        max3 = max2;
                        max2 = allCards.get(i).getValue();
                        i5 = i4;
                        i4 = i3;
                        i3 = i2;
                        i2 = i;
                    }
                    else if(allCards.get(i).getValue() > max3) {
                        max5 = max4;
                        max4 = max3;
                        max3 = allCards.get(i).getValue();
                        i5 = i4;
                        i4 = i3;
                        i3 = i;
                    }
                    else if(allCards.get(i).getValue() > max4) {
                        max5 = max4;
                        max4 = allCards.get(i).getValue();
                        i5 = i4;
                        i4 = i;
                    }
                    else if(allCards.get(i).getValue() > max5) {
                        max5 = allCards.get(i).getValue();
                        i5 = i;
                    }
                }
            }
            bestHand.put(0, allCards.get(i1));
            bestHand.put(1, allCards.get(i2));
            bestHand.put(2, allCards.get(i3));
            bestHand.put(3, allCards.get(i4));
            bestHand.put(4, allCards.get(i5));

            nameShort += ", " + bestHand.get(0).getValueNameLong() + " high";
            name = nameShort;
            points = 600000 + 38416* bestHand.get(0).getValue() + 2744* bestHand.get(1).getValue() + 196* bestHand.get(2).getValue()
                    + 14* bestHand.get(3).getValue() + bestHand.get(4).getValue();
        }

        //STRAIGHT/////////////////////////////////////////
        else if(isStraightAceHigh()) {
            nameShort = "Straight";
            name = "Straight, Ace high";
            points = 16370;
            for(int i = 0; i < allCards.size(); i++) {
                if(allCards.get(i).getValue() == 13) bestHand.put(0, allCards.get(i));
                if(allCards.get(i).getValue() == 12) bestHand.put(1, allCards.get(i));
                if(allCards.get(i).getValue() == 11) bestHand.put(2, allCards.get(i));
                if(allCards.get(i).getValue() == 10) bestHand.put(3, allCards.get(i));
                if(allCards.get(i).getValue() == 9) bestHand.put(4, allCards.get(i));
            }
        }

        else if(isStraight()) {
            nameShort = "Straight";
            int max1 = 0;
            int max2 = -1;
            int max3 = -2;
            int max4 = -3;
            int max5 = -4;
            int max6 = -5;
            int max7 = -6;
            int i1, i2, i3, i4, i5, i6, i7;
            i1 = i2 = i3 = i4 = i5 = i6 = i7 = 0;
            for(int k = 0; k < allCards.size(); k++) {
                if(allCards.get(k).getValue() % 13 > max1) {
                    max7 = max6;
                    max6 = max5;
                    max5 = max4;
                    max4 = max3;
                    max3 = max2;
                    max2 = max1;
                    max1 = allCards.get(k).getValue() % 13;
                    i7 = i6;
                    i6 = i5;
                    i5 = i4;
                    i4 = i3;
                    i3 = i2;
                    i2 = i1;
                    i1 = k;
                }
                else if(allCards.get(k).getValue() % 13 > max2 && allCards.get(k).getValue() % 13 < max1) {
                    max7 = max6;
                    max6 = max5;
                    max5 = max4;
                    max4 = max3;
                    max3 = max2;
                    max2 = allCards.get(k).getValue() % 13;
                    i7 = i6;
                    i6 = i5;
                    i5 = i4;
                    i4 = i3;
                    i3 = i2;
                    i2 = k;
                }
                else if(allCards.get(k).getValue() % 13 > max3 && allCards.get(k).getValue() % 13 < max2) {
                    max7 = max6;
                    max6 = max5;
                    max5 = max4;
                    max4 = max3;
                    max3 = allCards.get(k).getValue() % 13;
                    i7 = i6;
                    i6 = i5;
                    i5 = i4;
                    i4 = i3;
                    i3 = k;
                }
                else if(allCards.get(k).getValue() % 13 > max4 && allCards.get(k).getValue() % 13 < max3) {
                    max7 = max6;
                    max6 = max5;
                    max5 = max4;
                    max4 = allCards.get(k).getValue() % 13;
                    i7 = i6;
                    i6 = i5;
                    i5 = i4;
                    i4 = k;
                }
                else if(allCards.get(k).getValue() % 13 > max5 && allCards.get(k).getValue() % 13 < max4) {
                    max7 = max6;
                    max6 = max5;
                    max5 = allCards.get(k).getValue() % 13;
                    i7 = i6;
                    i6 = i5;
                    i5 = k;
                }
                else if(allCards.get(k).getValue() % 13 > max6 && allCards.get(k).getValue() % 13 < max5) {
                    max7 = max6;
                    max6 = allCards.get(k).getValue() % 13;
                    i7 = i6;
                    i6 = k;
                }
                else if(allCards.get(k).getValue() % 13 > max7 && allCards.get(k).getValue() % 13 < max6) {
                    max7 = allCards.get(k).getValue() % 13;
                    i7 = k;
                }
            }
            if(max1 - max5 == 4) {
                bestHand.put(0, allCards.get(i1));
                bestHand.put(1, allCards.get(i2));
                bestHand.put(2, allCards.get(i3));
                bestHand.put(3, allCards.get(i4));
                bestHand.put(4, allCards.get(i5));
            }
            else if(max2 - max6 == 4) {
                bestHand.put(0, allCards.get(i2));
                bestHand.put(1, allCards.get(i3));
                bestHand.put(2, allCards.get(i4));
                bestHand.put(3, allCards.get(i5));
                bestHand.put(4, allCards.get(i6));
            }
            else {
                bestHand.put(0, allCards.get(i3));
                bestHand.put(1, allCards.get(i4));
                bestHand.put(2, allCards.get(i5));
                bestHand.put(3, allCards.get(i6));
                bestHand.put(4, allCards.get(i7));
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
                    bestHand.put(key, allCards.get(i));
                    key++;
                }
            int i1 = 0;
            int i2 = 0;
            int max1 = 0;
            int max2 = -1;
            for(int j = 0; j < allCards.size(); j++)
                if(allCards.get(j).getValue() != value1) {
                    if(allCards.get(j).getValue() > max1) {
                        max2 = max1;
                        max1 = allCards.get(j).getValue();
                        i2 = i1;
                        i1 = j;
                    }
                    else if(allCards.get(j).getValue() > max2) {
                        max2 = allCards.get(j).getValue();
                        i2 = j;
                    }
                }
            bestHand.put(3, allCards.get(i1));
            bestHand.put(4, allCards.get(i2));

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
                    bestHand.put(key, allCards.get(i));
                    key++;
                }
            }
            for(int j = 0; j < allCards.size(); j++) {
                if(allCards.get(j).getValue() == value2) {
                    bestHand.put(key, allCards.get(j));
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
            bestHand.put(4, allCards.get(f));

            nameShort += ", " + bestHand.get(0).getValueNameLong() + "s and " + bestHand.get(2).getValueNameLong() + "s";
            name = nameShort + " with " + bestHand.get(4).getValueNameLong() + " kicker";
            points = 580000 + 196* bestHand.get(0).getValue() + 14* bestHand.get(2).getValue() + bestHand.get(4).getValue();
        }

        //ONE PAIR////////////////////////////////////
        else if(maxValue1 == 2) {
            nameShort = "One pair";
            int key = 0;
            for(int i = 0; i < allCards.size(); i++)
                if(allCards.get(i).getValue() == value1) {
                    bestHand.put(key, allCards.get(i));
                    key++;
                }
            int max1 = 0;
            int max2 = -1;
            int max3 = -2;
            int i1, i2, i3;
            i1 = i2 = i3 = 0;
            for(int j = 0; j < allCards.size(); j++)
                if(allCards.get(j).getValue() != value1) {
                    if(allCards.get(j).getValue() > max1) {
                        max3 = max2;
                        max2 = max1;
                        max1 = allCards.get(j).getValue();
                        i3 = i2;
                        i2 = i1;
                        i1 = j;
                    }
                    else if(allCards.get(j).getValue() > max2) {
                        max3 = max2;
                        max2 = allCards.get(j).getValue();
                        i3 = i2;
                        i2 = j;
                    }
                    else if(allCards.get(j).getValue() > max3) {
                        max3 = allCards.get(j).getValue();
                        i3 = j;
                    }
                }
            bestHand.put(2, allCards.get(i1));
            bestHand.put(3, allCards.get(i2));
            bestHand.put(4, allCards.get(i3));

            nameShort += ", " + bestHand.get(0).getValueNameLong() + "s";
            name = nameShort + " with " + bestHand.get(2).getValueNameLong() + "-"
                    + bestHand.get(3).getValueNameLong() + "-" + bestHand.get(4).getValueNameLong() + " kicker";
            points = 540000 + 2744* bestHand.get(0).getValue() + 196* bestHand.get(2).getValue() + 14* bestHand.get(3).getValue()
                    + bestHand.get(4).getValue();
        }

        //HIGH CARD//////////////////////////////////////
        else {
            nameShort = "High card";
            int max1 = 0;
            int max2 = -1;
            int max3 = -2;
            int max4 = -3;
            int max5 = -4;
            int i1, i2, i3, i4, i5;
            i1 = i2 = i3 = i4 = i5 = 0;
            for(int i = 0; i < allCards.size(); i++) {
                if(allCards.get(i).getValue() > max1) {
                    max5 = max4;
                    max4 = max3;
                    max3 = max2;
                    max2 = max1;
                    max1 = allCards.get(i).getValue();
                    i5 = i4;
                    i4 = i3;
                    i3 = i2;
                    i2 = i1;
                    i1 = i;
                }
                else if(allCards.get(i).getValue() > max2) {
                    max5 = max4;
                    max4 = max3;
                    max3 = max2;
                    max2 = allCards.get(i).getValue();
                    i5 = i4;
                    i4 = i3;
                    i3 = i2;
                    i2 = i;
                }
                else if(allCards.get(i).getValue() > max3) {
                    max5 = max4;
                    max4 = max3;
                    max3 = allCards.get(i).getValue();
                    i5 = i4;
                    i4 = i3;
                    i3 = i;
                }
                else if(allCards.get(i).getValue() > max4) {
                    max5 = max4;
                    max4 = allCards.get(i).getValue();
                    i5 = i4;
                    i4 = i;
                }
                else if(allCards.get(i).getValue() > max5) {
                    max5 = allCards.get(i).getValue();
                    i5 = i;
                }
            }
            bestHand.put(0, allCards.get(i1));
            bestHand.put(1, allCards.get(i2));
            bestHand.put(2, allCards.get(i3));
            bestHand.put(3, allCards.get(i4));
            bestHand.put(4, allCards.get(i5));

            nameShort += " " + bestHand.get(0).getValueNameLong();
            name = nameShort + " with " + bestHand.get(1).getValueNameLong() + "-"
                    + bestHand.get(2).getValueNameLong() + "-" + bestHand.get(3).getValueNameLong() + "-"
                    + bestHand.get(4).getValueNameLong() + " kicker";
            points = 38416* bestHand.get(0).getValue() + 2744* bestHand.get(1).getValue() + 192* bestHand.get(2).getValue()
                    + 14* bestHand.get(3).getValue() + bestHand.get(4).getValue();
        }
    }

    /* ==== GETTERS AND SETTERS ==== */

    int getPoints() {
        return points;
    }

    String getName() {
        return name;
    }

    public HashMap<Integer, Card> getBestHand() {
        return bestHand;
    }

    HashMap<Integer, Card> getAllCards() {
        return allCards;
    }
}
