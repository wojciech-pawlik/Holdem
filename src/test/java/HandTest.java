import poker.Deck;
import poker.Hand;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class HandTest {
    private Deck deck;
    private Hand hand;

    private String player1, player2, flop1, flop2, flop3, turn, river, record, nameOfHand;

    private static final String csvFile = "src/test/hands.csv";
    private static String path = new File("").getAbsolutePath();
    private static BufferedReader bufferedReader = null;
    private static final String csvSplitBy = ",";

    public HandTest(String player1, String player2, String flop1, String flop2, String flop3, String turn, String river, String record, String nameOfHand) {
        this.player1 = player1;
        this.player2 = player2;
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
        hand = new Hand();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> loadHands() {
        var records = new Object[34][8];
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
        hand.getAllCards().add(deck.getCards().get(Integer.parseInt(player1)));
        hand.getAllCards().add(deck.getCards().get(Integer.parseInt(player2)));
        hand.getAllCards().add(deck.getCards().get(Integer.parseInt(flop1)));
        hand.getAllCards().add(deck.getCards().get(Integer.parseInt(flop2)));
        hand.getAllCards().add(deck.getCards().get(Integer.parseInt(flop3)));
        hand.getAllCards().add(deck.getCards().get(Integer.parseInt(turn)));
        hand.getAllCards().add(deck.getCards().get(Integer.parseInt(river)));
        System.out.println(hand.getAllCards());

        hand.suitsAndValues();
        hand.checkHand();

        System.out.println(hand.getBestHand());
        System.out.println("Gained points: " + hand.getPoints() + ", expected: " + record);
        System.out.println("Checked hand: " + hand.getName() + ", expected: " + nameOfHand);
        Assert.assertEquals(hand.getPoints(), Integer.parseInt(record));
        hand.destroyHand();
    }
}
