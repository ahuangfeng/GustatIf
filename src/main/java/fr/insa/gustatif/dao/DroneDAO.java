/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insa.gustatif.dao;

import fr.insa.gustatif.metier.modele.Drone;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 */
public class DroneDAO {
    
    public void creerDrone(Drone drone){
        EntityManager em = JpaUtil.obtenirEntityManager();
        em.persist(drone);
    }
    
    public Drone findById(long id) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        try{
            return em.find(Drone.class, id);
        }
        catch(Exception e) {
            throw e;
        }
    }
    
    public List<Drone> findAll() throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        List<Drone> drone = null;
        try {
            Query q = em.createQuery("SELECT d FROM Drone d");
            drone = (List<Drone>) q.getResultList();
        }
        catch(Exception e) {
            throw e;
        }
        return drone;
    }
}
