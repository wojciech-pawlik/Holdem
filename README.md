# Project description
Project includes classes neccessary to write poker (Texas Hold’em) algorithms:
- comparing hands (class 'Hand')
- run of the game (class 'Board')
- calculating chances

## How does checking hands work?
The idea is simple. There is up to seven cards which can build five-card hand. The best configuration which can be made of these cards is described by points. Player with the biggest number of points wins. Algorithm checks the hand in decreasing order: Royal Flush, Straight Flush, Four of a Kind, Full House, Flush, Straight, Three of a Kind, Two Pair, One Pair, High Card. These hands can be different inside (e.g. Player 1 has Two Pair: Kings and Jacks with Ace kicker, Player 2 has the same Two Pair but with Queen high, Player 3 has Kings and Sevens - Player 1 wins), therefore solution based on points seems to be the most logical.
There are a few variables which helps to describe what's going on these cards: how many cards in one suit there are, how many cards with the same value exist and what is this value. I also created classes which compare cards in different ways. It's worth pointing out that Ace can be put at two different values (as card above the King or below the Two).

## Things to do:
- Complete GUI.
- Rewrite card system to a set for optimizing algorithms.

# Technical problems which I have dealt with

## Loading parameters to new Controller
Standard way of creating new window failed. I tried to load parameter (Board) to Run window:
- I had created a Board setter in class RunController then I called the setter in main Controller. Everything was OK up to my attempt to set up Panes depending on count of places on board. In method setSeats() which deploys panes I got null when I called it in initialize() method.
- I moved setSeats() to Board setter but again it caused NullPointerException.
- I created constructor of RunController with parameter of Board but it caused problem with loading FXML file.
- Lambda expression in initialize() didn't help:
```
Platform.runLater(() -> {
            setSeats();
        });
```
Fortunately, stackoverflow has came with help.
1. I deleted fx:controller from run.fxml and added fx:id.
2. I set controller to FXMLLoader, called AnchorPane from run.fxml by id and used it instead of root:
```
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
```
