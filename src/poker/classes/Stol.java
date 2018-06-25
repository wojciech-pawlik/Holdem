package poker.classes;

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
    private HashMap<Integer, Gracz> gracze; //hashmap, nie arraylist ze wzgledu na pozniejsze zastosowanie
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
        NrRaise = 0;
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

    // == stacki ante bez powtorzen == //
    private int liczbaStackowAnte() {
        int pivot = 0;
        for(Integer i : stacki)
            if(i <= Ante) pivot++;
        return pivot;
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
        System.out.printf("%s %s %s\n", flop1.getNazwa(), flop2.getNazwa(), flop3.getNazwa());
    }

    private void LosujTurn()
    {
        turn = talia.LosujKarte(random);
        System.out.printf("%s %s %s\t%s\n", flop1.getNazwa(), flop2.getNazwa(), flop3.getNazwa(), turn.getNazwa());
    }

    private void LosujRiver()
    {
        river = talia.LosujKarte(random);
        System.out.printf("%s %s %s\t%s\t%s\n", flop1.getNazwa(), flop2.getNazwa(), flop3.getNazwa(), turn.getNazwa(), river.getNazwa());
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
                gracze.get(i).setZetony();
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
        for(int i = 0; i < LiczbaGraczy; i++) System.out.printf("%s\t\t\t", gracze.get(i).getNickname()); System.out.println();
        for(int i = 0; i < LiczbaGraczy; i++) {
            int zetony = gracze.get(i).getZetony() - gracze.get(i).getZaklad();
            System.out.printf("%d\t\t\t", zetony);
        }
        System.out.println();
    }

    private void WyswietlGraczyPoPodzialePuli() {
        for(int i = 0; i < LiczbaGraczy; i++) System.out.printf("%s\t\t\t", gracze.get(i).getNickname()); System.out.println();
        for(int i = 0; i < LiczbaGraczy; i++) System.out.printf("%d\t\t\t", gracze.get(i).getZetony());
        System.out.println();
    }

    private void WyswietlStacki() {
        System.out.println("WyswietlStacki()");
        for (int i = 0; i < stacki.size(); i++) System.out.printf("%d: %d\n", i, stacki.get(i));
        System.out.println("stacki.size(): " + stacki.size());
    }

    private void WyswietlPule() {
        System.out.println("WyswietlPule()");
        for(int i = 0; i < pule.size(); i++) System.out.printf("%d: %d\n", i, pule.get(i));
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
            System.out.printf("%s%s\t\t\t", gracze.get(i).getKarta1().getNazwa(), gracze.get(i).getKarta2().getNazwa());
        System.out.println();
        if(StackiNormalne >= LiczbaGraczy - 1) {		//BEZ ROZGRYWKI PREFLOP (GŁÓWNIE HU)
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
        System.out.println("Dostosuj()");

        // <--- Sotrowanie i redukowanie listy stackow ---> // (redukowanie - usuwanie powtorzen)
        sortujStacki();
        System.out.println("stacki.size(): " + stacki.size());
        if(stacki.size() > 1)
            for (int i = stacki.size() - 1; i > 0; i--)
                if (stacki.get(i).equals(stacki.get(i - 1))) {
                    stacki.remove(i);
                    System.out.println("remove");
                }
        WyswietlStacki();

        // <--- Dodawanie puli pobocznych ---> //
        for(int i = pule.size() - 1; i >= liczbaStackowAnte(); i--)
            pule.remove(i);
        int Suma = 0;
        for(int i = StackiAnte; i < stacki.size(); i++) {
            int k = 0;

            if(i == 0) {
                for(int j = 0; j < gracze.size(); j++)
                    if (gracze.get(j).getZaklad() <= stacki.get(i) - Ante)
                        k += gracze.get(j).getZaklad() + Ante;
            }
            else
                for (int j = 0; j < gracze.size(); j++)
                    if (gracze.get(j).getZaklad() >= stacki.get(i) - Ante)
                        k += stacki.get(i) - Ante;
            k -= Suma;
            pule.add(k);
            Suma += k;
        }
        WyswietlPule();

        // <--- Ustawienie maksymalnego zakladu na 0 (nowa tura) ---> //
        setMaxBet(0);

        // <--- Jesli gracz wsunal all-ina, a ma wiekszy stack od pozostalych stakujacych sie graczy ---> //
        // <--- Zwrocenie nadwyzki wrzuconej przez gracza do puli ---> //
        if(StackiNormalne > 1 && Foldy + StackiNormalne == LiczbaGraczy)
            for(int i = 0; i < LiczbaGraczy; i++)
                if(gracze.get(i).getZetony() > stacki.get(stacki.size() - 1))
                    gracze.get(i).setZaklad(stacki.get(stacki.size() - 1));
    }

    private void Preflop(Scanner scanner) {
        int ruch; // deklaracja zmiennej mowiacej, ktory gracz ma ruch
        NrRaise = -1;

        // === okreslenie, ktory gracz rozpoczyna rozgrywke === //
        if(LiczbaGraczy == 2)
            ruch = Button;
        else
            ruch = (Button + 3) % LiczbaGraczy;

        // === ROZGRYWKA PREFLOP === //
        while(NrRaise < LiczbaGraczy - 1) {
            if(gracze.get(ruch).isCzyGra() && gracze.get(ruch).getZetony() > gracze.get(ruch).getZaklad()) {
                // === WSZYSCY ZLIMPOWALI DO GRACZA NA BB === //
                if(gracze.get(ruch).getZaklady(PREFLOP) == MaxBet) {
                    System.out.printf("Gracz %s: 1 - raise, 2 - check).\n", gracze.get(ruch).getNickname());
                    String wybor = scanner.next();
                    switch (wybor) {
                        case "1":
                            System.out.print("Podaj kwote zakladu.");
                            int b;
                            if(gracze.get(ruch).getZetony() - MaxBet < BB)
                                b = gracze.get(ruch).getZetony() - MaxBet;

                            else {
                                b = scanner.nextInt();
                                if(b < MaxBet + BB)
                                    b = MaxBet + BB;
                                else if(b > gracze.get(ruch).getZetony())
                                    b = gracze.get(ruch).getZetony();
                            }

                            System.out.println("Kwota zakladu: " + b);
                            addPula(b - gracze.get(ruch).getZaklady(PREFLOP));
                            gracze.get(ruch).addZaklady(0, b);

                            resetNrRaise();
                            break;

                        default:
                            NrRaise++;
                            break;
                    }
                }

                // === JEST MOŻLIWOSC RAISU === //
                else if(gracze.get(ruch).getZaklady(PREFLOP) < MaxBet) {
                    System.out.printf("Gracz %s: 1 - raise, 2 - call, 3 - fold).\n", gracze.get(ruch).getNickname());
                    String wybor = scanner.next();
                    switch (wybor) {

                        // == RAISE == //
                        case "1":
                            // a) okreslenie kwoty zakladu
                            int b;
                            // = JESLI GRACZ MA W STACKU MNIEJ NIŻ 1BB WIECEJ NIZ MAXBET = //
                            if (gracze.get(ruch).getZaklady(PREFLOP) + gracze.get(ruch).getZetony() - MaxBet < BB)
                                b = gracze.get(ruch).getZaklady(PREFLOP) + gracze.get(ruch).getZetony() - MaxBet;
                            // = NORMALNY PRZYPADEK = //
                            else {
                                System.out.println("Podaj kwote zakladu: ");
                                b = scanner.nextInt();
                                if (b < MaxBet + BB)
                                    b = MaxBet + BB;
                                else if (b > gracze.get(ruch).getZetony())
                                    b = gracze.get(ruch).getZetony();
                            }

                            // b) dodanie zakladu do puli
                            System.out.println("Kwota zakladu: " + b);
                            addPula(b - gracze.get(ruch).getZaklady(PREFLOP));
                            gracze.get(ruch).setZaklady(0, b);

                            // c)
                            resetNrRaise();
                            setMaxBet(b);
                            break;

                        // == CALL == //
                        case "2":
                            if (gracze.get(ruch).getZetony() <= MaxBet) {
                                addPula(gracze.get(ruch).getZetony() - gracze.get(ruch).getZaklady(PREFLOP));
                                gracze.get(ruch).addZaklady(0, gracze.get(ruch).getZetony() - gracze.get(ruch).getZaklad());
                            } else {
                                addPula(MaxBet - gracze.get(ruch).getZaklady(PREFLOP));
                                gracze.get(ruch).setZaklady(0, MaxBet);
                            }
                            NrRaise++;
                            break;

                        // == FOLD == //
                        default:
                            gracze.get(ruch).setCzyGra(false);
                            Foldy++;
                            NrRaise++;
                            break;
                    }
                }

                gracze.get(ruch).Zaklad();
                if(gracze.get(ruch).getZetony() == gracze.get(ruch).getZaklad()) {
                    StackiNormalne++;
                    stacki.add(gracze.get(ruch).getZetony() + Ante);
                }
            }
            ruch = (ruch + 1) % LiczbaGraczy;
        }
    }

    // <=== ROZGRYWKA PO FLOPIE ===> //
    private void Postflop(Scanner scanner, int tura) {
        System.out.println("Postflop() " + tura);
        int ruch = (Button + 1) % LiczbaGraczy;
        NrRaise = -1;
        while(NrRaise < LiczbaGraczy - 1) {
            if(gracze.get(ruch).isCzyGra() && gracze.get(ruch).getZetony() > gracze.get(ruch).getZaklad()) {
                if(gracze.get(ruch).getZaklady(tura) < MaxBet) {
                    System.out.printf("Gracz %s: 1 - raise, 2 - call, 3 - fold).\n", gracze.get(ruch).getNickname());
                    int wybor = scanner.nextInt();
                    switch (wybor) {
                        case 1:
                            int b;
                            if(gracze.get(ruch).getZetony() - gracze.get(ruch).DodalDoPuli(tura) <= 2 * MaxBet)
                                b = gracze.get(ruch).getZetony() - gracze.get(ruch).DodalDoPuli(tura);
                            else {
                                b = scanner.nextInt();
                                if(b < 2 * MaxBet) b = 2 * MaxBet;
                                else if(b > gracze.get(ruch).getZetony() - gracze.get(ruch).DodalDoPuli(tura))
                                    b = gracze.get(ruch).getZetony() - gracze.get(ruch).DodalDoPuli(tura);
                            }

                            addPula(b - gracze.get(ruch).getZaklady(tura));
                            gracze.get(ruch).setZaklady(tura, b);

                            resetNrRaise();
                            setMaxBet(b);
                            break;
                        case 2:
                            if(gracze.get(ruch).getZetony() - gracze.get(ruch).getZaklad() < MaxBet) {
                                addPula(MaxBet - gracze.get(ruch).getZaklady(tura));
                                gracze.get(ruch).addZaklady(tura, gracze.get(ruch).getZetony());
                            }
                            else {
                                addPula(MaxBet - gracze.get(ruch).getZaklady(tura));
                                gracze.get(ruch).setZaklady(tura, MaxBet);
                            }
                            NrRaise++;
                            break;
                        default:
                            gracze.get(ruch).setCzyGra(false);
                            Foldy++;
                            NrRaise++;
                            break;
                    }
                }

                else if(gracze.get(ruch).getZaklady(tura) == 0) {
                    System.out.printf("Gracz %s: 1 - bet, 2 - check).\n", gracze.get(ruch).getNickname());
                    int wybor = scanner.nextInt();
                    switch (wybor) {
                        case 1:
                            int b;
                            if(gracze.get(ruch).getZetony() - gracze.get(ruch).DodalDoPuli(tura) <= BB)
                                b = gracze.get(ruch).getZetony() - gracze.get(ruch).DodalDoPuli(tura);

                            else {
                                System.out.print("Podaj kwote zakladu: ");
                                b = scanner.nextInt();
                                if(b < BB) b = BB;
                                else if(b > gracze.get(ruch).getZetony() - gracze.get(ruch).DodalDoPuli(tura))
                                    b = gracze.get(ruch).getZetony() - gracze.get(ruch).DodalDoPuli(tura);
                            }

                            addPula(b);
                            gracze.get(ruch).setZaklady(tura, b);

                            setMaxBet(gracze.get(ruch).getZaklady(tura));
                            resetNrRaise();

                            break;
                        default:
                            NrRaise++;
                            break;
                    }
                }

                gracze.get(ruch).Zaklad();
                if(gracze.get(ruch).getZetony() == gracze.get(ruch).getZaklad()) {
                    StackiNormalne++;
                    stacki.add(gracze.get(ruch).getZetony() + Ante);
                }
            }
            else
                NrRaise++;

            ruch = (ruch + 1) % LiczbaGraczy;
        }
    }

    private void RozdzielFoldy() {
        System.out.println("RozdzielFoldy()");
        for(int i = 0; i < LiczbaGraczy; i++) {
            gracze.get(i).substractZetony(gracze.get(i).getZaklad()) ;
            if(gracze.get(i).isCzyGra()) {
                gracze.get(i).addZetony(Pula);
                System.out.println(Pula + "   " + gracze.get(i).getNickname());
            }
        }
        WyswietlGraczyPoPodzialePuli();
    }

    private void SprawdzUklady() {
        System.out.println("SprawdzUklady()");
        for(int i = 0; i < LiczbaGraczy; i++) {
            if(gracze.get(i).isCzyGra()) {
                gracze.get(i).setUklad(this);
                gracze.get(i).getUklad().sprawdzUklad();
                System.out.println(gracze.get(i).getNickname() + " zdobyl " + gracze.get(i).getUklad().getPunkty() + " punktow.");
            }
        }
    }

    // TODO: ustalenie tablicy 'Najlepsi' w przypadku 'StackiAnte > 0' //
    private void RozdzielPule() {
        System.out.println("RozdzielPule()");
        for(int i = 0; i < LiczbaGraczy; i++)
            gracze.get(i).substractZetony(gracze.get(i).getZaklad());
        for(int k = pule.size() - 1; k >= 0; k--) {
            var Najlepsi = new ArrayList<Gracz>();
            int MaxPts = 0;
            System.out.println("stacki.get(" + k + "): " + stacki.get(k)); //TEST
            for(int i = 0; i < LiczbaGraczy; i++) {
                if(gracze.get(i).isCzyGra() && gracze.get(i).getZaklad() >= stacki.get(k) - Ante)
                    if(gracze.get(i).getUklad().getPunkty() > MaxPts) {
                        Najlepsi.clear();
                        MaxPts = gracze.get(i).getUklad().getPunkty();
                        Najlepsi.add(gracze.get(i));
                    }
                    else if(gracze.get(i).getUklad().getPunkty() == MaxPts)
                        Najlepsi.add(gracze.get(i));
            }
            var strPula = new StringBuilder();
            System.out.println("Najlepsi.size(): " + Najlepsi.size());

            // TODO: inny napis w przypadku braku puli pobocznych
            if(Najlepsi.size() == 1) {
                Najlepsi.get(0).addZetony(pule.get(k));

                strPula.append(Najlepsi.get(0).getNickname())
                        .append(" wins a side pot ")
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

                    strPula.append(gracze.get(i).getNickname());
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
        WyswietlGraczyPoPodzialePuli();
    }
}
