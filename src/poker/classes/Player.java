package poker.classes;

public class Player {
    private String nickname;
    private Card card1, card2;
    private Hand hand;
    private int chips, place, bet;
    private boolean isPlaying;
    private int[] bets = new int[4];

    @SuppressWarnings("unused")
    public Player() {
        this("", 0, 0);
    }

    Player(String nickname, int chips, int place) {
        this.nickname = nickname;
        card1 = card2 = new Card();
        hand = new Hand();
        this.chips = chips;
        this.place = place;
        bet = 0;
        bets[0] = bets[1] = bets[2] = bets[3] = 0;
        isPlaying = true;
    }

    void bet() {
        bet = bets[0] + bets[1] + bets[2] + bets[3];
    }

    int addedToPot(int round) {
        int sum = 0;
        for(int i = 0; i < round; i++)
            sum += bets[i];
        return sum;
    }

    /* ==== GETTERS AND SETTERS ==== */

    String getNickname() {
        return nickname;
    }

    @SuppressWarnings("unused")
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    Card getCard1() {
        return card1;
    }

    void setCard1(Card card1) {
        this.card1 = card1;
    }

    Card getCard2() {
        return card2;
    }

    void setCard2(Card card2) {
        this.card2 = card2;
    }

    Hand getHand() {
        return hand;
    }

    @SuppressWarnings("unused")
    public void setHand(Hand hand) {
        this.hand = hand;
    }

    void setHand(Board board) {
        this.hand = new Hand(this, board);
    }

    int getChips() {
        return chips;
    }

    void setChips() {
        this.setChips(0);
    }

    private void setChips(int chips) {
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

    int getBet() {
        return bet;
    }

    @SuppressWarnings("unused")
    void setBet(int bet) {
        this.bet = bet;
    }

    boolean isPlaying() {
        return isPlaying;
    }

    void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    //BETS//

    @SuppressWarnings("unused")
    int getBets() {
        int sum = 0;
        for(int i = 0; i < 4; i++) sum += bets[i];
        return sum;
    }

    int getBets(int round) {
        return bets[round];
    }

    @SuppressWarnings("unused")
    void setBets(int[] bets) {
        this.bets = bets;
    }

    void setBets(int round, int value) {
        this.bets[round] = value;
    }

    void addBets(int round, int value) {
        this.bets[round] += value;
    }

    /////////////////////////////////////////////////////////////////////////////////

    void addChips() {
        this.chips++;
    }

    void addChips(int chips) {
        this.chips += chips;
    }

    void substractChips(int chips) {
        this.chips -= chips;
    }
}
