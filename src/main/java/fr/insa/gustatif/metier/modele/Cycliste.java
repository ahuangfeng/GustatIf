/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insa.gustatif.metier.modele;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Cycliste extends Livreur implements Serializable {

    private String nom;
    private String prenom;
    @Column(unique = true)
    private String mail;

    protected Cycliste() {
    }

    public Cycliste(String nom, String prenom, String mail, Integer capaciteMax, Boolean disponible, Double latitude, Double longitude) {
        super(capaciteMax, disponible, latitude, longitude);
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
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

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String getIdentifiant() {
        return getMail();
    }

    @Override
    public String toString() {
        return "Cycliste{" + super.toString() + ", nom=" + nom + ", prenom=" + prenom + ", mail=" + mail + '}';
    }
}
