package poker.classes;

import java.util.HashMap;

public class Uklad {
    private int Punkty;
    private String Nazwa;
    private int MaxKolor;				//Royal Flush, Straight Flush, Flush
    private int NrKolor;				//Royal Flush, Straight Flush, Flush
    private int MaxWartosc;				//Quads, Trips, Full House, Two Pair, One Pair
    private int NrWartosc;				//Quads, Trips, Full House, Two Pair, One Pair
    private int MaxWartosc2;			//Full House, Two Pair
    private int NrWartosc2;				//Full House, Two Pair
    private HashMap<Integer, Karta> NajlepszyUklad;	//Najlepszy 5-kartowy uklad
    private HashMap<Integer, Karta> WszystkieKarty;	//Wszystkie karty, z ktorych stworzony moze byc uklad

    public Uklad() {
        Punkty = 0;
        Nazwa = "";
        MaxKolor = 0;
        NrKolor = 0;
        MaxWartosc = 0;
        NrWartosc = 0;
        MaxWartosc2 = 0;
        NrWartosc2 = 0;
        NajlepszyUklad = new HashMap<>();
        WszystkieKarty = new HashMap<>();
    }

    @SuppressWarnings("unused")
    public Uklad(Gracz gracz, Stol stol) {
        this();
        WszystkieKarty.put(0, gracz.getKarta1());
        WszystkieKarty.put(1, gracz.getKarta2());
        WszystkieKarty.put(2, stol.getFlop1());
        WszystkieKarty.put(3, stol.getFlop2());
        WszystkieKarty.put(4, stol.getFlop3());
        WszystkieKarty.put(5, stol.getTurn());
        WszystkieKarty.put(6, stol.getRiver());

        ileKolor();
        MaxIloscWartosci();
        MaxIloscWartosci2();

        sprawdzUklad();
    }


    /* ==== FUNKCJE POMOCNICZE DO SPRAWDZANIA UKLADU ==== */


    private void ileKolor() {
        for(int i = 0; i < 4; i++) {
            int licznik = 0;
            for(int j = 0; j < 7; j++)
                if(WszystkieKarty.get(j).getKolor() == i) licznik++;
            if(licznik > MaxKolor) {
                MaxKolor = licznik;
                NrKolor = i;
            }
        }
    }

    private int WartoscKoloru() {
        int wartosc = 0;
        if(MaxKolor == 5) {
            for(int i = 0; i < 7; i++)
                if(WszystkieKarty.get(i).getKolor() == NrKolor)
                    wartosc += WszystkieKarty.get(i).getWartosc();
        }

        else if(MaxKolor == 6) {
            int index = 0;
            int min = 14;
            for(int j = 0; j < 7; j++)
                if(WszystkieKarty.get(j).getKolor() == NrKolor) {
                    wartosc += WszystkieKarty.get(j).getWartosc();
                    if(WszystkieKarty.get(j).getWartosc() < min) {
                        min = WszystkieKarty.get(j).getWartosc();
                        index = j;
                    }
                }
            wartosc -= WszystkieKarty.get(index).getWartosc();
        }

        else if(MaxKolor == 7) {
            int i1 = 0;
            int i2 = 0;
            int min1 = 14;
            int min2 = 15;
            for(int k = 0; k < 7; k++) {
                wartosc += WszystkieKarty.get(k).getWartosc();
                if(WszystkieKarty.get(k).getWartosc() < min1) {
                    min2 = min1;
                    i2 = i1;
                    min1 = WszystkieKarty.get(k).getWartosc();
                    i1 = k;
                }
                else if(WszystkieKarty.get(k).getWartosc() < min2) {
                    min2 = WszystkieKarty.get(k).getWartosc();
                    i2 = k;
                }
            }
            wartosc -= WszystkieKarty.get(i1).getWartosc() + WszystkieKarty.get(i2).getWartosc();
        }
        return wartosc;
    }

