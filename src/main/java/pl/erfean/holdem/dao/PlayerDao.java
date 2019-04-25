package pl.erfean.holdem.dao;

import org.springframework.stereotype.Repository;
import pl.erfean.holdem.model.Player;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class PlayerDao implements PlayerDaoI {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Player player) {
        entityManager.persist(player);
    }

    public Player get(Long id) {
        return entityManager.find(Player.class, id);
    }

    @Transactional
    public void update(Player player) {
        entityManager.merge(player);
    }

    @Transactional
    public void remove(Long id) {
        entityManager.remove(get(id));
    }
}
