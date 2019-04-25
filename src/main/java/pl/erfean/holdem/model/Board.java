package pl.erfean.holdem.model;

import lombok.Getter;
import lombok.Setter;
import pl.erfean.holdem.model.interfaces.BoardI;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
public class Board implements BoardI {

    private Deck deck;
    private List<Card> cards;
    private int pot, placesCount, playersCount, button, bigBlind, ante;
    private int maxBet; //biggest bet in a deal
    private int afterRaise; //number of players after last raise
    private int folds; //number of players who fold
    private int stacksNormal; //number of players on all-in
    private int stacksAnte; //number of players on ante all-in
    private List<Integer> pots;
    private List<Integer> stacks;
    private List<Player> players;

    @SuppressWarnings("unused")
    public Board() {
        this(9);
    }

    public Board(int placesCount) {
        this.placesCount = placesCount;
        deck = new Deck();
        button = -1;
        bigBlind = maxBet = 50;
        ante = 10;
        players = new ArrayList<>(placesCount);
        pots = new ArrayList<>();
        stacks = new ArrayList<>();
    }

    public Board(int placesCount, int playersCount, int button, int bigBlind, int ante, List<Player> players) {
        this.placesCount = placesCount;
        this.playersCount = playersCount;
        this.button = button;
        this.bigBlind = bigBlind;
        this.ante = ante;
        this.players = players;

        pots = new ArrayList<>();
        stacks = new ArrayList<>();
        deck = new Deck();
        pot = 0;
        afterRaise = 0;
        maxBet = bigBlind;
        stacksNormal = stacksAnte = folds = 0;
    }

    // Board with added cards
    public Board(int placesCount, int playersCount, int button, int bigBlind, int ante, List<Player> players, List<Card> cards) {
        this(placesCount, playersCount, button, bigBlind, ante, players);
        this.cards = cards;
    }

    /* ==== METHODS ==== */

    // Describing board situation

    public void addPot(int pot) { this.pot += pot; }
    public void resetAfterRaise() { afterRaise = 1; }
    public void increaseAfterRaise() { afterRaise++; }
    public void increaseFolds() { folds++; }
    public void increaseStacksNormal() { stacksNormal++; }
    // Counting number of stacks ante (without duplicates)
    public int countStacksAnte() {
        return (int)stacks.stream()
                .filter(i -> i <= ante)
                .count();
    }
    // Sorting stacks list
    public void sortStacks() {
        Collections.sort(stacks);
    }
    // Managing lists and values between rounds
    public void adjust() {
        System.out.println("adjust()");

        // <--- Sort and reduce list of stacks ---> //
        sortStacks();
        System.out.println("stacks.size(): " + stacks.size());
        if(stacks.size() > 1)
            for (int i = stacks.size() - 1; i > 0; i--)
                if (stacks.get(i).equals(stacks.get(i - 1))) {
                    stacks.remove(i);
                    System.out.println("remove");
                }
        displayStacks();

        // <--- Add side pots ---> //
        System.out.println("pots.size(): " + pots.size());
        for(int i = pots.size() - 1; i >= countStacksAnte(); i--)
            pots.remove(i);
        int sum = 0;
        if(stacks.size() == 0) {
            for(Player player : players) sum += player.getBet() + ante;
            pots.add(sum);
        }
        else {
            for(int i = stacksAnte; i < stacks.size(); i++) {
                System.out.println("i = " + i);
                int k = 0;

                if(i == 0) {
                    for(Player player : players)
                        if(player.getBet() <= stacks.get(i) - ante)
                            k += player.getBet() + ante;
                }
                else
                    for(Player player : players)
                        if(player.getBet() >= stacks.get(i) - ante)
                            k += stacks.get(i) - ante;
                k -= sum;
                pots.add(k);
                sum += k;
            }
        }
        displayPots();

        // <--- Set max bet 0 (new round) ---> //
        setMaxBet(0);

        // <--- If player goes all-in while having a bigger stack than other players on all-in ---> //
        // <--- Turn back overpot ---> //
        if(stacksNormal > 1 && folds + stacksNormal == playersCount)
            for(int i = 0; i < playersCount; i++)
                if(players.get(i).getChips() > stacks.get(stacks.size() - 1))
                    players.get(i).setBet(stacks.get(stacks.size() - 1));
    }

    // Board preparing

