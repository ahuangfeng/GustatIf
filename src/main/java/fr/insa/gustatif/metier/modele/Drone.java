/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insa.gustatif.metier.modele;

import java.io.Serializable;
import javax.persistence.Entity;

/**
 *
 */
@Entity
public class Drone extends Livreur implements Serializable {

    private Integer vitesse;

    protected Drone() {
    }

    public Drone(Integer vitesse, Integer capaciteMax, Boolean disponible, Double latitude, Double longitude) {
        super(capaciteMax, disponible, longitude, latitude);
        this.vitesse = vitesse;
    }

    public Integer getVitesse() {
        return vitesse;
    }

    public String getMatricule() {
        return "MAT" + getId();
    }

    public void setVitesse(Integer vitesse) {
        this.vitesse = vitesse;
    }

    @Override
    public String toString() {
        return "Drone{" + super.toString() + ", vitesse=" + vitesse + ", matricule=" + getMatricule() + '}';
    }

}
