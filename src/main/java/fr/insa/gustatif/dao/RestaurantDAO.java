package fr.insa.gustatif.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import fr.insa.gustatif.metier.modele.Restaurant;
import javax.persistence.PersistenceException;

/**
 * DAO de Restaurant
 */
public class RestaurantDAO implements BasicDAO<Restaurant> {

    /**
     * Récupère l'ID du restaurant possédant le produit ayant cet ID.
     * @param idProduit l'ID du produit.
     * @return L'ID du restaurant possédant ce produit, ou null.
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public Long getRestaurantIdByProduit(Long idProduit) throws PersistenceException {
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
