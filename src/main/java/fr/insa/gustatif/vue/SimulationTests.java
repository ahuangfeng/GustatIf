package fr.insa.gustatif.vue;

import com.google.maps.errors.OverDailyLimitException;
import fr.insa.gustatif.dao.JpaUtil;
import fr.insa.gustatif.exceptions.AucunLivreurDisponibleException;
import fr.insa.gustatif.exceptions.CommandeMalFormeeException;
import fr.insa.gustatif.exceptions.ServeurOccupeException;
import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Commande;
import fr.insa.gustatif.metier.modele.Produit;
import fr.insa.gustatif.metier.modele.ProduitCommande;
import fr.insa.gustatif.metier.modele.Restaurant;
import fr.insa.gustatif.metier.service.ServiceMetier;
import fr.insa.gustatif.util.Saisie;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

/**
 * Ce fichier contient les tests de la simulation.
 */
public class SimulationTests {

    public void run() {
        accueil();
    }

    public void accueil() {
        int choix = -1;
        while (choix != 2) {
            System.out.println("Liste des tests.");
            choix = Saisie.choixMenu("Que voulez-vous faire ?", new String[]{
                "Passer des commandes factices (10 requêtes)",
                "Retour"
            });
            switch (choix) {
                case 1: { // Passer des commandes factices (10 requêtes)
                    testPasserCommande();
                    break;
                }
            }
        }
    }
    
    public void testPasserCommande() {
        ServiceMetier sm = new ServiceMetier();

        // Effectue plusieurs commandes
        final int NB_COMMANDES = 10;
        for (int i = 0; i < NB_COMMANDES; i++) {
            try {
                Restaurant r = sm.recupererRestaurant(10L);
                List<Produit> produits = r.getProduits();
                Client c = sm.recupererClient("billy.feugeroy@free.fr");

                Saisie.pause();

                List<ProduitCommande> panier = new ArrayList<>();
                panier.add(new ProduitCommande(produits.get((int) (Math.random() * produits.size())), (int) (Math.random() * 4) + 1));

                sm.creerCommande(c, panier);
                System.out.println("Succès.");

            } catch (CommandeMalFormeeException ex) {
                System.out.println("Mauvaise commande : " + ex.getMessage());
            } catch (AucunLivreurDisponibleException ex) {
                System.out.println("Aucun livreur.");
            } catch (OverDailyLimitException ex) {
                System.out.println("Erreur Google API, quota expiré.");
            } catch (OptimisticLockException ex) {
                System.out.println("OptimisticLockException, on est pas censé en avoir ici...");
            } catch (RollbackException ex) {
                System.out.println("RollbackException, on est pas censé en avoir ici...");
            } catch (PersistenceException ex) {
                System.out.println("PersistenceException");
            } catch (ServeurOccupeException ex) {
                System.out.println("ServeurOccupeException");
            }

            // Vérifie la cohérence de la BDD
            List<Commande> commandes = sm.recupererCommandes();
            Set<Long> idLivreurs = new TreeSet<>();
            try {
                for (Commande commande : commandes) if (null == commande.getDateDeFin()) {
                    System.out.println(commande);
                    long idLivreur = commande.getLivreur().getId();
                    if (idLivreurs.contains(idLivreur)) {
                        System.err.println("Erreur de cohérence !!");
                        System.exit(42);
                    }
                    idLivreurs.add(commande.getLivreur().getId());
                }
            } catch (NullPointerException ex) {
                System.out.println("NullPointerException ! Voici les commandes :");
                System.out.println(commandes);
            }
            System.out.println("BDD cohérente.");
        }
    }
}
