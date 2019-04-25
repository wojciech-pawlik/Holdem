package pl.erfean.holdem.console.game;

import pl.erfean.holdem.model.Board;
import pl.erfean.holdem.model.Player;

import java.util.Scanner;

import static pl.erfean.holdem.model.interfaces.BoardI.*;

public class ConsoleGame {
    public static final int PLACES_COUNT = 5;

    public static void main(String[] args) {
        var board = new Board(PLACES_COUNT);
        var scanner = new Scanner(System.in);
        addPlayers(board, scanner);
        deal(board, scanner);
    }

    public static void addPlayers(Board board, Scanner scanner) {
        System.out.print("How many players? ");
        board.setPlayersCount(scanner.nextInt());
        if(board.getPlayersCount() > board.getPlacesCount())
            board.setPlayersCount(board.getPlacesCount());
        else if(board.getPlayersCount() < 2)
            board.setPlayersCount(2);
        System.out.printf("Count of players: %d\n", board.getPlayersCount());

        for(int i = 0; i < board.getPlayersCount(); i++) {
            System.out.printf("Enter a nickname of player %d: ", i + 1);
            Player player = new Player((long) i, scanner.next(), 10000, "");
            player.setSeat(i);
            board.getPlayers().add(player);
        }
        board.displayPlayers();
    }

    // Run of the game
    public static void deal(Board board, Scanner scanner) {
        board.clearDeck();
        board.slideButton();
        board.getDeck().shuffle();
        board.takeAnte();
        board.displayPlayers(); //TEST
        board.takeBlinds();
        board.displayPlayers(); //TEST
        board.drawCards(PREFLOP);
        for(int i = 0; i < board.getPlayersCount(); i++)
            System.out.printf("%s%s\t\t\t", board.getPlayers().get(i).getCard(0).getName(), board.getPlayers().get(i).getCard(1).getName());
        System.out.println();
        if(board.getStacksNormal() >= board.getPlayersCount() - 1) {		//WITHOUT PREFLOP
            board.adjust();
            board.displayPlayers(true); //TEST
            board.drawCards(FLOP);
            board.drawCards(TURN);
            board.drawCards(RIVER);
        }
        else {												//PREFLOP
            preflop(board, scanner);
            board.adjust();
            if(board.getFolds() == board.getPlayersCount() - 1) {                //EVERYONE FOLD PREFLOP
                System.out.println("distributeFolds"); //TEST
                board.distributeFolds();
            }
            else if(board.getFolds() + board.getStacksNormal() == board.getPlayersCount() - 1) {	//SHOWDOWN (ALL-IN) PREFLOP
                board.drawCards(FLOP);
                board.drawCards(TURN);
                board.drawCards(RIVER);
                board.checkHands(true);
                board.distribute();
            }
            else {											//FLOP
                board.drawCards(FLOP);
                postflop(board, scanner, FLOP);
                board.adjust();
                if(board.getFolds() == board.getPlayersCount() - 1)			//EVERYONE FOLD FLOP
                    board.distributeFolds();
                else if(board.getFolds() + board.getStacksNormal() == board.getPlayersCount() - 1) {	//SHOWDOWN (ALL-IN) FLOP
                    board.drawCards(TURN);
                    board.drawCards(RIVER);
                    board.checkHands(true);
                    board.distribute();
                }
                else {										//TURN
                    board.drawCards(TURN);
                    postflop(board, scanner, TURN);
                    board.adjust();
                    if(board.getFolds() == board.getPlayersCount() - 1)		//EVERYONE FOLD ON TURN
                        board.distributeFolds();
                    else if(board.getFolds() + board.getStacksNormal() == board.getPlayersCount() - 1) {	//SHOWDOWN (ALL-IN) TURN
                        board.drawCards(RIVER);
                        board.checkHands(true);
                        board.distribute();
                    }
                    else {									//RIVER
                        board.drawCards(RIVER);
                        postflop(board, scanner, RIVER);
                        board.adjust();
                        if(board.getFolds() == board.getPlayersCount() - 1)	//EVERYONE FOLD ON RIVER
                            board.distributeFolds();
                        else {								//SHOWDOWN RIVER
                            board.checkHands(true);
                            board.distribute();
                        }
                    }
                }
            }
        }
    }

