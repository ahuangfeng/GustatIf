package fr.insa.gustatif.dao;

import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.metier.modele.Cycliste;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

/**
 *
 */
public class CyclisteDAO implements BasicDAO<Cycliste>, EmailDAO<Cycliste> {

    @Override
    public void creer(Cycliste cycliste) {
        throw new UnsupportedOperationException("Utiliser la méthode creerCycliste() pour créer un cycliste.");
    }

    public void creerCycliste(Cycliste cycliste) throws PersistenceException, DuplicateEmailException {
        EntityManager em = JpaUtil.obtenirEntityManager();

        // Vérifie l'unicité de l'email du cycliste
        if (existWithMail(cycliste.getMail())) {
            throw new DuplicateEmailException(cycliste.getMail());
        }

        em.persist(cycliste);
    }
}
