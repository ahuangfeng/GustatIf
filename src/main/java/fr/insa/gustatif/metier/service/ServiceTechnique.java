package fr.insa.gustatif.metier.service;

import com.google.maps.errors.OverDailyLimitException;
import com.google.maps.errors.ZeroResultsException;
import com.google.maps.model.LatLng;
import fr.insa.gustatif.dao.LivreurDAO;
import fr.insa.gustatif.exceptions.CommandeMalFormeeException;
import fr.insa.gustatif.metier.modele.Commande;
import fr.insa.gustatif.metier.modele.Drone;
import fr.insa.gustatif.metier.modele.Livreur;
import fr.insa.gustatif.util.GeoTest;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.PersistenceException;

/**
 * Cette classe contient tous les services techniques de Gustat'IF.
 */
public class ServiceTechnique {

    static final Pattern VALID_EMAIL_ADDRESS_REGEX
            = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,8}$", Pattern.CASE_INSENSITIVE);

    /**
     * Envoi un mail fictif.
     *
     * @param destinataire Le destinataire du mail.
     * @param sujet Le sujet du mail.
     * @param corps Le corps du mail.
     */
    public void envoyerMail(String destinataire, String sujet, String corps) {
        System.out.println("-----------------------------------------------------------------------");

        System.out.println("Expéditeur : gustatif@gustatif.com");
        System.out.println("Pour : " + destinataire);
        System.out.println("Sujet : " + sujet);
        System.out.println("Corps :");
        System.out.println(corps);

        System.out.println("-----------------------------------------------------------------------");
    }

    /**
     * Génère une chaîne de caractères aléatoire. Sa longueur est déterminée
     * aléatoirement dans [3..5].
     *
     * @param capitalized true pour mettre la première lettre en majuscule.
     * @return La chaîne aléatoire.
     */
    public static String genererString(boolean capitalized) {
        int longueur = 3 + (int) (Math.random() * 3);
        String r = "";
        for (int i = 0; i < longueur; i++) {
            r += (char) ((char) (Math.random() * 26) + 'a');
        }
        if (capitalized) {
            return Character.toUpperCase(r.charAt(0)) + r.substring(1);
        }
        return r;
    }

    /**
     * Retourne les livreurs disponibles avec le temps estimé pour la livraison.
     * <br>
     * <strong>Il faut absolument qu'un EntityManager soit créé pour appeler
     * cette méthode.</strong>
     *
     * @param commande Commande à livrer.
     * @return Une Map (tempsEstimé, Livreur)
     * @throws CommandeMalFormeeException Si la commande n'a pas de client, ou
     * si celui-ci n'a pas de coordonnées
     * @throws OverDailyLimitException Si le quota Google Maps est atteint
     * @throws PersistenceException Si une exception de persistence intervient
     */
    Map<Double, Livreur> recupererLivreursParTemps(Commande commande) throws OverDailyLimitException, CommandeMalFormeeException, PersistenceException {
        if (null == commande.getClient()) {
            throw new CommandeMalFormeeException("Le client n'est pas défini.");
        }
        if (null == commande.getClient().getLatitude() || null == commande.getClient().getLongitude()) {
            throw new CommandeMalFormeeException("Le client n'est pas géolocalisé.");
        }

        // Quelques raccourcis
        LatLng coordsClient = new LatLng(commande.getClient().getLatitude(), commande.getClient().getLongitude());
        LatLng coordsResto = new LatLng(commande.getRestaurant().getLatitude(), commande.getRestaurant().getLongitude());

        // Tri les livreurs par temps pour livrer la commande
        LivreurDAO livreurDAO = new LivreurDAO();
        List<Livreur> livreursDispo = livreurDAO.recupererCapablesDeLivrer(commande.getPoids());
        Map<Double, Livreur> livreursParTemps = new TreeMap<>();
        for (Livreur livreur : livreursDispo) {
            LatLng coordsLivreur = new LatLng(livreur.getLatitude(), livreur.getLongitude());

            // Calcule le temps en fonction du type de livreur
            double temps;
            if (livreur instanceof Drone) {
                temps = ((Drone) livreur).getVitesse()
                        // Du livreur au restaurant
                        * (GeoTest.getFlightDistanceInKm(coordsLivreur, coordsResto)
                        // puis du restaurant au client
                        + GeoTest.getFlightDistanceInKm(coordsResto, coordsClient));
            } else {
                try {
                    // Du livreur au client, en passant par le restaurant
                    temps = GeoTest.getTripDurationByBicycleInMinute(coordsLivreur, coordsClient, coordsResto);
                } catch (ZeroResultsException ex) {
                    Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, "Aucun chemin entre le livreur et le client.");
                    continue;
                }
            }

            // Ajoute le livreur dans la map
            livreursParTemps.put(temps, livreur);
        }

        return livreursParTemps;
    }

    /**
     * Vérifie si l'adresse mail fournie est valide.
     *
     * @param email L'email à vérifier
     * @return true si elle est valide, sinon false
     */
    public static boolean verifierMail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }
}
