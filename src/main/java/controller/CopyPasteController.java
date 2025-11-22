package controller;

import java.awt.Point;

import clipboard.ClipboardMediator;
import clipboard.Colleague;
import clipboard.CopyStrategy;
import command.CommandBus;
import command.PasteCommand;
import model.Perspective;
import view.AbstractImageView;
import view.ImageViewListener;

/**
 * CopyPasteController
 * --------------------
 * Contrôleur responsable des actions de copie/collage.
 *
 * Il joue deux rôles :
 *   1. ImageViewListener → reçoit les intentions de l’utilisateur (Ctrl+C, Ctrl+V ou bouton)
 *   2. Colleague → participe au pattern Mediator (ClipboardMediator)
 *
 * Ce contrôleur délègue toute la logique de copie/collage au ClipboardMediator
 * selon le pattern COLLEAGUE/MEDIATOR :
 * - Le contrôleur NE stocke rien lui-même.
 * - Le médiateur conserve l'état "copié" et applique la stratégie de copie.
 */
public class CopyPasteController extends AbstractController
        implements ImageViewListener, Colleague {

    /** Médiateur de presse-papier (stockage + stratégie de copie). */
    private final ClipboardMediator clipboard;

    /**
     * Constructeur : on enregistre ce contrôleur comme collègue du médiateur.
     */
    public CopyPasteController(AbstractImageView view,
                               CommandBus bus,
                               ClipboardMediator clipboard) {
        super(view, bus);
        this.clipboard = clipboard;
        this.clipboard.registerColleague(this);  // liaison Mediator <--> Colleague
    }

    /**
     * Permet de changer dynamiquement la stratégie de copie
     * (ex : copie complète, copie du zoom seulement, copie de translation, etc.)
     */
    public void setStrategy(CopyStrategy strategy) {
        clipboard.setStrategy(strategy);
    }

    /**
     * Demande explicite de copie (ex. bouton ou autre).
     * Delegation → clipboard.mediateCopy(this)
     */
    public void handleCopy() {
        requestCopy();
    }

    /**
     * Demande explicite de collage.
     * Delegation → clipboard + exécution d’une commande Undo/Redo
     */
    public void handlePaste() {
        requestPaste();
    }

    // ----------------------------------------------------------
    // Implémentation de ImageViewListener : réactions aux actions utilisateur
    // ----------------------------------------------------------

    @Override
    public void onCopyRequested() {
        handleCopy();
    }

    @Override
    public void onPasteRequested() {
        handlePaste();
    }

    @Override
    public void onZoomRequested(double factor) {
        // Rien ici : le zoom appartient à ZoomController
    }

    @Override
    public void onPanDelta(int dx, int dy) {
        // Rien ici : les déplacements appartiennent à PanController
    }

    @Override
    public void onThumbnailClick(Point imagePoint) {
        // Non concerné
    }

    // ----------------------------------------------------------
    // Implémentation du pattern Mediator (Colleague)
    // ----------------------------------------------------------

    /**
     * Appelé par ce contrôleur ou la vue.
     * Demande au médiateur de copier la perspective active.
     */
    @Override
    public void requestCopy() {
        Perspective p = getPerspective();
        if (p != null) {
            clipboard.mediateCopy(this);  // Le Mediator récupère les données depuis ce Colleague
        }
    }

    /**
     * Demande de collage :
     * - Vérifie s’il existe un contenu dans le presse-papier
     * - Récupère la stratégie active
     * - Exécute une PasteCommand via le bus
     *
     * Suivi unifié dans l'historique UNDO / REDO.
     */
    @Override
    public void requestPaste() {
        Perspective p = getPerspective();
        if (p == null || clipboard.isEmpty()) {
            return;
        }

        CopyStrategy snapshot = clipboard.getCurrentStrategy();
        if (snapshot == null) {
            return;
        }

        // Command pattern : enregistrée dans l'historique pour Undo/Redo
        bus.execute(new PasteCommand(p, clipboard, snapshot, this));
    }

    /**
     * Le médiateur peut passer des données au collègue après une copie.
     * Actuellement inutilisé, mais l’API le permet pour une extension.
     */
    @Override
    public void receiveCopyData(Double scale, Point translation) {
        // Pas de comportement pour l’instant côté contrôleur.
    }

    /**
     * Fournit au Mediator la perspective sur laquelle opère ce contrôleur.
     */
    @Override
    public Perspective getPerspective() {
        return view.getActivePerspective();
    }
}
