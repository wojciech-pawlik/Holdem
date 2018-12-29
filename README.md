# Project description
Project includes classes neccessary to write poker (Texas Hold’em) algorithms:
- comparing hands (class 'Hand')
- run of the game (class 'Board')
- calculating chances

## How does checking hands work?
The idea is simple. There is up to seven cards which can build five-card hand. The best configuration which can be made of these cards is described by points. Player with the biggest number of points wins. Algorithm checks the hand in decreasing order: Royal Flush, Straight Flush, Four of a Kind, Full House, Flush, Straight, Three of a Kind, Two Pair, One Pair, High Card. These hands can be different inside (e.g. Player 1 has Two Pair: Kings and Jacks with Ace kicker, Player 2 has the same Two Pair but with Queen high, Player 3 has Kings and Sevens - Player 1 wins), therefore solution based on points seems to be the most logical.
There are a few variables which helps to describe what's going on these cards: how many cards in one suit there are, how many cards with the same value exist and what is this value. I also created classes which compare cards in different ways. It's worth pointing out that Ace can be put at two different values (as card above the King or below the Two).

## Things to do:
- 'Board', 'Hand': make different names of hand in dependence of difference between best and second best hand
- make a graphical interface (JavaFX)
- create connection with a database (Hibernate)
- make a 'Tournament' class
