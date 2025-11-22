package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import model.ImageModel;
import model.Perspective;

/**
 * CoordinatesView
 *
 * Petite vue overlay affichant les coordonnées de translation et
 * le niveau de zoom actuels.
 *
 * Cette vue ne dessine pas l'image, mais uniquement une boîte d'information
 * destinée à aider l'utilisateur à comprendre la position courante.
 *
 * Comme toutes les sous-classes d’AbstractImageView :
 *  - elle se redessine automatiquement quand la Perspective change,
 *  - elle utilise la méthode template 'render(Graphics2D)' pour dessiner.
 */
public class CoordinatesView extends AbstractImageView {

    /**
     * Constructeur simple : récupère le modèle et la perspective à observer.
     */
    public CoordinatesView(ImageModel model, Perspective perspective) {
        super(model, perspective);
    }

    /**
     * Dessine le contenu de la vue : un petit panneau transparent contenant
     * :
     *   - la translation courante (t.x, t.y)
     *   - le zoom courant (scale)
     *
     * Le but est purement informatif.
     */
    @Override
    protected void render(Graphics2D g2d) {
        var t = perspective.getTranslation(); // translation actuelle
        double s = perspective.getScale();    // facteur de zoom courant

        // --- Fond translucide de la boîte d'information ---
        g2d.setColor(new Color(255, 255, 255, 210));  // blanc légèrement transparent
        g2d.fillRoundRect(5, 5, 220, 50, 10, 10);

        // --- Paramètres du texte ---
        g2d.setColor(Color.BLACK);
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 12f));

        // Lignes de texte informatif
        String txt1 = "X: " + t.x + " | Y: " + t.y;
        String txt2 = String.format("Zoom: %.2f", s);

        // --- Dessin du texte ---
        g2d.drawString(txt1, 15, 25);
        g2d.drawString(txt2, 15, 40);
    }
}
