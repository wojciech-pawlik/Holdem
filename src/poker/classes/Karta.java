package poker.classes;

public class Karta {
    private int Wartosc; //2 -> 1, ..., T -> 9, J -> 10, Q -> 11, K -> 12, A -> 13//    // LICZĘ OD 1, bo A może przyjąć dwie wartości: 0 i 13, domyślnie ma 13
    private int Kolor; //kier -> 0, karo -> 1, pik -> 2, trefl -> 3//
    private boolean Uzyta;
    private String NazwaWartosci;
    private String DlugaNazwaWartosci;
    private String NazwaKoloru;
    private String DlugaNazwaKoloru;
    private String Nazwa;
    private String DlugaNazwa;

    public Karta() {
        this(0, 0);
    }

    public Karta(int Kolor, int Wartosc) {
        this.Wartosc = Wartosc;
        this.Kolor = Kolor;
        Uzyta = false;
        UstawWartosc();
        UstawKolor();
        UstawNazwe();
    }

    private void UstawWartosc() {
        if(Wartosc < 9) NazwaWartosci = "" + (Wartosc + 1);
        else if(Wartosc == 9) {
            NazwaWartosci = "T";
            DlugaNazwaWartosci = "Ten";
        }
        else if(Wartosc == 10) {
            NazwaWartosci = "J";
            DlugaNazwaWartosci = "Jack";
        }
        else if(Wartosc == 11) {
            NazwaWartosci = "Q";
            DlugaNazwaWartosci = "Queen";
        }
        else if(Wartosc == 12) {
            NazwaWartosci = "K";
            DlugaNazwaWartosci = "King";
        }
        else if(Wartosc == 13) {
            NazwaWartosci = "A";
            DlugaNazwaWartosci = "Ace";
        }
        else NazwaWartosci = "";
        if(Wartosc == 1) DlugaNazwaWartosci = "Two";
        else if(Wartosc == 2) DlugaNazwaWartosci = "Three";
        else if(Wartosc == 3) DlugaNazwaWartosci = "Four";
        else if(Wartosc == 4) DlugaNazwaWartosci = "Five";
        else if(Wartosc == 5) DlugaNazwaWartosci = "Six";
        else if(Wartosc == 6) DlugaNazwaWartosci = "Seven";
        else if(Wartosc == 7) DlugaNazwaWartosci = "Eight";
        else if(Wartosc == 8) DlugaNazwaWartosci = "Nine";
    }

    private void UstawKolor() {
        if(Kolor == 0) {
            NazwaKoloru = "h";
            DlugaNazwaKoloru = "of hearts";
        }
        else if(Kolor == 1) {
            NazwaKoloru = "d";
            DlugaNazwaKoloru = "of diamonds";
        }
        else if(Kolor == 2) {
            NazwaKoloru = "s";
            DlugaNazwaKoloru = "of spades";
        }
        else if(Kolor == 3) {
            NazwaKoloru = "c";
            DlugaNazwaKoloru = "of clubs";
        }
        else NazwaKoloru = "";
    }

    private void UstawNazwe() {
        if(NazwaWartosci.equals("") || NazwaKoloru.equals("")) Nazwa = DlugaNazwa = "";
        else {
            Nazwa = NazwaWartosci + NazwaKoloru;
            DlugaNazwa = DlugaNazwaWartosci + " " + DlugaNazwaKoloru;
        }
    }

    /* ==== GETTERS AND SETTERS ==== */

    public int getWartosc() {
        return Wartosc;
    }

    @SuppressWarnings("unused")
    public void setWartosc(int Wartosc) {
        this.Wartosc = Wartosc;
    }

    public int getKolor() {
        return Kolor;
    }

    @SuppressWarnings("unused")
    public void setKolor(int Kolor) {
        this.Kolor = Kolor;
    }

    public boolean isUzyta() {
        return Uzyta;
    }

    public void setCzyUzyta(boolean Uzyta) {
        this.Uzyta = Uzyta;
    }

    @SuppressWarnings("unused")
    public String getNazwaWartosci() {
        return NazwaWartosci;
    }

    @SuppressWarnings("unused")
    public void setNazwaWartosci(String NazwaWartosci) {
        this.NazwaWartosci = NazwaWartosci;
    }

    public String getDlugaNazwaWartosci() {
        return DlugaNazwaWartosci;
    }

    @SuppressWarnings("unused")
    public void setDlugaNazwaWartosci(String DlugaNazwaWartosci) {
        this.DlugaNazwaWartosci = DlugaNazwaWartosci;
    }

    @SuppressWarnings("unused")
    public String getNazwaKoloru() {
        return NazwaKoloru;
    }

    @SuppressWarnings("unused")
    public void setNazwaKoloru(String NazwaKoloru) {
        this.NazwaKoloru = NazwaKoloru;
    }

    @SuppressWarnings("unused")
    public String getDlugaNazwaKoloru() {
        return DlugaNazwaKoloru;
    }

    @SuppressWarnings("unused")
    public void setDlugaNazwaKoloru(String DlugaNazwaKoloru) {
        this.DlugaNazwaKoloru = DlugaNazwaKoloru;
    }

    public String getNazwa() {
        return Nazwa;
    }

    @SuppressWarnings("unused")
    public void setNazwa(String Nazwa) {
        this.Nazwa = Nazwa;
    }

    @SuppressWarnings("unused")
    public String getDlugaNazwa() {
        return DlugaNazwa;
    }

    @SuppressWarnings("unused")
    public void setDlugaNazwa(String DlugaNazwa) {
        this.DlugaNazwa = DlugaNazwa;
    }

    @Override
    public String toString() {
        return DlugaNazwa;
    }
}
