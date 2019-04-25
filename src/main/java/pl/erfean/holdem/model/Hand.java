package pl.erfean.holdem.model;

import lombok.Getter;
import lombok.Setter;
import pl.erfean.holdem.model.comparators.CardComparatorAceAsOne;
import pl.erfean.holdem.model.interfaces.HandI;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
public class Hand implements HandI {
    private int points;
    private int maxCountOfSuitedCards;				// Max count of cards with the same mostFrequentSuit -> Royal Flush, Straight Flush, Flush
    private int mostFrequentSuit;				    // Suit of maxCountOfSuitedCards cards -> Royal Flush, Straight Flush, Flush
    private int maxCountOfValues;				    // Max count of cards with the same value -> Quads, Trips, Full House, Two Pair, One Pair
    private int mostFrequentValue;				    // Value of cards with value of maxCountOfValues -> Quads, Trips, Full House, Two Pair, One Pair
    private int secondMaxCountOfValues;			// Second max count of cards with the same value -> Full House, Two Pair
    private int secondMostFrequentValue;			// Value of cards with value of secondMaxCountOfValues -> Full House, Two Pair
    private List<Card> bestHand;	                // Best 5-card hand
    private List<Card> allCards;	                // All cards which can build a hand

    private static final int[] HAND_POINTS_BASE = {1200000, 1180000, 1150000, 1140000, 600000, 590000, 583000, 580000, 540000, 0};
    private static final String[] HAND_TYPES = {"Royal flush", "Straight flush", "Four of a kind", "Full house", "Flush", "Straight", "Three of a kind",
                                                "Two pair", "One pair", "High card"};

    public Hand(List<Card> allCards) {
        this.allCards = allCards;
        bestHand = new ArrayList<>(5);

        howManySuited();
        maxValuePowers();
    }

    /* ==== HELPFUL METHODS TO CHECK HAND ==== */

    // Checks Royal Flush
    public int suitValue() {
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
    public void howManySuited() {
        Map<Integer, Long> countOfSuits = allCards.stream()
                .collect(Collectors.groupingBy(Card::getSuit, Collectors.counting()));
        maxCountOfSuitedCards = Collections.max(countOfSuits.values()).intValue();
        mostFrequentSuit = countOfSuits.entrySet().stream()
                .filter(value -> (value.getValue().intValue() == maxCountOfSuitedCards)).findFirst().get().getKey();
    }

    // Checks Straight Flush
    public boolean isStraightFlush() {
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
    public void maxValuePowers() {
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
    public boolean isStraightAceHigh() {
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
    public boolean isStraight() {
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

    /* ==== MAIN METHOD WHICH CHECKS THE HAND ==== */

    @SuppressWarnings("Duplicates")
    public int checkHand() {
        //ROYAL FLUSH//
        if(suitValue() == 55) {
            points = HAND_POINTS_BASE[0];
            bestHand.addAll(allCards.stream()
                    .filter(card -> (card.getSuit() == mostFrequentSuit))
                    .sorted(Comparator.comparingInt(Card::getValue).reversed())
                    .limit(5)
                    .collect(Collectors.toList()));
        }

        //STRAIGHT FLUSH//
        else if(isStraightFlush()) {
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

            points = HAND_POINTS_BASE[1] + bestHand.get(0).getValue();
        }

        //FOUR OF A KIND//
        else if(maxCountOfValues == 4) {
            allCards.sort(Comparator.comparingInt(Card::getValue).reversed().thenComparing(Card::getSuit));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() == mostFrequentValue)).collect(Collectors.toList()));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() != mostFrequentValue)).limit(1).collect(Collectors.toList()));
            points = HAND_POINTS_BASE[2] + 14* bestHand.get(0).getValue() + bestHand.get(4).getValue();
        }

