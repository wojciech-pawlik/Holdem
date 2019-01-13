package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import poker.Board;
import poker.Player;

import java.io.File;
import java.net.URL;
import java.util.*;

import static poker.Board.PREFLOP;

public class RunController implements Initializable {
    private static final String PATH_TO_BOARD = "\\src\\main\\java\\poker\\board-components\\board.jpg";
    private static final String PATH_TO_CARDS = "\\src\\main\\java\\poker\\cards\\icons\\png\\";

    static final int RUN_WIDTH = 1380;
    static final int RUN_HEIGHT = 640;

    private static final int PLAYER_WIDTH = 200;
    private static final int PLAYER_HEIGHT = 120;
    private static final int CARD_WIDTH = PLAYER_WIDTH / 3;
    private static final int CARD_HEIGHT = 2 * PLAYER_HEIGHT / 3;
    private static final int BOARD_WIDTH = 600;
    private static final int BOARD_HEIGHT = 280;

    private static final int CONTROL_WIDTH = 300;
    private static final int CONTROL_HEIGHT = 600;
    private static final int CONTROL_COMPONENT_HEIGHT = CONTROL_HEIGHT/12;


    private static final String CARD_PANE_STYLE = "card-pane";
    private static final String NICK_PANE_STYLE = "nick-pane";
    private static final String CHIPS_PANE_STYLE = "chips-pane";
    private static final String BOARD_IMAGE_STYLE = "board-image";
    private static final String CONTROL_BUTTON = "control-button";

    private static final int CARD1_PANE = 0;
    private static final int CARD2_PANE = 1;
    private static final int NICK_PANE = 2;
    private static final int CHIPS_PANE = 3;

    private int turn;

    private Board board;
    private ArrayList<Pane> playerPanes;

    @FXML
    private AnchorPane runController;

    @FXML
    private Pane boardPane, controlPane, boardInfo, playerAction;

    public RunController(Board board) {
        this.board = board;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureBoardPane();
        configureControlPane();
        playerPanes = new ArrayList<>(board.getPlacesCount());

        setSeats();
        runGame();
    }

    private void setSeats() {
        int x = (RUN_WIDTH - CONTROL_WIDTH) / 2;
        int y = RUN_HEIGHT / 2;
        for (int i = 0; i < board.getPlacesCount(); i++) {
            System.out.println(i);
            x += (RUN_WIDTH - CONTROL_WIDTH) / 3 * Math.sin(2 * Math.PI / board.getPlacesCount() * i);
            y -= RUN_HEIGHT / 3 * Math.cos(2 * Math.PI / board.getPlacesCount() * i);

            var pane = new Pane();
            configurePane(pane, PLAYER_WIDTH, PLAYER_HEIGHT, x - PLAYER_WIDTH / 2, y - PLAYER_HEIGHT / 2, "seat");
            setPlayerView(pane);
            pane.setVisible(false);
            playerPanes.add(pane);
            runController.getChildren().add(pane);

            x = (RUN_WIDTH - CONTROL_WIDTH) / 2;
            y = RUN_HEIGHT / 2;
        }
    }

    private void setPlayerView(Pane pane) {
        var card1 = new Pane();
        var card2 = new Pane();
        var nickname = new Pane();
        var chips = new Pane();

        configurePane(card1, CARD_WIDTH, CARD_HEIGHT, PLAYER_WIDTH / 6, 0, CARD_PANE_STYLE);
        configurePane(card2, CARD_WIDTH, CARD_HEIGHT, PLAYER_WIDTH / 2, 0, CARD_PANE_STYLE);
        configurePane(nickname, PLAYER_WIDTH, PLAYER_HEIGHT / 6, 0, CARD_HEIGHT, NICK_PANE_STYLE);
        configurePane(chips, PLAYER_WIDTH, PLAYER_HEIGHT / 6, 0, CARD_HEIGHT + PLAYER_HEIGHT / 6, CHIPS_PANE_STYLE);

        pane.getChildren().add(card1);
        pane.getChildren().add(card2);
        pane.getChildren().add(nickname);
        pane.getChildren().add(chips);
    }