    public void drawCards(int round) {
        switch (round) {
            case 0:
                for(Player player : players) player.setCards(new Card[]{deck.drawCard(), deck.drawCard()});
                break;
            case 1:
                IntStream.range(0, 3).forEach(i -> cards.add(deck.drawCard()));
            case 2:
            case 3:
                cards.add(deck.drawCard());
        }
    }
    public void clearDeck() {
        try {
            cards.clear();
            pots.clear();
            stacks.clear();
        } catch (NullPointerException e) {
            System.out.println("Nothing to clear there");
        }
        pot = 0;
        for(Player player : players) player.setPlaying(true);
        stacksNormal = stacksAnte = folds = 0;
    }
    public void slideButton() {
        button = (button + 1) % playersCount;
    }
    public void takeAnte() {
        for(Player player : players) {
            if(player.getChips() <= ante) {
                pot += player.getChips();
                stacks.add(player.getChips());
                player.setChips(0);
                stacksAnte++;
            }
            else {
                player.subtractChips(ante);
                pot += ante;
            }
        }
        Collections.sort(stacks);
        for(int i = 0; i < stacks.size(); i++) {
            pots.add((playersCount - stacksNormal) * stacks.get(stacksNormal));
            stacksNormal++;
        }
    }
    public void takeBlinds() {
        if(playersCount == 2) {
            if(players.get(button).getChips() <= bigBlind / 2) {
                players.get(button).setBets(0, players.get(button).getChips());
                players.get((button + 1) % 2).setBets(0, players.get(button).getChips());
                pot += 2* players.get(button).getBets(0);
                stacks.add(ante + players.get(button).getChips());
                stacksNormal++;
            }
            else {
                if(players.get((button + 1) % 2).getChips() <= bigBlind) {
                    if(players.get((button + 1) % 2).getChips() <= bigBlind / 2) {
                        players.get((button + 1) % 2).setBets(0, players.get((button + 1) % 2).getChips());
                        players.get(button).setBets(0, players.get((button + 1) % 2).getChips());
                        pot += 2* players.get(button).getBets(0);
                        stacks.add(ante + players.get((button + 1) % 2).getChips());
                        stacksNormal++;
                    }
                    else {
                        players.get(button).setBets(0, bigBlind /2);
                        pot += bigBlind /2;
                        players.get((button + 1) % 2).setBets(0, players.get((button + 1) % 2).getChips());
                        pot += players.get((button + 1) % 2).getChips();
                        stacks.add(ante + players.get((button + 1) % 2).getChips());
                    }
                }
                else {
                    players.get(button).setBets(0, bigBlind /2);
                    players.get((button + 1) % 2).setBets(0, bigBlind);
                    pot += bigBlind + bigBlind /2;
                }
            }
        }
        else {
            if(players.get((button + 1) % playersCount).getChips() < bigBlind / 2) {
                players.get((button + 1) % playersCount).setBets(0, players.get((button + 1) % playersCount).getChips());
                pot += players.get((button + 1) % playersCount).getChips();
            }
            else {
                players.get((button + 1) % playersCount).setBets(0, bigBlind /2);
                pot += bigBlind / 2;
            }
            if(players.get((button + 2) % playersCount).getChips() < bigBlind) {
                players.get((button + 2) % playersCount).setBets(0, players.get((button + 2) % playersCount).getChips());
                pot += players.get((button + 2) % playersCount).getChips();
            }
            else {
                players.get((button + 2) % playersCount).setBets(0, bigBlind);
                pot += bigBlind;
            }
        }
        for(int i = 0; i < playersCount; i++)
            players.get(i).bet();
        maxBet = bigBlind;
    }

    // Displaying methods

