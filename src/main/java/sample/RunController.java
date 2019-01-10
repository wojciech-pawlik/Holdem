package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import poker.Board;
import poker.Player;

import java.net.URL;
import java.util.*;

import static javafx.scene.paint.Color.BLACK;

public class RunController implements Initializable {
    static final int RUN_WIDTH = 1080;
    static final int RUN_HEIGHT = 640;
    private static final int PLAYER_WIDTH = 100;
    private static final int PLAYER_HEIGHT = 100;
    private static final int BOARD_WIDTH = 300;
    private static final int BOARD_HEIGHT = 200;

    private Board board;
    private ArrayList<Pane> panes;

    @FXML private Pane player0,player1,player2,player3,player4,player5,player6,player7,player8,player9;
    @FXML private Pane boardPane;

    public RunController(Board board) {
        this.board = board;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        boardPane.setMinSize(BOARD_WIDTH, BOARD_HEIGHT);
        boardPane.setLayoutX(RUN_WIDTH/2 - BOARD_WIDTH/2);
        boardPane.setLayoutY(RUN_HEIGHT/2 - BOARD_HEIGHT/2);
        Pane[] panesArray = {player0, player1, player2, player3, player4, player5, player6, player7, player8, player9};
        panes = new ArrayList<>(10);
        panes.addAll(Arrays.asList(panesArray));
        Platform.runLater(() -> {
            setSeats();
        });
    }

    private void setSeats() {
        int x = RUN_WIDTH/2;
        int y = RUN_HEIGHT/2;
        for(int i = 0; i < board.getPlacesCount(); i++) {
            Pane pane = panes.get(i);
            x += RUN_WIDTH/3 * Math.sin(2*Math.PI/board.getPlacesCount()*i);
            y -= RUN_HEIGHT/3 * Math.cos(2*Math.PI/board.getPlacesCount()*i);
            pane.setMinSize(PLAYER_WIDTH, PLAYER_HEIGHT);
            pane.setLayoutX(x - PLAYER_WIDTH/2);
            pane.setLayoutY(y - PLAYER_HEIGHT/2);
            x = RUN_WIDTH/2;
            y = RUN_HEIGHT/2;
        }
    }
}
