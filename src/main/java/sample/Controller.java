package sample;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import poker.Player;

public class Controller {
    @FXML
    private TableView<Player> tableView;

    private ObservableList<Player> playerObservableList;

    public void initialize() {
        SessionFactory factory = new Configuration()
                .configure("/hibernate.cfg.xml")
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();

        Session session = factory.getCurrentSession();

        try {
            // CREATING OBJECT //
            Player player = new Player(0,"Andrzej", 5000);

        } finally {
            factory.close();
        }
    }

    @FXML
    public void showAddPlayerDialog() {
        System.out.println("showAddPlayerDialog");
    }

    @FXML
    public void showEditPlayerDialog() {
        System.out.println("showEditPlayerDialog");
    }

    @FXML
    public void deletePlayer() {
        System.out.println();
    }
}
