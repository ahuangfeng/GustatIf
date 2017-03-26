package fr.insa.gustatif.util;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.NotFoundException;
import com.google.maps.errors.OverDailyLimitException;
import com.google.maps.errors.ZeroResultsException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DASI Team
 * @author B3233 : Gestion des exceptions
 */
public class GeoTest {
    final static String GOOGLE_API_KEY = "AIzaSyAhf3JleYpal9S-xouJYH8lf7Dvz5Y2Nko";

    final static GeoApiContext GEOAPI_CONTEXT = new GeoApiContext().setApiKey(GOOGLE_API_KEY);

    public static LatLng getLatLng(String adresse) throws NotFoundException {
        try {
            GeocodingResult[] results = GeocodingApi.geocode(GEOAPI_CONTEXT, adresse).await();
            return results[0].geometry.location;
        } catch (NotFoundException ex) {
            throw ex;
        } catch (OverDailyLimitException ex) {
            Logger.getLogger(GeoTest.class.getName()).log(Level.SEVERE, "Quota Google Maps dépassé, coordonnées bidon utilisée");
            return new LatLng(45.78126 + Math.random() / 10., 4.87221 + Math.random() / 10.);
        } catch (Exception ex) {
            Logger.getLogger(GeoTest.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static double toRad(double angleInDegree) {
        return angleInDegree * Math.PI / 180.0;
    }

    public static double getFlightDistanceInKm(LatLng origin, LatLng destination) {

        // From: http://www.movable-type.co.uk/scripts/latlong.html
        double R = 6371.0; // Average radius of Earth (km)
        double dLat = toRad(destination.lat - origin.lat);
        double dLon = toRad(destination.lng - origin.lng);
        double lat1 = toRad(origin.lat);
        double lat2 = toRad(destination.lat);

        double a = Math.sin(dLat / 2.0) * Math.sin(dLat / 2.0)
                + Math.sin(dLon / 2.0) * Math.sin(dLon / 2.0) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        double d = R * c;

        return Math.round(d * 1000.0) / 1000.0;
    }

    public static Double getTripDurationByBicycleInMinute(LatLng origin, LatLng destination, LatLng... steps) throws OverDailyLimitException, ZeroResultsException {
        return getTripDurationOrDistance(TravelMode.BICYCLING, true, origin, destination, steps);
    }

    public static Double getTripDistanceByCarInKm(LatLng origin, LatLng destination, LatLng... steps) throws OverDailyLimitException, ZeroResultsException {
        return getTripDurationOrDistance(TravelMode.DRIVING, false, origin, destination, steps);
    }

    public static Double getTripDurationOrDistance(TravelMode mode, boolean duration, LatLng origin, LatLng destination, LatLng... steps) throws OverDailyLimitException, ZeroResultsException {
        DirectionsApiRequest request = DirectionsApi.getDirections(GEOAPI_CONTEXT, origin.toString(), destination.toString());
        request.mode(mode);
        request.region("fr");

        if (steps.length > 0) {

            String[] stringSteps = new String[steps.length];
            for (int i = 0; i < steps.length; i++) {
                stringSteps[i] = steps[i].toString();
            }

            request.waypoints(stringSteps);
        }

        double cumulDistance = 0.0;
        double cumulDuration = 0.0;

        try {
            DirectionsResult result = request.await();
            DirectionsRoute[] directions = result.routes;

            for (DirectionsLeg leg : directions[0].legs) {
                cumulDistance += leg.distance.inMeters / 1000.0;
                cumulDuration += Math.ceil(leg.duration.inSeconds / 60.0);
            }

        } catch (OverDailyLimitException | ZeroResultsException ex) {
            throw ex;
        } catch (Exception ex) {
            return null;
        }

        if (duration) {
            return cumulDuration;
        } else {
            return cumulDistance;
        }
    }
}
