package fr.insa.gustatif.metier.modele;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Livreur implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer capaciteMax;
    private Boolean disponible;
    private Double longitude;
    private Double latitude;

    protected Livreur() {
    }

    public Livreur(Integer capaciteMax, Boolean disponible, Double longitude, Double latitude) {
        this.capaciteMax = capaciteMax;
        this.disponible = disponible;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Long getId() {
        return id;
    }

    public Integer getCapaciteMax() {
        return capaciteMax;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCapaciteMax(Integer capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLatitudeLongitude(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Livreur{" + "id=" + id + ", capaciteMax=" + capaciteMax + ", disponible=" + disponible + ", longitude=" + longitude + ", latitude=" + latitude + '}';
    }

}
