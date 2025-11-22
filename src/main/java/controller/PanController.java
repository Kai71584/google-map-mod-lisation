package controller;

import java.awt.Point;

import command.CommandBus;
import command.TranslateCommand;
import model.Perspective;
import view.AbstractImageView;
import view.ImageViewListener;

/**
 * PanController
 * --------------
 * Contrôleur responsable du "pan" (déplacement de la vue dans l’image),
 * déclenché principalement par un glisser de souris dans la vue (drag).
 *
 * Il écoute les événements provenant de ImageView via l’interface
 * ImageViewListener et réagit uniquement aux demandes de translation
 * (onPanDelta).
 *
 * Le déplacement est appliqué via une commande (TranslateCommand)
 * envoyée au CommandBus, permettant undo/redo.
 */
public class PanController extends AbstractController implements ImageViewListener {

    /**
     * Initialise le contrôleur.
     *
     * @param view Vue contenant la Perspective active.
     * @param bus  Bus de commandes utilisé pour exécuter les déplacements
     *             et permettre undo/redo.
     */
    public PanController(AbstractImageView view, CommandBus bus) {
        super(view, bus);
    }

    /**
     * Événement : la vue signale un déplacement de souris indiquant un "pan".
     *
     * @param dx déplacement horizontal (positif = vers la droite)
     * @param dy déplacement vertical (positif = vers le bas)
     *
     * Le contrôleur récupère la Perspective active et exécute une
     * TranslateCommand pour appliquer la translation.
     */
    @Override
    public void onPanDelta(int dx, int dy) {
        Perspective p = view.getActivePerspective();
        if (p != null) {
            // On applique la translation via une commande afin d’être undoable
            bus.execute(new TranslateCommand(p, dx, dy));
        }
    }

    // Les méthodes suivantes ne concernent pas ce contrôleur,
    // donc elles sont laissées en no-op.

    @Override
    public void onZoomRequested(double factor) {
        // Ce contrôleur ne gère pas le zoom
    }

    @Override
    public void onCopyRequested() {
        // Ce contrôleur ne gère pas le copier
    }

    @Override
    public void onPasteRequested() {
        // Ce contrôleur ne gère pas le coller
    }

    @Override
    public void onThumbnailClick(Point imagePoint) {
        // Ce contrôleur ne gère pas les clics sur la vignette
    }
}
