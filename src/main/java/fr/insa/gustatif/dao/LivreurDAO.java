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
 * DAO de Livreur
 */
public class LivreurDAO implements BasicDAO<Livreur> {

    /**
     * Renvoi la liste de tous les livreurs disponibles dont la capacité est
     * supérieure à poids.
     *
     * @param poids Le poids de la commande que les livreurs doivent être
     * capables de transporter.
     * @return La liste des livreurs capablies de livrer.
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public List<Livreur> recupererCapablesDeLivrer(double poids) throws PersistenceException {
        try {
            EntityManager em = JpaUtil.obtenirEntityManager();
            Query q = em.createQuery("SELECT l FROM Livreur l WHERE l.disponible = true and l.capaciteMax >= :poids");
            q.setParameter("poids", poids);
            return q.getResultList();
        } catch (PersistenceException e) {
            return null;
        }
    }

    /**
     * Termine la livraison en cours du livreur.
     *
     * @param livreur Le livreur à qui il faut terminer la livraison en cours.
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public void terminerCommandeEnCours(Livreur livreur) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        livreur.terminerCommandeEnCours();
        em.merge(livreur);
    }

    /**
     * Change l'attribut <i>disponible</i> du livreur, et flush immédiatement.
     *
     * @param livreur Le livreur à qui changer la disponibilité.
     * @param bisponibilite La nouvelle disponibilité du livreur.
     */
    public void setDisponible(Livreur livreur, boolean bisponibilite) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        livreur.setDisponible(bisponibilite);
        em.merge(livreur);
        em.flush();
    }
}
