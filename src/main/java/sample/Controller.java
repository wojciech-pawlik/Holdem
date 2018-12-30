package sample;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import lombok.Getter;
import org.hibernate.*;
import org.hibernate.boot.model.source.spi.PluralAttributeElementNature;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.SessionFactoryServiceRegistryBuilder;
import org.hibernate.sql.Select;
import poker.Player;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    @FXML private HBox mainPanel;

    @FXML private TableView<Player> playersTable;

    @FXML private TableColumn idColumn;
    @FXML private TableColumn nicknameColumn;
    @FXML private TableColumn chipsColumn;
    @FXML private TableColumn notesColumn;
//    @FXML private TableColumn selectColumn;

    private ObservableList<Player> playersList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<Player, Integer>("id"));
        nicknameColumn.setCellValueFactory(new PropertyValueFactory<Player, String>("nickname"));
        chipsColumn.setCellValueFactory(new PropertyValueFactory<Player, Integer>("chips"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<Player, String>("notes"));
//        selectColumn.setCellValueFactory(new PropertyValueFactory<Player, Integer>("check"));
        playersTable.setItems(getPlayers());
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
        FXMLLoader fxmlLoader = new FXMLLoader();
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
        FXMLLoader fxmlLoader = new FXMLLoader();
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
