package pl.erfean.holdem.generators;

import com.opencsv.CSVWriter;
import pl.erfean.holdem.model.Board;
import pl.erfean.holdem.model.Card;
import pl.erfean.holdem.model.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BoardGenerator {
    private static final String finalPath = "boards.csv";
    private static final int numberOfPlayers = 3;
    private static final int records = 20;

    public static void main(String[] args) {
        Card card = new Card(0,5);
        System.out.println(card.hashCode());
        generateBoards(numberOfPlayers, 0, records, "boards0.csv");
        generateBoards(numberOfPlayers, 1, records, "boards1.csv");
        generateBoards(numberOfPlayers, 2, records, "boards2.csv");
    }

    private static void generateBoards(int numberOfPlayers, int round, int records, String filePath) {
        var board = new Board(numberOfPlayers);
        board.getDeck().shuffle();
        for(int i = 0; i < numberOfPlayers; i++) {
            board.getPlayers().add(new Player((long)i, "Player " + i, 10000, ""));
            board.setPlayersCount(board.getPlayersCount()+1);
        }
        List<String> finalList = new ArrayList<>();
        double[] chances;

        var file = new File(filePath);
        try {
            // create file if not exists
            try {
                file.createNewFile();
            } catch (FileAlreadyExistsException e) {
                e.printStackTrace();
            }
            // create FileWriter object with file as parameter
            var outputFile = new FileWriter(file);

            // create CSVWriter object FileWriter object as parameter
            var writer = new CSVWriter(outputFile);

            for(int i = 0; i < records; i++) {
                IntStream.range(0, round+1).forEach(r -> board.drawCards(r));
                for(Player player : board.getPlayers()){
                    finalList.add(player.getCard(0).hashCode()+"");
                    finalList.add(player.getCard(1).hashCode()+"");
                }
                for(Card card : board.getCards())
                    finalList.add(card.hashCode()+"");

                chances = board.calculate(round)[0];
                for(double chance : chances) finalList.add(chance + "");
                System.out.println(finalList);
                writer.writeNext(finalList.toArray(new String[0]));
                finalList.clear();
                board.getDeck().shuffle();
            }

            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