    public void displayPlayers() {
        displayPlayers(false);
    }
    public void displayPlayers(boolean cards) {
        IntStream.range(0, playersCount).forEach(i -> System.out.printf("%s\t\t\t", players.get(i).getNickname()));
        System.out.println();
        IntStream.range(0, playersCount).map(i -> players.get(i).getChips() - players.get(i).getBet()).forEach(chips -> System.out.printf("%d\t\t\t", chips));
        System.out.println();
        if(cards) {
            IntStream.range(0, playersCount).forEach(i -> System.out.printf("%s%s\t\t\t", players.get(i).getCard(0), players.get(i).getCard(1)));
            System.out.println();
        }
    }
    public void displayPlayersAfterDistribution() {
        for(int i = 0; i < playersCount; i++) System.out.printf("%s\t\t\t", players.get(i).getNickname()); System.out.println();
        for(int i = 0; i < playersCount; i++) System.out.printf("%d\t\t\t", players.get(i).getChips());
        System.out.println();
    }
    public void displayStacks() {
        System.out.println("displayStacks()");
        for (int i = 0; i < stacks.size(); i++) System.out.printf("%d: %d\n", i, stacks.get(i));
        System.out.println("stacks.size(): " + stacks.size());
    }
    public void displayPots() {
        System.out.println("displayPots()");
        System.out.println("pots.size(): " + pots.size());
        for(int i = 0; i < pots.size(); i++) System.out.printf("%d: %d\n", i, pots.get(i));
    }

    // === ACTION TYPES === //

    public boolean checkOrRaise(Player player, int round) {
        return round == PREFLOP && player.getBets(PREFLOP) == maxBet;
    }

    public boolean checkOrBet(Player player, int round) {
        return round > PREFLOP && player.getBets(round) == 0;
    }

    public boolean foldOrCall(Player player, int round) {
        return player.getBets(round) < maxBet &&
                (afterRaise == playersCount - 1 &&
                        players.get((player.getSeat() + 1) % players.size()).getChips() - players.get((player.getSeat() + 1) % players.size()).getBet() == 0 ||
                        player.getChips() - player.addedToPot(round) + player.getBets(round) < maxBet);
    }

    public boolean allIn(Player player) {
        return player.getChips() == player.getBet();
    }

    public boolean lessThanBigBlindAbove(Player player, int round) {
        return player.getChips() - player.getBets() - maxBet < bigBlind;
    }

    public boolean canMove(Player player) {
        return player.isPlaying() && player.getChips() > player.getBet();
    }

    public void distributeFolds() {
        System.out.println("distributeFolds()");
        for(int i = 0; i < playersCount; i++) {
            players.get(i).subtractChips(players.get(i).getBet()) ;
            if(players.get(i).isPlaying()) {
                players.get(i).addChips(pot);
                System.out.println(pot + "   " + players.get(i).getNickname());
            }
        }
        displayPlayersAfterDistribution();
    }

    public void checkHands() {
        players.stream()
                .filter(Player::isPlaying)
                .forEach(player -> {
                    player.setHand(cards);
                    player.getHand().checkHand();
                });
    }

    public void checkHands(boolean display) {
        checkHands();
        if(display) for(int i = 0; i < playersCount; i++)
            System.out.println(players.get(i).getNickname() + " gains " + players.get(i).getHand().getPoints() + " points (hand: "
                    + players.get(i).getHand().getName() + ").");
    }

    // === distribute chips after a round with multiple pots === //
    public void distribute() {
        System.out.println("distribute()");
        for(int i = 0; i < playersCount; i++)
            players.get(i).subtractChips(players.get(i).getBet());
        int remains = stacks.size();
        for(int k = (pots.size() - 1); k >= 0; k--) {
            var bestPlayers = new ArrayList<Player>();
            int maxPoints = 0;
            if(remains == 0) {
                for(int i = 0; i < playersCount; i++) {
                    if(players.get(i).isPlaying()) {
                        System.out.println("players.get(" + i + ").getHand.getPoints(): " + players.get(i).getHand().getPoints());
                        maxPoints = checkRecord(bestPlayers, maxPoints, players.get(i));
                    }
                }
            }
            else {
                for(int i = 0; i < playersCount; i++) {
                    if(players.get(i).isPlaying() && players.get(i).getBet() >= stacks.get(remains - 1) - ante) {
                        System.out.println("players.get(" + i + ").getHand.getPoints(): " + players.get(i).getHand().getPoints());
                        maxPoints = checkRecord(bestPlayers, maxPoints, players.get(i));
                    }
                }
                remains--;
            }

            var stringPot = new StringBuilder();

            if(bestPlayers.size() == 1) {
                bestPlayers.get(0).addChips(pots.get(k));

                stringPot.append(bestPlayers.get(0).getNickname());
                if(pots.size() == 1) {
                    stringPot.append(" wins a pot of ");
                }
                else {
                    if(k == 0) {
                        stringPot.append(" wins main pot of ");
                    }
                    else {
                        stringPot.append(" wins side pot of ");
                    }
                }
                stringPot.append(pots.get(k))
                        .append(" with ")
                        .append(bestPlayers.get(0).getHand().getName())
                        .append(".");
            }
            else {
                if(pots.size() == 1) {
                    stringPot.append("Side pot number ")
                            .append(k)
                            .append(" of ");
                }
                else {
                    if(k == 0) {
                        stringPot.append("Main pot of ");
                    }
                    else {
                        stringPot.append("A pot of ");
                    }
                }
                stringPot.append(pots.get(k))
                        .append(" is won by: ");
                for(int i = 0; i < bestPlayers.size(); i++) {
                    bestPlayers.get(i).addChips(pots.get(k) / bestPlayers.size());

                    stringPot.append(players.get(i).getNickname());
                    if(i < bestPlayers.size() - 1)
                        stringPot.append(", ");
                    else
                        stringPot.append(" ");
                }
                stringPot.append(" with ")
                        .append(bestPlayers.get(0).getHand().getName())
                        .append(".");

                int t = bestPlayers.size() * (pots.get(k) / bestPlayers.size());
                int q = pots.get(k) - t;
                int i = 0;
                while(q > 0) {
                    bestPlayers.get(i).addChips();
                    i++;
                    q--;
                }
            }
            System.out.println("stringPot: " + stringPot);
        }
        displayPlayersAfterDistribution();
    }

