package fr.insa.gustatif.dao;

import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.metier.modele.Cycliste;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 */
public class CyclisteDAO {

    public void creerCycliste(Cycliste cycliste) throws PersistenceException, DuplicateEmailException {
        EntityManager em = JpaUtil.obtenirEntityManager();

        // Vérifie l'unicité de l'email du cycliste
        if (existWithMail(cycliste.getMail())) {
            throw new DuplicateEmailException(cycliste.getMail());
        }

        em.persist(cycliste);
    }

    public Cycliste findById(long id) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        return em.find(Cycliste.class, id);
    }

    public Cycliste findByEmail(String mail) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Query emailQuery = em.createQuery("select c from Cycliste c where c.mail = :mail");
        emailQuery.setParameter("mail", mail);
        try {
            return (Cycliste) emailQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean existWithMail(String mail) throws PersistenceException {
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

    public List<Cycliste> findAll() throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Query q = em.createQuery("SELECT c FROM Cycliste c");
        return q.getResultList();
    }
}
