package poker;

import lombok.Getter;
import lombok.Setter;
import poker.comprators.CardComparatorAceAsOne;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@Setter
public class Hand {
    private int points;
    private String name, shortName;
    private int maxCountOfSuitedCards;				// Max count of cards with the same mostFrequentSuit -> Royal Flush, Straight Flush, Flush
    private int mostFrequentSuit;				    // Suit of maxCountOfSuitedCards cards -> Royal Flush, Straight Flush, Flush
    private int maxCountOfValues;				    // Max count of cards with the same value -> Quads, Trips, Full House, Two Pair, One Pair
    private int mostFrequentValue;				    // Value of cards with value of maxCountOfValues -> Quads, Trips, Full House, Two Pair, One Pair
    private int secondMaxCountOfValues;			// Second max count of cards with the same value -> Full House, Two Pair
    private int secondMostFrequentValue;			// Value of cards with value of secondMaxCountOfValues -> Full House, Two Pair
    private ArrayList<Card> bestHand;	            // Best 5-card hand
    private ArrayList<Card> allCards;	            // All cards which can build a hand

    public Hand() {
        points = 0;
        name = "";
        shortName = "";
        maxCountOfSuitedCards = 0;
        mostFrequentSuit = 0;
        maxCountOfValues = 0;
        mostFrequentValue = 0;
        secondMaxCountOfValues = 0;
        secondMostFrequentValue = 0;
        bestHand = new ArrayList<>(5);
        allCards = new ArrayList<>(7);
    }

    public Hand(Player player, Board board) {
        this();
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

        howManySuited();
        maxValuePowers();

        checkHand();
    }

    /* ==== HELPFUL METHODS TO CHECK HAND ==== */

    // ONLY FOR TESTS //
    public void suitsAndValues() {
        howManySuited();
        maxValuePowers();
    }

    // Checks Royal Flush
    private int suitValue() {
        if(maxCountOfSuitedCards < 5)
            return 0;
        return allCards.stream()
                .filter(card -> (card.getSuit() == mostFrequentSuit))
                .sorted(Comparator.comparingInt(Card::getValue).reversed())
                .limit(5)
                .mapToInt(Card::getValue)
                .sum();
    }

    // Checks what is the most frequent suit and is't frequency
    private void howManySuited() {
        Map<Integer, Long> countOfSuits = allCards.stream()
                .collect(Collectors.groupingBy(Card::getSuit, Collectors.counting()));
        maxCountOfSuitedCards = Collections.max(countOfSuits.values()).intValue();
        mostFrequentSuit = countOfSuits.entrySet().stream()
                .filter(value -> (value.getValue().intValue() == maxCountOfSuitedCards)).findFirst().get().getKey();
    }

    // Checks Straight Flush
    private boolean isStraightFlush() {
        if(maxCountOfSuitedCards < 5) return false;
        List<Card> cardsOfMostFrequentSuit = allCards.stream()
                .filter(card -> (card.getSuit() == mostFrequentSuit))
                .sorted(new CardComparatorAceAsOne())
                .collect(Collectors.toList());
        if(cardsOfMostFrequentSuit.size() == 5)
            return cardsOfMostFrequentSuit.get(0).getValue() - (cardsOfMostFrequentSuit.get(4).getValue() % 13) == 4;
        if(cardsOfMostFrequentSuit.size() == 6)
            return cardsOfMostFrequentSuit.get(0).getValue() - cardsOfMostFrequentSuit.get(4).getValue() == 4 ||
                    cardsOfMostFrequentSuit.get(1).getValue() - (cardsOfMostFrequentSuit.get(5).getValue() % 13) == 4;
        if(cardsOfMostFrequentSuit.size() == 7)
            return cardsOfMostFrequentSuit.get(0).getValue() - cardsOfMostFrequentSuit.get(4).getValue() == 4 ||
                    cardsOfMostFrequentSuit.get(1).getValue() - cardsOfMostFrequentSuit.get(5).getValue() == 4 ||
                    cardsOfMostFrequentSuit.get(2).getValue() - (cardsOfMostFrequentSuit.get(6).getValue() % 13) == 4;
        return false;
    }

