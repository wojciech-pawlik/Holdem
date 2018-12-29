package poker;

import java.util.*;
import java.util.stream.IntStream;

public class Board {
    private static final int PREFLOP = 0;
    private static final int FLOP = 1;
    private static final int TURN = 2;
    private static final int RIVER = 3;

    private static final int DRAW_COUNT = 10000; //COUNTING PROBABILITY

    private Deck deck;
    private Card flop1, flop2, flop3, turn, river;
    private int pot, playersCount, placesCount, button, bigBlind, ante;
    private int maxBet; //biggest bet in a deal
    private int afterRaise; //number of players after last raise
    private int folds; //number of players who fold
    private int stacksNormal; //number of players on all-in
    private int stacksAnte; //number of players on ante all-in
    private ArrayList<Integer> pots;
    private ArrayList<Integer> stacks;
    private ArrayList<Player> players;
    private Random random;

    @SuppressWarnings("unused")
    public Board() {
        this(9);
    }

    public Board(int placesCount) {
        pots = new ArrayList<>();
        stacks = new ArrayList<>();
        players = new ArrayList<>();
        deck = new Deck();
        flop1 = flop2 = flop3 = turn = river = new Card();
        pot = 0;
        this.placesCount = placesCount;
        playersCount = 0;
        button = -1;
        afterRaise = 0;
        bigBlind = maxBet = 50;
        ante = 10;
        stacksNormal = stacksAnte = folds = 0;
        random = new Random();
    }

    /* ==== GETTERS AND SETTERS ==== */

    //CARDS//

    Card getFlop1() {
        return flop1;
    }

    Card getFlop2() {
        return flop2;
    }

    Card getFlop3() {
        return flop3;
    }

    Card getTurn() {
        return turn;
    }

    Card getRiver() {
        return river;
    }

    public void setFlop1(Card flop1) {
        this.flop1 = flop1;
    }

    public void setFlop2(Card flop2) {
        this.flop2 = flop2;
    }

    public void setFlop3(Card flop3) {
        this.flop3 = flop3;
    }

    public void setTurn(Card turn) {
        this.turn = turn;
    }

    public void setRiver(Card river) {
        this.river = river;
    }

    // GETTERS, SETTERS, etc.//

    private void addPot(int pot) {
        this.pot += pot;
    }

    private void setPlayersCount(int playersCount) {
        this.playersCount = playersCount;
    }

    @SuppressWarnings("unused")
    private void setPlacesCount(int placesCount) {
        this.placesCount = placesCount;
    }

    @SuppressWarnings("unused")
    public void setButton(int button) {
        this.button = button;
    }

    @SuppressWarnings("unused")
    private void setBigBlind(int bigBlind) {
        this.bigBlind = bigBlind;
    }

    @SuppressWarnings("unused")
    private void setAnte(int ante) {
        this.ante = ante;
    }

    private void setMaxBet(int maxBet) {
        this.maxBet = maxBet;
    }

    private void resetNrRaise() {
        this.afterRaise = 1;
    }

    // == stacks ante without duplicates == //
    private int countStacksAnte() {
        int count = 0;
        for(Integer i : stacks)
            if(i <= ante) count++;
        return count;
    }

    /* ==== SORT ==== */

    private void sortStacks() {
        Collections.sort(stacks);
    }

    /* ==== METHODS ==== */

    private void drawCards() {
        for(int i = 0; i < playersCount; i++) {
            players.get(i).setCard1(deck.drawCard(random));
            players.get(i).setCard2(deck.drawCard(random));
        }
    }

    private void drawFlop() {
        flop1 = deck.drawCard(random);
        flop2 = deck.drawCard(random);
        flop3 = deck.drawCard(random);
        System.out.printf("%s %s %s\n", flop1.getName(), flop2.getName(), flop3.getName());
    }

    private void drawFlop(int f1, int f2, int f3) {
        flop1 = deck.drawCard(f1);
        flop2 = deck.drawCard(f2);
        flop3 = deck.drawCard(f3);
    }

    private void drawTurn()
    {
        turn = deck.drawCard(random);
        System.out.printf("%s %s %s\t%s\n", flop1.getName(), flop2.getName(), flop3.getName(), turn.getName());
    }

