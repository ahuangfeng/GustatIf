package fr.insa.gustatif.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import fr.insa.gustatif.metier.modele.Restaurant;
import javax.persistence.PersistenceException;

public class RestaurantDAO {

    public void creerRestaurant(Restaurant restaurant) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        em.persist(restaurant);
    }

    public Restaurant findById(long id) {
        try {
            EntityManager em = JpaUtil.obtenirEntityManager();
            return em.find(Restaurant.class, id);
        } catch (PersistenceException e) {
            return null;
        }
    }

    public List<Restaurant> findAll() {
        try {
            EntityManager em = JpaUtil.obtenirEntityManager();
            Query q = em.createQuery("SELECT r FROM Restaurant r");
            return q.getResultList();
        } catch (PersistenceException e) {
            return null;
        }
    }

    public List<Restaurant> findAllSortedByName() {
        try {
            EntityManager em = JpaUtil.obtenirEntityManager();
            Query q = em.createQuery("SELECT r FROM Restaurant r ORDER BY r.denomination ASC");
            return q.getResultList();
        } catch (PersistenceException e) {
            return null;
        }
    }

    public Long getRestaurantIdByProduit(Long idProduit) { // TODO : Verifier le JPQL
        try {
            EntityManager em = JpaUtil.obtenirEntityManager();
            Query q = em.createQuery("SELECT r.id FROM Restaurant r JOIN r.produits p where p.id=:idProduit");
            q.setParameter("idProduit", idProduit);
            return (Long) q.getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }
}
