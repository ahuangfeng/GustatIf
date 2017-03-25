/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insa.gustatif.dao;

import fr.insa.gustatif.metier.modele.Livreur;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 */
public class LivreurDAO implements BasicDAO<Livreur> {
    
    public boolean modifierLivreur(Livreur livreur) throws PersistenceException{
        EntityManager em = JpaUtil.obtenirEntityManager();
        if (exists(livreur.getId())) {
            em.merge(livreur);
            return true;
        }
        return false;
    }
    
    public void terminerCommandeEnCours(Livreur livreur) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        livreur.terminerCommandeEnCours();
        em.merge(livreur);
        
        // TODO: Mettre le livreur dans un état "terminé"
    }
}
