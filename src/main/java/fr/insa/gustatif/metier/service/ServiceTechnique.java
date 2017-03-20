package fr.insa.gustatif.metier.service;

import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import fr.insa.gustatif.exceptions.BadLocationException;
import fr.insa.gustatif.util.GeoTest;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class ServiceTechnique {

    static final Pattern VALID_EMAIL_ADDRESS_REGEX
            = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    void EnvoyerMail(String destinataire, String sujet, String corps) {
        AfficherSeparateur(System.out);

        System.out.println("Expéditeur : gustatif@gustatif.com");
        System.out.println("Pour : " + destinataire);
        System.out.println("Sujet : " + sujet);
        System.out.println("Corps :");
        System.out.println(corps);

        AfficherSeparateur(System.out);
    }

    void AfficherSeparateur(PrintStream ps) {
        ps.println("-----------------------------------------------------------------------");
    }

    public static String GenererString() {
        int longueur = 4 + (int) (Math.random() * 8);
        String r = "";
        for (int i = 0; i < longueur; i++) {
            r += (char) ((char) (Math.random() * 26) + 'A');
        }
        return r;
    }

    public boolean VerifierMail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    public static LatLng getLatLng(String adresse) throws BadLocationException {
        LatLng r = GeoTest.getLatLng(adresse);
        if (null == r) {
            throw new BadLocationException();
        }
        return r;
    }

    public static Double getTripDurationByBicycleInMinute(LatLng origin, LatLng destination, LatLng... steps) throws BadLocationException {
        Double r = GeoTest.getTripDurationByBicycleInMinute(origin, destination, steps);
        if (null == r) {
            throw new BadLocationException();
        }
        return r;
    }

    public static Double getTripDistanceByCarInKm(LatLng origin, LatLng destination, LatLng... steps) throws BadLocationException {
        Double r = GeoTest.getTripDistanceByCarInKm(origin, destination, steps);
        if (null == r) {
            throw new BadLocationException();
        }
        return r;
    }

    public static Double getTripDurationOrDistance(TravelMode mode, boolean duration, LatLng origin, LatLng destination, LatLng... steps) throws BadLocationException {
        Double r = GeoTest.getTripDurationOrDistance(mode, duration, origin, destination, steps);
        if (null == r) {
            throw new BadLocationException();
        }
        return r;
    }

}
