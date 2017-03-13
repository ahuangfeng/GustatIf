package fr.insa.gustatif.metier.modele;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

@Entity
public class Commande implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private Client client;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateDeCommande;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateDeFin;
    private Double prix;

    @OneToMany(cascade = javax.persistence.CascadeType.ALL)
    private List<ProduitCommande> produitsCommande;

    protected Commande() {
        this.produitsCommande = new ArrayList<>();
    }

    public Commande(Client client, Date heureDeCommande, Date heureDeFin, List<ProduitCommande> produits) {
        this.client = client;
        this.dateDeCommande = heureDeCommande;
        this.dateDeFin = heureDeFin;
        this.produitsCommande = produits;
        this.prix = 0.;
        for (ProduitCommande produitCommande : this.produitsCommande) {
            this.prix += produitCommande.getProduit().getPrix() * produitCommande.getQuantity();
        }
    }

    public Long getId() {
        return id;
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

    public void setClient(Client client) {
        this.client = client;
    }
    
    public void setDateDeCommande(Date dateDeCommande) {
        this.dateDeCommande = dateDeCommande;
    }

    public void setDateDeFin(Date dateDeFin) {
        this.dateDeFin = dateDeFin;
    }

    /**
     * Le prix est calculé automatiquement, cette méthode est donc protégée.
     * @param prix 
     */
    protected void setPrix(Double prix) {
        this.prix = prix;
    }

    public void addProduit(Produit produit, Integer quantity) {
        // Persistance en cascade
        this.produitsCommande.add(new ProduitCommande(produit, quantity));
    }
    
    public void setProduits(List<ProduitCommande> com) {
        this.produitsCommande = com;
    }

    @Override
    public String toString() {
        String r = "Commande #" + id + " de " + client.getMail() + ", " + prix + " €, effectuée à " + dateDeCommande + " et terminée à " + dateDeFin + " :";
        for (ProduitCommande produitCommande : produitsCommande) {
            r += "\n";
            r += produitCommande;
        }
        return r;
    }
}
