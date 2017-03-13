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
import javax.persistence.Query;

/**
 *
 */
public class LivreurDAO {
    
    public Livreur findById(long id) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Livreur livreur = null;
        try{
            livreur = em.find(Livreur.class, id);
        }
        catch(Exception e) {
            throw e;
        }
        return livreur;
    }
    
    public boolean exists(long id) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        try {
            em.find(Livreur.class, id);
            return true;
        } catch (NoResultException e) {
            return false;
        } catch (NonUniqueResultException e) {
            return true;
        }
    }
    
    public boolean modifierLivreur(Livreur livreur) throws Exception{
        EntityManager em = JpaUtil.obtenirEntityManager();
        
        if (exists(livreur.getId())) {
            em.merge(livreur);
            return true;
        }
        return false;
    }
    
    public List<Livreur> findAll() throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        List<Livreur> livreur = null;
        try {
            Query q = em.createQuery("SELECT l FROM Livreur l");
            livreur = (List<Livreur>) q.getResultList();
        }
        catch(Exception e) {
            throw e;
        }
        return livreur;
    }
    
}