    private void drawTurn(int t) {
        turn = deck.drawCard(t);
    }

    private void drawRiver()
    {
        river = deck.drawCard(random);
        System.out.printf("%s %s %s\t%s\t%s\n", flop1.getName(), flop2.getName(), flop3.getName(), turn.getName(), river.getName());
    }

    private void drawRiver(int r) {
        river = deck.drawCard(r);
    }

    private void clearDeck()
    {
        flop1 = flop2 = flop3 = turn = river = new Card();
        pot = 0;
        for(int i = 0; i < playersCount; i++) players.get(i).setPlaying(true);
        pots.clear();
        stacks.clear();
        stacksNormal = stacksAnte = folds = 0;
    }

    private void slideButton() {
        button = (button + 1) % playersCount;
    }

    private void takeAnte() {
        for(int i = 0; i < playersCount; i++) {
            if(players.get(i).getChips() <= ante) {
                pot += players.get(i).getChips();
                stacks.add(players.get(i).getChips());
                players.get(i).setChips();
                stacksAnte++;
            }
            else {
                players.get(i).subtractChips(ante);
                pot += ante;
            }
        }
        Collections.sort(stacks);
        for(int i = 0; i < stacks.size(); i++) {
            pots.add((playersCount - stacksNormal) * stacks.get(stacksNormal));
            stacksNormal++;
        }
    }

