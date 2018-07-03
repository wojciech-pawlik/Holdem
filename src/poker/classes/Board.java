package poker.classes;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Board {
    private static final int PREFLOP = 0;
    private static final int FLOP = 1;
    private static final int TURN = 2;
    private static final int RIVER = 3;

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
    private HashMap<Integer, Player> players;
    private Random random;

    @SuppressWarnings("unused")
    public Board() {
        this(9);
    }

    public Board(int placesCount) {
        pots = new ArrayList<>();
        stacks = new ArrayList<>();
        players = new HashMap<>();
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

    //KARTY//

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


    //INTY//

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
        this.afterRaise = 0;
    }

    // == stacks ante bez powtorzen == //
    private int countStacksAnte() {
        int count = 0;
        for(Integer i : stacks)
            if(i <= ante) count++;
        return count;
    }

    /* ==== SORTOWANIE ==== */

    private void sortStacks() {
        Collections.sort(stacks);
    }

    /* ==== FUNKCJE	PROGRAMU ==== */

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

    private void drawTurn()
    {
        turn = deck.drawCard(random);
        System.out.printf("%s %s %s\t%s\n", flop1.getName(), flop2.getName(), flop3.getName(), turn.getName());
    }

    private void drawRiver()
    {
        river = deck.drawCard(random);
        System.out.printf("%s %s %s\t%s\t%s\n", flop1.getName(), flop2.getName(), flop3.getName(), turn.getName(), river.getName());
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
                players.get(i).substractChips(ante);
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

    public void addPlayers(@NotNull Scanner scanner) {
        System.out.print("Podaj liczbe graczy: ");
        setPlayersCount(scanner.nextInt());
        if(playersCount > placesCount)
            setPlayersCount(placesCount);
        else if(playersCount < 2)
            setPlayersCount(2);
        System.out.printf("Liczba graczy: %d\n", playersCount);

        for(int i = 0; i < playersCount; i++) {
            System.out.printf("Podaj nickname gracza nr %d: ", i + 1);
            Player player = new Player(scanner.next(), 10000, i);
            players.put(i, player);
        }
        displayPlayers();
    }

    private void displayPlayers() {
        for(int i = 0; i < playersCount; i++) System.out.printf("%s\t\t\t", players.get(i).getNickname()); System.out.println();
        for(int i = 0; i < playersCount; i++) {
            int zetony = players.get(i).getChips() - players.get(i).getBet();
            System.out.printf("%d\t\t\t", zetony);
        }
        System.out.println();
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
        if(stacksNormal >= playersCount - 1) {		//BEZ ROZGRYWKI PREFLOP (GŁÓWNIE HU)
            adjust();
            displayPlayers(); //TEST
            drawFlop();
            drawTurn();
            drawRiver();
        }
        else {												//ROZGRYWKA PREFLOP
            displayPlayers(); //TEST
            System.out.println("preflop"); //TEST
            preflop(scanner);
            displayPlayers(); //TEST
            adjust();
            displayPlayers(); //TEST
            if(folds == playersCount - 1) {                //WSZYSCY SPASOWALI PREFLOP
                System.out.println("distributeFolds"); //TEST
                distributeFolds();
            }
            else if(folds + stacksNormal == playersCount - 1) {	//SHOWDOWN (ALL-IN) PREFLOP
                System.out.println("drawFlop"); //TEST
                drawFlop();
                System.out.println("drawTurn"); //TEST
                drawTurn();
                System.out.println("drawRiver"); //TEST
                drawRiver();
                System.out.printf("%s %s %s\t%s\t%s\n", flop1.getName(), flop2.getName(), flop3.getName(), turn.getName(),
                        river.getName()); //TEST
                System.out.println("checkHands"); //TEST
                checkHands();
                System.out.println("distribute"); //TEST
                distribute();
            }
            else {											//ROZGRYWKA NA FLOPIE
                drawFlop();
                postflop(scanner, FLOP);
                adjust();
                if(folds == playersCount - 1)			//WSZYSCY SPASOWALI NA FLOPIE
                    distributeFolds();
                else if(folds + stacksNormal == playersCount - 1) {	//SHOWDOWN (ALL-IN) NA FLOPIE
                    drawTurn();
                    drawRiver();
                    checkHands();
                    distribute();
                }
                else {										//ROZGRYWKA NA TURNIE
                    drawTurn();
                    postflop(scanner, TURN);
                    adjust();
                    if(folds == playersCount - 1)		//WSZYSCY SPASOWALI NA TURNIE
                        distributeFolds();
                    else if(folds + stacksNormal == playersCount - 1) {	//SHOWDOWN (ALL-IN) NA TURNIE
                        drawRiver();
                        checkHands();
                        distribute();
                    }
                    else {									//ROZGRYWKA NA RIVERZE
                        drawRiver();
                        postflop(scanner, RIVER);
                        adjust();
                        if(folds == playersCount - 1)	//WSZYSCY SPASOWALI NA RIVERZE
                            distributeFolds();
                        else {								//SHOWDOWN NA RIVERZE
                            checkHands();
                            distribute();
                        }
                    }
                }
            }
        }
    }

    private void adjust() {
        System.out.println("adjust()");

        // <--- Sotrowanie i redukowanie listy stackow ---> // (redukowanie - usuwanie powtorzen)
        sortStacks();
        System.out.println("stacks.size(): " + stacks.size());
        if(stacks.size() > 1)
            for (int i = stacks.size() - 1; i > 0; i--)
                if (stacks.get(i).equals(stacks.get(i - 1))) {
                    stacks.remove(i);
                    System.out.println("remove");
                }
        displayStacks();

        // <--- Dodawanie puli pobocznych ---> //
        System.out.println("pots.size(): " + pots.size());
        for(int i = pots.size() - 1; i >= countStacksAnte(); i--)
            pots.remove(i);
        int sum = 0;
        if(stacks.size() == 0) {
            for(int j = 0; j < players.size(); j++)
                sum += players.get(j).getBet() + ante;
            pots.add(sum);
        }
        else {
            for(int i = stacksAnte; i < stacks.size(); i++) {
                System.out.println("i = " + i);
                int k = 0;

                if(i == 0) {
                    for(int j = 0; j < players.size(); j++)
                        if (players.get(j).getBet() <= stacks.get(i) - ante)
                            k += players.get(j).getBet() + ante;
                }
                else
                    for (int j = 0; j < players.size(); j++)
                        if (players.get(j).getBet() >= stacks.get(i) - ante)
                            k += stacks.get(i) - ante;
                k -= sum;
                pots.add(k);
                sum += k;
            }
        }
        displayPots();

        // <--- Ustawienie maksymalnego zakladu na 0 (nowa tura) ---> //
        setMaxBet(0);

        // <--- Jesli gracz wsunal all-ina, a ma wiekszy stack od pozostalych stakujacych sie graczy ---> //
        // <--- Zwrocenie nadwyzki wrzuconej przez gracza do puli ---> //
        if(stacksNormal > 1 && folds + stacksNormal == playersCount)
            for(int i = 0; i < playersCount; i++)
                if(players.get(i).getChips() > stacks.get(stacks.size() - 1))
                    players.get(i).setBet(stacks.get(stacks.size() - 1));
    }

    private void preflop(Scanner scanner) {
        int turn; // deklaracja zmiennej mowiacej, ktory gracz ma ruch
        afterRaise = -1;

        // === okreslenie, ktory gracz rozpoczyna rozgrywke === //
        if(playersCount == 2)
            turn = button;
        else
            turn = (button + 3) % playersCount;

        // === ROZGRYWKA PREFLOP === //
        while(afterRaise < playersCount - 1) {
            if(players.get(turn).isPlaying() && players.get(turn).getChips() > players.get(turn).getBet()) {
                // === WSZYSCY ZLIMPOWALI DO GRACZA NA bigBlind === //
                if(players.get(turn).getBets(PREFLOP) == maxBet) {
                    System.out.printf("Player %s: 1 - raise, 2 - check).\n", players.get(turn).getNickname());
                    String choice = scanner.next();
                    switch (choice) {
                        case "1":
                            System.out.print("Podaj kwote zakladu.");
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

                            System.out.println("Kwota zakladu: " + b);
                            addPot(b - players.get(turn).getBets(PREFLOP));
                            players.get(turn).addBets(0, b);

                            resetNrRaise();
                            break;

                        default:
                            afterRaise++;
                            break;
                    }
                }

                // === JEST MOŻLIWOSC RAISU === //
                else if(players.get(turn).getBets(PREFLOP) < maxBet) {
                    System.out.printf("Player %s: 1 - raise, 2 - call, 3 - fold).\n", players.get(turn).getNickname());
                    String choice = scanner.next();
                    switch (choice) {

                        // == RAISE == //
                        case "1":
                            // a) okreslenie kwoty zakladu
                            int b;
                            // = JESLI GRACZ MA W STACKU MNIEJ NIŻ 1BB WIECEJ NIZ MAXBET = //
                            if (players.get(turn).getBets(PREFLOP) + players.get(turn).getChips() - maxBet < bigBlind)
                                b = players.get(turn).getBets(PREFLOP) + players.get(turn).getChips() - maxBet;
                            // = NORMALNY PRZYPADEK = //
                            else {
                                System.out.println("Podaj kwote zakladu: ");
                                b = scanner.nextInt();
                                if (b < maxBet + bigBlind)
                                    b = maxBet + bigBlind;
                                else if (b > players.get(turn).getChips())
                                    b = players.get(turn).getChips();
                            }

                            // b) dodanie zakladu do puli
                            System.out.println("Kwota zakladu: " + b);
                            addPot(b - players.get(turn).getBets(PREFLOP));
                            players.get(turn).setBets(0, b);

                            // c)
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

    // <=== ROZGRYWKA PO FLOPIE ===> //
    private void postflop(Scanner scanner, int round) {
        System.out.println("postflop() " + round);
        int turn = (button + 1) % playersCount;
        afterRaise = -1;
        while(afterRaise < playersCount - 1) {
            if(players.get(turn).isPlaying() && players.get(turn).getChips() > players.get(turn).getBet()) {
                if(players.get(turn).getBets(round) < maxBet) {
                    System.out.printf("Player %s: 1 - raise, 2 - call, 3 - fold).\n", players.get(turn).getNickname());
                    int choice = scanner.nextInt();
                    switch (choice) {
                        case 1:
                            int b;
                            if(players.get(turn).getChips() - players.get(turn).addedToPot(round) <= 2 * maxBet)
                                b = players.get(turn).getChips() - players.get(turn).addedToPot(round);
                            else {
                                b = scanner.nextInt();
                                if(b < 2 * maxBet) b = 2 * maxBet;
                                else if(b > players.get(turn).getChips() - players.get(turn).addedToPot(round))
                                    b = players.get(turn).getChips() - players.get(turn).addedToPot(round);
                            }

                            addPot(b - players.get(turn).getBets(round));
                            players.get(turn).setBets(round, b);

                            resetNrRaise();
                            setMaxBet(b);
                            break;
                        case 2:
                            if(players.get(turn).getChips() - players.get(turn).getBet() < maxBet) {
                                addPot(maxBet - players.get(turn).getBets(round));
                                players.get(turn).addBets(round, players.get(turn).getChips());
                            }
                            else {
                                addPot(maxBet - players.get(turn).getBets(round));
                                players.get(turn).setBets(round, maxBet);
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

                else if(players.get(turn).getBets(round) == 0) {
                    System.out.printf("Player %s: 1 - bet, 2 - check).\n", players.get(turn).getNickname());
                    int choice = scanner.nextInt();
                    switch (choice) {
                        case 1:
                            int b;
                            if(players.get(turn).getChips() - players.get(turn).addedToPot(round) <= bigBlind)
                                b = players.get(turn).getChips() - players.get(turn).addedToPot(round);

                            else {
                                System.out.print("Podaj kwote zakladu: ");
                                b = scanner.nextInt();
                                if(b < bigBlind) b = bigBlind;
                                else if(b > players.get(turn).getChips() - players.get(turn).addedToPot(round))
                                    b = players.get(turn).getChips() - players.get(turn).addedToPot(round);
                            }

                            addPot(b);
                            players.get(turn).setBets(round, b);

                            setMaxBet(players.get(turn).getBets(round));
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
            players.get(i).substractChips(players.get(i).getBet()) ;
            if(players.get(i).isPlaying()) {
                players.get(i).addChips(pot);
                System.out.println(pot + "   " + players.get(i).getNickname());
            }
        }
        displayPlayersAfterDistribution();
    }

    private void checkHands() {
        System.out.println("checkHands()");
        for(int i = 0; i < playersCount; i++) {
            if(players.get(i).isPlaying()) {
                players.get(i).setHand(this);
                players.get(i).getHand().checkHand();
                System.out.println(players.get(i).getNickname() + " zdobyl " + players.get(i).getHand().getPoints() + " punktow.");
            }
        }
    }

    // === distribute chips after a round with multiple pots === //
    private void distribute() {
        System.out.println("distribute()");
        for(int i = 0; i < playersCount; i++)
            players.get(i).substractChips(players.get(i).getBet());
        int remains = stacks.size();
        for(int k = (pots.size() - 1); k >= 0; k--) {
            var bestPlayers = new ArrayList<Player>();
            int maxPoints = 0;
            System.out.println(k);
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
            System.out.println("bestPlayers.size(): " + bestPlayers.size());

            // TODO: another descriptions in cases: main pot, no side pots
            if(bestPlayers.size() == 1) {
                bestPlayers.get(0).addChips(pots.get(k));

                stringPot.append(bestPlayers.get(0).getNickname())
                        .append(" wins a side pot ")
                        .append(pots.get(k))
                        .append(" with a ")
                        .append(bestPlayers.get(0).getHand().getName())
                        .append(".");
            }
            else {
                stringPot.append("A side pot number ")
                        .append(pots.get(k))
                        .append(" is won by: ");
                for(int i = 0; i < bestPlayers.size(); i++) {
                    bestPlayers.get(i).addChips(pots.get(k) / bestPlayers.size());

                    stringPot.append(players.get(i).getNickname());
                    if(i < bestPlayers.size() - 1)
                        stringPot.append(", ");
                    else
                        stringPot.append(" ");
                }
                stringPot.append(" with a ")
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
}
