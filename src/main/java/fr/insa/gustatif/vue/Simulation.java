package fr.insa.gustatif.vue;

import fr.insa.gustatif.dao.JpaUtil;
import fr.insa.gustatif.metier.service.ServiceMetier;
import fr.insa.gustatif.util.Saisie;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Simulation {

    public static void main(String[] args) {
        try {
            JpaUtil.init();
        } catch (Exception e) {
            Logger.getLogger(ServiceMetier.class.getName()).log(Level.SEVERE, 
                    "Impossible d'initialiser le contexte de persistance. Le serveur de BDD est sûrement arrêté."
            );
            System.exit(1);
        }

        int choix = -1;
        while (choix != 4) {
            choix = Saisie.choixMenu("Quelle simulation voulez-vous lancer ?", new String[]{
                "Simulation de l'IHM publique",
                "Simulation de l'IHM d'admin",
                "Peupler la base de données (démo)",
                "Quitter"
            });

            switch (choix) {
                case 1: {
                    SimulationPublique sp = new SimulationPublique();
                    sp.run();
                    break;
                }
                case 2: {
                    SimulationAdmin sa = new SimulationAdmin();
                    sa.run();
                    break;
                }
                case 3: {
                    ServiceMetier sm = new ServiceMetier();
                    sm.genererComptesFictifs();
                    break;
                }
            }
        }

        JpaUtil.destroy();
    }
}