    private static void preflop(Board board, Scanner scanner) {
        System.out.println("preflop()");
        board.displayPlayers(true);
        System.out.println(board.getCards());
        board.calculate(PREFLOP);
        int turn; // which player is on action

        // === who begins === //
        turn = board.getPlayersCount() == 2 ? board.getButton() : (board.getButton() + 3) % board.getPlayersCount();

        // === GAME PREFLOP === //
        while(board.getAfterRaise() < board.getPlayersCount()) {
            if(board.getPlayers().get(turn).isPlaying() && board.getPlayers().get(turn).getChips() > board.getPlayers().get(turn).getBet()) {
                // === Everyone limped to player on BB === //
                if(board.getPlayers().get(turn).getBets(PREFLOP) == board.getMaxBet()) {
                    System.out.printf("Player %s: 1 - raise, 2 - check).\n", board.getPlayers().get(turn).getNickname());
                    String choice = scanner.next();
                    switch (choice) {
                        case "1":
                            System.out.print("Enter a bet size.");
                            int b;
                            if(board.getPlayers().get(turn).getChips() - board.getMaxBet() < board.getBigBlind())
                                b = board.getPlayers().get(turn).getChips() - board.getMaxBet();

                            else {
                                b = scanner.nextInt();
                                if(b > board.getPlayers().get(turn).getChips())
                                    b = board.getPlayers().get(turn).getChips();
                                else if(b < board.getMaxBet() + board.getBigBlind())
                                    b = board.getMaxBet() + board.getBigBlind();
                            }

                            System.out.println("Bet size: " + b);
                            board.addPot(b - board.getPlayers().get(turn).getBets(PREFLOP));
                            board.getPlayers().get(turn).addBets(PREFLOP, b);
                            board.setMaxBet(b);

                            board.resetAfterRaise();
                            break;

                        default:
                            board.increaseAfterRaise();
                            break;
                    }
                }

                // === CALL POSSIBILITY === //
                else if(board.getPlayers().get(turn).getBets(PREFLOP) < board.getMaxBet()) {
                    System.out.printf("Player %s: 1 - raise, 2 - call, 3 - fold).\n", board.getPlayers().get(turn).getNickname());
                    String choice = scanner.next();
                    switch (choice) {

                        // == RAISE == //
                        case "1":
                            int b;
                            // = If player has in his stack less than 1BB more than maxbet = //
                            if (board.getPlayers().get(turn).getBets(PREFLOP) + board.getPlayers().get(turn).getChips() - board.getMaxBet() < board.getBigBlind())
                                b = board.getPlayers().get(turn).getBets(PREFLOP) + board.getPlayers().get(turn).getChips() - board.getMaxBet();
                                // = NORMAL CASE = //
                            else {
                                System.out.println("Enter a bet size: ");
                                b = scanner.nextInt();
                                if (b < board.getMaxBet() + board.getBigBlind())
                                    b = board.getMaxBet() + board.getBigBlind();
                                else if (b > board.getPlayers().get(turn).getChips())
                                    b = board.getPlayers().get(turn).getChips();
                            }

                            System.out.println("Bet equals: " + b);
                            board.addPot(b - board.getPlayers().get(turn).getBets(PREFLOP));
                            board.getPlayers().get(turn).setBets(0, b);

                            board.resetAfterRaise();
                            board.setMaxBet(b);
                            break;

                        // == CALL == //
                        case "2":
                            if (board.getPlayers().get(turn).getChips() <= board.getMaxBet()) {
                                board.addPot(board.getPlayers().get(turn).getChips() - board.getPlayers().get(turn).getBets(PREFLOP));
                                board.getPlayers().get(turn).addBets(0, board.getPlayers().get(turn).getChips() - board.getPlayers().get(turn).getBet());
                            } else {
                                board.addPot(board.getMaxBet() - board.getPlayers().get(turn).getBets(PREFLOP));
                                board.getPlayers().get(turn).setBets(0, board.getMaxBet());
                            }
                            board.increaseAfterRaise();
                            break;

                        // == FOLD == //
                        default:
                            board.getPlayers().get(turn).setPlaying(false);
                            board.increaseFolds();
                            board.increaseAfterRaise();
                            break;
                    }
                }

                board.getPlayers().get(turn).bet();
                if(board.allIn(board.getPlayers().get(turn))) {
                    board.increaseStacksNormal();
                    board.getStacks().add(board.getPlayers().get(turn).getChips() + board.getAnte());
                }
            }
            turn = (turn + 1) % board.getPlayersCount();
        }
    }

