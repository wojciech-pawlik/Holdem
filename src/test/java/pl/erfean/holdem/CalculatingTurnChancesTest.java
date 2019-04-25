package pl.erfean.holdem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;
import pl.erfean.holdem.model.Board;
import pl.erfean.holdem.model.Card;
import pl.erfean.holdem.model.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static pl.erfean.holdem.CalculatingPreflopChancesTest.displayCalculatingTestInfo;
import static pl.erfean.holdem.model.Board.TURN;

public class CalculatingTurnChancesTest {
    private Board board;
    private Player player1, player2, player3;

    private String player1Card1, player1Card2, player2Card1, player2Card2, player3Card1, player3Card2,
            flop1, flop2, flop3, turn,
            player1ChancesToWin, player1ChancesToSplit, player2ChancesToWin, player2ChancesToSplit, player3ChancesToWin, player3ChancesToSplit;

    private static final String csvFile = "src/test/boards0.csv";
    private static final String path = new File("").getAbsolutePath();
    private static BufferedReader bufferedReader = null;
    private static final String csvSplitBy = ",";

    private static final int BOARDS_COUNT = 20;
    private static final int ARRAY_SIZE = 16;
    private static final double DELTA = 0.03;

    public CalculatingTurnChancesTest(String player1Card1, String player1Card2,
                                      String player2Card1, String player2Card2,
                                      String player3Card1, String player3Card2,
                                      String flop1, String flop2, String flop3, String turn,
                                      String player1ChancesToWin, String player2ChancesToWin, String player3ChancesToWin,
                                      String player1ChancesToSplit, String player2ChancesToSplit, String player3ChancesToSplit) {
        this.player1Card1 = player1Card1;
        this.player1Card2 = player1Card2;
        this.player2Card1 = player2Card1;
        this.player2Card2 = player2Card2;
        this.player3Card1 = player3Card1;
        this.player3Card2 = player3Card2;
        this.flop1 = flop1;
        this.flop2 = flop2;
        this.flop3 = flop3;
        this.turn = turn;
        this.player1ChancesToWin = player1ChancesToWin;
        this.player1ChancesToSplit = player1ChancesToSplit;
        this.player2ChancesToWin = player2ChancesToWin;
        this.player2ChancesToSplit = player2ChancesToSplit;
        this.player3ChancesToWin = player3ChancesToWin;
        this.player3ChancesToSplit = player3ChancesToSplit;
    }

    @Before
    public void setup() {
        board = new Board(3);
        player1 = new Player(0L, "player1", 10000, "");
        player2 = new Player(1L, "player2", 10000, "");
        player3 = new Player(2L, "player3", 10000, "");

        player1.setSeat(0);
        player2.setSeat(1);
        player3.setSeat(2);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> loadBoards() {
        var records = new Object[BOARDS_COUNT][ARRAY_SIZE];
        try {
            bufferedReader = new BufferedReader(new FileReader(path + "\\" + csvFile));
            bufferedReader.readLine();
            int numberOfRecord = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
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
    public void calculatePreflopChances() {
        // Setting up players' cards
        player1.setCards(new Card[]{board.getDeck().drawCard(Integer.parseInt(player1Card1)), board.getDeck().drawCard(Integer.parseInt(player1Card2))});
        player2.setCards(new Card[]{board.getDeck().drawCard(Integer.parseInt(player2Card1)), board.getDeck().drawCard(Integer.parseInt(player2Card2))});
        player3.setCards(new Card[]{board.getDeck().drawCard(Integer.parseInt(player3Card1)), board.getDeck().drawCard(Integer.parseInt(player3Card2))});

        // Setting players to the board
        board.clearDeck();
        board.setPlayers(new ArrayList<>(Arrays.asList(player1, player2, player3)));
        board.setPlayersCount(3);

        //Setting tables cards
        //Setting tables cards
        board.setCards(Arrays.asList(
                board.getDeck().drawCard(Integer.parseInt(flop1)),
                board.getDeck().drawCard(Integer.parseInt(flop2)),
                board.getDeck().drawCard(Integer.parseInt(flop3)),
                board.getDeck().drawCard(Integer.parseInt(turn))
        ));

        // Display'ing test info
        displayCalculatingTestInfo();

        // Calculate chances (flop mode)
        var chances = board.calculate(TURN);
        var chancesToWin = chances[0];
        var chancesToSplit = chances[1];
        String[] actualChancesAsString = {player1ChancesToWin, player2ChancesToWin, player3ChancesToWin,
                player1ChancesToSplit, player2ChancesToSplit, player3ChancesToSplit};
        double[] actualChances = Arrays.stream(actualChancesAsString).mapToDouble(Double::parseDouble).toArray();
        System.out.println("[" + actualChances[0] + ", " + actualChances[1] + ", " + actualChances[2] + ", "
                + actualChances[3] + ", " + actualChances[4] + ", " + actualChances[5] + "]");

        Assert.assertEquals(chancesToWin[0], actualChances[0], DELTA);
        Assert.assertEquals(chancesToWin[1], actualChances[1], DELTA);
        Assert.assertEquals(chancesToWin[2], actualChances[2], DELTA);
        Assert.assertEquals(chancesToSplit[0], actualChances[3], DELTA);
        Assert.assertEquals(chancesToSplit[1], actualChances[4], DELTA);
        Assert.assertEquals(chancesToSplit[2], actualChances[5], DELTA);
    }
}
