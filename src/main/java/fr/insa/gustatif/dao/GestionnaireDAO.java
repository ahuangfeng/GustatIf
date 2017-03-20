package fr.insa.gustatif.dao;

import fr.insa.gustatif.metier.modele.Gestionnaire;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 */
public class GestionnaireDAO {
    
    public boolean creerGestionnaire(Gestionnaire gestionnaire) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();

        // TODO: Lancer l'exception DuplicateMailExc au lieu du return
        
        // Vérifie l'unicité de l'email du gestionnaire
        if (existWithMail(gestionnaire.getMail())) {
            return false;
        }

        em.persist(gestionnaire);
        return true;
    }

    public Gestionnaire findById(long id) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        return em.find(Gestionnaire.class, id);
    }

    public Gestionnaire findByEmail(String mail) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Query emailQuery = em.createQuery("select g from Gestionnaire g where g.mail = :mail");
        emailQuery.setParameter("mail", mail);
        try {
            return (Gestionnaire) emailQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean existWithMail(String mail) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Query emailQuery = em.createQuery("select g from Gestionnaire g where g.mail = :mail");
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

    public List<Gestionnaire> findAll() throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        Query q = em.createQuery("SELECT g FROM Gestionnaire g");
        return q.getResultList();
    }
}
