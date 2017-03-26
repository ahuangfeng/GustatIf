package fr.insa.gustatif.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 * Cette interface implémente toutes les méthodes d'un DAO basique.
 *
 * @param <T> Classe métier
 */
public interface BasicDAO<T> {

    /**
     * Persiste entity.
     *
     * @param entity L'entité à persister
     * @throws PersistenceException Si une exception de persistence intervient
     */
    default public void creer(T entity) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        em.persist(entity);
    }

    /**
     * Merge l'entité.
     *
     * @param entity L'entité à merge
     * @param id L'ID de l'entité à merge
     * @return true si l'ID existe, sinon false
     * @throws PersistenceException Si une exception de persistence intervient
     */
    default public boolean modifier(T entity, long id) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        if (exists(id)) {
            em.merge(entity);
            return true;
        }
        return false;
    }

    /**
     * Rafraichit l'entité en la mettant à jour par rapport à la version dans la
     * BDD.
     *
     * @param entity L'entité à rafraichir.
     * @throws PersistenceException Si une exception de persistence intervient
     */
    default public void rafraichir(T entity) throws PersistenceException {
        EntityManager em = JpaUtil.obtenirEntityManager();
        em.refresh(entity);
    }

    /**
     * Récupère l'entité ayant l'ID id, ou null s'il n'existe pas.
     *
     * @param id L'ID de l'entité à récupérer
     * @return L'entité ayant l'ID id, ou null
     * @throws PersistenceException Si une exception de persistence intervient
     */
    default public T findById(long id) throws PersistenceException {
        Class<?> templateClass = (Class<?>) ((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];

        EntityManager em = JpaUtil.obtenirEntityManager();
        return (T) em.find(templateClass, id);
    }

    /**
     * Récupère toutes les entités.
     *
     * @return la liste de toutes les entités
     * @throws PersistenceException Si une exception de persistence intervient
     */
    default public List<T> findAll() throws PersistenceException {
        String templateName = ((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName();
        String[] templateFullName = templateName.split("\\.");
        templateName = templateFullName[templateFullName.length - 1];

        EntityManager em = JpaUtil.obtenirEntityManager();
        Query q = em.createQuery("SELECT p FROM " + templateName + " p");
        return q.getResultList();
    }

    /**
     * Vérifie si l'entité ayant l'ID id existe dans la BDD.
     *
     * @param id L'ID de l'entité pour laquelle vérifier l'existence
     * @return L'entité ayant l'ID id, ou null
     * @throws PersistenceException Si une exception de persistence intervient
     */
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

    default public void supprimerToutesLesEntites() throws PersistenceException {
        String templateName = ((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName();
        String[] templateFullName = templateName.split("\\.");
        templateName = templateFullName[templateFullName.length - 1];

        EntityManager em = JpaUtil.obtenirEntityManager();
        em.createQuery("DELETE FROM " + templateName + " p").executeUpdate();
    }
}