    private void takeBlinds() {
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

    public void addPlayers(Scanner scanner) {
        System.out.print("How many players? ");
        setPlayersCount(scanner.nextInt());
        if(playersCount > placesCount)
            setPlayersCount(placesCount);
        else if(playersCount < 2)
            setPlayersCount(2);
        System.out.printf("Count of players: %d\n", playersCount);

        for(int i = 0; i < playersCount; i++) {
            System.out.printf("Enter a nickname of player %d: ", i + 1);
            Player player = new Player(i, scanner.next(), 10000);
            player.setPlace(i);
            players.add(player);
        }
        displayPlayers();
    }

    private void displayPlayers() {
        displayPlayers(false);
    }

    private void displayPlayers(boolean cards) {
        IntStream.range(0, playersCount).forEach(i -> System.out.printf("%s\t\t\t", players.get(i).getNickname()));
        System.out.println();
        IntStream.range(0, playersCount).map(i -> players.get(i).getChips() - players.get(i).getBet()).forEach(chips -> System.out.printf("%d\t\t\t", chips));
        System.out.println();
        if(cards) {
            IntStream.range(0, playersCount).forEach(i -> System.out.printf("%s%s\t\t\t", players.get(i).getCard1().getName(), players.get(i).getCard2().getName()));
            System.out.println();
        }
    }

    private void displayPlayersAfterDistribution() {
        for(int i = 0; i < playersCount; i++) System.out.printf("%s\t\t\t", players.get(i).getNickname()); System.out.println();
        for(int i = 0; i < playersCount; i++) System.out.printf("%d\t\t\t", players.get(i).getChips());
        System.out.println();
    }

    private void displayStacks() {
        System.out.println("displayStacks()");
        for (int i = 0; i < stacks.size(); i++) System.out.printf("%d: %d\n", i, stacks.get(i));
        System.out.println("stacks.size(): " + stacks.size());
    }

    private void displayPots() {
        System.out.println("displayPots()");
        System.out.println("pots.size(): " + pots.size());
        for(int i = 0; i < pots.size(); i++) System.out.printf("%d: %d\n", i, pots.get(i));
    }

    public void deal(Scanner scanner) {
        clearDeck();
        slideButton();
        deck.shuffle();
        takeAnte();
        displayPlayers(); //TEST
        takeBlinds();
        displayPlayers(); //TEST
        drawCards();
        for(int i = 0; i < playersCount; i++)
            System.out.printf("%s%s\t\t\t", players.get(i).getCard1().getName(), players.get(i).getCard2().getName());
        System.out.println();
        if(stacksNormal >= playersCount - 1) {		//WITHOUT PREFLOP
            adjust();
            displayPlayers(true); //TEST
            drawFlop();
            drawTurn();
            drawRiver();
        }
        else {												//PREFLOP
            preflop(scanner);
            adjust();
            if(folds == playersCount - 1) {                //EVERYONE FOLD PREFLOP
                System.out.println("distributeFolds"); //TEST
                distributeFolds();
            }
            else if(folds + stacksNormal == playersCount - 1) {	//SHOWDOWN (ALL-IN) PREFLOP
                drawFlop();
                drawTurn();
                drawRiver();
                System.out.printf("%s %s %s\t%s\t%s\n", flop1.getName(), flop2.getName(), flop3.getName(), turn.getName(),
                        river.getName()); //TEST
                checkHands(true);
                distribute();
            }
            else {											//FLOP
                drawFlop();
                postflop(scanner, FLOP);
                adjust();
                if(folds == playersCount - 1)			//EVERYONE FOLD FLOP
                    distributeFolds();
                else if(folds + stacksNormal == playersCount - 1) {	//SHOWDOWN (ALL-IN) FLOP
                    drawTurn();
                    drawRiver();
                    checkHands(true);
                    distribute();
                }
                else {										//TURN
                    drawTurn();
                    postflop(scanner, TURN);
                    adjust();
                    if(folds == playersCount - 1)		//EVERYONE FOLD ON TURN
                        distributeFolds();
                    else if(folds + stacksNormal == playersCount - 1) {	//SHOWDOWN (ALL-IN) TURN
                        drawRiver();
                        checkHands(true);
                        distribute();
                    }
                    else {									//RIVER
                        drawRiver();
                        postflop(scanner, RIVER);
                        adjust();
                        if(folds == playersCount - 1)	//EVERYONE FOLD ON RIVER
                            distributeFolds();
                        else {								//SHOWDOWN RIVER
                            checkHands(true);
                            distribute();
                        }
                    }
                }
            }
        }
    }

    private void adjust() {
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

    private void preflop(Scanner scanner) {
        System.out.println("preflop()");
        displayPlayers(true);
        calculate(PREFLOP);
        int turn; // which player is on action
        afterRaise = 1;

        // === who begins === //
        turn = playersCount == 2 ? button : (button + 3) % playersCount;

        // === GAME PREFLOP === //
        while(afterRaise < playersCount) {
            if(players.get(turn).isPlaying() && players.get(turn).getChips() > players.get(turn).getBet()) {
                // === Everyone limped to player on BB === //
                if(players.get(turn).getBets(PREFLOP) == maxBet) {
                    System.out.printf("Player %s: 1 - raise, 2 - check).\n", players.get(turn).getNickname());
                    String choice = scanner.next();
                    switch (choice) {
                        case "1":
                            System.out.print("Enter a bet size.");
                            int b;
                            if(players.get(turn).getChips() - maxBet < bigBlind)
                                b = players.get(turn).getChips() - maxBet;

                            else {
                                b = scanner.nextInt();
                                if(b < maxBet + bigBlind)
                                    b = maxBet + bigBlind;
                                else if(b > players.get(turn).getChips())
                                    b = players.get(turn).getChips();
                            }

                            System.out.println("Bet size: " + b);
                            addPot(b - players.get(turn).getBets(PREFLOP));
                            players.get(turn).addBets(PREFLOP, b);
                            setMaxBet(b);

                            resetNrRaise();
                            break;

                        default:
                            afterRaise++;
                            break;
                    }
                }

                // === RAISE POSSIBILITY === //
                else if(players.get(turn).getBets(PREFLOP) < maxBet) {
                    System.out.printf("Player %s: 1 - raise, 2 - call, 3 - fold).\n", players.get(turn).getNickname());
                    String choice = scanner.next();
                    switch (choice) {

                        // == RAISE == //
                        case "1":
                            int b;
                            // = If player has in his stack less than 1BB more than maxbet = //
                            if (players.get(turn).getBets(PREFLOP) + players.get(turn).getChips() - maxBet < bigBlind)
                                b = players.get(turn).getBets(PREFLOP) + players.get(turn).getChips() - maxBet;
                                // = NORMAL CASE = //
                            else {
                                System.out.println("Enter a bet size: ");
                                b = scanner.nextInt();
                                if (b < maxBet + bigBlind)
                                    b = maxBet + bigBlind;
                                else if (b > players.get(turn).getChips())
                                    b = players.get(turn).getChips();
                            }

                            System.out.println("Bet equals: " + b);
                            addPot(b - players.get(turn).getBets(PREFLOP));
                            players.get(turn).setBets(0, b);

                            resetNrRaise();
                            setMaxBet(b);
                            break;

                        // == CALL == //
                        case "2":
                            if (players.get(turn).getChips() <= maxBet) {
                                addPot(players.get(turn).getChips() - players.get(turn).getBets(PREFLOP));
                                players.get(turn).addBets(0, players.get(turn).getChips() - players.get(turn).getBet());
                            } else {
                                addPot(maxBet - players.get(turn).getBets(PREFLOP));
                                players.get(turn).setBets(0, maxBet);
                            }
                            afterRaise++;
                            break;

                        // == FOLD == //
                        default:
                            players.get(turn).setPlaying(false);
                            folds++;
                            afterRaise++;
                            break;
                    }
                }

                players.get(turn).bet();
                if(players.get(turn).getChips() == players.get(turn).getBet()) {
                    stacksNormal++;
                    stacks.add(players.get(turn).getChips() + ante);
                }
            }
            turn = (turn + 1) % playersCount;
        }
    }

    // <=== GAME AFTER FLOP ===> //
    private void postflop(Scanner scanner, int part) {
        System.out.println("postflop() " + part);
        displayPlayers(true);
        calculate(part);
        int turn = (button + 1) % playersCount;
        afterRaise = 1;
        while(afterRaise < playersCount) {
            if(players.get(turn).isPlaying() && players.get(turn).getChips() > players.get(turn).getBet()) {
                if(players.get(turn).getBets(part) < maxBet) {
                    System.out.printf("Player %s: 1 - raise, 2 - call, 3 - fold).\n", players.get(turn).getNickname());
                    int choice = scanner.nextInt();
                    switch (choice) {
                        case 1:
                            int b;
                            if(players.get(turn).getChips() - players.get(turn).addedToPot(part) <= 2 * maxBet)
                                b = players.get(turn).getChips() - players.get(turn).addedToPot(part);
                            else {
                                b = scanner.nextInt();
                                if(b < 2 * maxBet) b = 2 * maxBet;
                                else if(b > players.get(turn).getChips() - players.get(turn).addedToPot(part))
                                    b = players.get(turn).getChips() - players.get(turn).addedToPot(part);
                            }

                            addPot(b - players.get(turn).getBets(part));
                            players.get(turn).setBets(part, b);
                            System.out.println("Bet " + b);

                            resetNrRaise();
                            setMaxBet(b);
                            break;
                        case 2:
                            // = ALL-IN = //
                            if(players.get(turn).getChips() - players.get(turn).getBet() < maxBet) {
                                addPot(maxBet - players.get(turn).getBets(part));
                                players.get(turn).addBets(part, players.get(turn).getChips() - players.get(turn).getBet());
                                System.out.println("Bet " + players.get(turn).getBets(part));
                            }
                            // = COLD CALL = //
                            else {
                                addPot(maxBet - players.get(turn).getBets(part));
                                players.get(turn).setBets(part, maxBet);
                            }
                            afterRaise++;
                            break;
                        default:
                            players.get(turn).setPlaying(false);
                            folds++;
                            afterRaise++;
                            break;
                    }
                }

                else if(players.get(turn).getBets(part) == 0) {
                    System.out.printf("Player %s: 1 - bet, 2 - check).\n", players.get(turn).getNickname());
                    int choice = scanner.nextInt();
                    switch (choice) {
                        case 1:
                            int b;
                            if(players.get(turn).getChips() - players.get(turn).addedToPot(part) <= bigBlind)
                                b = players.get(turn).getChips() - players.get(turn).addedToPot(part);

                            else {
                                System.out.print("Enter a bet size: ");
                                b = scanner.nextInt();
                                if(b < bigBlind) b = bigBlind;
                                else if(b > players.get(turn).getChips() - players.get(turn).addedToPot(part))
                                    b = players.get(turn).getChips() - players.get(turn).addedToPot(part);
                            }

                            System.out.println("Bet size: " + b);
                            addPot(b);
                            players.get(turn).setBets(part, b);

                            setMaxBet(players.get(turn).getBets(part));
                            resetNrRaise();

                            break;
                        default:
                            afterRaise++;
                            break;
                    }
                }

                players.get(turn).bet();
                if(players.get(turn).getChips() == players.get(turn).getBet()) {
                    stacksNormal++;
                    stacks.add(players.get(turn).getChips() + ante);
                }
            }
            else
                afterRaise++;

            turn = (turn + 1) % playersCount;
        }
    }

    private void distributeFolds() {
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

    private void checkHands() {
        checkHands(false);
    }

    private void checkHands(boolean display) {
//        System.out.println("checkHands()");
        for(int i = 0; i < playersCount; i++) {
            if(players.get(i).isPlaying()) {
                players.get(i).setHand(this);
                players.get(i).getHand().checkHand();
                if(display) {
                    System.out.println(players.get(i).getNickname() + " gains " + players.get(i).getHand().getPoints() + " points (hand: "
                            + players.get(i).getHand().getName() + ").");
                }
            }
        }
    }

    // === distribute chips after a round with multiple pots === //
    private void distribute() {
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

    private int checkRecord(ArrayList<Player> bestPlayers, int maxPoints, Player player) {
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

    private void calculate(int part) {
//        System.out.println("calculate()");
        var wins = new float[players.size()];
        var chances = new float[players.size()];
        int sum = 0;
        for(float k : wins) k = 0;
        System.out.println(Arrays.toString(wins));

        if(part == PREFLOP) {
            calcFlop(wins);
        }
        else if(part == FLOP) {
            calcTurn(wins);
        }
        else if(part == TURN) {
            calcRiver(wins);
        }
        else if(part == RIVER) {
            checkHands(wins);
        }

        for(float win : wins) sum += win;

        for(int player = 0; player < players.size(); player++) {
            try {
                chances[player] = wins[player] / sum;
            } catch(ArithmeticException e) {
                System.out.println(e.getMessage());
            }

        }

        System.out.println(Arrays.toString(chances));
    }

    private void calcFlop(float[] wins) {
        int count = DRAW_COUNT;
        do {
            try {
                var result = drawFive();
                setFlop1(deck.getCards().get(result[0]));
                setFlop2(deck.getCards().get(result[1]));
                setFlop3(deck.getCards().get(result[2]));
                setTurn(deck.getCards().get(result[3]));
                setRiver(deck.getCards().get(result[4]));
                checkHands(wins);
                count--;
            } catch(NullPointerException e) {
                e.getMessage();
            }
        } while(count > 0);
    }

    private int[] drawFive() {
        var result = new int[5];
        for(int card = 0; card < 5; card++)
            result[card] = random.nextInt(52);

        return result;
    }

    private void calcTurn(float[] wins) {
        for(int t = 0; t < deck.getCards().size(); t++)
            if(!deck.getCards().get(t).isUsed()) {
                turn = deck.drawCard(t);
                calcRiver(wins);

                deck.getCards().get(t).setUsed(false);
            }
    }

    private void calcRiver(float[] wins) {
        for(int r = 0; r < deck.getCards().size(); r++)
            if(!deck.getCards().get(r).isUsed()) {
                river = deck.drawCard(r);
                checkHands(wins);

                deck.getCards().get(r).setUsed(false);
            }
    }

    private void checkHands(float[] wins) {
        var points = new ArrayList<Integer>();
        int winnersCount = 0;
        checkHands();
        for(Player player : players) {
            if(player.isPlaying())
                points.add(player.getHand().getPoints());
            else points.add(0);
        }
        for(int count : points)
            if(count == Collections.max(points))
                winnersCount++;
        for(int player = 0; player < points.size(); player++)
            if(points.get(player).equals(Collections.max(points))) {
                try {
                    wins[player] += (1 / winnersCount);
                } catch(ArithmeticException e) {
                    System.out.println("You fool!");
                }

            }
    }
}
