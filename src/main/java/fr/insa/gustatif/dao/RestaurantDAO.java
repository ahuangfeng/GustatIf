package fr.insa.gustatif.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import fr.insa.gustatif.metier.modele.Restaurant;
import javax.persistence.PersistenceException;

public class RestaurantDAO implements BasicDAO<Restaurant> {

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
