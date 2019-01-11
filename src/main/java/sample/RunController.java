package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import poker.Board;

import java.io.File;
import java.net.URL;
import java.util.*;

public class RunController implements Initializable {
    private static final String PATH_TO_BOARD = "\\src\\main\\java\\poker\\board-components\\board.jpg";
    static final int RUN_WIDTH = 1380;
    static final int RUN_HEIGHT = 640;
    private static final int PLAYER_WIDTH = 200;
    private static final int PLAYER_HEIGHT = 120;
    private static final int BOARD_WIDTH = 600;
    private static final int BOARD_HEIGHT = 280;
    private static final int CONTROL_WIDTH = 300;
    private static final int CONTROL_HEIGHT = 600;
    private static final String CARD_PANE_STYLE = "card-pane";
    private static final String NICK_PANE_STYLE = "nick-pane";
    private static final String CHIPS_PANE_STYLE = "chips-pane";
    private static final String BOARD_IMAGE_STYLE = "board-image";

    private Board board;
    private ArrayList<Pane> playerPanes;

    @FXML private AnchorPane runController;

    @FXML private Pane boardPane, controlPane, boardInfo, playerAction;

    public RunController(Board board) {
        this.board = board;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureBoardPane();
        configureControlPane();
        playerPanes = new ArrayList<>(board.getPlacesCount());
        setSeats();
    }

    private void setSeats() {
        int x = (RUN_WIDTH - CONTROL_WIDTH)/2;
        int y = RUN_HEIGHT/2;
        for(int i = 0; i < board.getPlacesCount(); i++) {
            System.out.println(i);
            x += (RUN_WIDTH - CONTROL_WIDTH)/3 * Math.sin(2*Math.PI/board.getPlacesCount()*i);
            y -= RUN_HEIGHT/3 * Math.cos(2*Math.PI/board.getPlacesCount()*i);

            var pane = new Pane();
            configurePane(pane, PLAYER_WIDTH, PLAYER_HEIGHT, x - PLAYER_WIDTH/2, y - PLAYER_HEIGHT/2, "seat");
            setPlayerView(pane);
            playerPanes.add(pane);
            runController.getChildren().add(pane);

            x = (RUN_WIDTH - CONTROL_WIDTH)/2;
            y = RUN_HEIGHT/2;
        }
    }

    private void setPlayerView(Pane pane) {
        var card1 = new Pane(); var card2 = new Pane(); var nickname = new Pane(); var chips = new Pane();

        configurePane(card1, PLAYER_WIDTH/3, 2*PLAYER_HEIGHT/3, PLAYER_WIDTH/6, 0, CARD_PANE_STYLE);
        configurePane(card2, PLAYER_WIDTH/3, 2*PLAYER_HEIGHT/3, PLAYER_WIDTH/2, 0, CARD_PANE_STYLE);
        configurePane(nickname, PLAYER_WIDTH, PLAYER_HEIGHT/6, 0, 2*PLAYER_HEIGHT/3, NICK_PANE_STYLE);
        configurePane(chips, PLAYER_WIDTH, PLAYER_HEIGHT/6, 0, 5*PLAYER_HEIGHT/6, CHIPS_PANE_STYLE);

        pane.getChildren().add(card1);
        pane.getChildren().add(card2);
        pane.getChildren().add(nickname);
        pane.getChildren().add(chips);
    }

    private void configureBoardPane() {
        boardPane.setMinSize(BOARD_WIDTH, BOARD_HEIGHT);
        boardPane.setLayoutX((RUN_WIDTH - CONTROL_WIDTH)/2 - BOARD_WIDTH/2);
        boardPane.setLayoutY(RUN_HEIGHT/2 - BOARD_HEIGHT/2);
        System.out.println(new File("").getAbsolutePath() + PATH_TO_BOARD);
        ImageView boardImage = new ImageView(new Image("file:" + new File("").getAbsolutePath()
                + PATH_TO_BOARD, BOARD_WIDTH, BOARD_HEIGHT, false, false));
        configureImageView(boardImage, BOARD_IMAGE_STYLE);
        boardPane.getChildren().add(boardImage);

        var flop1 = new Pane(); var flop2 = new Pane(); var flop3 = new Pane(); var turn = new Pane(); var river = new Pane();

        configurePane(flop1, PLAYER_WIDTH/3, 2*PLAYER_HEIGHT/3, BOARD_WIDTH/5, BOARD_HEIGHT/3, CARD_PANE_STYLE);
        configurePane(flop2, PLAYER_WIDTH/3, 2*PLAYER_HEIGHT/3, BOARD_WIDTH/5+PLAYER_WIDTH/3+2, BOARD_HEIGHT/3, CARD_PANE_STYLE);
        configurePane(flop3, PLAYER_WIDTH/3, 2*PLAYER_HEIGHT/3, BOARD_WIDTH/5+2*PLAYER_WIDTH/3+4, BOARD_HEIGHT/3, CARD_PANE_STYLE);
        configurePane(turn, PLAYER_WIDTH/3, 2*PLAYER_HEIGHT/3, BOARD_WIDTH/5+3*PLAYER_WIDTH/3+8, BOARD_HEIGHT/3, CARD_PANE_STYLE);
        configurePane(river, PLAYER_WIDTH/3, 2*PLAYER_HEIGHT/3, BOARD_WIDTH/5+4*PLAYER_WIDTH/3+12, BOARD_HEIGHT/3, CARD_PANE_STYLE);

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

    private void configureListView(ListView lv, int width, int height, int posX, int posY, String styleClass) {
        lv.setMinSize(width, height);
        lv.setMaxSize(width, height);
        lv.setLayoutX(posX);
        lv.setLayoutY(posY);
        lv.getStyleClass().add(styleClass);
    }

    private void configureImageView(ImageView iv, String styleClass) {
        iv.minWidth(BOARD_WIDTH);
        iv.minHeight(BOARD_HEIGHT);
        iv.getStyleClass().add(styleClass);
    }

    private void configureControlPane() {
        configurePane(controlPane, CONTROL_WIDTH, CONTROL_HEIGHT, RUN_WIDTH - CONTROL_WIDTH - 10, 10, "control-pane");
        configurePane(boardInfo, CONTROL_WIDTH - 10, CONTROL_HEIGHT/2 - 5, 5,5, "board-info");
        configurePane(playerAction, CONTROL_WIDTH - 10,CONTROL_HEIGHT/2 - 5,5,CONTROL_HEIGHT/2 + 5,"player-action");

        var potLabelTitle = new Label(); var potLabel = new Label(); var actionList = new ListView<String>();
        configureLabel(potLabelTitle, "Pot size", 0,10,"pot-label-title");
        configureLabel(potLabel, "0", 0,40,"pot-label");
        configureListView(actionList, CONTROL_WIDTH - 20, CONTROL_HEIGHT/4, 5, 70, "action-list");

        boardInfo.getChildren().addAll(Arrays.asList(potLabelTitle, potLabel, actionList));
    }

    private void prepareBoard() {

    }

    private void displayPlayers() {

    }

    private void adjust() {

    }

    private void drawCards() {

    }

    private void drawFlop() {

    }

    private void drawTurn() {

    }

    private void drawRiver() {

    }

    private void preflop() {

    }

    private void postflop(int round) {

    }
}
