package poker;

import poker.classes.Stol;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);

        Stol stol = new Stol(3);

        stol.DodajGraczy(scanner);
        stol.Rozdaj(scanner);

        scanner.close();
    }
}
