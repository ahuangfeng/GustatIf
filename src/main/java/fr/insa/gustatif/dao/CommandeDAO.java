package fr.insa.gustatif.dao;

import java.util.List;
import javax.persistence.EntityManager;
import fr.insa.gustatif.metier.modele.Commande;
import java.util.Date;
import javax.persistence.PersistenceException;

/**
 * DAO de Commande
 */
public class CommandeDAO implements BasicDAO<Commande> {

    /**
     * Retourne toutes les commandes ou uniquement celles en cours.
     *
     * @param uniquementEnCours si true, uniquement les commandes en cours
     * @return La liste des commandes en cours de livraison par un drône.
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public List<Commande> recupererCommandesFiltre(boolean uniquementEnCours) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        if (uniquementEnCours) {
            return em.createQuery("SELECT c FROM Commande c WHERE c.dateDeFin is null").getResultList();
        } else {
            return em.createQuery("SELECT c FROM Commande c").getResultList();
        }
    }

    /**
     * Valide une commande, c'est-à-dire marque sa date de livraison à
     * maintenant.
     *
     * @param commande La commande à valider.
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public void validerCommande(Commande commande) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        commande.setDateDeFin(new Date());
        em.merge(commande);
    }
}