    // <=== GAME AFTER FLOP ===> //
    private static void postflop(Board board, Scanner scanner, int part) {
        System.out.println("postflop() " + part);
        board.displayPlayers(true);
        board.calculate(part);
        System.out.println(board.getCards());
        int turn = (board.getButton() + 1) % board.getPlayersCount();
        board.setAfterRaise(1);
        while(board.getAfterRaise() < board.getPlayersCount()) {
            if(board.getPlayers().get(turn).isPlaying() && board.getPlayers().get(turn).getChips() > board.getPlayers().get(turn).getBet()) {
                if(board.getPlayers().get(turn).getBets(part) < board.getMaxBet()) {
                    System.out.printf("Player %s: 1 - raise, 2 - call, 3 - fold).\n", board.getPlayers().get(turn).getNickname());
                    int choice = scanner.nextInt();
                    switch (choice) {
                        case 1:
                            int b;
                            if(board.getPlayers().get(turn).getChips() - board.getPlayers().get(turn).addedToPot(part) <= 2 * board.getMaxBet())
                                b = board.getPlayers().get(turn).getChips() - board.getPlayers().get(turn).addedToPot(part);
                            else {
                                b = scanner.nextInt();
                                if(b < 2 * board.getMaxBet()) b = 2 * board.getMaxBet();
                                else if(b > board.getPlayers().get(turn).getChips() - board.getPlayers().get(turn).addedToPot(part))
                                    b = board.getPlayers().get(turn).getChips() - board.getPlayers().get(turn).addedToPot(part);
                            }

                            board.addPot(b - board.getPlayers().get(turn).getBets(part));
                            board.getPlayers().get(turn).setBets(part, b);
                            System.out.println("Bet " + b);

                            board.resetAfterRaise();
                            board.setMaxBet(b);
                            break;
                        case 2:
                            // = ALL-IN = //
                            if(board.getPlayers().get(turn).getChips() - board.getPlayers().get(turn).getBet() < board.getMaxBet()) {
                                board.addPot(board.getMaxBet() - board.getPlayers().get(turn).getBets(part));
                                board.getPlayers().get(turn).addBets(part, board.getPlayers().get(turn).getChips() - board.getPlayers().get(turn).getBet());
                                System.out.println("Bet " + board.getPlayers().get(turn).getBets(part));
                            }
                            // = COLD CALL = //
                            else {
                                board.addPot(board.getMaxBet() - board.getPlayers().get(turn).getBets(part));
                                board.getPlayers().get(turn).setBets(part, board.getMaxBet());
                            }
                            board.increaseAfterRaise();
                            break;
                        default:
                            board.getPlayers().get(turn).setPlaying(false);
                            board.increaseFolds();
                            board.increaseAfterRaise();
                            break;
                    }
                }

                else if(board.getPlayers().get(turn).getBets(part) == 0) {
                    System.out.printf("Player %s: 1 - bet, 2 - check).\n", board.getPlayers().get(turn).getNickname());
                    int choice = scanner.nextInt();
                    switch (choice) {
                        case 1:
                            int b;
                            if(board.getPlayers().get(turn).getChips() - board.getPlayers().get(turn).addedToPot(part) <= board.getBigBlind())
                                b = board.getPlayers().get(turn).getChips() - board.getPlayers().get(turn).addedToPot(part);

                            else {
                                System.out.print("Enter a bet size: ");
                                b = scanner.nextInt();
                                if(b < board.getBigBlind()) b = board.getBigBlind();
                                else if(b > board.getPlayers().get(turn).getChips() - board.getPlayers().get(turn).addedToPot(part))
                                    b = board.getPlayers().get(turn).getChips() - board.getPlayers().get(turn).addedToPot(part);
                            }

                            System.out.println("Bet size: " + b);
                            board.addPot(b);
                            board.getPlayers().get(turn).setBets(part, b);

                            board.setMaxBet(board.getPlayers().get(turn).getBets(part));
                            board.resetAfterRaise();

                            break;
                        default:
                            board.increaseAfterRaise();
                            break;
                    }
                }

                board.getPlayers().get(turn).bet();
                if(board.getPlayers().get(turn).getChips() == board.getPlayers().get(turn).getBet()) {
                    board.increaseStacksNormal();
                    board.getStacks().add(board.getPlayers().get(turn).getChips() + board.getAnte());
                }
            }
            else
                board.increaseAfterRaise();

            turn = (turn + 1) % board.getPlayersCount();
        }
    }
}
