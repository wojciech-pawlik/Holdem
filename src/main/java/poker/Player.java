package poker;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Arrays;

import static poker.Board.PREFLOP;

@Entity
@Table(name = "Players")
@Getter
@Setter
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

    @Transient
    private Card card1, card2;
    @Transient
    private Hand hand;
    @Transient
    private int seat;
    @Transient
    private int bet;
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
        seat = -1;
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
        if(round == PREFLOP) return 0;
        return Arrays.stream(bets).limit(round - 1).sum();
    }

    public void setHand(Board board) {
        this.hand = new Hand(this, board);
    }

    public void setChips() {
        this.setChips(0);
    }

    //BETS//

    public int getBets() {
        return Arrays.stream(bets).sum();
    }

    public int getBets(int round) {
        return bets[round];
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