    private void configureBoardPane() {
        boardPane.setMinSize(BOARD_WIDTH, BOARD_HEIGHT);
        boardPane.setLayoutX((RUN_WIDTH - CONTROL_WIDTH) / 2 - BOARD_WIDTH / 2);
        boardPane.setLayoutY(RUN_HEIGHT / 2 - BOARD_HEIGHT / 2);
        System.out.println(new File("").getAbsolutePath() + PATH_TO_BOARD);
        ImageView boardImage = new ImageView(new Image("file:" + new File("").getAbsolutePath()
                + PATH_TO_BOARD, BOARD_WIDTH, BOARD_HEIGHT, false, false));
        configureImageView(boardImage, BOARD_WIDTH, BOARD_HEIGHT, BOARD_IMAGE_STYLE);
        boardPane.getChildren().add(boardImage);

        var flop1 = new Pane();
        var flop2 = new Pane();
        var flop3 = new Pane();
        var turn = new Pane();
        var river = new Pane();

        configurePane(flop1, CARD_WIDTH, CARD_HEIGHT, BOARD_WIDTH / 5, BOARD_HEIGHT / 3, CARD_PANE_STYLE);
        configurePane(flop2, CARD_WIDTH, CARD_HEIGHT, BOARD_WIDTH / 5 + CARD_WIDTH + 2, BOARD_HEIGHT / 3, CARD_PANE_STYLE);
        configurePane(flop3, CARD_WIDTH, CARD_HEIGHT, BOARD_WIDTH / 5 + 2 * CARD_WIDTH + 4, BOARD_HEIGHT / 3, CARD_PANE_STYLE);
        configurePane(turn, CARD_WIDTH, CARD_HEIGHT, BOARD_WIDTH / 5 + 3 * CARD_WIDTH + 8, BOARD_HEIGHT / 3, CARD_PANE_STYLE);
        configurePane(river, CARD_WIDTH, CARD_HEIGHT, BOARD_WIDTH / 5 + 4 * CARD_WIDTH + 12, BOARD_HEIGHT / 3, CARD_PANE_STYLE);

        boardPane.getChildren().addAll(flop1, flop2, flop3, turn, river);
    }

    private void configurePane(Pane pane, int width, int height, int posX, int posY, String styleClass) {
        pane.setMinSize(width, height);
        pane.setLayoutX(posX);
        pane.setLayoutY(posY);
        pane.getStyleClass().add(styleClass);
    }

    private void configureLabel(Label label, String text, int posX, int posY, String styleClass) {
        label.setText(text);
        label.setLayoutX(posX);
        label.setLayoutY(posY);
        label.getStyleClass().add(styleClass);
    }

    private void configureButton(Button button, String text, int width, int height, int posX, int posY, String styleClass) {
        button.setText(text);
        button.setMinSize(CONTROL_WIDTH - 20, CONTROL_COMPONENT_HEIGHT);
        button.setMaxSize(CONTROL_WIDTH - 20, CONTROL_COMPONENT_HEIGHT);
        button.setPrefSize(CONTROL_WIDTH - 20, CONTROL_COMPONENT_HEIGHT);
        button.setLayoutX(posX);
        button.setLayoutY(posY);
        button.getStyleClass().add(styleClass);
    }

    private void configureListView(ListView lv, int width, int height, int posX, int posY, String styleClass) {
        lv.setMinSize(width, height);
        lv.setMaxSize(width, height);
        lv.setLayoutX(posX);
        lv.setLayoutY(posY);
        lv.getStyleClass().add(styleClass);
    }

    private void configureImageView(ImageView iv, int width, int height, String styleClass) {
        iv.minWidth(width);
        iv.minHeight(height);
        iv.maxWidth(width);
        iv.maxHeight(height);
        iv.setFitWidth(width);
        iv.setFitHeight(height);
        iv.getStyleClass().add(styleClass);
    }

