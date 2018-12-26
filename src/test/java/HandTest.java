import classes.Deck;
import classes.Hand;
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
    private static String[] array = new String[9];

    private String player1, player2, flop1, flop2, flop3, turn, river, record, nameOfHand;

    private static String csvFile = "src/test/hands.csv";
    private static String path = new File("").getAbsolutePath();
    private static BufferedReader bufferedReader = null;
    private static String line = "";
    private static String csvSplitBy = ",";

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
            while((line = bufferedReader.readLine()) != null) {
                array = line.split(csvSplitBy);
                records[numberOfRecord++] = array;
            }
            System.out.println();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        System.out.println(records);
        return Arrays.asList(records);
    }


    @Test
    public void checkHands() throws Exception {
        hand.getAllCards().put(0, deck.getCards().get(Integer.parseInt(player1)));
        hand.getAllCards().put(1, deck.getCards().get(Integer.parseInt(player2)));
        hand.getAllCards().put(2, deck.getCards().get(Integer.parseInt(flop1)));
        hand.getAllCards().put(3, deck.getCards().get(Integer.parseInt(flop2)));
        hand.getAllCards().put(4, deck.getCards().get(Integer.parseInt(flop3)));
        hand.getAllCards().put(5, deck.getCards().get(Integer.parseInt(turn)));
        hand.getAllCards().put(6, deck.getCards().get(Integer.parseInt(river)));

        hand.suitsAndValues();
        hand.checkHand();

        System.out.println(hand.getAllCards());
        System.out.println(hand.getBestHand());
        System.out.println("Gained points: " + hand.getPoints() + ", expected: " + record);
        System.out.println("Checked hand: " + hand.getName() + ", expected: " + nameOfHand);
        Assert.assertEquals(hand.getPoints(), Integer.parseInt(record));
        hand.destroyHand();
    }
}
