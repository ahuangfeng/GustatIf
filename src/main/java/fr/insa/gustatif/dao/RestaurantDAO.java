package fr.insa.gustatif.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import fr.insa.gustatif.metier.modele.Restaurant;

public class RestaurantDAO {

    public void creerRestaurant(Restaurant restaurant) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        em.persist(restaurant);
    }

    public Restaurant findById(long id) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Restaurant restaurant = null;
        try {
            restaurant = em.find(Restaurant.class, id);
        } catch (Exception e) {
            throw e;
        }
        return restaurant;
    }

    public List<Restaurant> findAll() throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Query q = em.createQuery("SELECT r FROM Restaurant r");
        return q.getResultList();
    }

    public List<Restaurant> findAllSortedByName() throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Query q = em.createQuery("SELECT r FROM Restaurant r ORDER BY r.denomination ASC");
        return q.getResultList();
    }
    
    public long getRestaurantIdByProduit(Long idProduit){   //TODO : Verifier le JPQL
        EntityManager em = JpaUtil.obtenirEntityManager();
        Query q = em.createQuery("SELECT r.id FROM Restaurant r join r.produits p where p.id="+idProduit);
        long idRestaurant = (Long) q.getSingleResult();
        return idRestaurant;
    }
}
