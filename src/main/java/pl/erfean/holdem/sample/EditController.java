package pl.erfean.holdem.sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import pl.erfean.holdem.model.Player;

public class EditController {
    @FXML private TextField nicknameField;
    @FXML private TextField chipsField;
    @FXML private TextField notesField;

    @FXML
    public Player getNewPlayer() {
        return new Player(1L,nicknameField.getText(),Integer.parseInt(chipsField.getText()),notesField.getText());
    }

    @FXML
    public void editPlayer(Player player) {
        nicknameField.setText(player.getNickname());
        chipsField.setText(""+player.getChips());
        notesField.setText(player.getNotes());
    }

    @FXML
    public Player updatePlayer(Player player) {
        player.setNickname(nicknameField.getText());
        player.setChips(Integer.parseInt(chipsField.getText()));
        player.setNotes(notesField.getText());
        return player;
    }
}