        //FULL HOUSE//
        else if(maxCountOfValues == 3 && secondMaxCountOfValues >= 2) {
            allCards.sort(Comparator.comparingInt(Card::getValue).reversed().thenComparing(Card::getSuit));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() == mostFrequentValue)).collect(Collectors.toList()));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() == secondMostFrequentValue)).collect(Collectors.toList()));

            points = HAND_POINTS_BASE[3] + 14* bestHand.get(0).getValue() + bestHand.get(3).getValue();
        }

        //FLUSH//
        else if(maxCountOfSuitedCards >= 5) {
            bestHand.addAll(allCards.stream()
                    .filter(card -> (card.getSuit() == mostFrequentSuit))
                    .sorted(Comparator.comparingInt(Card::getValue).reversed())
                    .limit(5)
                    .collect(Collectors.toList()));

            points = HAND_POINTS_BASE[4] + 38416* bestHand.get(0).getValue() + 2744* bestHand.get(1).getValue() + 196* bestHand.get(2).getValue()
                    + 14* bestHand.get(3).getValue() + bestHand.get(4).getValue();
        }

        //STRAIGHT//
        else if(isStraightAceHigh()) {
            points = 600000;
            bestHand.addAll(allCards.stream()
                    .filter(distinctByKey(Card::getValue))
                    .limit(5)
                    .sorted(Comparator.comparingInt(Card::getValue).reversed())
                    .collect(Collectors.toList()));
        }

        else if(isStraight()) {
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

            points = HAND_POINTS_BASE[5] + bestHand.get(0).getValue();
        }

        //THREE OF A KIND//
        else if(maxCountOfValues == 3) {
            allCards.sort(Comparator.comparingInt(Card::getValue).reversed().thenComparing(Card::getSuit));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() == mostFrequentValue)).collect(Collectors.toList()));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() != mostFrequentValue)).limit(2).collect(Collectors.toList()));

            points = HAND_POINTS_BASE[6] + 196* bestHand.get(1).getValue() + 14* bestHand.get(3).getValue() + bestHand.get(4).getValue();
        }

        //TWO PAIR//
        else if(secondMaxCountOfValues == 2) {
            allCards.sort(Comparator.comparingInt(Card::getValue).reversed().thenComparing(Card::getSuit));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() == mostFrequentValue)).collect(Collectors.toList()));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() == secondMostFrequentValue)).collect(Collectors.toList()));
            bestHand.addAll(allCards.stream()
                    .filter(card -> (card.getValue() != mostFrequentValue && card.getValue() != secondMostFrequentValue)).limit(1)
                    .collect(Collectors.toList()));

            points = HAND_POINTS_BASE[7] + 196* bestHand.get(0).getValue() + 14* bestHand.get(2).getValue() + bestHand.get(4).getValue();
        }

        //ONE PAIR//
        else if(maxCountOfValues == 2) {
            allCards.sort(Comparator.comparingInt(Card::getValue).reversed().thenComparing(Card::getSuit));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() == mostFrequentValue)).collect(Collectors.toList()));
            bestHand.addAll(allCards.stream().filter(card -> (card.getValue() != mostFrequentValue)).limit(3).collect(Collectors.toList()));

            points = HAND_POINTS_BASE[8] + 2744* bestHand.get(0).getValue() + 196* bestHand.get(2).getValue() + 14* bestHand.get(3).getValue()
                    + bestHand.get(4).getValue();
        }

        //HIGH CARD//
        else {
            allCards.sort(Comparator.comparingInt(Card::getValue).reversed().thenComparing(Card::getSuit));
            bestHand.addAll(allCards.stream().limit(5).collect(Collectors.toList()));

            points = 38416* bestHand.get(0).getValue() + 2744* bestHand.get(1).getValue() + 196* bestHand.get(2).getValue()
                    + 14* bestHand.get(3).getValue() + bestHand.get(4).getValue();
        }
        return points;
    }

    public String getType() {
        return HAND_TYPES[IntStream.range(0, HAND_POINTS_BASE.length)
                .filter(i -> (i <= points))
                .min().getAsInt()];
    }

    @SuppressWarnings("Duplicates")
    public String getName() {
        var stringBuilder = new StringBuilder();
        // Royal flush
        if(points == HAND_POINTS_BASE[0]) {
            return "Royal flush";
        }
        // Straight flush
        else if(points >= HAND_POINTS_BASE[1]) {
            return stringBuilder.append(HAND_TYPES[1])
                    .append(", ")
                    .append(bestHand.get(0).getValueName())
                    .append(" high")
                    .toString();
        }
        // Four of a kind
        else if(points >= HAND_POINTS_BASE[2]) {
            if(bestHand.get(0).hashCode() % 13 == 4)
                return stringBuilder.append(HAND_TYPES[2])
                    .append(", sixes with ")
                    .append(bestHand.get(4).getValueName())
                    .append("-kicker")
                    .toString();
            return stringBuilder.append(HAND_TYPES[2])
                    .append(", ")
                    .append(bestHand.get(0).getValueName())
                    .append(" with ")
                    .append(bestHand.get(4).getValueName())
                    .append("-kicker")
                    .toString();
        }
        // Full house
        else if(points >= HAND_POINTS_BASE[3]) {
            if(bestHand.get(0).hashCode() % 13 == 4)
                return stringBuilder.append(HAND_TYPES[3])
                        .append(", sixes full of ")
                        .append(bestHand.get(3).getValueName())
                        .append("s")
                        .toString();
            if(bestHand.get(3).hashCode() % 13 == 4)
                return stringBuilder.append(HAND_TYPES[3])
                        .append(", ")
                        .append(bestHand.get(0).getValueName())
                        .append("s full of sixes")
                        .toString();
            return stringBuilder.append(HAND_TYPES[3])
                    .append(", ")
                    .append(bestHand.get(0).getValueName())
                    .append("s full of ")
                    .append(bestHand.get(3).getValueName())
                    .append("s")
                    .toString();
        }
        // Flush
        else if(points > HAND_POINTS_BASE[4]) {
            return stringBuilder.append(HAND_TYPES[4])
                    .append(", ")
                    .append(bestHand.get(0).getValueName())
                    .append("-high")
                    .toString();
        }
        // Straight
        else if(points >= HAND_POINTS_BASE[5]) {
            return stringBuilder.append(HAND_TYPES[5])
                    .append(", ")
                    .append(bestHand.get(0).getValueName())
                    .append("-high")
                    .toString();
        }
        // Three of a kind
        else if(points >= HAND_POINTS_BASE[6]) {
            stringBuilder.append(HAND_TYPES[6])
                    .append(", ");
            if(bestHand.get(0).hashCode() % 13 == 4)
                stringBuilder.append("sixes with ");
            else stringBuilder.append(bestHand.get(0).getValueName())
                    .append("s with ");
            return stringBuilder.append(bestHand.get(3).getValueName())
                    .append("-")
                    .append(bestHand.get(4).getValueName())
                    .append("-kicker")
                    .toString();
        }
        // Two pair
        else if(points >= HAND_POINTS_BASE[7]) {
            stringBuilder.append(HAND_TYPES[7])
                    .append(", ");
            if(bestHand.get(0).hashCode() % 13 == 4)
                stringBuilder.append("sixes and ")
                        .append(bestHand.get(2).getValueName())
                        .append("s with ");
            else if(bestHand.get(2).hashCode() % 13 == 4)
                stringBuilder.append(bestHand.get(0).getValueName())
                        .append("s and sixes with ");
            else stringBuilder.append(bestHand.get(0).getValueName())
                        .append("s and ")
                        .append(bestHand.get(2).getValueName())
                        .append("s with ");
            return stringBuilder.append(bestHand.get(4).getValueName())
                    .append("-kicker")
                    .toString();
        }
        // One pair
        else if(points >= HAND_POINTS_BASE[8]) {
            stringBuilder.append(HAND_TYPES[8])
                    .append(", ");
            if(bestHand.get(0).hashCode() % 13 == 4)
                stringBuilder.append("sixes with ");
            else stringBuilder.append(bestHand.get(0).getValueName())
                    .append("s with ");
            return stringBuilder.append(bestHand.get(2).getValueName())
                    .append("-")
                    .append(bestHand.get(3).getValueName())
                    .append("-")
                    .append(bestHand.get(4).getValueName())
                    .append("-kicker")
                    .toString();
        }
        // High card
        else {
            return stringBuilder.append(HAND_TYPES[9])
                    .append(" ")
                    .append(bestHand.get(0).getValueName())
                    .toString();
        }
    }

    public void destroyHand() {
        points = 0;
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