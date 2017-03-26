package fr.insa.gustatif.metier.modele;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

@Entity
public class Commande implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Restaurant restaurant;
    @OneToOne
    private Client client;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateDeCommande;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateDeFin;
    private Double prix;
    private Double poids;
    private Double tempsEstime;

    @OneToMany(cascade = javax.persistence.CascadeType.ALL)
    private List<ProduitCommande> produitsCommande;

    @ManyToOne
    private Livreur livreur;

    protected Commande() {
        this.produitsCommande = new ArrayList<>();
    }

    public Commande(Client client, Date heureDeCommande, Date heureDeFin, List<ProduitCommande> produits, Restaurant restaurant) {
        this.restaurant = restaurant;
        this.client = client;
        this.dateDeCommande = heureDeCommande;
        this.dateDeFin = heureDeFin;
        this.produitsCommande = new ArrayList<>(produits);
        this.prix = 0.;
        this.poids = 0.;
        for (ProduitCommande produitCommande : this.produitsCommande) {
            this.prix += produitCommande.getProduit().getPrix() * produitCommande.getQuantity();
            this.poids += produitCommande.getProduit().getPoids() * produitCommande.getQuantity();
        }
        this.tempsEstime = null;
    }

    public Long getId() {
        return id;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public Client getClient() {
        return client;
    }

    public Date getDateDeCommande() {
        return dateDeCommande;
    }

    public Date getDateDeFin() {
        return dateDeFin;
    }

    public Double getPrix() {
        return prix;
    }

    public List<ProduitCommande> getProduits() {
        return produitsCommande;
    }

    public double getPoids() {
        return this.poids;
    }

    public Livreur getLivreur() {
        return livreur;
    }

    public Double getTempsEstime() {
        return tempsEstime;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setDateDeCommande(Date dateDeCommande) {
        this.dateDeCommande = dateDeCommande;
    }

    public void setDateDeFin(Date dateDeFin) {
        this.dateDeFin = dateDeFin;
    }

    public void setLivreur(Livreur livreur) {
        this.livreur = livreur;
    }

    @Override
    public String toString() {
        String r = "Commande #" + id + " de " + client.getMail() + ", " + prix + " € et " + poids + " kg"
                + "\n    effectuée à " + dateDeCommande + " et terminée à " + dateDeFin + " :";
        if (null != livreur) {
            r += "\n    livreur : " + livreur.getIdentifiant();
        }
        for (ProduitCommande produitCommande : produitsCommande) {
            r += "\n";
            r += "    - " + produitCommande;
        }
        return r;
    }
}
