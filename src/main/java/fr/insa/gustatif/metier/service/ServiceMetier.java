package fr.insa.gustatif.metier.service;

import fr.insa.gustatif.dao.ClientDAO;
import fr.insa.gustatif.dao.JpaUtil;
import fr.insa.gustatif.dao.ProduitDAO;
import fr.insa.gustatif.metier.modele.Client;
import fr.insa.gustatif.metier.modele.Produit;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ServiceMetier {
    
    public void creerClient(Client client) {
        JpaUtil.ouvrirTransaction();
        
        ClientDAO clientDAO = new ClientDAO();
        clientDAO.creerClient(client);
        
        JpaUtil.validerTransaction();
    }
    
    /**
     * TODO: vérifier le return
     * @return 
     */
    public List<Client> recupererClients() {
        ClientDAO clientDAO = new ClientDAO();
        try {
            return clientDAO.findAll();
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    public void creerProduit(Produit produit) {
        JpaUtil.ouvrirTransaction();

        ProduitDAO produitDAO = new ProduitDAO();
        produitDAO.creerProduit(produit);

        JpaUtil.validerTransaction();
    }

    /**
     * TODO: vérifier le return
     *
     * @return
     */
    public List<Produit> recupererProduits() {
        ProduitDAO produitDAO = new ProduitDAO();
        try {
            return produitDAO.findAll();
        } catch (Exception ex) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }
}
