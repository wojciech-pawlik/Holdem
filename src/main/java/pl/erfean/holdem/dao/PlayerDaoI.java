package pl.erfean.holdem.dao;

import pl.erfean.holdem.model.Player;

public interface PlayerDaoI {
    void save(Player player);
    Player get(Long id);
    void update(Player player);
    void remove(Long id);
}
