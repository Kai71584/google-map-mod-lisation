package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import model.ImageModel;
import model.Perspective;

/**
 * ThumbnailView affiche une vignette (aperçu miniature) de l'image entière
 * ainsi qu'un rectangle montrant la zone actuellement visible dans la vue principale.
 *
 * C'est une vue passive (overlay), non-interactive, placée au-dessus ou à côté
 * de la vue principale.
 *
 * Elle observe le modèle (AbstractImageView) et se met à jour quand
 * la Perspective change (zoom / pan).
 */
public class ThumbnailView extends AbstractImageView {

    // Référence vers la vue principale (permet d'obtenir sa taille réelle)
    private final AbstractImageView mainView;

    // Marges internes de la vignette
    private final int padding = 6;

    /**
     * Constructeur.
     *
     * @param model        modèle contenant l’image
     * @param perspective  perspective suivie (la même que la vue principale)
     * @param mainView     vue principale (utilisée pour calculer le viewport)
     * @param preferred    taille souhaitée de la vignette
     */
    public ThumbnailView(
            ImageModel model,
            Perspective perspective,
            AbstractImageView mainView,
            Dimension preferred
    ) {
        super(model, perspective);
        this.mainView = mainView;

        // La vignette ne doit pas peindre un fond opaque
        setOpaque(false);

        // On fixe sa taille dans tous les systèmes (preferred/min/max)
        setPreferredSize(preferred);
        setSize(preferred);
        setMaximumSize(preferred);
        setMinimumSize(preferred);

        // Désactivation d'interactivité
        setFocusable(false);
        setEnabled(false);
    }

    /**
     * Dessine la vignette + le rectangle du viewport.
     * Méthode appelée par AbstractImageView (Template Method).
     */
    @Override
    protected void render(Graphics2D g2d) {

        // Récupère l'image du modèle
        BufferedImage img = model.getSource().image();
        if (img == null)
            return;

        int w = getWidth();
        int h = getHeight();

        // --- 1) Dessine un fond semi-transparent arrondi ---
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(0, 0, w, h, 8, 8);

        // --- 2) Calcul pour ajuster l’image dans la vignette ---
        // Ratio largeur/hauteur disponible en tenant compte du padding
        double scaleX = (double) (w - 2 * padding) / img.getWidth();
        double scaleY = (double) (h - 2 * padding) / img.getHeight();

        // On prend le plus petit pour conserver le ratio
        double scale = Math.min(scaleX, scaleY);

        // Dimensions de l’image miniaturisée
        int drawW = (int) Math.round(img.getWidth() * scale);
        int drawH = (int) Math.round(img.getHeight() * scale);

        // Centrage de l’image miniature dans la vignette
        int offsetX = padding + (w - 2 * padding - drawW) / 2;
        int offsetY = padding + (h - 2 * padding - drawH) / 2;

        // --- 3) Dessine l’image redimensionnée avec interpolation bilinéaire ---
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                             RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2d.drawImage(img, offsetX, offsetY, drawW, drawH, null);

        // --- 4) Calcul du rectangle visible (viewport) ---

        // Zoom courant
        double s = perspective.getScale();

        // Taille réelle de la vue principale (en pixels)
        int mainW = Math.max(1, mainView.getWidth());
        int mainH = Math.max(1, mainView.getHeight());

        // Taille visible en coordonnées image (inverse du zoom)
        double visImgW = mainW / s;
        double visImgH = mainH / s;

        // Dimensions de l'image
        double imgW = img.getWidth();
        double imgH = img.getHeight();

        // --- Conversion des coordonnées de l'écran → coordonnées image ---
        //
        // La Perspective centre l’image en (0,0) avant translation,
        // donc on recentre par rapport à mainView,
        // puis on enlève la translation,
        // puis on divise par le zoom,
        // enfin on compense le fait que l’image est centrée dans ses coordonnées.
        //
        double imgX0 = ((0 - mainW / 2.0 - perspective.getTranslation().x) / s) + imgW / 2.0;
        double imgY0 = ((0 - mainH / 2.0 - perspective.getTranslation().y) / s) + imgH / 2.0;

        // --- 5) Conversion coordonnées image → coordonnées vignette ---
        double rx = offsetX + (imgX0 * scale);
        double ry = offsetY + (imgY0 * scale);
        double rW = visImgW * scale;
        double rH = visImgH * scale;

        // --- 6) Dessine le rectangle du viewport ---
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.drawRect(
            (int) Math.round(rx),
            (int) Math.round(ry),
            Math.max(1, (int) Math.round(rW)),
            Math.max(1, (int) Math.round(rH))
        );

        // --- 7) Bord de la vignette ---
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
    }

    /**
     * Méthode appelée quand la Perspective change (zoom, pan).
     * Ici on ne fait que redessiner la vignette.
     */
    @Override
    public void update() {
        repaint();
    }
}
