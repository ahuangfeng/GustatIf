package fr.insa.gustatif.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import fr.insa.gustatif.metier.modele.Commande;
import fr.insa.gustatif.metier.modele.EtatLivraison;
import fr.insa.gustatif.metier.modele.EtatPaiement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

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
    
    public boolean exists(long id) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        try {
            em.find(Commande.class, id);
            return true;
        } catch (NoResultException e) {
            return false;
        } catch (NonUniqueResultException e) {
            return true;
        }
    }
    
    public boolean modifierCommande(long id, Commande commande){ // TODO commande ne sers a rien
        EntityManager em = JpaUtil.obtenirEntityManager();
        try {
            Commande cm = findById(id);
            if(exists(id)){
                em.merge(cm);
            }
            return true;
        } catch (Exception ex) {
            Logger.getLogger(CommandeDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public List<Commande> findAll() throws Exception {
        // TODO: OMG à quoi sert le catch ici ??
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
    
    public boolean payer(long id){
        EntityManager em = JpaUtil.obtenirEntityManager();
        Commande commande = null;
        try{
            commande = em.find(Commande.class, id);
            commande.setEtatpaiement(EtatPaiement.PAYE);
            return true;
        }catch (Exception e){
            throw e;
        }
    }
    
    public boolean payerALaLivraison(long id){
        boolean res = false;
        
        EntityManager em = JpaUtil.obtenirEntityManager();
        Commande commande = null;
        try{
            commande = em.find(Commande.class, id);
            commande.setEtatpaiement(EtatPaiement.PAYER_A_LA_LIVRAISON);
            res = true;
        }catch (Exception e){
            throw e;
        }
        return res;
    }

    public void livraisonComplete(Commande commande) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        commande.setEtatLivaison(EtatLivraison.LIVRE);
        em.merge(commande);
    }

    public void livraisonEnCours(Commande commande) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        commande.setEtatLivaison(EtatLivraison.EN_COURS);
        em.merge(commande);
    }
    
    public void livraisonEnAttente(Commande commande) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        commande.setEtatLivaison(EtatLivraison.EN_ATTENTE);
        em.merge(commande);
    }
    
}
