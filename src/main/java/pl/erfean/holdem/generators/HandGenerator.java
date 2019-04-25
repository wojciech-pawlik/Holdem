package pl.erfean.holdem.generators;

import com.opencsv.CSVWriter;
import pl.erfean.holdem.model.Deck;
import pl.erfean.holdem.model.Hand;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Random;
import java.util.stream.Collectors;

public class HandGenerator {
    private enum HandNames {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        STRAIGHT,
        FLUSH,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        STRAIGHT_FLUSH
    }

    public static void main(String[] args) {
        var pathHands = "hands.csv";
        var numberOfHands = 1000;
        generateHands(numberOfHands, pathHands);
    }

    private static void generateHands(int numberOfRecords, String filePath) {
        var deck = new Deck();
        var random = new Random();
        var handType = 0;
        String name;
        var handArray = new String[15];
        var file = new File(filePath);
        try {
            // create file if not exists
            try {
                file.createNewFile();
            } catch (FileAlreadyExistsException e) {
                e.printStackTrace();
            }
            // create FileWriter object with file as parameter
            var outputFile = new FileWriter(file);

            // create CSVWriter object FileWriter object as parameter
            var writer = new CSVWriter(outputFile);

            // write all hands from selected range
            for(int i = 0; i < numberOfRecords; i++) {
                deck.shuffle();
                var hand = new Hand(deck.getCards().stream().limit(7).collect(Collectors.toList()));
                System.out.println(hand.getAllCards());
                System.out.println(hand.checkHand());
                hand.checkHand();
                System.out.println(hand.getName());
                name = hand.getName();
                switch (name) {
                    case "Royal flush":
                    case "Straight flush":
                        handType = HandNames.STRAIGHT_FLUSH.ordinal();
                        break;
                    case "Four of a kind":
                        handType = HandNames.FOUR_OF_A_KIND.ordinal();
                        break;
                    case "Full house":
                        handType = HandNames.FULL_HOUSE.ordinal();
                        break;
                    case "Flush":
                        handType = HandNames.FLUSH.ordinal();
                        break;
                    case "Straight":
                        handType = HandNames.STRAIGHT.ordinal();
                        break;
                    case "Three of a kind":
                        handType = HandNames.THREE_OF_A_KIND.ordinal();
                        break;
                    case "Two pair":
                        handType = HandNames.TWO_PAIR.ordinal();
                        break;
                    case "One pair":
                        handType = HandNames.ONE_PAIR.ordinal();
                        break;
                    default:
                        handType = HandNames.HIGH_CARD.ordinal();
                        break;
                }
                for(int card = 0; card < 7; card++) {
                    handArray[2*card] = hand.getAllCards().get(card).getValue() + "";
                    handArray[2*card+1] = hand.getAllCards().get(card).getSuit() + "";
                }
                handArray[14] = handType + "";
                writer.writeNext(handArray);
                hand.destroyHand();
            }

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