    // Checks two the most frequent values and their frequency
    private void maxValuePowers() {
        maxCountOfValues = secondMaxCountOfValues = mostFrequentValue = secondMostFrequentValue = 0;
        Map<Integer, Long> countOfValues = allCards.stream()
                .sorted(Comparator.comparingInt(Card::getValue).reversed().thenComparing(Card::getSuit))
                .collect(Collectors.groupingBy(Card::getValue, Collectors.counting()));
        for(Integer key : countOfValues.keySet()) {
            if(countOfValues.get(key) >= maxCountOfValues) {
                secondMaxCountOfValues = maxCountOfValues;
                secondMostFrequentValue = mostFrequentValue;
                maxCountOfValues = countOfValues.get(key).intValue();
                mostFrequentValue = key;
            }
            else if(countOfValues.get(key) >= secondMaxCountOfValues) {
                secondMaxCountOfValues = countOfValues.get(key).intValue();
                secondMostFrequentValue = key;
            }
        }
    }

    // Checks if there is an Ace-high Straight
    private boolean isStraightAceHigh() {
        List<Integer> allCardsDistinctValues = allCards.stream()
                .map(Card::getValue)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        if(allCardsDistinctValues.size() >= 5)
            return allCardsDistinctValues.get(0) == 13 && allCardsDistinctValues.get(4) == 9;
        return false;
    }

    // Checks Straight (if there is no Ace-high Straight - but without 'if' statement checking Ace-high straight
    //      because I use this method in another 'else if' block)
    private boolean isStraight() {
        List<Integer> allCardsDistinctValuesAceAsOne = allCards.stream()
                .map(card -> (card.getValue() % 13))
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        if(allCardsDistinctValuesAceAsOne.size() == 7)
            return allCardsDistinctValuesAceAsOne.get(0) - allCardsDistinctValuesAceAsOne.get(4) == 4 ||
                allCardsDistinctValuesAceAsOne.get(1) - allCardsDistinctValuesAceAsOne.get(5) == 4 ||
                allCardsDistinctValuesAceAsOne.get(2) - allCardsDistinctValuesAceAsOne.get(6) == 4;
        if(allCardsDistinctValuesAceAsOne.size() == 6)
            return allCardsDistinctValuesAceAsOne.get(0) - allCardsDistinctValuesAceAsOne.get(4) == 4 ||
                    allCardsDistinctValuesAceAsOne.get(1) - allCardsDistinctValuesAceAsOne.get(5) == 4;
        if(allCardsDistinctValuesAceAsOne.size() == 5)
            return allCardsDistinctValuesAceAsOne.get(0) - allCardsDistinctValuesAceAsOne.get(4) == 4;
        return false;
    }

    /* ==== MAIN FUNCTION WHICH CHECKS THE HAND ==== */

