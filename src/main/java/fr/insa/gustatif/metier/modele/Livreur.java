package fr.insa.gustatif.metier.modele;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

/**
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Livreur implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Version
    private Long version;
    private Integer capaciteMax;
    private Boolean disponible;
    private Double latitude;
    private Double longitude;
    @OneToOne
    private Commande commandeEnCours;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Commande> commandesLivrees;

    protected Livreur() {
        this.commandesLivrees = new ArrayList<>();
    }

    public Livreur(Integer capaciteMax, Boolean disponible, Double latitude, Double longitude) {
        this.capaciteMax = capaciteMax;
        this.disponible = disponible;
        this.latitude = latitude;
        this.longitude = longitude;
        this.commandesLivrees = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
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

    public Commande getCommandeEnCours() {
        return commandeEnCours;
    }

    public List<Commande> getCommandesLivrees() {
        return commandesLivrees;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    protected void setVersion(Long version) {
        this.version = version;
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

    public void setCommandeEnCours(Commande commandeEnCours) {
        this.commandeEnCours = commandeEnCours;
        this.disponible = false;
    }

    public void ajouterCommandesLivree(Commande commandeLivree) {
        this.commandesLivrees.add(commandeLivree);
    }

    public void terminerCommandeEnCours() {
        /*Client client = commandeEnCours.getClient();
        if (null != client && null != client.getLatitude() && null != client.getLongitude()) {
            this.latitude = client.getLatitude();
            this.longitude = client.getLongitude();
        }*/
        
        this.commandesLivrees.add(commandeEnCours);
        this.commandeEnCours = null;
        this.disponible = true;
    }

    @Override
    public String toString() {
        return "Livreur{" + "id=" + id + ", capaciteMax=" + capaciteMax + ", disponible=" + disponible + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }

    abstract public String getIdentifiant();
}
