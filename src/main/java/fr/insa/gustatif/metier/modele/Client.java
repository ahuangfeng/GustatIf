package fr.insa.gustatif.metier.modele;

import com.google.maps.model.LatLng;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class Client implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nom;
    private String prenom;
    private String mail;
    private String adresse;
    private Double latitude;
    private Double longitude;

    @OneToMany
    List<Commande> commandes;

    @ManyToMany
    @JoinTable(name = "CLIENT_PANIER_PRODUITS")
    List<ProduitCommande> panier;

    protected Client() {
    }

    public Client(String nom, String prenom, String mail, String adresse) {
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.adresse = adresse;
        this.longitude = null;
        this.latitude = null;
        this.commandes = new ArrayList<>();
        this.panier = new ArrayList<>();
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

    public String getAdresse() {
        return adresse;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    public List<ProduitCommande> getPanier() {
        return panier;
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

    public void setAdresse(String adresse) {
        this.adresse = adresse;
        // TODO: Récupérer les coordonées
    }

    public void setLatitudeLongitude(LatLng coords) {
        this.latitude = coords.lat;
        this.longitude = coords.lng;
    }

    public void addCommande(Commande commande) {
        this.commandes.add(commande);
    }

    public void ajouterAuPanier(ProduitCommande produitCommande) {
        this.panier.add(produitCommande);
    }

    public void viderPanier() {
        this.panier.clear();
    }

    @Override
    public String toString() {
        String r = "Client{" + "id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", mail=" + mail + ", adresse=" + adresse + ", longitude=" + longitude + ", latitude=" + latitude + '}';
        if (!panier.isEmpty()) {
            r += '\n';
            r += "  - Panier :";
            for (ProduitCommande produit : panier) {
                r += '\n';
                r += "      -> " + produit;
            }
        }
        return r;
    }
}
