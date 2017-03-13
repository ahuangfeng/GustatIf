package fr.insa.gustatif.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import fr.insa.gustatif.metier.modele.ProduitCommande;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

public class ProduitCommandeDAO {
    
    public void creerProduitCommande(ProduitCommande produitCommande) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        em.persist(produitCommande);
    }

    public ProduitCommande findById(long id) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        ProduitCommande produitCommande = null;
        try {
            produitCommande = em.find(ProduitCommande.class, id);
        }
        catch(Exception e) {
            throw e;
        }
        return produitCommande;
    }
    
    //TODO : ça marche?
    public List<ProduitCommande> findAll() throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        List<ProduitCommande> produitsCommande = null;
        try {
            Query q = em.createQuery("SELECT p FROM ProduitCommande p");
            produitsCommande = (List<ProduitCommande>) q.getResultList();
        }
        catch(Exception e) {
            throw e;
        }
        
        return produitsCommande;
    }
    
    public boolean exists(long id) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        try {
            em.find(ProduitCommande.class, id);
            return true;
        } catch (NoResultException e) {
            return false;
        } catch (NonUniqueResultException e) {
            return true;
        }
    }
    
    
    public boolean modifierProduitCommande(long id, ProduitCommande produitCommande){
        EntityManager em = JpaUtil.obtenirEntityManager();
        try {
            ProduitCommande pc = findById(id);
            if(exists(id)){
                em.merge(pc);
            }
            return true;
        } catch (Exception ex) {
            Logger.getLogger(CommandeDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
