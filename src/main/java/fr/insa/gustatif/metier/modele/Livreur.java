package fr.insa.gustatif.metier.modele;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 */
@Entity
public class Livreur implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nom;
    private String prenom;
    private String mail;
    private Integer capaciteMax;
    private Boolean disponible;
    private Double longitude;
    private Double latitude;
    
    protected Livreur(){
    }

    public Livreur(String nom, String prenom, String mail, Integer capaciteMax, Boolean disponible) {
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.capaciteMax = capaciteMax;
        this.disponible = disponible;
        this.longitude = null;
        this.latitude = null;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getMail() {
        return mail;
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

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setMail(String mail) {
        this.mail = mail;
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
    
    public String getString(){
        return "id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", mail=" + mail + ", capaciteMax=" + capaciteMax + ", disponible=" + disponible + ", longitude=" + longitude + ", latitude=" + latitude;
    }
    
    @Override
    public String toString() {
        return "Livreur{" + "id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", mail=" + mail + ", capaciteMax=" + capaciteMax + ", disponible=" + disponible + ", longitude=" + longitude + ", latitude=" + latitude + '}';
    }
    
}
