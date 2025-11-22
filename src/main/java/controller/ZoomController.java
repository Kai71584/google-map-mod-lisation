package controller;

import java.awt.Point;

import command.CommandBus;
import command.ZoomCommand;
import model.Perspective;
import view.AbstractImageView;
import view.ImageViewListener;

/**
 * Contrôleur responsable de gérer les commandes de zoom.
 *
 * Il écoute les événements provenant de la vue (ImageView) via l’interface
 * ImageViewListener :
 *    - molette de souris  → onZoomRequested()
 *    - boutons + et -     → appels directs à handleZoomIn/out()
 *
 * Il encapsule tout dans un ZoomCommand exécuté par le CommandBus
 * afin de permettre:
 *    ✔ Annulation / Rétablissement (Undo/Redo)
 *    ✔ Découplage complet entre la vue et le modèle
 */
public class ZoomController extends AbstractController implements ImageViewListener {

    /** Facteur de zoom par défaut utilisé pour les boutons + / - */
    private static final double DEFAULT_FACTOR = 1.1;

    /**
     * Le constructeur initialise le contrôleur avec :
     *   - la vue associée (AbstractImageView)
     *   - le bus de commandes (CommandBus)
     *
     * Le contrôleur pourra ainsi créer et faire exécuter des ZoomCommand.
     */
    public ZoomController(AbstractImageView view, CommandBus bus) {
        super(view, bus);
    }

    /**
     * Méthode appelée quand l’utilisateur clique sur le bouton "+"
     * → applique un zoom avant.
     */
    public void handleZoomIn() {
        executeZoom(DEFAULT_FACTOR);
    }

    /**
     * Méthode appelée quand l’utilisateur clique sur le bouton "-"
     * → applique un zoom arrière.
     */
    public void handleZoomOut() {
        executeZoom(1.0 / DEFAULT_FACTOR);
    }

    /**
     * Exécute la logique du zoom via une commande (ZoomCommand).
     * Le contrôleur :
     *   1) récupère la Perspective active depuis la vue,
     *   2) crée une ZoomCommand(perspective, factor),
     *   3) demande au CommandBus de l’exécuter.
     */
    private void executeZoom(double factor) {
        Perspective p = view.getActivePerspective();
        if (p == null)
            return;

        bus.execute(new ZoomCommand(p, factor));
    }

    /**
     * Appelé automatiquement par ImageView quand la molette de souris est tournée.
     * Il délègue au même mécanisme que les boutons (Command + CommandBus).
     */
    @Override
    public void onZoomRequested(double factor) {
        executeZoom(factor);
    }

    // Les méthodes suivantes existent car l'interface ImageViewListener les exige,
    // mais ce contrôleur ne les utilise pas (principe du "no-op").

    @Override
    public void onPanDelta(int dx, int dy) {
        // Ce contrôleur ne gère pas le déplacement (pan).
    }

    @Override
    public void onCopyRequested() {
        // Ce contrôleur ne gère pas le copier/coller.
    }

    @Override
    public void onPasteRequested() {
        // Rien ici non plus.
    }

    @Override
    public void onThumbnailClick(Point imagePoint) {
        // Le zoom controller n’a rien à faire lorsqu’on clique dans la vignette.
    }
}
