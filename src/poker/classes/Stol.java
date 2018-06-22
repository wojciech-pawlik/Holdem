package poker.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class Stol {
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
    }

    public void LosujKarty(Random random, Talia talia) {
        for(int i = 0; i < LiczbaGraczy; i++) {
            gracze.get(i).setKarta1(talia.LosujKarte(random));
            gracze.get(i).setKarta2(talia.LosujKarte(random));
        }
    }

    public void LosujFlop(Random random) {
        flop1 = talia.LosujKarte(random);
        flop2 = talia.LosujKarte(random);
        flop3 = talia.LosujKarte(random);
    }

    public void LosujTurn(Random random)
    {
        turn = talia.LosujKarte(random);
    }

    public void LosujRiver(Random random)
    {
        river = talia.LosujKarte(random);
    }

    public void WyczyscStol()
    {
        flop1 = flop2 = flop3 = turn = river = new Karta();
        Pula = 0;
        for(int i = 0; i < LiczbaGraczy; i++) gracze.get(i).setCzyGra(true);
        pule.clear();
        stacki.clear();
        StackiNormalne = StackiAnte = Foldy = 0;
    }

    public void PrzesunButton() {
        Button = (Button + 1) % LiczbaGraczy;
    }

    public void PobierzAnte() {
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

    public void PobierzCiemne() {
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

    /* ==== GETTERS AND SETTERS ==== */

    //TALIE//

    @SuppressWarnings("unused")
    public Talia getTalia() {
        return talia;
    }

    @SuppressWarnings("unused")
    public void setTalia(Talia talia) {
        this.talia = talia;
    }

    //KARTY//

    public Karta getFlop1() {
        return flop1;
    }

    public Karta getFlop2() {
        return flop2;
    }

    public Karta getFlop3() {
        return flop3;
    }

    public Karta getTurn() {
        return turn;
    }

    public Karta getRiver() {
        return river;
    }

    //INTY//

    public int getPula() {
        return Pula;
    }

    public void addPula(int Pula) {
        this.Pula += Pula;
    }

    public int getLiczbaGraczy() {
        return LiczbaGraczy;
    }

    public void setLiczbaGraczy(int LiczbaGraczy) {
        this.LiczbaGraczy = LiczbaGraczy;
    }

    public int getLiczbaMiejsc() {
        return LiczbaMiejsc;
    }

    @SuppressWarnings("unused")
    public void setLiczbaMiejsc(int LiczbaMiejsc) {
        this.LiczbaMiejsc = LiczbaMiejsc;
    }


    public int getButton() {
        return Button;
    }

    @SuppressWarnings("unused")
    public void setButton(int Button) {
        this.Button = Button;
    }

    public int getBB() {
        return BB;
    }

    @SuppressWarnings("unused")
    public void setBB(int BB) {
        this.BB = BB;
    }

    public int getAnte() {
        return Ante;
    }

    @SuppressWarnings("unused")
    public void setAnte(int Ante) {
        this.Ante = Ante;
    }


    public int getMaxBet() {
        return MaxBet;
    }

    public void setMaxBet(int MaxBet) {
        this.MaxBet = MaxBet;
    }

    public void addMaxBet(int Wartosc) {
        this.MaxBet += Wartosc;
    }

    public int getNrRaise() {
        return NrRaise;
    }

    public void setNrRaise(int NrRaise) {
        this.NrRaise = NrRaise;
    }

    public void plusNrRaise() {
        this.NrRaise++;
    }


    public int getFoldy() {
        return Foldy;
    }

    @SuppressWarnings("unused")
    public void setFoldy(int Foldy) {
        this.Foldy = Foldy;
    }

    public void plusFoldy() {
        this.Foldy++;
    }

    public int getStackiNormalne() {
        return this.StackiNormalne;
    }

    public void plusStackiNormalne() {
        this.StackiNormalne++;
    }

    public int getStackiAnte() {
        return StackiAnte;
    }

    //LISTY I MAPY//

    public int getStacki(int index) {
        if(index >= 0) return stacki.get(index);
        return -1;
    }

    public ArrayList<Integer> getStacki() {
        return this.stacki;
    }

    public HashMap<Integer, Gracz> getGracze() {
        return gracze;
    }

    public Gracz getGracze(int Numer) {
        return gracze.get(Numer);
    }

    public ArrayList<Integer> getPule() {
        return pule;
    }


    /* ==== SORTOWANIE ==== */

    public void sortujStacki() {
        Collections.sort(stacki);
    }
}
