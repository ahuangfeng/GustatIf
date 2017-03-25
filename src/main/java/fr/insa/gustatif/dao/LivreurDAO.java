/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insa.gustatif.dao;

import fr.insa.gustatif.metier.modele.Livreur;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 */
public class LivreurDAO implements BasicDAO<Livreur> {

    public List<Livreur> recupererCapablesDeLivrer(double poids) {
        try {
            EntityManager em = JpaUtil.obtenirEntityManager();
            Query q = em.createQuery("SELECT l FROM Livreur l WHERE l.disponible = true and l.capaciteMax >= :poids");
            q.setParameter("poids", poids);
            return q.getResultList();
        } catch (PersistenceException e) {
            return null;
        }
    }

    
    public void terminerCommandeEnCours(Livreur livreur) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        livreur.terminerCommandeEnCours();
        em.merge(livreur);
    }
}
