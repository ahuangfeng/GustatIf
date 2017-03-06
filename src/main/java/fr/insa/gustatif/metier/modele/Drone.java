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
public class Drone extends Livreur implements Serializable{
    
    private Integer vitesse;
    private String matricule;
    
    protected Drone(){
    }

    public Drone(Integer vitesse, String matricule) {
        this.vitesse = vitesse;
        this.matricule = matricule;
    }

    public Drone(String matricule, Integer vitesse, Integer capaciteMax, Boolean disponible, Double longitude, Double latitude) {
        super(capaciteMax, disponible, longitude, latitude);
        this.vitesse = vitesse;
        this.matricule = matricule;
    }

    public Integer getVitesse() {
        return vitesse;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setVitesse(Integer vitesse) {
        this.vitesse = vitesse;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    @Override
    public String toString() {
        return "Drone{" + super.getString() +"vitesse=" + vitesse + ", matricule=" + matricule + '}';
    }
    
    

    
}