    private void configureControlPane() {
        configurePane(controlPane, CONTROL_WIDTH, CONTROL_HEIGHT, RUN_WIDTH - CONTROL_WIDTH - 10, 0, "control-pane");
        configurePane(boardInfo, CONTROL_WIDTH - 10, CONTROL_HEIGHT / 2 - 5, 5, 0, "board-info");
        configurePane(playerAction, CONTROL_WIDTH - 10, CONTROL_HEIGHT / 2 - 5, 5, CONTROL_HEIGHT / 2, "player-action");

        var potLabelTitle = new Label();
        var potLabel = new Label();
        var actionList = new ListView<String>();
        configureLabel(potLabelTitle, "Pot size", 0, 10, "pot-label-title");
        configureLabel(potLabel, "0", 0, 40, "pot-label");
        configureListView(actionList, CONTROL_WIDTH - 20, CONTROL_HEIGHT / 4, 5, 70, "action-list");

        boardInfo.getChildren().addAll(Arrays.asList(potLabelTitle, potLabel, actionList));
    }

    private void configureTextField(TextField tf, String text, int width, int height, int posX, int posY, String styleClass) {
        tf.setText(text);
        tf.setPrefSize(width, height);
        tf.setLayoutX(posX);
        tf.setLayoutY(posY);
        tf.getStyleClass().add(styleClass);
    }

    private void runGame() {
        prepareBoard();
        displayPlayers();
        drawCards();

        if(board.getStacksNormal() >= board.getPlayersCount() - 1) {		//WITHOUT PREFLOP
            adjust();
            drawFlop();
            drawTurn();
            drawRiver();
        }
        else {
            setPlayerAction(board.getPlayers().get(0), PREFLOP);
            playRound(PREFLOP);
            adjust();
        }
    }

    private void prepareBoard() {
        board.clearDeck();
        board.slideButton();
        board.getDeck().shuffle();
        board.takeAnte();
        board.takeBlinds();
    }

    private void displayPlayers() {
        for (Player player : board.getPlayers()) {
            var pane = playerPanes.get(player.getSeat());
            pane.setVisible(true);
            System.out.println(pane.getChildren());

            var nickPane = (Pane) pane.getChildren().get(NICK_PANE);
            var nickLabel = new Label();
            configureLabel(nickLabel, player.getNickname(), 10, 10, "nick-label");
            nickPane.getChildren().add(nickLabel);

            var chipsPane = (Pane) pane.getChildren().get(CHIPS_PANE);
            var stackLabel = new Label();
            var potLabel = new Label();
            configureLabel(stackLabel, "" + (player.getChips() - player.getBet() - board.getAnte()), 10, 10, "stack-label");
            configureLabel(potLabel, "" + player.getBets(PREFLOP), PLAYER_WIDTH / 2 + 10, 10, "bet-label");
            chipsPane.getChildren().addAll(stackLabel, potLabel);
        }
    }

    private void adjust() {
        board.adjust();
    }

    private void drawCards() {
        board.drawCards();
        for (Player player : board.getPlayers()) {
            var card1 = (Pane) playerPanes.get(player.getSeat()).getChildren().get(CARD1_PANE);
            var card2 = (Pane) playerPanes.get(player.getSeat()).getChildren().get(CARD2_PANE);
            var card1Img = new ImageView(new Image("file:" + (new File("").getAbsolutePath()) +
                    PATH_TO_CARDS + player.getCard1().getName() + ".png"));
            configureImageView(card1Img, CARD_WIDTH, CARD_HEIGHT, CARD_PANE_STYLE);
            var card2Img = new ImageView(new Image("file:" + (new File("").getAbsolutePath()) +
                    PATH_TO_CARDS + player.getCard2().getName() + ".png"));
            configureImageView(card2Img, CARD_WIDTH, CARD_HEIGHT, CARD_PANE_STYLE);
            card1.getChildren().add(card1Img);
            card2.getChildren().add(card2Img);
        }
    }

    private void drawFlop() {
        board.drawFlop();
        var flop1 = (Pane) boardPane.getChildren().get(0);
        var flop2 = (Pane) boardPane.getChildren().get(0);
        var flop3 = (Pane) boardPane.getChildren().get(0);
        var flop1Img = new ImageView(new Image("file:" + (new File("").getAbsolutePath() +
                PATH_TO_CARDS + board.getFlop1().getName() + ".png")));
        var flop2Img = new ImageView(new Image("file:" + (new File("").getAbsolutePath() +
                PATH_TO_CARDS + board.getFlop2().getName() + ".png")));
        var flop3Img = new ImageView(new Image("file:" + (new File("").getAbsolutePath() +
                PATH_TO_CARDS + board.getFlop3().getName() + ".png")));
        configureImageView(flop1Img, CARD_WIDTH, CARD_HEIGHT, CARD_PANE_STYLE);
        configureImageView(flop2Img, CARD_WIDTH, CARD_HEIGHT, CARD_PANE_STYLE);
        configureImageView(flop3Img, CARD_WIDTH, CARD_HEIGHT, CARD_PANE_STYLE);
        flop1.getChildren().add(flop1Img);
        flop2.getChildren().add(flop2Img);
        flop3.getChildren().add(flop3Img);
    }

