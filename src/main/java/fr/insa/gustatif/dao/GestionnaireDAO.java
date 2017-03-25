package fr.insa.gustatif.dao;

import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.metier.modele.Gestionnaire;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

/**
 *
 */
public class GestionnaireDAO implements BasicDAO<Gestionnaire>, EmailDAO<Gestionnaire> {
    
    @Override
    public void creer(Gestionnaire gestionnaire) {
        throw new UnsupportedOperationException("Utiliser la méthode creerGestionnaire() pour créer un gestionnaire.");
    }

    public void creerGestionnaire(Gestionnaire gestionnaire) throws PersistenceException, DuplicateEmailException {
        // Vérifie l'unicité de l'email du gestionnaire
        if (existWithMail(gestionnaire.getMail())) {
            throw new DuplicateEmailException(gestionnaire.getMail());
        }

        EntityManager em = JpaUtil.obtenirEntityManager();
        em.persist(gestionnaire);
    }
}