    @SuppressWarnings({"unused", "Duplicates"})
    private boolean CzyPoker() {
        System.out.println(WszystkieKarty.get(0).getWartosc());
        if(MaxKolor == 5) {
            int min = 14;
            int max = 0;
            for(int i = 0; i < 7; i++)
                if(WszystkieKarty.get(i).getKolor() == NrKolor) {
                    if(WszystkieKarty.get(i).getWartosc() % 13 < min) min = WszystkieKarty.get(i).getWartosc() % 13;
                    if(WszystkieKarty.get(i).getWartosc() % 13 > max) max = WszystkieKarty.get(i).getWartosc() % 13;
                }
            return max - min == 4;
        }
        else if(MaxKolor == 6) {
            int min1 = 14;
            int min2 = 15;
            int max1 = 0;
            int max2 = -1;
            for(int i = 0; i < 7; i++)
                if(WszystkieKarty.get(i).getKolor() == NrKolor) {
                    if(WszystkieKarty.get(i).getWartosc() % 13 < min1) {
                        min2 = min1;
                        min1 = WszystkieKarty.get(i).getWartosc() % 13;
                    }
                    else if(WszystkieKarty.get(i).getWartosc() % 13 < min2)
                        min2 = WszystkieKarty.get(i).getWartosc() % 13;
                    if(WszystkieKarty.get(i).getWartosc() % 13 > max1) {
                        max2 = max1;
                        max1 = WszystkieKarty.get(i).getWartosc() % 13;
                    }
                    else if(WszystkieKarty.get(i).getWartosc() % 13 > max2)
                        max2 = WszystkieKarty.get(i).getWartosc() % 13;
                }
            return max1 - min2 == 4 || max2 - min1 == 4;
        }
        else if(MaxKolor == 7) {
            int mini1 = 14;
            int mini2 = 15;
            int mini3 = 16;
            int maxi1 = 0;
            int maxi2 = -1;
            int maxi3 = -2;
            for(int i = 0; i < 7; i++) {
                if(WszystkieKarty.get(i).getWartosc() % 13 < mini1) {
                    mini3 = mini2;
                    mini2 = mini1;
                    mini1 = WszystkieKarty.get(i).getWartosc() % 13;
                }
                else if(WszystkieKarty.get(i).getWartosc() % 13 < mini2) {
                    mini3 = mini2;
                    mini2 = WszystkieKarty.get(i).getWartosc() % 13;
                }
                else if(WszystkieKarty.get(i).getWartosc() % 13 < mini3)
                    mini3 = WszystkieKarty.get(i).getWartosc() % 13;

                if(WszystkieKarty.get(i).getWartosc() % 13 > maxi1) {
                    maxi3 = maxi2;
                    maxi2 = maxi1;
                    maxi1 = WszystkieKarty.get(i).getWartosc() % 13;
                }
                else if(WszystkieKarty.get(i).getWartosc() % 13 > maxi2) {
                    maxi3 = maxi2;
                    maxi2 = WszystkieKarty.get(i).getWartosc() % 13;
                }
                else if(WszystkieKarty.get(i).getWartosc() % 13 > maxi3)
                    maxi3 = WszystkieKarty.get(i).getWartosc() % 13;
            }
            return maxi1 - mini3 == 4 || maxi2 - mini2 == 4 || maxi3 - mini1 == 4;
        }
        return false;
    }

    private void MaxIloscWartosci() {
        int max = 0;
        int nr = 0;
        for(int i = 1; i <= 13; i++) {
            int licznik = 0;
            for(int j = 0; j < 7; j++)
                if(WszystkieKarty.get(j).getWartosc() == i) licznik++;
            if(licznik >= max) {
                max = licznik;
                nr = i;
            }
        }
        MaxWartosc = max;
        NrWartosc = nr;
    }

    private void MaxIloscWartosci2() {
        int max = 0;
        int nr = 0;
        for(int i = 1; i <= 13; i++)
            if(i != NrWartosc) {
                int licznik = 0;
                for(int j = 0; j < 7; j++)
                    if(WszystkieKarty.get(j).getWartosc() == i) licznik++;
                if(licznik >= max) {
                    max = licznik;
                    nr = i;
                }
            }
        MaxWartosc2 = max;
        NrWartosc2 = nr;
    }

    @SuppressWarnings("Duplicates")
    private boolean CzyStrit() {
        int maxi1 = 0;
        int maxi2 = -1;
        int maxi3 = -2;
        int mini1 = 14;
        int mini2 = 15;
        int mini3 = 16;
        System.out.println(WszystkieKarty.get(0).getWartosc());
        for(int i = 0; i < 7; i++) {
            System.out.println(WszystkieKarty.get(i).getWartosc());
            if(WszystkieKarty.get(i).getWartosc() % 13 < mini1) {
                mini3 = mini2;
                mini2 = mini1;
                mini1 = WszystkieKarty.get(i).getWartosc() % 13;
            }
            else if(WszystkieKarty.get(i).getWartosc() % 13 < mini2) {
                mini3 = mini2;
                mini2 = WszystkieKarty.get(i).getWartosc() % 13;
            }
            else if(WszystkieKarty.get(i).getWartosc() % 13 < mini3)
                mini3 = WszystkieKarty.get(i).getWartosc() % 13;

            if(WszystkieKarty.get(i).getWartosc() > maxi1) {
                maxi3 = maxi2;
                maxi2 = maxi1;
                maxi1 = WszystkieKarty.get(i).getWartosc();
            }
            else if(WszystkieKarty.get(i).getWartosc() > maxi2) {
                maxi3 = maxi2;
                maxi2 = WszystkieKarty.get(i).getWartosc();
            }
            else if(WszystkieKarty.get(i).getWartosc() > maxi3)
                maxi3 = WszystkieKarty.get(i).getWartosc();
        }
        return maxi1 - mini3 == 4 || maxi2 - mini2 == 4 || maxi3 - mini1 == 4;
    }

