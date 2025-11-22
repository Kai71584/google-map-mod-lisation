package model;

import java.awt.image.BufferedImage;

/**
 * Représente une source d'image.
 *
 * Cette interface permet d'abstraire l'origine de l'image affichée ou manipulée.
 * L'implémentation concrète peut récupérer l'image de différentes manières :
 *   - chargement depuis un fichier
 *   - téléchargement depuis Internet
 *   - génération procédurale (ex : rendu fractal)
 *   - extraction depuis un buffer mémoire déjà existant
 *
 * Le but principal est de découpler Model / Persistence / View :
 * la vue ou le modèle n'ont pas besoin de connaître la façon dont l'image a été chargée.
 */
public interface ImageSource {

    /**
     * Retourne l'image sous forme de BufferedImage.
     * 
     * L'appelant ne sait pas si :
     *   - l’image est lue depuis un fichier
     *   - l’image est mise en cache
     *   - l’image est générée
     * 
     * Cette abstraction permet :
     *   ✔ plus de flexibilité
     *   ✔ d'éviter les dépendances directes sur le système de fichiers
     *   ✔ de faciliter les tests (mock d'image)
     */
    BufferedImage image();
}
