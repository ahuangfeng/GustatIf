package fr.insa.gustatif.vue;

import fr.insa.gustatif.dao.JpaUtil;
import fr.insa.gustatif.util.Saisie;

/**
 *
 */
public class Simulation {

    public static void main(String[] args) {
        JpaUtil.init();
        JpaUtil.creerEntityManager();
        
        int choix = Saisie.choixMenu("Quelle simulation voulez-vous lancer ?", new String[]{
            "Simulation de l'IHM publique",
            "Simulation de l'IHM d'admin",
            "Quitter"
        });

        switch (choix) {
            case 1: {
                SimulationPublic sp = new SimulationPublic();
                sp.run();
                break;
            }
            case 2: {
                SimulationAdmin sa = new SimulationAdmin();
                sa.run();
                break;
            }
        }
        
        JpaUtil.fermerEntityManager();
    }
}
