/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insa.gustatif.dao;

import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Cycliste;
import fr.insa.gustatif.metier.modele.Drone;
import fr.insa.gustatif.metier.modele.Livreur;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

/**
 *
 */
public class CyclisteDAO {

    public boolean creerCycliste(Cycliste cycliste) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();

        // Vérifie l'unicité de l'email du cycliste
        if (existWithMail(cycliste.getMail())) {
            return false;
        }

        em.persist(cycliste);
        return true;
    }

    public Cycliste findById(long id) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        try {
            return em.find(Cycliste.class, id);
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean existWithMail(String mail) throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Query emailQuery = em.createQuery("select c from Cycliste c where c.mail = :mail");
        emailQuery.setParameter("mail", mail);
        try {
            emailQuery.getSingleResult();
        } catch (NoResultException e) {
            return false;
        } catch (NonUniqueResultException e) {
            return true;
        }
        return true;
    }

    public List<Cycliste> findAll() throws Exception {
        EntityManager em = JpaUtil.obtenirEntityManager();
        try {
            Query q = em.createQuery("SELECT c FROM Cycliste c");
            return q.getResultList();
        } catch (Exception e) {
            throw e;
        }
    }

}
