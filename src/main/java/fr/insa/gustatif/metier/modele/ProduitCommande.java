package fr.insa.gustatif.metier.modele;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
public class ProduitCommande implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Produit produit;
    private Integer quantity;
    @Version
    Integer version;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public ProduitCommande(Produit produit, Integer quantity) {
        this.produit = produit;
        this.quantity = quantity;
    }

    protected ProduitCommande() {
    }

    public Long getId() {
        return id;
    }

    public Produit getProduit() {
        return produit;
    }

    public Integer getQuantity() {
        return quantity;
    }
    
    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return quantity + " x " + produit;
    }
}
