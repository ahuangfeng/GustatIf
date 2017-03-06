package fr.insa.gustatif.vue;

import fr.insa.gustatif.dao.JpaUtil;

/**
 *
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Hola amigos !");
        
        JpaUtil.init();
        
        // ...
        
        JpaUtil.destroy();
    }
}
