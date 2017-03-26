package fr.insa.gustatif.dao;

import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.metier.modele.Gestionnaire;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

/**
 * DAO de Gestionnaire
 */
public class GestionnaireDAO implements BasicDAO<Gestionnaire>, EmailDAO<Gestionnaire> {

    /**
     * NE PAS UTILISER CETTE METHODE, UTILISER creerClient()
     *
     * @param gestionnaire Non utilisé.
     */
    @Override
    public void creer(Gestionnaire gestionnaire) {
        throw new UnsupportedOperationException("Utiliser la méthode creerGestionnaire() pour créer un gestionnaire.");
    }

    /**
     * Crée un gestionnaire en vérifiant que le mail est unique.
     *
     * @param gestionnaire Le gestionnaire à persister
     * @throws DuplicateEmailException Si le mail est déjà utilisé.
     * @throws PersistenceException Si une exception de persistence intervient
     */
    public void creerGestionnaire(Gestionnaire gestionnaire) throws PersistenceException, DuplicateEmailException {
        // Vérifie l'unicité de l'email du gestionnaire
        if (existWithMail(gestionnaire.getMail())) {
            throw new DuplicateEmailException(gestionnaire.getMail());
        }

        EntityManager em = JpaUtil.obtenirEntityManager();
        em.persist(gestionnaire);
    }
}
