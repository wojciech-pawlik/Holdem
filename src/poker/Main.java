package poker;

import poker.classes.Board;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);

        Board board = new Board(3);

        board.addPlayers(scanner);
        board.deal(scanner);

        scanner.close();
    }
}
