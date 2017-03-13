package fr.insa.gustatif.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import fr.insa.gustatif.metier.modele.Commande;

public class CommandeDAO {

    public void creerCommande(Commande commande) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        em.persist(commande);
    }

    public Commande findById(long id) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Commande commande = null;
        try {
            commande = em.find(Commande.class, id);
        } catch (Exception e) {
            throw e;
        }
        return commande;
    }

    public List<Commande> findAll() throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        List<Commande> commandes = null;
        try {
            Query q = em.createQuery("SELECT c FROM Commande c");
            commandes = (List<Commande>) q.getResultList();
        } catch (Exception e) {
            throw e;
        }
        return commandes;
    }
}