    public int checkRecord(List<Player> bestPlayers, int maxPoints, Player player) {
        if(player.getHand().getPoints() > maxPoints) {
            bestPlayers.clear();
            maxPoints = player.getHand().getPoints();
            bestPlayers.add(player);
        }
        else if(player.getHand().getPoints() == maxPoints)
            bestPlayers.add(player);
        return maxPoints;
    }

    // <=== CALCULATION METHODS ===> //

    public double[][] calculate(int part) throws ArithmeticException {
        var wins = new int[players.size()];
        var splits = new double[players.size()];
        var chancesToWin = new double[players.size()];
        var chancesToSplit = new double[players.size()];
        int combinations = 1;

        // Summarizing wins and splits count for every player
        switch (part) {
            case PREFLOP:
                simulatePreflop(wins, splits);
                combinations = DRAW_COUNT;
                break;
            case FLOP:
                simulateFlop(wins, splits);
                combinations = deck.getCards().size()*(deck.getCards().size()-1) / 2;
                break;
            case TURN:
                simulateTurn(wins, splits);
                combinations = deck.getCards().size();
                break;
        }

        for(int player = 0; player < players.size(); player++) {
            chancesToWin[player] = (double)wins[player] / combinations;
            chancesToSplit[player] = splits[player] / combinations;
        }

        return new double[][]{chancesToWin, chancesToSplit};
    }

    public void simulatePreflop(int[] wins, double[] splits) {
        List<Card> remainingCards = deck.getCards();
        for(int draw = 0; draw < DRAW_COUNT; draw++) {
            Collections.shuffle(remainingCards);
            cards = remainingCards.stream().limit(5).collect(Collectors.toList());

            checkHands(wins, splits);
        }
    }
    public void simulateFlop(int[] wins, double[] splits) {
        IntStream.range(0, deck.getCards().size() - 1)
                .forEach(turnNumber -> {
                    cards.add(deck.getCards().get(turnNumber));
                    IntStream.range(turnNumber, deck.getCards().size())
                            .forEach(riverNumber -> {
                                cards.add(deck.getCards().get(riverNumber));
                                checkHands(wins, splits);
                                cards.remove(RIVER_CARD);
                            });
                    cards.remove(TURN_CARD);
                });
    }
    public void simulateTurn(int[] wins, double[] splits) {
        for(Card card : deck.getCards()) {
            cards.add(card);
            checkHands(wins, splits);
            cards.remove(RIVER_CARD);
        }
    }

    public void checkHands(int[] wins, double[] splits) {
        checkHands();
        int record = players.stream()
                .mapToInt(player -> player.getHand().getPoints())
                .max().getAsInt();
        var bestPlayers = players.stream()
                .filter(player -> (player.getHand().getPoints() == record))
                .collect(Collectors.toList());

        if(bestPlayers.size() == 1) wins[bestPlayers.get(0).getSeat()]++;
        else for (Player player : bestPlayers) splits[player.getSeat()] += 1.0/bestPlayers.size();
    }
}
