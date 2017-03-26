package fr.insa.gustatif.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import fr.insa.gustatif.metier.modele.Commande;
import fr.insa.gustatif.metier.modele.Drone;
import java.util.Date;
import java.util.Iterator;

public class CommandeDAO implements BasicDAO<Commande> {

    public List<Commande> recupererCommandesEnCoursParDrones() {
        // TODO: A factoriser une fois les tests passés
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
    
    public void validerCommande(Commande commande) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        commande.setDateDeFin(new Date());
        em.merge(commande);
    }
}
