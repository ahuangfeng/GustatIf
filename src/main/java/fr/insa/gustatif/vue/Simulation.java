package fr.insa.gustatif.vue;

import fr.insa.gustatif.dao.JpaUtil;
import fr.insa.gustatif.metier.service.ServiceMetier;
import fr.insa.gustatif.util.Saisie;

/**
 *
 */
public class Simulation {

    public static void main(String[] args) {
        JpaUtil.init();

        // TODO: A METTRE DANS CHAQUE METHODE DE SERVICE
        JpaUtil.creerEntityManager();

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
                    SimulationPublic sp = new SimulationPublic();
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

        JpaUtil.fermerEntityManager();
        JpaUtil.destroy();
    }
}
