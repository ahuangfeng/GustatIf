/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insa.gustatif.metier.modele;

import javax.persistence.Entity;

/**
 *
 */
@Entity
public class Drone extends Livreur{
    
    private Integer vitesse;
    
    public Drone() {
    }

    public Drone(String nom, String prenom, String mail, Integer capaciteMax, Boolean disponible, Integer vitesse) {
        super(nom, prenom, mail, capaciteMax, disponible);
        this.vitesse = vitesse;
    }

    public Integer getVitesse() {
        return vitesse;
    }

    public void setVitesse(Integer vitesse) {
        this.vitesse = vitesse;
    }

    @Override
    public String toString() {
        return "Drone{" + super.getString() +", vitesse=" + vitesse + '}';
    }

    
}
