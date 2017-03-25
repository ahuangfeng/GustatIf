package fr.insa.gustatif.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import fr.insa.gustatif.metier.modele.Commande;
import fr.insa.gustatif.metier.modele.Drone;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;

public class CommandeDAO implements BasicDAO<Commande> {

    public boolean modifierCommande(Commande commande) { // TODO commande en parametre ou id?
        EntityManager em = JpaUtil.obtenirEntityManager();
        try {
            if (exists(commande.getId())) {
                em.merge(commande);
            }
            return true;
        } catch (Exception ex) {
            Logger.getLogger(CommandeDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public List<Commande> recupererCommandesEnCoursParDrones() {
        // TODO: A factoriser une fois les tests passés
        EntityManager em = JpaUtil.obtenirEntityManager();
        Query q = em.createQuery("SELECT c FROM Commande c WHERE c.dateDeFin is null");
        List<Commande> lc = q.getResultList();
        for (Iterator<Commande> it = lc.iterator(); it.hasNext();) {
            Commande commande = it.next();
            if (commande.getLivreur() instanceof Drone) {
                it.remove();
            }
        }
        return lc;
    }
    
    public void validerCommande(Commande commande) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        commande.setDateDeFin(new Date());
        em.merge(commande);
    }
}
