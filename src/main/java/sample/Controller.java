package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Getter;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import poker.Board;
import poker.Player;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static sample.RunController.RUN_HEIGHT;
import static sample.RunController.RUN_WIDTH;

@Getter
public class Controller implements Initializable {
    @FXML private HBox mainPanel;

    @FXML private ChoiceBox<Integer> seatsChoiceBox;
    @FXML private TextField blindsTextField;
    @FXML private CheckBox antesCheckBox;
    @FXML private TextField antesTextField;

    @FXML private TableView<Player> playersTable;

    @FXML private TableColumn<Player, Integer> idColumn;
    @FXML private TableColumn<Player, String> nicknameColumn;
    @FXML private TableColumn<Player, Integer> chipsColumn;
    @FXML private TableColumn<Player, String> notesColumn;

    @FXML private Button newRunButton;

    private ObservableList<Player> playersList;

    private static int RUN_ID = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        seatsChoiceBox.getItems().addAll(2,3,4,5,6,7,8,9,10);
        seatsChoiceBox.setValue(2);
        blindsTextField.setText(""+10);
        antesTextField.setText(""+0.1);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nicknameColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        chipsColumn.setCellValueFactory(new PropertyValueFactory<>("chips"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        playersList = getPlayers();
        playersTable.setItems(playersList);

        playersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public ObservableList<Player> getPlayers() {
        ObservableList<Player> list = FXCollections.observableArrayList();
        Session session = new Configuration()
                .configure("/hibernate.cfg.xml")
                .addAnnotatedClass(Player.class)
                .buildSessionFactory()
                .getCurrentSession();
        session.beginTransaction();
        List<Player> players = session.createQuery("FROM Player", Player.class).getResultList();
        list.addAll(players);
        session.close();
        return list;
    }

    @FXML
    public void showAddPlayerDialog() {
        var fxmlLoader = createLoader("edit.fxml");
        var dialog = createDialog(fxmlLoader, "Add new contact");

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            EditController editController = fxmlLoader.getController();
            Player player = editController.getNewPlayer();
            createSession(player);
            refreshTable(player);
        }
    }

    @FXML
    public void showEditPlayerDialog() {
        Player selectedPlayer = playersTable.getSelectionModel().getSelectedItem();
        if(selectedPlayer == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No player selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the player you want to edit.");
            alert.showAndWait();
            return;
        }

        var fxmlLoader = createLoader("edit.fxml");
        var dialog = createDialog(fxmlLoader, "Edit contact");
        EditController editController = fxmlLoader.getController();
        editController.editPlayer(selectedPlayer);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            editController.updatePlayer(selectedPlayer);
            createSession(selectedPlayer);
            refreshTable(selectedPlayer);
        }
    }

    @FXML
    public void deletePlayer() {
        Player selectedPlayer = playersTable.getSelectionModel().getSelectedItem();
        if(selectedPlayer == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No player selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the player you want to delete.");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete contact");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to delete player: " + selectedPlayer.getNickname() + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            Session session = new Configuration()
                    .configure("/hibernate.cfg.xml")
                    .addAnnotatedClass(Player.class)
                    .buildSessionFactory().getCurrentSession();
            session.beginTransaction();
            session.remove(selectedPlayer);
            session.getTransaction().commit();
            session.close();
            playersTable.setItems(getPlayers());
            playersTable.getSelectionModel().selectLast();
        }
    }

    @FXML
    public void newRunDialog() {
        List<Player> selectedPlayers = playersTable.getSelectionModel().getSelectedItems();
        for(Player player : selectedPlayers) System.out.println("Player " + player.getNickname() + " seat: " + player.getSeat());
        int seats = Integer.parseInt(seatsChoiceBox.getValue().toString());
        double antes = 0.0;
        int blinds = Integer.parseInt(blindsTextField.getText());
        if(antesCheckBox.isSelected()) antes = (double) blinds * Double.parseDouble(antesTextField.getText());
        int ante = (int) antes;

        if(selectedPlayers == null || selectedPlayers.size() < 2) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Too few players!");
            alert.setHeaderText(null);
            alert.setContentText("Please select at least two players.");
            alert.showAndWait();
            return;
        }
        else if(selectedPlayers.size() > seats) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Too many players!");
            alert.setHeaderText(null);
            alert.setContentText("Please select at most players as number of seats.");
            alert.showAndWait();
            return;
        }

        var fxmlLoader = createLoader("seats.fxml");
        var dialog = createDialog(fxmlLoader, "Seat players");
        SeatsController seatsController = fxmlLoader.getController();
        seatsController.manageSeats(selectedPlayers);
        var players = new ArrayList<Player>();

        Optional<ButtonType> result = dialog.showAndWait();
        if(!seatsController.checkIfCorrectlySelected(seats)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invalid seats of players!");
            alert.setHeaderText(null);
            alert.setContentText("Please select different seats from 0 to " + (seats-1));
            alert.showAndWait();
            return;
        }
        if(result.isPresent() && result.get() == ButtonType.OK) {
            players.addAll(seatsController.getPlayers());
        }

        var board = new Board(seats, selectedPlayers.size(), 0, blinds, ante, players);
        try {
            var loader = new FXMLLoader(getClass().getResource("/run.fxml"));
            var runController = new RunController(board);
            loader.setController(runController);
            AnchorPane runPane = loader.load();
            var stage = new Stage();
            stage.setTitle("Run #" + ++RUN_ID);
            stage.setScene(new Scene(runPane, RUN_WIDTH,RUN_HEIGHT));
            stage.show();

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadDialog(Dialog<ButtonType> dialog, FXMLLoader fxmlLoader) {
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog.");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
    }

    private void refreshTable(Player player) {
        playersTable.setItems(getPlayers());
        playersTable.getSelectionModel().select(player);
        playersTable.scrollTo(player);
    }

    private void createSession(Player player) {
        Session session = new Configuration()
                .configure("/hibernate.cfg.xml")
                .addAnnotatedClass(Player.class)
                .buildSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.update(player);
        session.getTransaction().commit();
        session.close();
    }

    private Dialog createDialog(FXMLLoader fxmlLoader, String title) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPanel.getScene().getWindow());
        dialog.setTitle(title);
        loadDialog(dialog, fxmlLoader);
        return dialog;
    }

    private FXMLLoader createLoader(String filename) {
        var fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/" + filename));
        return fxmlLoader;
    }
}
