package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import lombok.Getter;
import poker.Player;

import java.util.List;

@Getter
public class SeatsController {
    private ObservableList<Player> players;

    @FXML private TableView<Player> playersTableSeats;
    @FXML private TableColumn playerColumn;
    @FXML private TableColumn<Player, Number> seatColumn;

    @FXML private TextField seatField;

    public void initialize() {
        players = FXCollections.observableArrayList();
        playersTableSeats.setItems(players);
        seatColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSeat()));
    }

    public void manageSeats(List<Player> list) {
        players.addAll(list);
        for(int i = 0; i < players.size(); i++) players.get(i).setSeat(i);
    }

    @FXML
    public void setSeat() {
        Player selectedPlayer = playersTableSeats.getSelectionModel().getSelectedItem();
        selectedPlayer.setSeat(Integer.parseInt(seatField.getText()));
        playersTableSeats.refresh();
        System.out.println(selectedPlayer.getSeat());
    }

    public boolean checkIfCorrectlySelected(int tableSeats) {
        if(players.get(players.size()-1).getSeat() < 0 || players.get(players.size()-1).getSeat() >= tableSeats) {
            System.out.println("false");
            return false;
        }
        for(int i = 0; i < players.size()-1; i++) {
            if(players.get(i).getSeat() < 0 || players.get(i).getSeat() >= tableSeats) {
                System.out.println("false");
                return false;
            }
            for(int j = i+1; j < players.size(); j++)
                if(players.get(i).getSeat() == players.get(j).getSeat()) {
                    System.out.println("false");
                    return false;
                }
        }
        System.out.println("true");
        return true;
    }
}