    private void drawTurn() {
        board.drawTurn();
        var turn = (Pane) boardPane.getChildren().get(0);
        var turnImg = new ImageView(new Image("file:" + (new File("").getAbsolutePath() +
                PATH_TO_CARDS + board.getTurn().getName() + ".png")));
        configureImageView(turnImg, CARD_WIDTH, CARD_HEIGHT, CARD_PANE_STYLE);
        turn.getChildren().add(turnImg);
    }

    private void drawRiver() {
        board.drawRiver();
        var river = (Pane) boardPane.getChildren().get(0);
        var riverImg = new ImageView(new Image("file:" + (new File("").getAbsolutePath() +
                PATH_TO_CARDS + board.getRiver().getName() + ".png")));
        configureImageView(riverImg, CARD_WIDTH, CARD_HEIGHT, CARD_PANE_STYLE);
        river.getChildren().add(riverImg);
    }

    private void playRound(int round) {
//        board.calculate(round);
        board.setAfterRaise(1);
        System.out.println(0);

        // === who begins === //
        if(round == PREFLOP)
            turn = board.getPlayersCount() == 2 ? board.getButton() : (board.getButton() + 3) % board.getPlayersCount();
        else turn = (board.getButton() + 1) % board.getPlayersCount();

        var task = new Task<Void>() {
            @Override
            public Void call() {
                while(board.getAfterRaise() < board.getPlayersCount()) {
                    System.out.println("Turn: " + turn);
                    var player = board.getPlayers().get(turn);
                    if(board.canMove(player))
                        setPlayerAction(player, round);
                    turn = (turn + 1) % board.getPlayers().size();
                }
                return null;
            }
        };
        new Thread(task).start();
        task.setOnSucceeded(event -> {
            System.out.println("OK!");
        });

    }

