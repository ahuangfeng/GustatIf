package fr.insa.gustatif.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public interface BasicDAO<T> {

    default public void creer(T entity) {
        EntityManager em = JpaUtil.obtenirEntityManager();
        em.persist(entity);
    }

    default public boolean modifier(T entity, long id) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        if (exists(id)) {
            em.merge(entity);
            return true;
        }
        return false;
    }

    default public void rafraichir(T entity) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        em.refresh(entity);
    }

    default public T findById(long id) {
        Class<?> templateClass = (Class<?>) ((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];

        try {
            EntityManager em = JpaUtil.obtenirEntityManager();
            return (T) em.find(templateClass, id);
        } catch (PersistenceException e) {
            return null;
        }
    }

    default public List<T> findAll() {
        String templateName = ((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName();
        String[] templateFullName = templateName.split("\\.");
        templateName = templateFullName[templateFullName.length - 1];

        try {
            EntityManager em = JpaUtil.obtenirEntityManager();
            Query q = em.createQuery("SELECT p FROM " + templateName + " p");
            return q.getResultList();
        } catch (PersistenceException e) {
            return null;
        }
    }

    default public boolean exists(long id) throws PersistenceException {
        Class<?> templateClass = (Class<?>) ((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];

        try {
            EntityManager em = JpaUtil.obtenirEntityManager();
            em.find(templateClass, id);
            return true;
        } catch (NonUniqueResultException e) {
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }
}