    @SuppressWarnings("Duplicates")
    public int checkHand() {
        //ROYAL FLUSH//
        if(suitValue() == 55) {
            points = 1200000;
            name = shortName = "Royal flush";
            bestHand.addAll(allCards.stream()
                    .filter(card -> (card.getSuit() == mostFrequentSuit))
                    .sorted(Comparator.comparingInt(Card::getValue).reversed())
                    .limit(5)
                    .collect(Collectors.toList()));
        }

        //STRAIGHT FLUSH//
        else if(isStraightFlush()) {
            name = "Straight flush";
            ArrayList<Card> cardsOfMostFrequentSuit = (ArrayList<Card>)allCards.stream()
                    .filter(card -> (card.getSuit() == mostFrequentSuit))
                    .sorted(new CardComparatorAceAsOne())
                    .collect(Collectors.toList());
            if(cardsOfMostFrequentSuit.size() == 7) {
                if(cardsOfMostFrequentSuit.get(0).getValue() - cardsOfMostFrequentSuit.get(4).getValue() == 4)
                    bestHand.addAll(cardsOfMostFrequentSuit.subList(0, 5));
                else if(cardsOfMostFrequentSuit.get(1).getValue() - cardsOfMostFrequentSuit.get(5).getValue() == 4)
                    bestHand.addAll(cardsOfMostFrequentSuit.subList(1, 6));
                else bestHand.addAll(cardsOfMostFrequentSuit.subList(2, 7));
            }
            else if(cardsOfMostFrequentSuit.size() == 6) {
                if(cardsOfMostFrequentSuit.get(0).getValue() - cardsOfMostFrequentSuit.get(4).getValue() == 4)
                    bestHand.addAll(cardsOfMostFrequentSuit.subList(0, 5));
                else bestHand.addAll(cardsOfMostFrequentSuit.subList(1, 6));
            }
            else bestHand = cardsOfMostFrequentSuit;

            name = new StringBuilder(name).append(" - ").append(bestHand.get(0).getValueNameLong()).append(" high").toString();
            shortName = name;
            points = 1180000 + bestHand.get(0).getValue();
        }

        //FOUR OF A KIND//
        else if(maxCountOfValues == 4) {
            shortName = "Four of a kind";
            allCards.sort(Comparator.comparingInt(Card::getValue).reversed().thenComparing(Card::getSuit));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() == mostFrequentValue)).collect(Collectors.toList()));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() != mostFrequentValue)).limit(2).collect(Collectors.toList()));
            points = 1150000 + 14* bestHand.get(0).getValue() + bestHand.get(4).getValue();
            shortName = new StringBuilder(shortName).append(", ").append(bestHand.get(0).getValueNameLong()).append("s").toString();
            name = new StringBuilder(shortName).append(" with ").append(bestHand.get(4).getValueNameLong()).append(" kicker").toString();
        }

        //FULL HOUSE//
        else if(maxCountOfValues == 3 && secondMaxCountOfValues >= 2) {
            shortName = "Full house";
            allCards.sort(Comparator.comparingInt(Card::getValue).reversed().thenComparing(Card::getSuit));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() == mostFrequentValue)).collect(Collectors.toList()));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() == secondMostFrequentValue)).collect(Collectors.toList()));
            shortName = new StringBuilder(shortName).append(", ").append(bestHand.get(0).getValueNameLong()).append("s full of ").append(bestHand.get(3).getValueNameLong()).append("s").toString();
            name = shortName;
            points = 1140000 + 14* bestHand.get(0).getValue() + bestHand.get(3).getValue();
        }

        //FLUSH//
        else if(maxCountOfSuitedCards >= 5) {
            shortName = "Flush";
            bestHand.addAll(allCards.stream()
                    .filter(card -> (card.getSuit() == mostFrequentSuit))
                    .sorted(Comparator.comparingInt(Card::getValue).reversed())
                    .limit(5)
                    .collect(Collectors.toList()));

            shortName = new StringBuilder(shortName).append(", ").append(bestHand.get(0).getValueNameLong()).append(" high").toString();
            name = shortName;
            points = 600000 + 38416* bestHand.get(0).getValue() + 2744* bestHand.get(1).getValue() + 196* bestHand.get(2).getValue()
                    + 14* bestHand.get(3).getValue() + bestHand.get(4).getValue();
        }

        //STRAIGHT//
        else if(isStraightAceHigh()) {
            shortName = "Straight";
            name = "Straight, Ace high";
            points = 600000;
            bestHand.addAll(allCards.stream().filter(distinctByKey(Card::getValue)).limit(5).collect(Collectors.toList()));
        }

        else if(isStraight()) {
            shortName = "Straight";
            List<Card> allCardsDistinctValuesAceAsOne = allCards.stream()
                    .filter(distinctByKey(Card::getValue))
                    .distinct()
                    .sorted(new CardComparatorAceAsOne())
                    .collect(Collectors.toList());
            if(allCardsDistinctValuesAceAsOne.size() == 7)
                if(allCardsDistinctValuesAceAsOne.get(0).getValue() - allCardsDistinctValuesAceAsOne.get(4).getValue() == 4)
                    bestHand.addAll(allCardsDistinctValuesAceAsOne.subList(0,5));
                else if(allCardsDistinctValuesAceAsOne.get(1).getValue() - allCardsDistinctValuesAceAsOne.get(5).getValue() == 4)
                    bestHand.addAll(allCardsDistinctValuesAceAsOne.subList(1,6));
                else bestHand.addAll(allCardsDistinctValuesAceAsOne.subList(2,7));
            if(allCardsDistinctValuesAceAsOne.size() == 6)
                if(allCardsDistinctValuesAceAsOne.get(0).getValue() - allCardsDistinctValuesAceAsOne.get(4).getValue() == 4)
                    bestHand.addAll(allCardsDistinctValuesAceAsOne.subList(0,5));
                else bestHand.addAll(allCardsDistinctValuesAceAsOne.subList(1,6));
            if(allCardsDistinctValuesAceAsOne.size() == 5)
                bestHand.addAll(allCardsDistinctValuesAceAsOne.subList(0,5));

            shortName = new StringBuilder(shortName).append(", ").append(bestHand.get(0).getValueNameLong()).append(" high").toString();
            name = shortName;
            points = 590000 + bestHand.get(0).getValue();
        }

        //THREE OF A KIND//
        else if(maxCountOfValues == 3) {
            shortName = "Three of a kind";
            allCards.sort(Comparator.comparingInt(Card::getValue).reversed().thenComparing(Card::getSuit));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() == mostFrequentValue)).collect(Collectors.toList()));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() != mostFrequentValue)).limit(2).collect(Collectors.toList()));

            shortName = new StringBuilder(shortName).append(", ").append(bestHand.get(0).getValueNameLong()).append("s").toString();
            name = new StringBuilder(shortName).append(" with ").append(bestHand.get(3).getValueNameLong())
                    .append("-").append(bestHand.get(4).getValueNameLong()).append(" kicker").toString();
            points = 583000 + 196* bestHand.get(1).getValue() + 14* bestHand.get(3).getValue() + bestHand.get(4).getValue();
        }

        //TWO PAIR//
        else if(secondMaxCountOfValues == 2) {
            shortName = "Two pair";
            allCards.sort(Comparator.comparingInt(Card::getValue).reversed().thenComparing(Card::getSuit));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() == mostFrequentValue)).collect(Collectors.toList()));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() == secondMostFrequentValue)).collect(Collectors.toList()));
            bestHand.addAll(allCards.stream()
                    .filter(card -> (card.getValue() != mostFrequentValue && card.getValue() != secondMostFrequentValue)).limit(1)
                    .collect(Collectors.toList()));

            shortName = new StringBuilder(shortName).append(", ").append(bestHand.get(0).getValueNameLong())
                    .append("s and ").append(bestHand.get(2).getValueNameLong()).append("s").toString();
            name = new StringBuilder(shortName).append(" with ").append(bestHand.get(4).getValueNameLong()).append(" kicker").toString();
            points = 580000 + 196* bestHand.get(0).getValue() + 14* bestHand.get(2).getValue() + bestHand.get(4).getValue();
        }

        //ONE PAIR//
        else if(maxCountOfValues == 2) {
            shortName = "One pair";
            allCards.sort(Comparator.comparingInt(Card::getValue).reversed().thenComparing(Card::getSuit));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() == mostFrequentValue)).collect(Collectors.toList()));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() != mostFrequentValue)).limit(3).collect(Collectors.toList()));

            shortName = new StringBuilder(shortName).append(", ").append(bestHand.get(0).getValueNameLong()).append("s").toString();
            name = new StringBuilder(shortName).append(" with ").append(bestHand.get(2).getValueNameLong())
                    .append("-").append(bestHand.get(3).getValueNameLong()).append("-").append(bestHand.get(4).getValueNameLong())
                    .append(" kicker").toString();
            points = 540000 + 2744* bestHand.get(0).getValue() + 196* bestHand.get(2).getValue() + 14* bestHand.get(3).getValue()
                    + bestHand.get(4).getValue();
        }

        //HIGH CARD//
        else {
            shortName = "High card";
            allCards.sort(Comparator.comparingInt(Card::getValue).reversed().thenComparing(Card::getSuit));
            bestHand.addAll(allCards.stream().limit(5).collect(Collectors.toList()));

            shortName = new StringBuilder(shortName).append(" ").append(bestHand.get(0).getValueNameLong()).toString();
            name = new StringBuilder(shortName).append(" with ").append(bestHand.get(1).getValueNameLong()).append("-")
                    .append(bestHand.get(2).getValueNameLong()).append("-").append(bestHand.get(3).getValueNameLong()).append("-")
                    .append(bestHand.get(4).getValueNameLong()).append(" kicker").toString();
            points = 38416* bestHand.get(0).getValue() + 2744* bestHand.get(1).getValue() + 196* bestHand.get(2).getValue()
                    + 14* bestHand.get(3).getValue() + bestHand.get(4).getValue();
        }
        return points;
    }

    public void destroyHand() {
        points = 0;
        name = "";
        shortName = "";
        maxCountOfSuitedCards = 0;
        mostFrequentSuit = 0;
        maxCountOfValues = 0;
        mostFrequentValue = 0;
        secondMaxCountOfValues = 0;
        secondMostFrequentValue = 0;
        bestHand.clear();
        allCards.clear();
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}