package fr.insa.gustatif.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import fr.insa.gustatif.metier.modele.Commande;
import fr.insa.gustatif.metier.modele.Drone;
import java.util.Date;
import java.util.Iterator;
import javax.persistence.PersistenceException;

/**
 * DAO de Commande
 */
public class CommandeDAO implements BasicDAO<Commande> {

    /**
     * Retourne toutes les commandes en cours de livraison par un drône.
     *
     * @return La liste des commandes en cours de livraison par un drône.
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public List<Commande> recupererCommandesEnCoursParDrone() throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Query q = em.createQuery("SELECT c FROM Commande c WHERE c.dateDeFin is null");
        List<Commande> lc = q.getResultList();
        for (Iterator<Commande> it = lc.iterator(); it.hasNext();) {
            Commande commande = it.next();
            if (null == commande.getLivreur() || !(commande.getLivreur() instanceof Drone)) {
                it.remove();
            }
        }
        return lc;
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
