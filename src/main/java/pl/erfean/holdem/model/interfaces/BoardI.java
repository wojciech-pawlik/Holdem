package pl.erfean.holdem.model.interfaces;

import pl.erfean.holdem.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public interface BoardI {
    // Rounds
    int PREFLOP = 0;
    int FLOP = 1;
    int TURN = 2;
    int RIVER = 3;
    // Board cards
    int FLOP_CARD1 = 0;
    int FLOP_CARD2 = 1;
    int FLOP_CARD3 = 2;
    int TURN_CARD = 3;
    int RIVER_CARD = 4;
    // Number of drawings for counting probability method (preflop case)
    int DRAW_COUNT = 100000;

    // Describing board situation
    void addPot(int pot);
    void resetAfterRaise();
    void increaseAfterRaise();
    void increaseFolds();
    void increaseStacksNormal();
    int countStacksAnte(); // Counting number of stacks ante (without duplicates)
    void sortStacks(); // Sorting stacks list
    void adjust(); // Managing lists and values between rounds
    // Board preparing
    void drawCards(int round);
    void clearDeck();
    void slideButton();
    void takeAnte();
    void takeBlinds();
    // Displaying on console
    void displayPlayers();
    void displayPlayersAfterDistribution();
    void displayStacks();
    void displayPots();
    // Summarizing the game
    void checkHands();
    void distribute();
    int checkRecord(List<Player> bestPlayers, int maxPoints, Player player);
    // Calculating chances
    double[][] calculate(int part); // Chances to win and to split for every player
    void simulatePreflop(int[] wins, double[] splits); // Simulation at preflop
    void simulateFlop(int[] wins, double[] splits); // Simulation at flop
    void simulateTurn(int[] wins, double[] splits); // Simulation at turn
    void checkHands(int[] wins, double[] splits);
}
