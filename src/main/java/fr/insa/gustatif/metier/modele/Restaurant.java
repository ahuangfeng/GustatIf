package fr.insa.gustatif.metier.modele;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Restaurant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String denomination;
    private String description;
    private String adresse;
    private Double latitude;
    private Double longitude;
    @OneToMany
    private List<Produit> produits;

    protected Restaurant() {
        this.produits = new ArrayList<>();
    }

    public Restaurant(String denomination, String description, String adresse) {
        this.denomination = denomination;
        this.description = description;
        this.adresse = adresse;
        this.latitude = null;
        this.longitude = null;
        this.produits = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getDenomination() {
        return denomination;
    }

    public String getDescription() {
        return description;
    }

    public String getAdresse() {
        return adresse;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public List<Produit> getProduits() {
        return produits;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setLatitudeLongitude(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void addProduit(Produit produit) {
        this.produits.add(produit);
    }

    @Override
    public String toString() {
        return "Restaurant{" + "id=" + id + ", denomination=" + denomination
                + ", description=" + description + ", adresse=" + adresse
                + ", latitude=" + latitude + ", longitude=" + longitude
                + ", produits=" + produits + '}';
    }
}
