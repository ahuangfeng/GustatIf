ALTER TABLE COMMANDE DROP COLUMN LIVREUR_ID;
ALTER TABLE COMMANDE DROP COLUMN CLIENT_ID;
ALTER TABLE COMMANDE DROP COLUMN RESTAURANT_ID;

DROP TABLE CLIENT_COMMANDE;
DROP TABLE COMMANDE_PRODUITCOMMANDE;
DROP TABLE LIVREUR_COMMANDE;
DROP TABLE RESTAURANT_PRODUIT;
DROP TABLE CYCLISTE;
DROP TABLE DRONE;
DROP TABLE PRODUITCOMMANDE;
DROP TABLE LIVREUR;
DROP TABLE GESTIONNAIRE;
DROP TABLE PRODUIT;
DROP TABLE RESTAURANT;
DROP TABLE COMMANDE;
DROP TABLE CLIENT;
DROP TABLE SEQUENCE;