    private void setPlayerAction(Player player, int round) {
        var playerInfo = new Label();
        var sb = new StringBuilder();
        sb.append("Player turn: ").append(player.getNickname());
        configureLabel(playerInfo, sb.toString(), 10, 10, "");
        playerAction.getChildren().add(playerInfo);

        /* CAN'T CALL */
        if (board.checkOrRaise(player, round) || board.checkOrBet(player, round)) {
            System.out.println("Check or raise");
            var checkButton = new Button();
            configureButton(checkButton, "Check", CONTROL_WIDTH - 20, CONTROL_COMPONENT_HEIGHT, 10, CONTROL_HEIGHT / 3, CONTROL_BUTTON);
            checkButton.setOnMouseClicked(mouseEvent -> {
                applyCheck(player, round);
                return;
            });
            playerAction.getChildren().add(checkButton);
            if (board.checkOrRaise(player, round)) {
                var raiseSize = new TextField();
                var raiseButton = new Button();
                configureTextField(raiseSize, board.getBigBlind() + "",
                        CONTROL_WIDTH - 20, CONTROL_COMPONENT_HEIGHT, 10, CONTROL_HEIGHT/2 + 10, "");
                configureButton(raiseButton, "Raise", CONTROL_WIDTH - 20, CONTROL_COMPONENT_HEIGHT,
                        10, 2 * CONTROL_HEIGHT / 3 + 20, CONTROL_BUTTON);
                raiseButton.setOnMouseClicked(mouseEvent -> {
                    System.out.println("Raise");
                    applyBet(player, round, Integer.parseInt(raiseSize.getText()));
                    return;
                });
                playerAction.getChildren().add(raiseButton);
            } else {
                var betSize = new TextField();
                var betButton = new Button();
                configureTextField(betSize, "0", CONTROL_WIDTH - 20, CONTROL_COMPONENT_HEIGHT, 10, CONTROL_HEIGHT / 2 + 10, "");
                configureButton(betButton, "Bet", CONTROL_WIDTH - 20, CONTROL_COMPONENT_HEIGHT,
                        10, 2 * CONTROL_HEIGHT / 3 + 20, CONTROL_BUTTON);
                betButton.setOnMouseClicked(mouseEvent -> {
                    System.out.println("Bet");
                setPlayerAction(player, round);    applyBet(player, round, Integer.parseInt(betSize.getText()));
                    return;
                });
                playerAction.getChildren().add(betButton);
            }
        }
        else {
            System.out.println("Can call");
            var foldButton = new Button(); var callButton = new Button();

            configureButton(foldButton, "Fold", CONTROL_WIDTH - 20, CONTROL_HEIGHT/6, 10, CONTROL_HEIGHT/3, CONTROL_BUTTON);
            foldButton.setOnMouseClicked(mouseEvent -> {
                applyFold(player, round);
                return;
            });
            playerAction.getChildren().add(foldButton);

            configureButton(callButton, "Call", CONTROL_WIDTH - 20, CONTROL_HEIGHT/6, 10, CONTROL_HEIGHT/2, CONTROL_BUTTON);
            callButton.setOnMouseClicked(mouseEvent -> {
                applyCall(player, round);
                return;
            });
            playerAction.getChildren().add(callButton);

            if(!board.foldOrCall(player, round)) {
                System.out.println("...and raise");
                var raiseSize = new TextField();
                var raiseButton = new Button();

                configureTextField(raiseSize, board.getBigBlind() + "",
                        CONTROL_WIDTH - 20, CONTROL_HEIGHT/6, 10, 2*CONTROL_HEIGHT/3, "");
                playerAction.getChildren().add(raiseSize);

                configureButton(raiseButton, "Raise", CONTROL_WIDTH - 20, CONTROL_COMPONENT_HEIGHT,
                        10, 5 * CONTROL_COMPONENT_HEIGHT, CONTROL_BUTTON);
                raiseButton.setOnMouseClicked(mouseEvent -> {
                    applyBet(player, round, Integer.parseInt(raiseSize.getText()));
                    playerAction.getChildren().clear();
                    return;
                });
                playerAction.getChildren().add(raiseButton);
            }
        }
    }


    private int adjustBetSize(Player player, int round, int b) {
        if (board.lessThanBigBlindAbove(player, round))
            b = player.getChips() - player.addedToPot(round);
        else {
            if (b > player.getChips() - player.addedToPot(round))
                b = player.getChips() - player.addedToPot(round);
            else if (round == PREFLOP && b < board.getMaxBet() - board.getBigBlind())
                b = Math.min(board.getMaxBet() - board.getBigBlind(), player.getChips() - player.addedToPot(round));
            else if (round > PREFLOP && b < 2 * board.getMaxBet())
                b = Math.min(2 * board.getMaxBet(), player.getChips() - player.addedToPot(round));
        }
        return b;
    }

    private void applyBet(Player player, int round, int bet) {
        adjustBetSize(player, round, bet);
        System.out.println("Bet size: " + bet);
        board.addPot(bet - player.getBets(round));
        player.addBets(round, bet);
        board.setMaxBet(bet);
        board.resetAfterRaise();
        playerAction.getChildren().clear();
    }

    private void applyCall(Player player, int round) {
        if(board.allIn(player)) {
            board.addPot(board.getMaxBet() - player.getBets(round));
            player.addBets(round, player.getChips() - player.getBet());
            System.out.println("Bet " + player.getBets(round));
        }
        else {
            board.addPot(board.getMaxBet() - player.getBets(round));
            player.setBets(round, board.getMaxBet());
        }
        board.plusAfterRaise();
        playerAction.getChildren().clear();
    }

    private void applyFold(Player player, int round) {
        player.setPlaying(false);
        board.plusFolds();
        board.plusAfterRaise();
        playerAction.getChildren().clear();
    }

    private void applyCheck(Player player, int round) {
        board.plusAfterRaise();
        playerAction.getChildren().clear();
    }

    private void distributeFolds() {
        board.distributeFolds();
    }

    private void distribute() {
        board.distribute();
    }
}