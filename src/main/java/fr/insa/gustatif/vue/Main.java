package fr.insa.gustatif.vue;

import fr.insa.gustatif.dao.ClientDAO;
import fr.insa.gustatif.dao.JpaUtil;
import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Produit;
import fr.insa.gustatif.metier.service.ServiceMetier;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;

/**
 *
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Hola los amigos !");
        
        JpaUtil.init();
        JpaUtil.creerEntityManager();
        
        // Tests
        ServiceMetier sm = new ServiceMetier();
        
        // Tests Client
        sm.creerClient(new Client("1", "2", "3", "4"));
        for (Client result : sm.recupererClients()) {
            System.out.println(result);
        }
        
        // Tests Produits
        sm.creerProduit(new Produit("Gros cookie", "Un gros gros gros cookie", 1.4, 2.1));
        for (Produit result : sm.recupererProduits()) {
            System.out.println(result);
        }
        
        
        JpaUtil.fermerEntityManager();
        JpaUtil.destroy();
    }
}
