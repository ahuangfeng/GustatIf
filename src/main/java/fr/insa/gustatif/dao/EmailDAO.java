package fr.insa.gustatif.dao;

import java.lang.reflect.ParameterizedType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 * Cette interface implémente les méthodes de recherche d'un DAO d'un objet
 * métier possédant une adresse mail.
 * <strong>L'attribut doit se nommer "mail"</strong>
 *
 * @param <T> Classe métier
 */
public interface EmailDAO<T> {

    /**
     * Récupère l'entité ayant le email mail, ou null s'il n'existe pas.
     *
     * @param mail L'email de l'entité à récupérer
     * @return L'entité ayant l'ID id, ou null
     * @throws PersistenceException Si une exception de persistence intervient
     */
    default public T findByEmail(String mail) throws PersistenceException {
        String templateName = ((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName();
        String[] templateFullName = templateName.split("\\.");
        templateName = templateFullName[templateFullName.length - 1];

        try {
            EntityManager em = JpaUtil.obtenirEntityManager();
            Query emailQuery = em.createQuery("select c from " + templateName + " c where c.mail = :mail");
            emailQuery.setParameter("mail", mail);
            return (T) emailQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Vérifie si l'entité ayant le email mail existe dans la BDD.
     *
     * @param mail L'email de l'entité pour laquelle vérifier l'existence
     * @return L'entité ayant l'ID id, ou null
     * @throws PersistenceException Si une exception de persistence intervient
     */
    default public boolean existWithMail(String mail) throws PersistenceException {
        String templateName = ((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName();
        String[] templateFullName = templateName.split("\\.");
        templateName = templateFullName[templateFullName.length - 1];

        try {
            EntityManager em = JpaUtil.obtenirEntityManager();
            Query emailQuery = em.createQuery("select c from " + templateName + " c where c.mail = :mail");
            emailQuery.setParameter("mail", mail);
            emailQuery.getSingleResult();
        } catch (NonUniqueResultException e) {
            return true;
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }
}
