package poker.classes;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Stol {
    private static final int PREFLOP = 0;
    private static final int FLOP = 1;
    private static final int TURN = 2;
    private static final int RIVER = 3;

    private Talia talia;
    private Karta flop1, flop2, flop3, turn, river;
    private int Pula, LiczbaGraczy, LiczbaMiejsc, Button, BB, Ante;
    private int MaxBet; //MAKSYMALNY ZAKLAD W ROZDANIU
    private int NrRaise; //NUMER OSTATNIEGO AGRESORA
    private int Foldy; //LICZBA GRACZY, KTÓRZY SPASOWALI W ROZDANIU
    private int StackiNormalne; //LICZBA GRACZY, KTÓRZY WRZUCILI WSZYSTKIE ŻETONY
    private int StackiAnte; //LICZBA GRACZY, KTÓRZY WRZUCILI WSZYSTKIE ŻETONY, PLACĄC ANTE
    private ArrayList<Integer> pule;
    private ArrayList<Integer> stacki;
    private HashMap<Integer, Gracz> gracze;
    private Random random;

    @SuppressWarnings("unused")
    public Stol() {
        this(9);
    }

    public Stol(int LiczbaMiejsc) {
        pule = new ArrayList<>();
        stacki = new ArrayList<>();
        gracze = new HashMap<>();
        talia = new Talia();
        flop1 = flop2 = flop3 = turn = river = new Karta();
        Pula = 0;
        this.LiczbaMiejsc = LiczbaMiejsc;
        LiczbaGraczy = 0;
        Button = -1;
        BB = MaxBet = 50;
        Ante = 10;
        StackiNormalne = StackiAnte = Foldy = 0;
        random = new Random();
    }

    /* ==== GETTERS AND SETTERS ==== */

    //KARTY//

    Karta getFlop1() {
        return flop1;
    }

    Karta getFlop2() {
        return flop2;
    }

    Karta getFlop3() {
        return flop3;
    }

    Karta getTurn() {
        return turn;
    }

    Karta getRiver() {
        return river;
    }


    //INTY//

    private void addPula(int Pula) {
        this.Pula += Pula;
    }

    private void setLiczbaGraczy(int LiczbaGraczy) {
        this.LiczbaGraczy = LiczbaGraczy;
    }

    @SuppressWarnings("unused")
    private void setLiczbaMiejsc(int LiczbaMiejsc) {
        this.LiczbaMiejsc = LiczbaMiejsc;
    }

    @SuppressWarnings("unused")
    public void setButton(int Button) {
        this.Button = Button;
    }

    @SuppressWarnings("unused")
    private void setBB(int BB) {
        this.BB = BB;
    }

    @SuppressWarnings("unused")
    private void setAnte(int Ante) {
        this.Ante = Ante;
    }

    private void setMaxBet(int MaxBet) {
        this.MaxBet = MaxBet;
    }

    private void resetNrRaise() {
        this.NrRaise = 0;
    }

    //LISTY I MAPY//

    @Contract(pure = true)
    private int getStacki(int index) {
        if(index >= 0) return stacki.get(index);
        return -1;
    }

    private Gracz getGracze(int Numer) {
        return gracze.get(Numer);
    }

    /* ==== SORTOWANIE ==== */

    private void sortujStacki() {
        Collections.sort(stacki);
    }

    /* ==== FUNKCJE	PROGRAMU ==== */

    private void LosujKarty(Talia talia) {
        for(int i = 0; i < LiczbaGraczy; i++) {
            gracze.get(i).setKarta1(talia.LosujKarte(random));
            gracze.get(i).setKarta2(talia.LosujKarte(random));
        }
    }

    private void LosujFlop() {
        flop1 = talia.LosujKarte(random);
        flop2 = talia.LosujKarte(random);
        flop3 = talia.LosujKarte(random);
    }

    private void LosujTurn()
    {
        turn = talia.LosujKarte(random);
    }

    private void LosujRiver()
    {
        river = talia.LosujKarte(random);
    }

    private void WyczyscStol()
    {
        flop1 = flop2 = flop3 = turn = river = new Karta();
        Pula = 0;
        for(int i = 0; i < LiczbaGraczy; i++) gracze.get(i).setCzyGra(true);
        pule.clear();
        stacki.clear();
        StackiNormalne = StackiAnte = Foldy = 0;
    }

    private void PrzesunButton() {
        Button = (Button + 1) % LiczbaGraczy;
    }

    private void PobierzAnte() {
        for(int i = 0; i < LiczbaGraczy; i++) {
            if(gracze.get(i).getZetony() <= Ante) {
                Pula += gracze.get(i).getZetony();
                stacki.add(gracze.get(i).getZetony());
                gracze.get(i).setZetony(0);
                StackiAnte++;
            }
            else {
                gracze.get(i).substractZetony(Ante);
                Pula += Ante;
            }
        }
        Collections.sort(stacki);
        for(int i = 0; i < stacki.size(); i++) {
            pule.add((LiczbaGraczy - StackiNormalne) * stacki.get(StackiNormalne));
            StackiNormalne++;
        }
    }

    private void PobierzCiemne() {
        if(LiczbaGraczy == 2) {
            if(gracze.get(Button).getZetony() <= BB / 2) {
                gracze.get(Button).setZaklady(0, gracze.get(Button).getZetony());
                gracze.get((Button + 1) % 2).setZaklady(0, gracze.get(Button).getZetony());
                Pula += 2*gracze.get(Button).getZaklady(0);
                stacki.add(Ante + gracze.get(Button).getZetony());
                StackiNormalne++;
            }
            else {
                if(gracze.get((Button + 1) % 2).getZetony() <= BB) {
                    if(gracze.get((Button + 1) % 2).getZetony() <= BB / 2) {
                        gracze.get((Button + 1) % 2).setZaklady(0, gracze.get((Button + 1) % 2).getZetony());
                        gracze.get(Button).setZaklady(0, gracze.get((Button + 1) % 2).getZetony());
                        Pula += 2*gracze.get(Button).getZaklady(0);
                        stacki.add(Ante + gracze.get((Button + 1) % 2).getZetony());
                        StackiNormalne++;
                    }
                    else {
                        gracze.get(Button).setZaklady(0,BB/2);
                        Pula += BB/2;
                        gracze.get((Button + 1) % 2).setZaklady(0, gracze.get((Button + 1) % 2).getZetony());
                        Pula += gracze.get((Button + 1) % 2).getZetony();
                        stacki.add(Ante + gracze.get((Button + 1) % 2).getZetony());
                    }
                }
                else {
                    gracze.get(Button).setZaklady(0,BB/2);
                    gracze.get((Button + 1) % 2).setZaklady(0, BB);
                    Pula += BB + BB/2;
                }
            }
        }
        else {
            if(gracze.get((Button + 1) % LiczbaGraczy).getZetony() < BB / 2) {
                gracze.get((Button + 1) % LiczbaGraczy).setZaklady(0,gracze.get((Button + 1) % LiczbaGraczy).getZetony());
                Pula += gracze.get((Button + 1) % LiczbaGraczy).getZetony();
            }
            else {
                gracze.get((Button + 1) % LiczbaGraczy).setZaklady(0,BB/2);
                Pula += BB / 2;
            }
            if(gracze.get((Button + 2) % LiczbaGraczy).getZetony() < BB) {
                gracze.get((Button + 2) % LiczbaGraczy).setZaklady(0, gracze.get((Button + 2) % LiczbaGraczy).getZetony());
                Pula += gracze.get((Button + 2) % LiczbaGraczy).getZetony();
            }
            else {
                gracze.get((Button + 2) % LiczbaGraczy).setZaklady(0, BB);
                Pula += BB;
            }
        }
        for(int i = 0; i < LiczbaGraczy; i++)
            gracze.get(i).Zaklad();
        MaxBet = BB;
    }

    public void DodajGraczy(@NotNull Scanner scanner) {
        System.out.print("Podaj liczbe graczy: ");
        setLiczbaGraczy(scanner.nextInt());
        if(LiczbaGraczy > LiczbaMiejsc)
            setLiczbaGraczy(LiczbaMiejsc);
        else if(LiczbaGraczy < 2)
            setLiczbaGraczy(2);
        System.out.printf("Liczba graczy: %d\n", LiczbaGraczy);

        for(int i = 0; i < LiczbaGraczy; i++) {
            System.out.printf("Podaj nickname gracza nr %d: ", i + 1);
            Gracz gracz = new Gracz(scanner.next(), 10000, i);
            gracze.put(i, gracz);
        }
        WyswietlGraczy();
    }

    private void WyswietlGraczy() {
        for(int i = 0; i < LiczbaGraczy; i++) System.out.printf("%s\t\t\t", getGracze(i).getNickname()); System.out.println();
        for(int i = 0; i < LiczbaGraczy; i++) {
            int zetony = getGracze(i).getZetony() - getGracze(i).getZaklad();
            System.out.printf("%d\t\t\t", zetony);
        }
        System.out.println();
    }

    public void Rozdaj(Scanner scanner) {
        WyczyscStol();
        PrzesunButton();
        talia.Tasuj();
        PobierzAnte();
        WyswietlGraczy(); //TEST
        PobierzCiemne();
        WyswietlGraczy(); //TEST
        LosujKarty(talia);
        for(int i = 0; i < LiczbaGraczy; i++)
            System.out.printf("%s%s\t\t\t", getGracze(i).getKarta1().getNazwa(), getGracze(i).getKarta2().getNazwa());
        System.out.println();
        if(StackiNormalne == LiczbaGraczy - 1) {		//BEZ ROZGRYWKI PREFLOP (GŁÓWNIE HU)
            Dostosuj();
            WyswietlGraczy(); //TEST
            LosujFlop();
            LosujTurn();
            LosujRiver();
        }
        else {												//ROZGRYWKA PREFLOP
            WyswietlGraczy(); //TEST
            System.out.println("Preflop"); //TEST
            Preflop(scanner);
            WyswietlGraczy(); //TEST
            System.out.println("Dostosuj"); //TEST
            Dostosuj();
            WyswietlGraczy(); //TEST
            if(Foldy == LiczbaGraczy - 1) {                //WSZYSCY SPASOWALI PREFLOP
                System.out.println("RozdzielFoldy"); //TEST
                RozdzielFoldy();
            }
            else if(Foldy + StackiNormalne == LiczbaGraczy - 1) {	//SHOWDOWN (ALL-IN) PREFLOP
                System.out.println("LosujFlop"); //TEST
                LosujFlop();
                System.out.println("LosujTurn"); //TEST
                LosujTurn();
                System.out.println("LosujRiver"); //TEST
                LosujRiver();
                System.out.printf("%s %s %s\t%s\t%s\n", flop1.getNazwa(), flop2.getNazwa(), flop3.getNazwa(), turn.getNazwa(),
                        river.getNazwa()); //TEST
                System.out.println("SprawdzUklady"); //TEST
                SprawdzUklady();
                System.out.println("RozdzielPule"); //TEST
                RozdzielPule();
            }
            else {											//ROZGRYWKA NA FLOPIE
                LosujFlop();
                Postflop(scanner, FLOP);
                Dostosuj();
                if(Foldy == LiczbaGraczy - 1)			//WSZYSCY SPASOWALI NA FLOPIE
                    RozdzielFoldy();
                else if(Foldy + StackiNormalne == LiczbaGraczy - 1) {	//SHOWDOWN (ALL-IN) NA FLOPIE
                    LosujTurn();
                    LosujRiver();
                    SprawdzUklady();
                    RozdzielPule();
                }
                else {										//ROZGRYWKA NA TURNIE
                    LosujTurn();
                    Postflop(scanner, TURN);
                    Dostosuj();
                    if(Foldy == LiczbaGraczy - 1)		//WSZYSCY SPASOWALI NA TURNIE
                        RozdzielFoldy();
                    else if(Foldy + StackiNormalne == LiczbaGraczy - 1) {	//SHOWDOWN (ALL-IN) NA TURNIE
                        LosujRiver();
                        SprawdzUklady();
                        RozdzielPule();
                    }
                    else {									//ROZGRYWKA NA RIVERZE
                        LosujRiver();
                        Postflop(scanner, RIVER);
                        Dostosuj();
                        if(Foldy == LiczbaGraczy - 1)	//WSZYSCY SPASOWALI NA RIVERZE
                            RozdzielFoldy();
                        else {								//SHOWDOWN NA RIVERZE
                            SprawdzUklady();
                            RozdzielPule();
                        }
                    }
                }
            }
        }
    }

    private void Dostosuj() {
        // <--- Sotrowanie listy stackow ---> //
        sortujStacki();

        // <--- Dodawanie puli pobocznych ---> //
        int Suma = 0;
        for(int i = StackiAnte; i < stacki.size(); i++) {
            int k = 0;
            for(int j = 0; j < LiczbaGraczy; j++) {
                if(getGracze(j).getZaklad() + Ante <= getStacki(i))
                    k += getGracze(j).getZaklad() + Ante;
            }
            k -= Suma;
            pule.add(k);
            Suma += k;
        }

        // <--- Ustawienie maksymalnego zakladu na 0 (nowa tura) ---> //
        setMaxBet(0);

        // <--- Jesli gracz wsunal all-ina, a ma wiekszy stack od pozostalych stakujacych sie graczy ---> //
        // <--- Zwrocenie nadwyzki wrzuconej przez gracza do puli ---> //
        if(StackiNormalne > 1 && Foldy + StackiNormalne == LiczbaGraczy) {
            for(int i = 0; i < LiczbaGraczy; i++) {
                if(getGracze(i).getZetony() + Ante > getStacki(stacki.size() - 1))
                    getGracze(i).setZaklad(getStacki(stacki.size() - 1) - Ante);
            }
        }
    }

    private void Preflop(Scanner scanner) {
        int ruch; // deklaracja zmiennej mowiacej, ktory gracz ma ruch

        // === okreslenie, ktory gracz rozpoczyna rozgrywke === //
        if(LiczbaGraczy == 2)
            ruch = Button;
        else
            ruch = (Button + 3) % LiczbaGraczy;

        // === ROZGRYWKA PREFLOP === //
        while(Foldy + StackiNormalne < LiczbaGraczy - 1 && NrRaise < LiczbaGraczy) {
            if(getGracze(ruch).isCzyGra() && getGracze(ruch).getZetony() > getGracze(ruch).getZaklad()) {
                /////---TEST-->///
                System.out.println("ZAKLAD GRACZA " + getGracze(ruch).getNickname() + ": " + getGracze(ruch).getZaklady(PREFLOP));
                System.out.println("MAXBET: " + MaxBet);
                for (int bet : stacki)
                    System.out.println("STACK " + bet);
                System.out.println("PULA: " + Pula);
                /////<--TEST---///
                // === WSZYSCY ZLIMPOWALI DO GRACZA NA BB === //
                if(getGracze(ruch).getZaklady(PREFLOP) == MaxBet) {
                    System.out.printf("Gracz %s: 1 - raise, 2 - check).\n", getGracze(ruch).getNickname());
                    String k = scanner.next();
                    if(k.equals("1")) {
                        System.out.print("Podaj kwote zakladu.");
                        int b;
                        if(getGracze(ruch).getZetony() - MaxBet < BB)
                            b = getGracze(ruch).getZetony() - MaxBet;

                        else {
                            b = scanner.nextInt();
                            if(b < MaxBet + BB)
                                b = MaxBet + BB;
                            else if(b > getGracze(ruch).getZetony())
                                b = getGracze(ruch).getZetony();
                        }


                        addPula(b - getGracze(ruch).getZaklady(PREFLOP));
                        getGracze(ruch).addZaklady(0, b);
                        if(getGracze(ruch).getZaklad() == getGracze(ruch).getZetony()) {
                            StackiNormalne++;
                            stacki.add(getGracze(ruch).getZetony() + Ante);
                        }

                        resetNrRaise();
                    }
                    else if(k.equals("2")) {
                        resetNrRaise();
                        break;
                    }
                }

                // === BADANA FUNCKJA ===> //
                // === JEST MOŻLIWOSC RAISU === //
                else if(getGracze(ruch).getZaklady(PREFLOP) < MaxBet) {
                    System.out.printf("Gracz %s: 1 - raise, 2 - call, 3 - fold).\n", getGracze(ruch).getNickname());
                    String wybor = scanner.next();
                    switch (wybor) {

                        // == RAISE == //
                        case "1":
                            int b;
                            // = JESLI GRACZ MA W STACKU MNIEJ NIŻ 1BB WIECEJ NIZ MAXBET = //
                            if (getGracze(ruch).getZaklady(PREFLOP) + getGracze(ruch).getZetony() - MaxBet < BB)
                                b = getGracze(ruch).getZaklady(PREFLOP) + getGracze(ruch).getZetony() - MaxBet;
                            else {
                                b = scanner.nextInt();
                                if (b < MaxBet + BB)
                                    b = MaxBet + BB;
                                else if (b > getGracze(ruch).getZaklady(PREFLOP) + getGracze(ruch).getZetony())
                                    b = getGracze(ruch).getZaklady(PREFLOP) + getGracze(ruch).getZetony();
                            }

                            addPula(b - getGracze(ruch).getZaklady(PREFLOP));
                            getGracze(ruch).setZaklady(0, b);

                            resetNrRaise();
                            setMaxBet(b);
                            /////---TEST-->///
                            System.out.println("ZAKLAD GRACZA " + getGracze(ruch).getNickname() + ": " + getGracze(ruch).getZaklady(PREFLOP));
                            System.out.println("MAXBET: " + MaxBet);
                            for (int bet : stacki)
                                System.out.println("STACK " + bet);
                            System.out.println("PULA: " + Pula);
                            /////<--TEST---///
                            break;

                        // == CALL == //
                        case "2":
                            if (getGracze(ruch).getZetony() <= MaxBet) {
                                addPula(MaxBet - getGracze(ruch).getZaklady(PREFLOP));
                                getGracze(ruch).addZaklady(0, getGracze(ruch).getZetony());
                                StackiNormalne++;
                                stacki.add(getGracze(ruch).getZaklady(PREFLOP) + Ante);
                            } else {
                                addPula(MaxBet - getGracze(ruch).getZaklady(PREFLOP));
                                getGracze(ruch).setZaklady(0, MaxBet);
                            }
                            NrRaise++;
                            /////---TEST-->///
                            System.out.println("ZAKLAD GRACZA " + getGracze(ruch).getNickname() + ": " + getGracze(ruch).getZaklady(PREFLOP));
                            System.out.println("MAXBET: " + MaxBet);
                            for (int bet : stacki)
                                System.out.println("STACK " + bet);
                            System.out.println("PULA: " + Pula);
                            /////<--TEST---///
                            break;


                        // == FOLD == //
                        default:
                            getGracze(ruch).setCzyGra(false);
                            Foldy++;
                            NrRaise++;
                            /////---TEST-->///
                            System.out.println("ZAKLAD GRACZA " + getGracze(ruch).getNickname() + ": " + getGracze(ruch).getZaklady(PREFLOP));
                            System.out.println("MAXBET: " + MaxBet);
                            for (int bet : stacki)
                                System.out.println("STACK " + bet);
                            System.out.println("PULA: " + Pula);
                            /////<--TEST---///
                            break;
                    }
                }
                // <=== BADANA FUNCKJA === //

                getGracze(ruch).Zaklad();
            }
            ruch = (ruch + 1) % LiczbaGraczy;
        }
        System.out.println("Koniec Preflop");
    }

    private void Postflop(Scanner scanner, int tura) {
        int ruch = (Button + 1) % LiczbaGraczy;
        while(Foldy + StackiNormalne < LiczbaGraczy - 1 && NrRaise < LiczbaGraczy) {
            if(getGracze(ruch).isCzyGra() && getGracze(ruch).getZetony() > getGracze(ruch).getZaklad()) {
                if(getGracze(ruch).getZaklady(tura) == 0) {
                    System.out.printf("Gracz %s: 1 - bet, 2 - check).\n", getGracze(ruch).getNickname());
                    int k = scanner.nextInt();
                    if(k == 1) {
                        int b = 0;
                        if(getGracze(ruch).getZetony() - getGracze(ruch).DodalDoPuli(tura) <= BB)
                            b = getGracze(ruch).getZetony() - getGracze(ruch).DodalDoPuli(tura);

                        else {
                            System.out.print("Podaj kwote zakladu.");
                            while(b < BB || b > getGracze(ruch).getZetony() - getGracze(ruch).DodalDoPuli(tura))
                                b = scanner.nextInt();
                        }

                        addPula(b);
                        getGracze(ruch).setZaklady(tura, b);

                        setMaxBet(getGracze(ruch).getZaklady(tura));
                        resetNrRaise();
                    }
                    else if(k == 2)
                        NrRaise++;
                }
                else if(getGracze(ruch).getZaklady(tura) < MaxBet) {
                    System.out.printf("Gracz %s: 1 - raise, 2 - call, 3 - fold).\n", getGracze(ruch).getNickname());
                    int k = scanner.nextInt();
                    if(k == 1) {
                        int b = 0;
                        if(getGracze(ruch).getZetony() - getGracze(ruch).DodalDoPuli(tura) <= 2 * MaxBet)
                            b = getGracze(ruch).getZetony() - getGracze(ruch).DodalDoPuli(tura);
                        else while(b < 2 * MaxBet || b > getGracze(ruch).getZetony() - getGracze(ruch).DodalDoPuli(tura))
                            b = scanner.nextInt();

                        addPula(b - getGracze(ruch).getZaklady(tura));
                        getGracze(ruch).setZaklady(tura, b);

                        resetNrRaise();
                        setMaxBet(b);
                    }
                    else if(k == 2) {
                        if(getGracze(ruch).getZetony() - getGracze(ruch).getZaklad() < MaxBet) {
                            addPula(MaxBet - getGracze(ruch).getZaklady(tura));
                            getGracze(ruch).addZaklady(tura, getGracze(ruch).getZetony());
                        }
                        else {
                            addPula(MaxBet - getGracze(ruch).getZaklady(tura));
                            getGracze(ruch).setZaklady(tura, MaxBet);
                        }
                        NrRaise++;
                    }
                    else {
                        getGracze(ruch).setCzyGra(false);
                        NrRaise++;
                    }
                }
                getGracze(ruch).Zaklad();
                if(getGracze(ruch).getZetony() == getGracze(ruch).getZaklad()) {
                    StackiNormalne++;
                    stacki.add(getGracze(ruch).getZetony() + Ante);
                }
            }
            else
                NrRaise++;

            ruch = (ruch + 1) % LiczbaGraczy;
        }
    }

    private void RozdzielFoldy() {
        for(int i = 0; i < LiczbaGraczy; i++) {
            getGracze(i).substractZetony(getGracze(i).getZaklad()) ;
            if(getGracze(i).isCzyGra())
                getGracze(i).substractZetony(Pula);
        }
        WyswietlGraczy();
    }

    private void SprawdzUklady() {
        System.out.println("a");
        for(int i = 0; i < LiczbaGraczy; i++) {
            System.out.println("b");
            if(getGracze(i).isCzyGra()) {
                System.out.println("c");
                getGracze(i).setUklad(this);
                System.out.println(getGracze(i).getUklad().getWszystkieKarty().get(0).getWartosc());
                getGracze(i).getUklad().sprawdzUklad();
            }
        }
    }

    private void RozdzielPule() {
        for(int i = 0; i < LiczbaGraczy; i++)
            getGracze(i).substractZetony(getGracze(i).getZaklad());
        System.out.println("ST SIZE" + stacki.size());
        for(int k = pule.size() - 1; k >= 0; k--) {
            ArrayList<Gracz> Najlepsi = new ArrayList<>();
            int MaxPts = 0;
            System.out.println("ST" + getStacki(k));
            for(int i = 0; i < LiczbaGraczy; i++) {
                if(getGracze(i).isCzyGra() && getGracze(i).getZaklad() >= getStacki(k))
                    if(getGracze(i).getUklad().getPunkty() > MaxPts) {
                        Najlepsi.clear();
                        MaxPts = getGracze(i).getUklad().getPunkty();
                        Najlepsi.add(getGracze(i));
                    }
                    else if(getGracze(i).getUklad().getPunkty() == MaxPts)
                        Najlepsi.add(getGracze(i));
                System.out.println("MAX" + MaxPts + "MAX" + getGracze(i).getUklad().getPunkty());
            }
            var strPula = new StringBuilder();
            System.out.println("NAJ!!!" + Najlepsi.size());
            if(Najlepsi.size() == 1) {
                Najlepsi.get(0).addZetony(pule.get(k));

                strPula.append(Najlepsi.get(0))
                        .append(" wins a side pot number ")
                        .append(pule.get(k))
                        .append(" with a ")
                        .append(Najlepsi.get(0).getUklad().getNazwa())
                        .append(".");
            }
            else {
                strPula.append("A side pot number ")
                        .append(pule.get(k))
                        .append(" is won by: ");
                for(int i = 0; i < Najlepsi.size(); i++) {
                    Najlepsi.get(i).addZetony(pule.get(k) / Najlepsi.size());

                    strPula.append(getGracze(i).getNickname());
                    if(i < Najlepsi.size() - 1)
                        strPula.append(", ");
                    else
                        strPula.append(" ");
                }
                strPula.append(" with a ")
                        .append(Najlepsi.get(0).getUklad().getNazwa())
                        .append(".");

                int t = Najlepsi.size() * (pule.get(k) / Najlepsi.size());
                int q = pule.get(k) - t;
                int i = 0;
                while(q > 0) {
                    Najlepsi.get(i).plusZetony();
                    i++;
                    q--;
                }
            }
            System.out.println(strPula);
        }
        WyswietlGraczy();
    }
}
