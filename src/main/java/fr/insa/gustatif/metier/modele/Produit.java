package fr.insa.gustatif.metier.modele;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Produit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String denomination;
    private String description;
    private Double poids;
    private Double prix;

    public Produit() {
    }

    public Produit(String denomination, String description, Double poids, Double prix) {
        this.denomination = denomination;
        this.description = description;
        this.poids = poids;
        this.prix = prix;
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

    public Double getPoids() {
        return poids;
    }

    public Double getPrix() {
        return prix;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPoids(Double poids) {
        this.poids = poids;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    @Override
    public String toString() {
        return "Produit{" + "id=" + id + ", denomination=" + denomination + ", description=" + description + ", poids=" + poids + ", prix=" + prix + '}';
    }

}