    @SuppressWarnings({"Duplicates", "unused"})
    private boolean CzyStritOdAsa() {
        if(CzyStrit()) {
            int max1 = 0;
            int max2 = -1;
            int max3 = -2;
            int max4 = -3;
            int max5 = -4;
            for(int k = 0; k < 7; k++) {
                if(WszystkieKarty.get(k).getWartosc() > max1) {
                    max5 = max4;
                    max4 = max3;
                    max3 = max2;
                    max2 = max1;
                    max1 = WszystkieKarty.get(k).getWartosc();
                }
                else if(WszystkieKarty.get(k).getWartosc() > max2 && WszystkieKarty.get(k).getWartosc() < max1) {
                    max5 = max4;
                    max4 = max3;
                    max3 = max2;
                    max2 = WszystkieKarty.get(k).getWartosc();
                }
                else if(WszystkieKarty.get(k).getWartosc() > max3 && WszystkieKarty.get(k).getWartosc() < max2) {
                    max5 = max4;
                    max4 = max3;
                    max3 = WszystkieKarty.get(k).getWartosc();
                }
                else if(WszystkieKarty.get(k).getWartosc() > max4 && WszystkieKarty.get(k).getWartosc() < max3) {
                    max5 = max4;
                    max4 = WszystkieKarty.get(k).getWartosc();
                }
                else if(WszystkieKarty.get(k).getWartosc() > max5 && WszystkieKarty.get(k).getWartosc() < max4)
                    max5 = WszystkieKarty.get(k).getWartosc();
            }
            return max1 == 13 && max5 == 9;
        }
        return false;
    }


    /* ==== FUNKCJA GLÓWNA SPRAWDZAJĄCA UKLADY ==== */

