package fr.insa.gustatif.vue;

/**
 * Cette exception est une facilité pour retourner à l'accueil de la simulation,
 * permise car il s'agit ici d'une simulation et non du livrable final.
 * Cette exception n'hérite pas d'Exception, pour ne pas être attrapée par un
 * catch trop large.
 */
public class BackToHomeException extends Throwable {
    
}
