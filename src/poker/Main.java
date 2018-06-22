package poker;

import poker.classes.Rozdanie;
import poker.classes.Stol;

public class Main {

    public static void main(String[] args) {
        Stol stol = new Stol(3);
        Rozdanie rozdanie = new Rozdanie(stol);
    }
}
