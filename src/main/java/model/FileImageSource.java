package model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Implémentation concrète de l'interface ImageSource.
 *
 * Cette classe charge une image depuis un fichier du système
 * et fournit un BufferedImage que les vues pourront afficher.
 *
 * Elle encapsule complètement le chargement de l'image afin
 * que le reste de l'application n'ait pas à connaître les
 * détails de lecture du disque.
 */
public class FileImageSource implements ImageSource {

    /** 
     * Image chargée en mémoire depuis le fichier.
     * Immutable après le constructeur.
     */
    private final BufferedImage image;

    /**
     * Construit un FileImageSource en chargeant l'image depuis un chemin.
     *
     * @param path Chemin vers le fichier image (PNG, JPG, etc.)
     * @throws IOException si le fichier n'existe pas ou ne peut être lu.
     *
     * Le chargement se fait directement dans le constructeur :
     * si une erreur survient, l'objet n'est pas créé.
     */
    public FileImageSource(String path) throws IOException {
        this.image = ImageIO.read(new File(path)); // lecture du fichier image
    }

    /**
     * Retourne l'image chargée.
     *
     * Cette méthode est appelée par les vues (ImageView,
     * ThumbnailView, etc.) pour dessiner l’image avec Graphics2D.
     */
    @Override
    public BufferedImage image() {
        return image;
    }
}
