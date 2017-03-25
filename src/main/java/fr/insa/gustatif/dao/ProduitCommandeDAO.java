package fr.insa.gustatif.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import fr.insa.gustatif.metier.modele.ProduitCommande;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

public class ProduitCommandeDAO implements BasicDAO<ProduitCommande> {
    
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
