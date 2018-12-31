package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
//    @FXML private TableColumn selectColumn;

    @FXML private Button newRunButton;

    private ObservableList<Player> playersList;

    private static int RUN_ID = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        seatsChoiceBox.getItems().addAll(2,3,4,5,6,7,8,9,10);
        seatsChoiceBox.setValue(2);
        blindsTextField.setText(""+10);
        antesTextField.setText(""+0.1);

        idColumn.setCellValueFactory(new PropertyValueFactory<Player, Integer>("id"));
        nicknameColumn.setCellValueFactory(new PropertyValueFactory<Player, String>("nickname"));
        chipsColumn.setCellValueFactory(new PropertyValueFactory<Player, Integer>("chips"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<Player, String>("notes"));
//        selectColumn.setCellValueFactory(new PropertyValueFactory<Player, Integer>("check"));
        playersTable.setItems(getPlayers());

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
        System.out.println("showAddPlayerDialog");
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPanel.getScene().getWindow());
        dialog.setTitle("Add new contact");
        var fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/edit.fxml"));
        loadDialog(dialog, fxmlLoader);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            EditController editController = fxmlLoader.getController();
            Player player = editController.getNewPlayer();
            Session session = new Configuration()
                    .configure("/hibernate.cfg.xml")
                    .addAnnotatedClass(Player.class)
                    .buildSessionFactory().getCurrentSession();
            session.beginTransaction();
            session.save(player);
            session.getTransaction().commit();
            session.close();
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

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPanel.getScene().getWindow());
        dialog.setTitle("Edit contact");
        var fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/edit.fxml"));
        loadDialog(dialog, fxmlLoader);
        EditController editController = fxmlLoader.getController();
        editController.editPlayer(selectedPlayer);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            editController.updatePlayer(selectedPlayer);
            Session session = new Configuration()
                    .configure("/hibernate.cfg.xml")
                    .addAnnotatedClass(Player.class)
                    .buildSessionFactory().getCurrentSession();
            session.beginTransaction();
            session.update(selectedPlayer);
            session.getTransaction().commit();
            session.close();
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

        var dialog = new Dialog<ButtonType>();
        dialog.initOwner(mainPanel.getScene().getWindow());
        dialog.setTitle("Seat players: ");
        var fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/seats.fxml"));
        loadDialog(dialog, fxmlLoader);
        SeatsController seatsController = fxmlLoader.getController();
        seatsController.manageSeats(selectedPlayers);
        var players = new ArrayList<Player>();

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK && seatsController.checkIfCorrectlySelected(seats)) {
            players.addAll(seatsController.getPlayers());
        }
        else if(result.isPresent() && result.get() == ButtonType.OK) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invalid seats of players!");
            alert.setHeaderText(null);
            alert.setContentText("Please select different seats from 0 to " + (seats-1));
            alert.showAndWait();
            return;
        }

        var board = new Board(seats, selectedPlayers.size(), 0, blinds, ante, players);
        Parent root;
        try {
            root = fxmlLoader.load(getClass().getResource("/run.fxml"));
            var stage = new Stage();
            stage.setTitle("Run #" + ++RUN_ID);
            stage.setScene(new Scene(root, 1080,640));
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
}
