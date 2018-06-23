package poker.classes;

public class Gracz {
    private String Nickname;
    private Karta karta1, karta2;
    private Uklad uklad;
    private int Zetony, Miejsce, Zaklad;
    private boolean CzyGra;
    private int[] Zaklady = new int[4];

    @SuppressWarnings("unused")
    public Gracz() {
        this("", 0, 0);
    }

    Gracz(String Nickname, int Zetony, int Miejsce) {
        this.Nickname = Nickname;
        karta1 = karta2 = new Karta();
        uklad = new Uklad();
        this.Zetony = Zetony;
        this.Miejsce = Miejsce;
        Zaklad = 0;
        Zaklady[0] = Zaklady[1] = Zaklady[2] = Zaklady[3] = 0;
        CzyGra = true;
    }

    void Zaklad() {
        Zaklad = Zaklady[0] + Zaklady[1] + Zaklady[2] + Zaklady[3];
    }

    int DodalDoPuli(int tura) {
        int suma = 0;
        for(int i = 0; i < tura; i++)
            suma += Zaklady[i];
        return suma;
    }

    /* ==== GETTERS AND SETTERS ==== */

    String getNickname() {
        return Nickname;
    }

    @SuppressWarnings("unused")
    public void setNickname(String Nickname) {
        this.Nickname = Nickname;
    }

    Karta getKarta1() {
        return karta1;
    }

    void setKarta1(Karta karta1) {
        this.karta1 = karta1;
    }

    Karta getKarta2() {
        return karta2;
    }

    void setKarta2(Karta karta2) {
        this.karta2 = karta2;
    }

    Uklad getUklad() {
        return uklad;
    }

    @SuppressWarnings("unused")
    public void setUklad(Uklad uklad) {
        this.uklad = uklad;
    }

    void setUklad(Stol stol) {
        this.uklad = new Uklad(this, stol);
    }

    int getZetony() {
        return Zetony;
    }

    void setZetony() {
        this.setZetony(0);
    }

    void setZetony(int Zetony) {
        this.Zetony = Zetony;
    }

    @SuppressWarnings("unused")
    public int getMiejsce() {
        return Miejsce;
    }

    @SuppressWarnings("unused")
    public void setMiejsce(int Miejsce) {
        this.Miejsce = Miejsce;
    }

    int getZaklad() {
        return Zaklad;
    }

    @SuppressWarnings("unused")
    void setZaklad(int Zaklad) {
        this.Zaklad = Zaklad;
    }

    boolean isCzyGra() {
        return CzyGra;
    }

    void setCzyGra(boolean CzyGra) {
        this.CzyGra = CzyGra;
    }

    //ZAKLADY//

    @SuppressWarnings("unused")
    int getZaklady() {
        int suma = 0;
        for(int i = 0; i < 4; i++) suma += Zaklady[i];
        return suma;
    }

    int getZaklady(int Tura) {
        return Zaklady[Tura];
    }

    @SuppressWarnings("unused")
    void setZaklady(int[] Zaklady) {
        this.Zaklady = Zaklady;
    }

    void setZaklady(int Tura, int Wartosc) {
        this.Zaklady[Tura] = Wartosc;
    }

    void addZaklady(int Tura, int Wartosc) {
        this.Zaklady[Tura] += Wartosc;
    }

    /////////////////////////////////////////////////////////////////////////////////

    void addZetony(int Zetony) {
        this.Zetony += Zetony;
    }

    void substractZetony(int Zetony) {
        this.Zetony -= Zetony;
    }

    void plusZetony() {
        this.Zetony++;
    }
}
