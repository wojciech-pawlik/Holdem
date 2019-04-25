package pl.erfean.holdem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pl.erfean.holdem.model.Deck;
import pl.erfean.holdem.model.Hand;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class HandTest {
    private Deck deck;

    private String playerCard1, playerCard2, flop1, flop2, flop3, turn, river, record, nameOfHand;

    private static final String csvFile = "src/test/hands.csv";
    private static String path = new File("").getAbsolutePath();
    private static BufferedReader bufferedReader = null;
    private static final String csvSplitBy = ",";
    private static final int HANDS_COUNT = 34;

    public HandTest(String playerCard1, String playerCard2, String flop1, String flop2, String flop3, String turn, String river,
                    String record, String nameOfHand) {
        this.playerCard1 = playerCard1;
        this.playerCard2 = playerCard2;
        this.flop1 = flop1;
        this.flop2 = flop2;
        this.flop3 = flop3;
        this.turn = turn;
        this.river = river;
        this.record = record;
        this.nameOfHand = nameOfHand;
    }

    @Before
    public void setup() {
        deck = new Deck();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> loadHands() {
        var records = new Object[HANDS_COUNT][8];
        try {
            bufferedReader = new BufferedReader(new FileReader(path + "\\" + csvFile));
            bufferedReader.readLine();
            int numberOfRecord = 0;
            String line = "";
            while((line = bufferedReader.readLine()) != null) {
                String[] array = line.split(csvSplitBy);
                records[numberOfRecord++] = array;
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Arrays.asList(records);
    }


    @Test
    public void checkHands() {
        var hand = new Hand(Arrays.asList(
                deck.getCards().get(Integer.parseInt(playerCard1)),
                deck.getCards().get(Integer.parseInt(playerCard2)),
                deck.getCards().get(Integer.parseInt(flop1)),
                deck.getCards().get(Integer.parseInt(flop2)),
                deck.getCards().get(Integer.parseInt(flop3)),
                deck.getCards().get(Integer.parseInt(turn)),
                deck.getCards().get(Integer.parseInt(river))
        ));
        System.out.println(hand.getAllCards());

        hand.checkHand();

        System.out.println(hand.getBestHand());
        System.out.println("Gained points: " + hand.getPoints() + ", expected: " + record);
        System.out.println("Checked hand: " + hand.getName() + ", expected: " + nameOfHand);
        Assert.assertEquals(hand.getPoints(), Integer.parseInt(record));
        Assert.assertEquals(hand.getBestHand().size(), 5);
    }
}