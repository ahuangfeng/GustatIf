package dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import modele.Restaurant;

public class RestaurantDAO {
    
    public Restaurant findById(long id) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Restaurant restaurant = null;
        try {
            restaurant = em.find(Restaurant.class, id);
        }
        catch(Exception e) {
            throw e;
        }
        return restaurant;
    }
    
    public List<Restaurant> findAll() throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        List<Restaurant> restaurants = null;
        try {
            Query q = em.createQuery("SELECT r FROM Restaurant r");
            restaurants = (List<Restaurant>) q.getResultList();
        }
        catch(Exception e) {
            throw e;
        }
        
        return restaurants;
    }
}
