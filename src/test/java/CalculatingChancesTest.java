import org.junit.Before;
import org.junit.Test;
import poker.Board;
import poker.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class CalculatingChancesTest {
    private Board board;
    private Player player1, player2, player3 = new Player(3, "player4", 10000, "");
    
    @Before
    public void setup() {
        board = new Board(3);
        player1 = new Player(0, "player1", 10000, "");
        player2 = new Player(1, "player2", 10000, "");
        player3 = new Player(2, "player3", 10000, "");
        board.setPlayers((ArrayList<Player>)Arrays.asList(player1, player2, player3));
    }
}
