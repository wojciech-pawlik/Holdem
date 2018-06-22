package poker.classes;

import java.util.HashMap;
import java.util.Random;

public class Talia {
    private HashMap<Integer, Karta> Karty;

    public Talia() {
        Karty = new HashMap<>();
        int key = 0;
        for(int kolor = 0; kolor < 4; kolor++)
            for(int wartosc = 1; wartosc <= 13; wartosc++) {
                Karta karta = new Karta(kolor, wartosc);
                Karty.put(key++, karta);
            }
    }

    public void Tasuj() {
        for(int i = 0; i < 52; i++) Karty.get(i).setCzyUzyta(false);
    }

    public Karta LosujKarte(Random random) {
        int numer;
        while(true) {
            numer = random.nextInt(52);
            if(!Karty.get(numer).isUzyta()) {
                Karty.get(numer).setCzyUzyta(true);
                break;
            }
        }
        return Karty.get(numer);
    }

    /* ==== GETTERS AND SETTERS ==== */

    @SuppressWarnings("unused")
    public HashMap<Integer, Karta> getKarty() {
        return Karty;
    }

    @SuppressWarnings("unused")
    public void setKarty(HashMap<Integer, Karta> Karty) {
        this.Karty = Karty;
    }
}
