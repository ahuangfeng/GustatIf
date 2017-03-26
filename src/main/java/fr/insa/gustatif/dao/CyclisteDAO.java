package fr.insa.gustatif.dao;

import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.metier.modele.Cycliste;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

/**
 * DAO de Cycliste
 */
public class CyclisteDAO implements BasicDAO<Cycliste>, EmailDAO<Cycliste> {

    /**
     * NE PAS UTILISER CETTE METHODE, UTILISER creerCycliste()
     *
     * @param cycliste Non utilisé.
     */
    @Override
    public void creer(Cycliste cycliste) {
        throw new UnsupportedOperationException("Utiliser la méthode creerCycliste() pour créer un cycliste.");
    }

    /**
     * Crée un cycliste en vérifiant que le mail est unique.
     *
     * @param cycliste Le cycliste à persister
     * @throws DuplicateEmailException Si le mail est déjà utilisé.
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public void creerCycliste(Cycliste cycliste) throws PersistenceException, DuplicateEmailException {
        EntityManager em = JpaUtil.obtenirEntityManager();

        // Vérifie l'unicité de l'email du cycliste
        if (existWithMail(cycliste.getMail())) {
            throw new DuplicateEmailException(cycliste.getMail());
        }

        em.persist(cycliste);
    }
}
