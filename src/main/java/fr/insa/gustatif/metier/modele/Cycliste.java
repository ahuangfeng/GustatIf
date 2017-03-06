/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insa.gustatif.metier.modele;

import javax.persistence.Entity;

/**
 *
 * @author alexhuang05
 */
@Entity
public class Cycliste extends Livreur{

    public Cycliste() {
    }

    public Cycliste(String nom, String prenom, String mail, Integer capaciteMax, Boolean disponible) {
        super(nom, prenom, mail, capaciteMax, disponible);
    }

    @Override
    public String toString() {
        return "Cycliste{" + super.getString() + '}';
    }

    
}
