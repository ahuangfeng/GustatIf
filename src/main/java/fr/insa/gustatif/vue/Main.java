package fr.insa.gustatif.vue;

import com.google.maps.errors.OverDailyLimitException;
import fr.insa.gustatif.dao.JpaUtil;
import fr.insa.gustatif.exceptions.AucunLivreurDisponibleException;
import fr.insa.gustatif.exceptions.DuplicateEmailException;
import fr.insa.gustatif.exceptions.CommandeMalFormeeException;
import fr.insa.gustatif.exceptions.IllegalUserInfoException;
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
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;

/**
 *
 */
public class Main {

    public static void main(String[] args) throws DuplicateEmailException, IllegalUserInfoException {
        JpaUtil.init();

        ServiceMetier sm = new ServiceMetier();

        // Effectue plusieurs commandes
        // -> passerCommande effectue une pause random
        final int NB_COMMANDES = 10;
        Restaurant r = sm.recupererRestaurant(10L);
        List<Produit> produits = r.getProduits();
        Client c = sm.recupererClient("billy.feugeroy@free.fr");
        for (int i = 0; i < NB_COMMANDES; i++) {
            Saisie.pause();

            try {
                List<ProduitCommande> panier = new ArrayList<>();
                panier.add(new ProduitCommande(produits.get((int) (Math.random() * produits.size())), (int) (Math.random() * 4) + 1));

                sm.creerCommande(c, panier);
                System.out.println("Succès.");

            } catch (CommandeMalFormeeException ex) {
                System.out.println("Mauvaise commande : " + ex.getMessage());
            } catch (AucunLivreurDisponibleException ex) {
                System.out.println("Aucun livreur.");
            } catch (OverDailyLimitException ex) {
                System.out.println("Google API dead.");
            } catch (OptimisticLockException ex) {
                System.out.println("OptimisticLockException, on est pas censé en avoir ici...");
            } catch (RollbackException ex) {
                System.out.println("RollbackException, on est pas censé en avoir ici...");
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

        JpaUtil.destroy();
    }
}
