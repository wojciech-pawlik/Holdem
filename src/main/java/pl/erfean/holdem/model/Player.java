package pl.erfean.holdem.model;


import lombok.Getter;
import lombok.Setter;
import pl.erfean.holdem.model.interfaces.PlayerI;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static pl.erfean.holdem.model.Board.PREFLOP;

@Entity
@Table(name = "players")
@Getter
@Setter
public class Player implements PlayerI {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "nickname")
    private String nickname;
    @Column(name = "chips")
    private int chips;
    @Column(name = "notes")
    private String notes;

    @Transient
    private Card[] cards;
    private Hand hand;
    private int seat;
    private int bet;
    private boolean isPlaying;
    private int[] bets = new int[4];

    public Player(Long id, String nickname, int chips, String notes) {
        this.id = id;
        this.nickname = nickname;
        this.chips = chips;
        seat = -1;
        isPlaying = true;
        this.notes = notes;
    }

    // Operating with bets
    public void bet() {
        bet = bets[0] + bets[1] + bets[2] + bets[3];
    }
    public int addedToPot(int round) {
        if(round == PREFLOP) return 0;
        return Arrays.stream(bets).limit(round - 1).sum();
    }
    public void addBets(int round, int value) {
        this.bets[round] += value;
    }
    public void setBets(int round, int value) {
        this.bets[round] = value;
    }
    public int getBets() {
        return Arrays.stream(bets).sum();
    }
    public int getBets(int round) {
        return bets[round];
    }

    // Operating with chips
    public void addChips() {
        this.chips++;
    }
    public void addChips(int chips) {
        this.chips += chips;
    }
    public void subtractChips(int chips) {
        this.chips -= chips;
    }

    // Setting up hand
    public void setHand(List<Card> boardCards) {
        List<Card> allCards = new ArrayList<>(boardCards);
        allCards.add(cards[0]);
        allCards.add(cards[1]);
        this.hand = new Hand(allCards);
    }
    // Additional getter
    public Card getCard(int number) {
        return cards[number];
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", chips=" + chips +
                ", cards=" + Arrays.toString(cards) +
                '}';
    }
}
