import classes.Board;

import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);

        var board = new Board(9);

        board.addPlayers(scanner);
        board.deal(scanner);

        scanner.close();
    }
}
