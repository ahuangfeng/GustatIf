package fr.insa.gustatif.dao;

import java.lang.reflect.ParameterizedType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public interface EmailDAO<T> {

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
