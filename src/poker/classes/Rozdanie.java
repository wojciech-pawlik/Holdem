package poker.classes;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Rozdanie {
    public static final int FLOP = 1;
    public static final int TURN = 2;
    public static final int RIVER = 3;

    public Rozdanie() {
        new Rozdanie(new Stol(3));
    }

    public Rozdanie(Stol stol) {
        Talia talia = new Talia();
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        DodajGraczy(stol, scanner);
        Rozdaj(stol, talia, random, scanner);

        scanner.close();
    }

    /* ==== FUNKCJE	PROGRAMU ==== */

    private void DodajGraczy(@NotNull Stol stol, @NotNull Scanner scanner) {
        System.out.print("Podaj liczbe graczy: ");
        stol.setLiczbaGraczy(scanner.nextInt());
        if(stol.getLiczbaGraczy() > stol.getLiczbaMiejsc())
            stol.setLiczbaGraczy(stol.getLiczbaMiejsc());
        else if(stol.getLiczbaGraczy() < 2)
            stol.setLiczbaGraczy(2);
        System.out.printf("Liczba graczy: %d\n", stol.getLiczbaGraczy());

        for(int i = 0; i < stol.getLiczbaGraczy(); i++) {
            System.out.printf("Podaj nickname gracza nr %d: ", i + 1);
            Gracz gracz = new Gracz(scanner.next(), 10000, i);
            stol.getGracze().put(i, gracz);
        }
        WyswietlGraczy(stol);
    }

    private void WyswietlGraczy(Stol stol) {
        for(int i = 0; i < stol.getLiczbaGraczy(); i++) System.out.printf("%s\t\t\t", stol.getGracze(i).getNickname()); System.out.println();
        for(int i = 0; i < stol.getLiczbaGraczy(); i++) {
            int zetony = stol.getGracze(i).getZetony() - stol.getGracze(i).getZaklad();
            System.out.printf("%d\t\t\t", zetony);
        }
        System.out.println();
    }

    public void Rozdaj(Stol stol, Talia talia, Random random, Scanner scanner) {
        stol.WyczyscStol();
        stol.PrzesunButton();
        talia.Tasuj();
        stol.PobierzAnte();
        WyswietlGraczy(stol); //TEST
        stol.PobierzCiemne();
        WyswietlGraczy(stol); //TEST
        stol.LosujKarty(random, talia);
        for(int i = 0; i < stol.getLiczbaGraczy(); i++)
            System.out.printf("%s%s\t\t\t", stol.getGracze(i).getKarta1().getNazwa(), stol.getGracze(i).getKarta2().getNazwa());
        System.out.println();
        if(stol.getStackiNormalne() == stol.getLiczbaGraczy() - 1) {		//BEZ ROZGRYWKI PREFLOP (GŁÓWNIE HU)
            Dostosuj(stol);
            WyswietlGraczy(stol); //TEST
            stol.LosujFlop(random);
            stol.LosujTurn(random);
            stol.LosujRiver(random);
        }
        else {												//ROZGRYWKA PREFLOP
            WyswietlGraczy(stol); //TEST
            System.out.println("Preflop"); //TEST
            Preflop(stol, scanner);
            WyswietlGraczy(stol); //TEST
            System.out.println("Dostosuj"); //TEST
            Dostosuj(stol);
            WyswietlGraczy(stol); //TEST
            if(stol.getFoldy() == stol.getLiczbaGraczy() - 1) {                //WSZYSCY SPASOWALI PREFLOP
                System.out.println("RozdzielFoldy"); //TEST
                RozdzielFoldy(stol);
            }
            else if(stol.getFoldy() + stol.getStackiNormalne() == stol.getLiczbaGraczy() - 1) {	//SHOWDOWN (ALL-IN) PREFLOP
                System.out.println("LosujFlop"); //TEST
                stol.LosujFlop(random);
                System.out.println("LosujTurn"); //TEST
                stol.LosujTurn(random);
                System.out.println("LosujRiver"); //TEST
                stol.LosujRiver(random);
                System.out.printf("%s %s %s\t%s\t%s\n", stol.getFlop1().getNazwa(), stol.getFlop2().getNazwa(), stol.getFlop3().getNazwa(), stol.getTurn().getNazwa(),
                        stol.getRiver().getNazwa()); //TEST
                System.out.println("SprawdzUklady"); //TEST
                SprawdzUklady(stol);
                System.out.println("RozdzielPule"); //TEST
                RozdzielPule(stol);
            }
            else {											//ROZGRYWKA NA FLOPIE
                stol.LosujFlop(random);
                Postflop(stol, scanner, FLOP);
                Dostosuj(stol);
                if(stol.getFoldy() == stol.getLiczbaGraczy() - 1)			//WSZYSCY SPASOWALI NA FLOPIE
                    RozdzielFoldy(stol);
                else if(stol.getFoldy() + stol.getStackiNormalne() == stol.getLiczbaGraczy() - 1) {	//SHOWDOWN (ALL-IN) NA FLOPIE
                    stol.LosujTurn(random);
                    stol.LosujRiver(random);
                    SprawdzUklady(stol);
                    RozdzielPule(stol);
                }
                else {										//ROZGRYWKA NA TURNIE
                    stol.LosujTurn(random);
                    Postflop(stol, scanner, TURN);
                    Dostosuj(stol);
                    if(stol.getFoldy() == stol.getLiczbaGraczy() - 1)		//WSZYSCY SPASOWALI NA TURNIE
                        RozdzielFoldy(stol);
                    else if(stol.getFoldy() + stol.getStackiNormalne() == stol.getLiczbaGraczy() - 1) {	//SHOWDOWN (ALL-IN) NA TURNIE
                        stol.LosujRiver(random);
                        SprawdzUklady(stol);
                        RozdzielPule(stol);
                    }
                    else {									//ROZGRYWKA NA RIVERZE
                        stol.LosujRiver(random);
                        Postflop(stol, scanner, RIVER);
                        Dostosuj(stol);
                        if(stol.getFoldy() == stol.getLiczbaGraczy() - 1)	//WSZYSCY SPASOWALI NA RIVERZE
                            RozdzielFoldy(stol);
                        else {								//SHOWDOWN NA RIVERZE
                            SprawdzUklady(stol);
                            RozdzielPule(stol);
                        }
                    }
                }
            }
        }
    }

    private void Dostosuj(Stol stol) {
        stol.sortujStacki();
        int Suma = 0;
        for(int i = stol.getStackiAnte(); i < stol.getStacki().size(); i++) {
            int k = 0;
            for(int j = 0; j < stol.getLiczbaGraczy(); j++) {
                if(stol.getGracze(j).getZaklad() + stol.getAnte() <= stol.getStacki(i))
                    k += stol.getGracze(j).getZaklad() + stol.getAnte();
            }
            k -= Suma;
            stol.getPule().add(k);
            Suma += k;
        }
        stol.setMaxBet(0);
        //// <--- Jesli gracz wsunal all-ina, a ma wiekszy stack od pozostalych stakujacych sie graczy ---> ////
        if(stol.getStackiNormalne() > 1 && stol.getFoldy() + stol.getStackiNormalne() == stol.getLiczbaGraczy()) {
            for(int i = 0; i < stol.getLiczbaGraczy(); i++) {
                if(stol.getGracze(i).getZetony() + stol.getAnte() > stol.getStacki(stol.getStacki().size() - 1))
                    stol.getGracze(i).setZaklad(stol.getStacki(stol.getStacki().size() - 1) - stol.getAnte());
            }
        }
    }

    private void Preflop(Stol stol, Scanner scanner) {
        int ruch;
        if(stol.getLiczbaGraczy() == 2)
            ruch = stol.getButton();
        else
            ruch = (stol.getButton() + 3) % stol.getLiczbaGraczy();
        while(stol.getFoldy() + stol.getStackiNormalne() < stol.getLiczbaGraczy() - 1 && stol.getNrRaise() < stol.getLiczbaGraczy() - 1) {
            if(stol.getGracze(ruch).isCzyGra() && stol.getGracze(ruch).getZetony() > stol.getGracze(ruch).getZaklad()) {
                // === WSZYSCY ZLIMPOWALI DO GRACZA NA BB === //
                if(stol.getGracze(ruch).getZaklady(0) == stol.getMaxBet()) {
                    System.out.printf("Gracz %s: 1 - raise, 2 - check).\n", stol.getGracze(ruch).getNickname());
                    String k = scanner.next();
                    if(k.equals("1")) {
                        System.out.print("Podaj kwote zakladu.");
                        int b;
                        if(stol.getGracze(ruch).getZetony() - stol.getMaxBet() < stol.getBB())
                            b = stol.getGracze(ruch).getZetony() - stol.getMaxBet();

                        else {
                            b = scanner.nextInt();
                            if(b < stol.getMaxBet() + stol.getBB())
                                b = stol.getMaxBet() + stol.getBB();
                            else if(b > stol.getGracze(ruch).getZetony())
                                b = stol.getGracze(ruch).getZetony();
                        }


                        stol.addPula(b - stol.getGracze(ruch).getZaklady(0));
                        stol.getGracze(ruch).addZaklady(0, b);
                        if(stol.getGracze(ruch).getZaklad() == stol.getGracze(ruch).getZetony()) {
                            stol.plusStackiNormalne();
                            stol.getStacki().add(stol.getGracze(ruch).getZetony() + stol.getAnte());
                        }

                        stol.setNrRaise(0);
                    }
                    else if(k.equals("2")) {
                        stol.setNrRaise(0);
                        break;
                    }
                }

                // === BADANA FUNCKJA ===> //
                // === JEST MOŻLIWOSC RAISU === //
                else if(stol.getGracze(ruch).getZaklady(0) < stol.getMaxBet()) {
                    System.out.printf("Gracz %s: 1 - raise, 2 - call, 3 - fold).\n", stol.getGracze(ruch).getNickname());
                    String wybor = scanner.next();
                    switch (wybor) {

                        // == RAISE == //
                        case "1":
                            int b;
                            // = JESLI GRACZ MA W STACKU MNIEJ NIŻ 1BB WIECEJ NIZ MAXBET = //
                            if (stol.getGracze(ruch).getZaklady(0) + stol.getGracze(ruch).getZetony() - stol.getMaxBet() < stol.getBB())
                                b = stol.getGracze(ruch).getZaklady(0) + stol.getGracze(ruch).getZetony() - stol.getMaxBet();
                            else {
                                b = scanner.nextInt();
                                if (b < stol.getMaxBet() + stol.getBB())
                                    b = stol.getMaxBet() + stol.getBB();
                                else if (b > stol.getGracze(ruch).getZaklady(0) + stol.getGracze(ruch).getZetony())
                                    b = stol.getGracze(ruch).getZaklady(0) + stol.getGracze(ruch).getZetony();
                            }

                            stol.addPula(b - stol.getGracze(ruch).getZaklady(0));
                            stol.getGracze(ruch).setZaklady(0, b);

                            stol.setNrRaise(0);
                            stol.setMaxBet(b);
                            /////---TEST-->///
                            System.out.println("ZAKLAD GRACZA " + stol.getGracze(ruch).getNickname() + ": " + stol.getGracze(ruch).getZaklady(0));
                            System.out.println("MAXBET: " + stol.getMaxBet());
                            for (int bet : stol.getStacki())
                                System.out.println("STACK " + bet);
                            System.out.println("PULA: " + stol.getPula());
                            /////<--TEST---///
                            break;

                        // == CALL == //
                        case "2":
                            if (stol.getGracze(ruch).getZetony() <= stol.getMaxBet()) {
                                stol.addPula(stol.getMaxBet() - stol.getGracze(ruch).getZaklady(0));
                                stol.getGracze(ruch).addZaklady(0, stol.getGracze(ruch).getZetony());
                                stol.plusStackiNormalne();
                                stol.getStacki().add(stol.getGracze(ruch).getZaklady(0) + stol.getAnte());
                            } else {
                                stol.addPula(stol.getMaxBet() - stol.getGracze(ruch).getZaklady(0));
                                stol.getGracze(ruch).setZaklady(0, stol.getMaxBet());
                            }
                            stol.plusNrRaise();
                            /////---TEST-->///
                            System.out.println("ZAKLAD GRACZA " + stol.getGracze(ruch).getNickname() + ": " + stol.getGracze(ruch).getZaklady(0));
                            System.out.println("MAXBET: " + stol.getMaxBet());
                            for (int bet : stol.getStacki())
                                System.out.println("STACK " + bet);
                            System.out.println("PULA: " + stol.getPula());
                            /////<--TEST---///
                            break;


                        // == FOLD == //
                        default:
                            stol.getGracze(ruch).setCzyGra(false);
                            stol.plusFoldy();
                            stol.plusNrRaise();
                            /////---TEST-->///
                            System.out.println("ZAKLAD GRACZA " + stol.getGracze(ruch).getNickname() + ": " + stol.getGracze(ruch).getZaklady(0));
                            System.out.println("MAXBET: " + stol.getMaxBet());
                            for (int bet : stol.getStacki())
                                System.out.println("STACK " + bet);
                            System.out.println("PULA: " + stol.getPula());
                            /////<--TEST---///
                            break;
                    }
                }
                // <=== BADANA FUNCKJA === //

                stol.getGracze(ruch).getZaklad();
            }
            ruch = (ruch + 1) % stol.getLiczbaGraczy();
        }
        System.out.println("Koniec Preflop");
    }

    private void Postflop(Stol stol, Scanner scanner, int tura) {
        int ruch = (stol.getButton() + 1) % stol.getLiczbaGraczy();
        while(stol.getFoldy() + stol.getStackiNormalne() < stol.getLiczbaGraczy() - 1 && stol.getNrRaise() < stol.getLiczbaGraczy() - 1) {
            if(stol.getGracze(ruch).isCzyGra() && stol.getGracze(ruch).getZetony() > stol.getGracze(ruch).getZaklad()) {
                if(stol.getGracze(ruch).getZaklady(tura) == 0) {
                    System.out.printf("Gracz %s: 1 - bet, 2 - check).\n", stol.getGracze(ruch).getNickname());
                    int k = scanner.nextInt();
                    if(k == 1) {
                        int b = 0;
                        if(stol.getGracze(ruch).getZetony() - stol.getGracze(ruch).DodalDoPuli(tura) <= stol.getBB())
                            b = stol.getGracze(ruch).getZetony() - stol.getGracze(ruch).DodalDoPuli(tura);

                        else {
                            System.out.print("Podaj kwote zakladu.");
                            while(b < stol.getBB() || b > stol.getGracze(ruch).getZetony() - stol.getGracze(ruch).DodalDoPuli(tura))
                                b = scanner.nextInt();
                        }

                        stol.addPula(b);
                        stol.getGracze(ruch).setZaklady(tura, b);

                        stol.setMaxBet(stol.getGracze(ruch).getZaklady(tura));
                        stol.setNrRaise(0);
                    }
                    else if(k == 2)
                        stol.plusNrRaise();
                }
                else if(stol.getGracze(ruch).getZaklady(tura) < stol.getMaxBet()) {
                    System.out.printf("Gracz %s: 1 - raise, 2 - call, 3 - fold).\n", stol.getGracze(ruch).getNickname());
                    int k = scanner.nextInt();
                    if(k == 1) {
                        int b = 0;
                        if(stol.getGracze(ruch).getZetony() - stol.getGracze(ruch).DodalDoPuli(tura) <= 2 * stol.getMaxBet())
                            b = stol.getGracze(ruch).getZetony() - stol.getGracze(ruch).DodalDoPuli(tura);
                        else while(b < 2 * stol.getMaxBet() || b > stol.getGracze(ruch).getZetony() - stol.getGracze(ruch).DodalDoPuli(tura))
                            b = scanner.nextInt();

                        stol.addPula(b - stol.getGracze(ruch).getZaklady(tura));
                        stol.getGracze(ruch).setZaklady(tura, b);

                        stol.setNrRaise(0);
                        stol.setMaxBet(b);
                    }
                    else if(k == 2) {
                        if(stol.getGracze(ruch).getZetony() - stol.getGracze(ruch).getZaklad() < stol.getMaxBet()) {
                            stol.addPula(stol.getMaxBet() - stol.getGracze(ruch).getZaklady(tura));
                            stol.getGracze(ruch).addZaklady(tura, stol.getGracze(ruch).getZetony());
                        }
                        else {
                            stol.addPula(stol.getMaxBet() - stol.getGracze(ruch).getZaklady(tura));
                            stol.getGracze(ruch).setZaklady(tura, stol.getMaxBet());
                        }
                        stol.plusNrRaise();
                    }
                    else {
                        stol.getGracze(ruch).setCzyGra(false);
                        stol.plusNrRaise();
                    }
                }
                stol.getGracze(ruch).getZaklad();
                if(stol.getGracze(ruch).getZetony() == stol.getGracze(ruch).getZaklad()) {
                    stol.plusStackiNormalne();
                    stol.getStacki().add(stol.getGracze(ruch).getZetony() + stol.getAnte());
                }
            }
            else
                stol.plusNrRaise();

            ruch = (ruch + 1) % stol.getLiczbaGraczy();
        }
    }

    private void RozdzielFoldy(Stol stol) {
        for(int i = 0; i < stol.getLiczbaGraczy(); i++) {
            stol.getGracze(i).substractZetony(stol.getGracze(i).getZaklad()) ;
            if(stol.getGracze(i).isCzyGra())
                stol.getGracze(i).substractZetony(stol.getPula());
        }
        WyswietlGraczy(stol);
    }

    private void SprawdzUklady(Stol stol) {
        System.out.println("a");
        for(int i = 0; i < stol.getLiczbaGraczy(); i++) {
            System.out.println("b");
            if(stol.getGracze(i).isCzyGra()) {
                System.out.println("c");
                stol.getGracze(i).setUklad(stol);
                System.out.println(stol.getGracze(i).getUklad().getWszystkieKarty().get(0).getWartosc());
                stol.getGracze(i).getUklad().sprawdzUklad();
            }
        }
    }

    private void RozdzielPule(Stol stol) {
        for(int i = 0; i < stol.getLiczbaGraczy(); i++)
            stol.getGracze(i).substractZetony(stol.getGracze(i).getZaklad());
        System.out.println("ST SIZE" + stol.getStacki().size());
        for(int k = stol.getPule().size() - 1; k >= 0; k--) {
            ArrayList<Gracz> Najlepsi = new ArrayList<>();
            int MaxPts = 0;
            System.out.println("ST" + stol.getStacki(k));
            for(int i = 0; i < stol.getLiczbaGraczy(); i++) {
                if(stol.getGracze(i).isCzyGra() && stol.getGracze(i).getZaklad() >= stol.getStacki(k))
                    if(stol.getGracze(i).getUklad().getPunkty() > MaxPts) {
                        Najlepsi.clear();
                        MaxPts = stol.getGracze(i).getUklad().getPunkty();
                        Najlepsi.add(stol.getGracze(i));
                    }
                    else if(stol.getGracze(i).getUklad().getPunkty() == MaxPts)
                        Najlepsi.add(stol.getGracze(i));
                System.out.println("MAX" + MaxPts + "MAX" + stol.getGracze(i).getUklad().getPunkty());
            }
            StringBuilder strPula = new StringBuilder();
            System.out.println("NAJ!!!" + Najlepsi.size());
            if(Najlepsi.size() == 1) {
                Najlepsi.get(0).addZetony(stol.getPule().get(k));

                strPula.append(Najlepsi.get(0))
                        .append(" wins a side pot number ")
                        .append(stol.getPule().get(k))
                        .append(" with a ")
                        .append(Najlepsi.get(0).getUklad().getNazwa())
                        .append(".");
            }
            else {
                strPula.append("A side pot number ")
                        .append(stol.getPule().get(k))
                        .append(" is won by: ");
                for(int i = 0; i < Najlepsi.size(); i++) {
                    Najlepsi.get(i).addZetony(stol.getPule().get(k) / Najlepsi.size());

                    strPula.append(stol.getGracze(i).getNickname());
                    if(i < Najlepsi.size() - 1)
                        strPula.append(", ");
                    else
                        strPula.append(" ");
                }
                strPula.append(" with a ")
                        .append(Najlepsi.get(0).getUklad().getNazwa())
                        .append(".");

                int t = Najlepsi.size() * (stol.getPule().get(k) / Najlepsi.size());
                int q = stol.getPule().get(k) - t;
                int i = 0;
                while(q > 0) {
                    Najlepsi.get(i).plusZetony();
                    i++;
                    q--;
                }
            }
        }
        WyswietlGraczy(stol);
    }
}