    @SuppressWarnings({"Duplicates", "unused"})
    void sprawdzUklad() {
        System.out.println(WszystkieKarty.get(0).getWartosc());
        //ROYAL FLUSH////////////
        if(WartoscKoloru() == 55) {
            Punkty = 1200000;
            Nazwa = "Royal flush";
            for(int i = 0; i < 7; i++) {
                if(WszystkieKarty.get(i).getWartosc() == 13 && WszystkieKarty.get(i).getKolor() == NrKolor) NajlepszyUklad.put(0, WszystkieKarty.get(i));
                if(WszystkieKarty.get(i).getWartosc() == 12 && WszystkieKarty.get(i).getKolor() == NrKolor) NajlepszyUklad.put(1, WszystkieKarty.get(i));
                if(WszystkieKarty.get(i).getWartosc() == 11 && WszystkieKarty.get(i).getKolor() == NrKolor) NajlepszyUklad.put(2, WszystkieKarty.get(i));
                if(WszystkieKarty.get(i).getWartosc() == 10 && WszystkieKarty.get(i).getKolor() == NrKolor) NajlepszyUklad.put(3, WszystkieKarty.get(i));
                if(WszystkieKarty.get(i).getWartosc() == 9 && WszystkieKarty.get(i).getKolor() == NrKolor) NajlepszyUklad.put(4, WszystkieKarty.get(i));
            }
        }

        //STRAIGHT FLUSH/////////
        else if(CzyPoker()) {
            Nazwa = "Straight flush";
            if(MaxKolor == 5) {
                int max1 = 0;
                int max2 = -1;
                int max3 = -2;
                int max4 = -3;
                int max5 = -4;
                int q1, q2, q3, q4, q5;
                q1 = q2 = q3 = q4 = q5 = 0;
                for(int i = 0; i < 7; i++)
                    if(WszystkieKarty.get(i).getKolor() == NrKolor) {
                        if(WszystkieKarty.get(i).getWartosc() % 13 > max1) {
                            max5 = max4;
                            max4 = max3;
                            max3 = max2;
                            max2 = max1;
                            max1 = WszystkieKarty.get(i).getWartosc() % 13;
                            q5 = q4;
                            q4 = q3;
                            q3 = q2;
                            q2 = q1;
                            q1 = i;
                        }
                        else if(WszystkieKarty.get(i).getWartosc() % 13 > max2) {
                            max5 = max4;
                            max4 = max3;
                            max3 = max2;
                            max2 = WszystkieKarty.get(i).getWartosc() % 13;
                            q5 = q4;
                            q4 = q3;
                            q3 = q2;
                            q2 = i;
                        }
                        else if(WszystkieKarty.get(i).getWartosc() % 13 > max3) {
                            max5 = max4;
                            max4 = max3;
                            max3 = WszystkieKarty.get(i).getWartosc() % 13;
                            q5 = q4;
                            q4 = q3;
                            q3 = i;
                        }
                        else if(WszystkieKarty.get(i).getWartosc() % 13 > max4) {
                            max5 = max4;
                            max4 = WszystkieKarty.get(i).getWartosc() % 13;
                            q5 = q4;
                            q4 = i;
                        }
                        else if(WszystkieKarty.get(i).getWartosc() % 13 > max5) {
                            max5 = WszystkieKarty.get(i).getWartosc() % 13;
                            q5 = i;
                        }
                    }
                NajlepszyUklad.put(0, WszystkieKarty.get(q1));
                NajlepszyUklad.put(1, WszystkieKarty.get(q2));
                NajlepszyUklad.put(2, WszystkieKarty.get(q3));
                NajlepszyUklad.put(3, WszystkieKarty.get(q4));
                NajlepszyUklad.put(4, WszystkieKarty.get(q5));
            }
            else if(MaxKolor == 6) {
                int min1 = 14;
                int min2 = 15;
                int max1 = 0;
                int max2 = -1;
                int max3 = -2;
                int max4 = -3;
                int g1, g2, h1, h2, h3, h4;
                g1 = g2 = h1 = h2 = h3 = h4 = 0;
                for(int j = 0; j < 7; j++) {
                    if(WszystkieKarty.get(j).getKolor() == NrKolor) {
                        if(WszystkieKarty.get(j).getWartosc() % 13 < min1) {
                            min2 = min1;
                            min1 = WszystkieKarty.get(j).getWartosc() % 13;
                            g2 = g1;
                            g1 = j;
                        }
                        else if(WszystkieKarty.get(j).getWartosc() % 13 < min2) {
                            min2 = WszystkieKarty.get(j).getWartosc() % 13;
                            g2 = j;
                        }

                        if(WszystkieKarty.get(j).getWartosc() % 13 > max1) {
                            max4 = max3;
                            max3 = max2;
                            max2 = max1;
                            max1 = WszystkieKarty.get(j).getWartosc() % 13;
                            h4 = h3;
                            h3 = h2;
                            h2 = h1;
                            h1 = j;
                        }
                        else if(WszystkieKarty.get(j).getWartosc() % 13 > max2) {
                            max4 = max3;
                            max3 = max2;
                            max2 = WszystkieKarty.get(j).getWartosc() % 13;
                            h4 = h3;
                            h3 = h2;
                            h2 = j;
                        }
                        else if(WszystkieKarty.get(j).getWartosc() % 13 > max3) {
                            max4 = max3;
                            max3 = WszystkieKarty.get(j).getWartosc() % 13;
                            h4 = h3;
                            h3 = j;
                        }
                        else if(WszystkieKarty.get(j).getWartosc() % 13 > max4) {
                            max4 = WszystkieKarty.get(j).getWartosc() % 13;
                            h4 = j;
                        }
                    }
                }
                if(max1 - min2 == 4) {
                    NajlepszyUklad.put(0, WszystkieKarty.get(h1));
                    NajlepszyUklad.put(1, WszystkieKarty.get(h2));
                    NajlepszyUklad.put(2, WszystkieKarty.get(h3));
                    NajlepszyUklad.put(3, WszystkieKarty.get(h4));
                    NajlepszyUklad.put(4, WszystkieKarty.get(g2));
                }
                else {
                    NajlepszyUklad.put(0, WszystkieKarty.get(h2));
                    NajlepszyUklad.put(1, WszystkieKarty.get(h3));
                    NajlepszyUklad.put(2, WszystkieKarty.get(h4));
                    NajlepszyUklad.put(3, WszystkieKarty.get(g2));
                    NajlepszyUklad.put(4, WszystkieKarty.get(g1));
                }
            }
            else {
                int mini1 = 14;
                int mini2 = 15;
                int mini3 = 16;
                int maxi1 = 0;
                int maxi2 = -1;
                int maxi3 = -2;
                int maxi4 = -3;
                int r1, r2, r3, t1, t2, t3, t4;
                r1 = r2 = r3 = t1 = t2 = t3 = t4 = 0;
                for(int k = 0; k < 7; k++) {
                    if(WszystkieKarty.get(k).getWartosc() % 13 < mini1) {
                        mini3 = mini2;
                        mini2 = mini1;
                        mini1 = WszystkieKarty.get(k).getWartosc() % 13;
                        r3 = r2;
                        r2 = r1;
                        r1 = k;
                    }
                    else if(WszystkieKarty.get(k).getWartosc() % 13 < mini2) {
                        mini3 = mini2;
                        mini2 = WszystkieKarty.get(k).getWartosc() % 13;
                        r3 = r2;
                        r2 = k;
                    }
                    else if(WszystkieKarty.get(k).getWartosc() % 13 < mini3) {
                        mini3 = WszystkieKarty.get(k).getWartosc() % 13;
                        r3 = k;
                    }
                    if(WszystkieKarty.get(k).getWartosc() % 13 > maxi1) {
                        maxi4 = maxi3;
                        maxi3 = maxi2;
                        maxi2 = maxi1;
                        maxi1 = WszystkieKarty.get(k).getWartosc() % 13;
                        t4 = t3;
                        t3 = t2;
                        t2 = t1;
                        t1 = k;
                    }
                    else if(WszystkieKarty.get(k).getWartosc() % 13 > maxi2) {
                        maxi4 = maxi3;
                        maxi3 = maxi2;
                        maxi2 = WszystkieKarty.get(k).getWartosc() % 13;
                        t4 = t3;
                        t3 = t2;
                        t2 = k;
                    }
                    else if(WszystkieKarty.get(k).getWartosc() % 13 > maxi3) {
                        maxi4 = maxi3;
                        maxi3 = WszystkieKarty.get(k).getWartosc() % 13;
                        t4 = t3;
                        t3 = k;
                    }
                    else if(WszystkieKarty.get(k).getWartosc() % 13 > maxi4) {
                        maxi4 = WszystkieKarty.get(k).getWartosc() % 13;
                        t4 = k;
                    }
                }
                if(maxi1 - mini3 == 4) {
                    NajlepszyUklad.put(0, WszystkieKarty.get(t1));
                    NajlepszyUklad.put(1, WszystkieKarty.get(t2));
                    NajlepszyUklad.put(2, WszystkieKarty.get(t3));
                    NajlepszyUklad.put(3, WszystkieKarty.get(t4));
                    NajlepszyUklad.put(4, WszystkieKarty.get(r3));
                }
                else if(maxi2 - mini2 == 4) {
                    NajlepszyUklad.put(0, WszystkieKarty.get(t2));
                    NajlepszyUklad.put(1, WszystkieKarty.get(t3));
                    NajlepszyUklad.put(2, WszystkieKarty.get(t4));
                    NajlepszyUklad.put(3, WszystkieKarty.get(r3));
                    NajlepszyUklad.put(4, WszystkieKarty.get(r2));
                }
                else {
                    NajlepszyUklad.put(0, WszystkieKarty.get(t3));
                    NajlepszyUklad.put(1, WszystkieKarty.get(t4));
                    NajlepszyUklad.put(2, WszystkieKarty.get(r3));
                    NajlepszyUklad.put(3, WszystkieKarty.get(r2));
                    NajlepszyUklad.put(4, WszystkieKarty.get(r1));
                }
            }

            Nazwa = Nazwa + " - " + NajlepszyUklad.get(0).getDlugaNazwaWartosci() + " high";
            Punkty = 1180000 + NajlepszyUklad.get(0).getWartosc();
        }

        //FOUR OF A KIND///////////////////////
        else if(MaxWartosc == 4) {
            Nazwa = "Four of a kind";
            int key = 0;
            for(int i = 0; i < 7; i++) {
                if(WszystkieKarty.get(i).getWartosc() == NrWartosc) {
                    NajlepszyUklad.put(key, WszystkieKarty.get(i));
                    key++;
                }
            }
            int max = 0; //Kicker
            int index = 0;
            for(int j = 0; j < 7; j++)
                if(WszystkieKarty.get(j).getWartosc() != NrWartosc)
                    if(WszystkieKarty.get(j).getWartosc() > max) {
                        max = WszystkieKarty.get(j).getWartosc();
                        index = j;
                    }
            NajlepszyUklad.put(4, WszystkieKarty.get(index));
            Punkty = 1150000 + 14*NajlepszyUklad.get(1).getWartosc() + NajlepszyUklad.get(5).getWartosc();
            Nazwa = Nazwa + ", " + NajlepszyUklad.get(1).getDlugaNazwaWartosci() + "s with kicker: "
                    + NajlepszyUklad.get(5).getDlugaNazwaWartosci();
        }

        //FULL HOUSE//////////////////////////////////
        else if(MaxWartosc == 3 && MaxWartosc2 >= 2) {
            Nazwa = "Full house";
            int key = 0;
            for(int i = 0; i < 7; i++) {
                if(WszystkieKarty.get(i).getWartosc() == NrWartosc) {
                    NajlepszyUklad.put(key, WszystkieKarty.get(i));
                    key++;
                }
            }
            int k = 0;
            while(key < 5) {
                if(WszystkieKarty.get(k).getWartosc() == NrWartosc2) {
                    NajlepszyUklad.put(key, WszystkieKarty.get(k));
                    key++;
                }
                k++;
            }
            Nazwa = Nazwa + ": " + NajlepszyUklad.get(0).getDlugaNazwaWartosci() + "s full of "
                    + NajlepszyUklad.get(3).getDlugaNazwaWartosci() + "s";
            Punkty = 1140000 + 196*NajlepszyUklad.get(0).getWartosc() + 14*NajlepszyUklad.get(3).getWartosc();
        }

        //FLUSH///////////////////////////////////////////
        else if(MaxKolor >= 5) {
            Nazwa = "Flush";
            int max1 = 0;
            int max2 = -1;
            int max3 = -2;
            int max4 = -3;
            int max5 = -4;
            int i1, i2, i3, i4, i5;
            i1 = i2 = i3 = i4 = i5 = 0;
            for(int i = 0; i < 7; i++) {
                if(WszystkieKarty.get(i).getKolor() == NrKolor) {
                    if(WszystkieKarty.get(i).getWartosc() > max1) {
                        max5 = max4;
                        max4 = max3;
                        max3 = max2;
                        max2 = max1;
                        max1 = WszystkieKarty.get(i).getWartosc();
                        i5 = i4;
                        i4 = i3;
                        i3 = i2;
                        i2 = i1;
                        i1 = i;
                    }
                    else if(WszystkieKarty.get(i).getWartosc() > max2) {
                        max5 = max4;
                        max4 = max3;
                        max3 = max2;
                        max2 = WszystkieKarty.get(i).getWartosc();
                        i5 = i4;
                        i4 = i3;
                        i3 = i2;
                        i2 = i;
                    }
                    else if(WszystkieKarty.get(i).getWartosc() > max3) {
                        max5 = max4;
                        max4 = max3;
                        max3 = WszystkieKarty.get(i).getWartosc();
                        i5 = i4;
                        i4 = i3;
                        i3 = i;
                    }
                    else if(WszystkieKarty.get(i).getWartosc() > max4) {
                        max5 = max4;
                        max4 = WszystkieKarty.get(i).getWartosc();
                        i5 = i4;
                        i4 = i;
                    }
                    else if(WszystkieKarty.get(i).getWartosc() > max5) {
                        max5 = WszystkieKarty.get(i).getWartosc();
                        i5 = i;
                    }
                }
            }
            NajlepszyUklad.put(0, WszystkieKarty.get(i1));
            NajlepszyUklad.put(1, WszystkieKarty.get(i2));
            NajlepszyUklad.put(2, WszystkieKarty.get(i3));
            NajlepszyUklad.put(3, WszystkieKarty.get(i4));
            NajlepszyUklad.put(4, WszystkieKarty.get(i5));

            Nazwa += ", " + NajlepszyUklad.get(0).getDlugaNazwaWartosci() + " high";
            Punkty = 600000 + 38416*NajlepszyUklad.get(0).getWartosc() + 2744*NajlepszyUklad.get(1).getWartosc() + 196*NajlepszyUklad.get(2).getWartosc()
                    + 14*NajlepszyUklad.get(3).getWartosc() + NajlepszyUklad.get(4).getWartosc();
        }

        //STRAIGHT/////////////////////////////////////////
        else if(CzyStrit()) {
            Nazwa = "Straight";
            if(CzyStritOdAsa()) {
                Nazwa += ", Ace high";
                Punkty = 16370;
                for(int i = 0; i < 7; i++) {
                    if(WszystkieKarty.get(i).getWartosc() == 13) NajlepszyUklad.put(0, WszystkieKarty.get(i));
                    if(WszystkieKarty.get(i).getWartosc() == 12) NajlepszyUklad.put(1, WszystkieKarty.get(i));
                    if(WszystkieKarty.get(i).getWartosc() == 11) NajlepszyUklad.put(2, WszystkieKarty.get(i));
                    if(WszystkieKarty.get(i).getWartosc() == 10) NajlepszyUklad.put(3, WszystkieKarty.get(i));
                    if(WszystkieKarty.get(i).getWartosc() == 9) NajlepszyUklad.put(4, WszystkieKarty.get(i));
                }
            }
            else {
                int maxi1 = 0;
                int maxi2 = -1;
                int maxi3 = -2;
                int maxi4 = -3;
                int maxi5 = -4;
                int maxi6 = -5;
                int maxi7 = -6;
                int i1, i2, i3, i4, i5, i6, i7;
                i1 = i2 = i3 = i4 = i5 = i6 = i7 = 0;
                for(int k = 0; k < 7; k++) {
                    if(WszystkieKarty.get(k).getWartosc() % 13 > maxi1) {
                        maxi7 = maxi6;
                        maxi6 = maxi5;
                        maxi5 = maxi4;
                        maxi4 = maxi3;
                        maxi3 = maxi2;
                        maxi2 = maxi1;
                        maxi1 = WszystkieKarty.get(k).getWartosc() % 13;
                        i7 = i6;
                        i6 = i5;
                        i5 = i4;
                        i4 = i3;
                        i3 = i2;
                        i2 = i1;
                        i1 = k;
                    }
                    else if(WszystkieKarty.get(k).getWartosc() % 13 > maxi2 && WszystkieKarty.get(k).getWartosc() % 13 < maxi1) {
                        maxi7 = maxi6;
                        maxi6 = maxi5;
                        maxi5 = maxi4;
                        maxi4 = maxi3;
                        maxi3 = maxi2;
                        maxi2 = WszystkieKarty.get(k).getWartosc() % 13;
                        i7 = i6;
                        i6 = i5;
                        i5 = i4;
                        i4 = i3;
                        i3 = i2;
                        i2 = k;
                    }
                    else if(WszystkieKarty.get(k).getWartosc() % 13 > maxi3 && WszystkieKarty.get(k).getWartosc() % 13 < maxi2) {
                        maxi7 = maxi6;
                        maxi6 = maxi5;
                        maxi5 = maxi4;
                        maxi4 = maxi3;
                        maxi3 = WszystkieKarty.get(k).getWartosc() % 13;
                        i7 = i6;
                        i6 = i5;
                        i5 = i4;
                        i4 = i3;
                        i3 = k;
                    }
                    else if(WszystkieKarty.get(k).getWartosc() % 13 > maxi4 && WszystkieKarty.get(k).getWartosc() % 13 < maxi3) {
                        maxi7 = maxi6;
                        maxi6 = maxi5;
                        maxi5 = maxi4;
                        maxi4 = WszystkieKarty.get(k).getWartosc() % 13;
                        i7 = i6;
                        i6 = i5;
                        i5 = i4;
                        i4 = k;
                    }
                    else if(WszystkieKarty.get(k).getWartosc() % 13 > maxi5 && WszystkieKarty.get(k).getWartosc() % 13 < maxi4) {
                        maxi7 = maxi6;
                        maxi6 = maxi5;
                        maxi5 = WszystkieKarty.get(k).getWartosc() % 13;
                        i7 = i6;
                        i6 = i5;
                        i5 = k;
                    }
                    else if(WszystkieKarty.get(k).getWartosc() % 13 > maxi6 && WszystkieKarty.get(k).getWartosc() % 13 < maxi5) {
                        maxi7 = maxi6;
                        maxi6 = WszystkieKarty.get(k).getWartosc() % 13;
                        i7 = i6;
                        i6 = k;
                    }
                    else if(WszystkieKarty.get(k).getWartosc() % 13 > maxi7 && WszystkieKarty.get(k).getWartosc() % 13 < maxi6) {
                        maxi7 = WszystkieKarty.get(k).getWartosc() % 13;
                        i7 = k;
                    }
                }
                if(maxi1 - maxi5 == 4) {
                    NajlepszyUklad.put(0, WszystkieKarty.get(i1));
                    NajlepszyUklad.put(1, WszystkieKarty.get(i2));
                    NajlepszyUklad.put(2, WszystkieKarty.get(i3));
                    NajlepszyUklad.put(3, WszystkieKarty.get(i4));
                    NajlepszyUklad.put(4, WszystkieKarty.get(i5));
                }
                else if(maxi2 - maxi6 == 4) {
                    NajlepszyUklad.put(0, WszystkieKarty.get(i2));
                    NajlepszyUklad.put(1, WszystkieKarty.get(i3));
                    NajlepszyUklad.put(2, WszystkieKarty.get(i4));
                    NajlepszyUklad.put(3, WszystkieKarty.get(i5));
                    NajlepszyUklad.put(4, WszystkieKarty.get(i6));
                }
                else {
                    NajlepszyUklad.put(0, WszystkieKarty.get(i3));
                    NajlepszyUklad.put(1, WszystkieKarty.get(i4));
                    NajlepszyUklad.put(2, WszystkieKarty.get(i5));
                    NajlepszyUklad.put(3, WszystkieKarty.get(i6));
                    NajlepszyUklad.put(4, WszystkieKarty.get(i7));
                }
                Nazwa += ", " + NajlepszyUklad.get(0).getDlugaNazwaWartosci() + " high";
                Punkty = 590000 + NajlepszyUklad.get(0).getWartosc();
            }
        }

        //THREE OF A KIND///////////////////////////
        else if(MaxWartosc == 3) {
            Nazwa = "Three of a kind";
            int key = 0;
            for(int i = 0; i < 7; i++)
                if(WszystkieKarty.get(i).getWartosc() == NrWartosc) {
                    NajlepszyUklad.put(key, WszystkieKarty.get(i));
                    key++;
                }
            int i1 = 0;
            int i2 = 0;
            int max1 = 0;
            int max2 = -1;
            for(int j = 0; j < 7; j++)
                if(WszystkieKarty.get(j).getWartosc() != NrWartosc) {
                    if(WszystkieKarty.get(j).getWartosc() > max1) {
                        max2 = max1;
                        max1 = WszystkieKarty.get(j).getWartosc();
                        i2 = i1;
                        i1 = j;
                    }
                    else if(WszystkieKarty.get(j).getWartosc() > max2) {
                        max2 = WszystkieKarty.get(j).getWartosc();
                        i2 = j;
                    }
                }
            NajlepszyUklad.put(3, WszystkieKarty.get(i1));
            NajlepszyUklad.put(4, WszystkieKarty.get(i2));

            Nazwa += ", " + NajlepszyUklad.get(0).getDlugaNazwaWartosci() + "s with " + NajlepszyUklad.get(3).getDlugaNazwaWartosci() + "-"
                    + NajlepszyUklad.get(4).getDlugaNazwaWartosci() + " kicker";
            Punkty = 583000 + 196*NajlepszyUklad.get(1).getWartosc() + 14*NajlepszyUklad.get(3).getWartosc() + NajlepszyUklad.get(4).getWartosc();
        }

        //TWO PAIR///////////////////////////////////
        else if(MaxWartosc2 == 2) {
            int key = 0;
            for(int i = 0; i < 7; i++) {
                if(WszystkieKarty.get(i).getWartosc() == NrWartosc) {
                    NajlepszyUklad.put(key, WszystkieKarty.get(i));
                    key++;
                }
            }
            for(int j = 0; j < 7; j++) {
                if(WszystkieKarty.get(j).getWartosc() == NrWartosc2) {
                    NajlepszyUklad.put(key, WszystkieKarty.get(j));
                    key++;
                }
            }
            int max = 0;
            int f = 0;
            for(int k = 0; k < 7; k++)
                if(WszystkieKarty.get(k).getWartosc() != NrWartosc && WszystkieKarty.get(k).getWartosc() != NrWartosc2)
                    if(WszystkieKarty.get(k).getWartosc() > max) {
                        max = WszystkieKarty.get(k).getWartosc();
                        f = k;
                    }
            NajlepszyUklad.put(4, WszystkieKarty.get(f));

            Nazwa += ", " + NajlepszyUklad.get(0).getDlugaNazwaWartosci() + "s and " + NajlepszyUklad.get(2).getDlugaNazwaWartosci()
                    + "s with " + NajlepszyUklad.get(4).getDlugaNazwaWartosci() + " kicker";
            Punkty = 580000 + 196*NajlepszyUklad.get(0).getWartosc() + 14*NajlepszyUklad.get(2).getWartosc() + NajlepszyUklad.get(4).getWartosc();
        }

        //ONE PAIR////////////////////////////////////
        else if(MaxWartosc == 2) {
            Nazwa = "One pair";
            int key = 0;
            for(int i = 0; i < 7; i++)
                if(WszystkieKarty.get(i).getWartosc() == NrWartosc) {
                    NajlepszyUklad.put(key, WszystkieKarty.get(i));
                    key++;
                }
            int max1 = 0;
            int max2 = -1;
            int max3 = -2;
            int i1, i2, i3;
            i1 = i2 = i3 = 0;
            for(int j = 0; j < 7; j++)
                if(WszystkieKarty.get(j).getWartosc() != NrWartosc) {
                    if(WszystkieKarty.get(j).getWartosc() > max1) {
                        max3 = max2;
                        max2 = max1;
                        max1 = WszystkieKarty.get(j).getWartosc();
                        i3 = i2;
                        i2 = i1;
                        i1 = j;
                    }
                    else if(WszystkieKarty.get(j).getWartosc() > max2) {
                        max3 = max2;
                        max2 = WszystkieKarty.get(j).getWartosc();
                        i3 = i2;
                        i2 = j;
                    }
                    else if(WszystkieKarty.get(j).getWartosc() > max3) {
                        max3 = WszystkieKarty.get(j).getWartosc();
                        i3 = j;
                    }
                }
            NajlepszyUklad.put(2, WszystkieKarty.get(i1));
            NajlepszyUklad.put(3, WszystkieKarty.get(i2));
            NajlepszyUklad.put(4, WszystkieKarty.get(i3));

            Nazwa += ", " + NajlepszyUklad.get(0).getDlugaNazwaWartosci() + "s with " + NajlepszyUklad.get(2).getDlugaNazwaWartosci() + "-"
                    + NajlepszyUklad.get(3).getDlugaNazwaWartosci() + "-" + NajlepszyUklad.get(4).getDlugaNazwaWartosci() + " kicker";
            Punkty = 540000 + 2744*NajlepszyUklad.get(0).getWartosc() + 196*NajlepszyUklad.get(2).getWartosc() + 14*NajlepszyUklad.get(3).getWartosc()
                    + NajlepszyUklad.get(4).getWartosc();
        }

        //HIGH CARD//////////////////////////////////////
        else
        {
            Nazwa = "High card";
            int max1 = 0;
            int max2 = -1;
            int max3 = -2;
            int max4 = -3;
            int max5 = -4;
            int i1, i2, i3, i4, i5;
            i1 = i2 = i3 = i4 = i5 = 0;
            for(int i = 0; i < 7; i++) {
                if(WszystkieKarty.get(i).getWartosc() > max1) {
                    max5 = max4;
                    max4 = max3;
                    max3 = max2;
                    max2 = max1;
                    max1 = WszystkieKarty.get(i).getWartosc();
                    i5 = i4;
                    i4 = i3;
                    i3 = i2;
                    i2 = i1;
                    i1 = i;
                }
                else if(WszystkieKarty.get(i).getWartosc() > max2) {
                    max5 = max4;
                    max4 = max3;
                    max3 = max2;
                    max2 = WszystkieKarty.get(i).getWartosc();
                    i5 = i4;
                    i4 = i3;
                    i3 = i2;
                    i2 = i;
                }
                else if(WszystkieKarty.get(i).getWartosc() > max3) {
                    max5 = max4;
                    max4 = max3;
                    max3 = WszystkieKarty.get(i).getWartosc();
                    i5 = i4;
                    i4 = i3;
                    i3 = i;
                }
                else if(WszystkieKarty.get(i).getWartosc() > max4) {
                    max5 = max4;
                    max4 = WszystkieKarty.get(i).getWartosc();
                    i5 = i4;
                    i4 = i;
                }
                else if(WszystkieKarty.get(i).getWartosc() > max5) {
                    max5 = WszystkieKarty.get(i).getWartosc();
                    i5 = i;
                }
            }
            NajlepszyUklad.put(0, WszystkieKarty.get(i1));
            NajlepszyUklad.put(1, WszystkieKarty.get(i2));
            NajlepszyUklad.put(2, WszystkieKarty.get(i3));
            NajlepszyUklad.put(3, WszystkieKarty.get(i4));
            NajlepszyUklad.put(4, WszystkieKarty.get(i5));

            Nazwa += " " + NajlepszyUklad.get(0).getDlugaNazwaWartosci() + " with " + NajlepszyUklad.get(1).getDlugaNazwaWartosci() + "-"
                    + NajlepszyUklad.get(2).getDlugaNazwaWartosci() + "-" + NajlepszyUklad.get(3).getDlugaNazwaWartosci() + "-"
                    + NajlepszyUklad.get(4).getDlugaNazwaWartosci() + " kicker";
            Punkty = 38416*NajlepszyUklad.get(0).getWartosc() + 2744*NajlepszyUklad.get(1).getWartosc() + 192*NajlepszyUklad.get(2).getWartosc()
                    + 14*NajlepszyUklad.get(3).getWartosc() + NajlepszyUklad.get(4).getWartosc();
        }
    }

    /* ==== GETTERS AND SETTERS ==== */

    public int getPunkty() {
        return Punkty;
    }

    @SuppressWarnings("unused")
    public void setPunkty(int punkty) {
        Punkty = punkty;
    }

    public String getNazwa() {
        return Nazwa;
    }

    @SuppressWarnings("unused")
    public void setNazwa(String nazwa) {
        Nazwa = nazwa;
    }

    @SuppressWarnings("unused")
    public HashMap<Integer, Karta> getNajlepszyUklad() {
        return NajlepszyUklad;
    }

    @SuppressWarnings("unused")
    public void setNajlepszyUklad(HashMap<Integer, Karta> najlepszyUklad) {
        NajlepszyUklad = najlepszyUklad;
    }

    public HashMap<Integer, Karta> getWszystkieKarty() {
        return WszystkieKarty;
    }

    @SuppressWarnings("unused")
    public void setWszystkieKarty(HashMap<Integer, Karta> wszystkieKarty) {
        WszystkieKarty = wszystkieKarty;
    }
}
