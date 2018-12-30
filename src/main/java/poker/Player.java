package poker;

import javax.persistence.*;

@Entity
@Table(name = "Players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "chips")
    private int chips;

    @Column(name = "notes")
    private String notes;

//    @Column(name = "check")
//    private int check;

    @Transient
    private Card card1, card2;
    @Transient
    private Hand hand;
    @Transient
    private int place, bet;
    @Transient
    private boolean isPlaying;
    @Transient
    private int[] bets = new int[4];


    public Player() {}

    public Player(int id, String nickname, int chips, String notes/*, int check*/) {
        this.id = id;
        this.nickname = nickname;
        card1 = card2 = new Card();
        hand = new Hand();
        this.chips = chips;
        place = -1;
        bet = 0;
        bets[0] = bets[1] = bets[2] = bets[3] = 0;
        isPlaying = true;
        this.notes = notes;
//        this.check = check;
    }

    public void bet() {
        bet = bets[0] + bets[1] + bets[2] + bets[3];
    }

    public int addedToPot(int round) {
        int sum = 0;
        for(int i = 0; i < round; i++)
            sum += bets[i];
        return sum;
    }

    /* ==== GETTERS AND SETTERS ==== */

    public String getNickname() {
        return nickname;
    }

    @SuppressWarnings("unused")
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Card getCard1() {
        return card1;
    }

    public void setCard1(Card card1) {
        this.card1 = card1;
    }

    public Card getCard2() {
        return card2;
    }

    public void setCard2(Card card2) {
        this.card2 = card2;
    }

    public Hand getHand() {
        return hand;
    }

    @SuppressWarnings("unused")
    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public void setHand(Board board) {
        this.hand = new Hand(this, board);
    }

    public int getChips() {
        return chips;
    }

    public void setChips() {
        this.setChips(0);
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    @SuppressWarnings("unused")
    public int getPlace() {
        return place;
    }

    @SuppressWarnings("unused")
    public void setPlace(int place) {
        this.place = place;
    }

    public int getBet() {
        return bet;
    }

    @SuppressWarnings("unused")
    public void setBet(int bet) {
        this.bet = bet;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public int getCheck() {
//        return check;
//    }
//
//    public void setCheck(int check) {
//        this.check = check;
//    }

    //BETS//

    @SuppressWarnings("unused")
    public int getBets() {
        int sum = 0;
        for(int i = 0; i < 4; i++) sum += bets[i];
        return sum;
    }

    public int getBets(int round) {
        return bets[round];
    }

    @SuppressWarnings("unused")
    public void setBets(int[] bets) {
        this.bets = bets;
    }

    public void setBets(int round, int value) {
        this.bets[round] = value;
    }

    public void addBets(int round, int value) {
        this.bets[round] += value;
    }

    /////////////////////////////////////////////////////////////////////////////////

    public void addChips() {
        this.chips++;
    }

    public void addChips(int chips) {
        this.chips += chips;
    }

    public void subtractChips(int chips) {
        this.chips -= chips;
    }
}
