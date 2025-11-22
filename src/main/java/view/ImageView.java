package view;

import model.ImageModel;
import model.Perspective;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * ImageView
 *
 * Vue principale affichant l’image zoomable/pannable.
 * Elle gère :
 *  - l’affichage de l’image avec transformation (zoom + translation)
 *  - la redirection des intentions utilisateur (zoom, pan, clic, etc.)
 *  - la gestion de la vignette (thumbnail) en overlay
 *
 * Cette classe ne contient pas de logique métier : elle notifie simplement
 * les contrôleurs via ImageViewListener (pattern MVC + Observer).
 */
public class ImageView extends AbstractImageView {

    // Liste des listeners qui recevront les intentions utilisateur
    private final List<ImageViewListener> listeners = new ArrayList<>();

    /**
     * Constructeur : installe la vue, les interactions et la miniature.
     */
    public ImageView(ImageModel model, Perspective perspective) {
        super(model, perspective);

        // On désactive le layout manager pour positionner la vignette en absolu (overlay)
        setLayout(null);

        // --- Création de la vignette (thumbnail) ---
        Dimension thumbPref = new Dimension(200, 140);
        ThumbnailView thumb = new ThumbnailView(model, perspective, this, thumbPref);
        add(thumb);

        // Positionnement initial de la vignette (sera ajusté au resize)
        thumb.setBounds(
            Math.max(0, getWidth() - thumbPref.width - 10),
            10,
            thumbPref.width,
            thumbPref.height
        );

        // --- Repositionnement automatique de la vignette lors d’un resize ---
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int x = Math.max(6, getWidth() - thumbPref.width - 10);
                int y = 10;
                thumb.setBounds(x, y, thumbPref.width, thumbPref.height);
            }
        });

        // --- Zoom souris (molette) ---
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                // rotation négative = zoom avant
                double factor = (e.getPreciseWheelRotation() < 0)
                        ? 1.1
                        : (1.0 / 1.1);

                // On notifie tous les listeners
                for (ImageViewListener l : listeners) {
                    l.onZoomRequested(factor);
                }
            }
        });

        // --- Pan (drag souris) ---
        MouseAdapter dragAdapter = new MouseAdapter() {
            private int lastX, lastY;

            @Override
            public void mousePressed(MouseEvent e) {
                // On mémorise la position initiale pour mesurer le delta
                lastX = e.getX();
                lastY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // Calcul des déplacements
                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;

                // Mise à jour du point de référence
                lastX = e.getX();
                lastY = e.getY();

                // Notifie tous les listeners
                for (ImageViewListener l : listeners) {
                    l.onPanDelta(dx, dy);
                }
            }
        };

        // On écoute clics + drags
        addMouseListener(dragAdapter);
        addMouseMotionListener(dragAdapter);
    }

    // 🔌 Connexion d’un contrôleur (ZoomController, PanController, etc.)
    /**
     * Ajoute un listener pour recevoir les événements utilisateur.
     */
    public void addImageViewListener(ImageViewListener listener) {
        if (listener != null)
            listeners.add(listener);
    }

    /**
     * Supprime un listener.
     */
    public void removeImageViewListener(ImageViewListener listener) {
        listeners.remove(listener);
    }

    /**
     * Méthode Template Method à implémenter depuis AbstractImageView.
     * Rend l’image transformée dans le Graphics2D.
     */
    @Override
    protected void render(Graphics2D g2d) {
        BufferedImage img = model.getSource().image();
        if (img == null)
            return;

        double s = perspective.getScale();      // facteur de zoom
        var t = perspective.getTranslation();   // translation en pixels

        // Transformation complète : translation + zoom + recentrage de l'image
        AffineTransform at = new AffineTransform();
        at.translate(getWidth() / 2.0 + t.x, getHeight() / 2.0 + t.y);
        at.scale(s, s);
        at.translate(-img.getWidth() / 2.0, -img.getHeight() / 2.0);

        // Interpolation douce pour une meilleure qualité de zoom
        g2d.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );

        // Dessine l'image transformée
        g2d.drawImage(img, at, null);
    }
}
