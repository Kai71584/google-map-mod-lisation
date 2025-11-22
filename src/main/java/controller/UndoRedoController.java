package controller;

import command.CommandBus;
import model.Perspective;
import view.AbstractImageView;

/**
 * Contrôleur responsable de déclencher les opérations d'annulation (undo)
 * et ré-application (redo) sur la Perspective active.
 *
 * Ce contrôleur ne gère pas directement la logique d'annulation/répétition :
 * il délègue tout au CommandBus, qui centralise l’historique des commandes.
 *
 * Le contrôleur récupère simplement la Perspective active auprès de la vue
 * et appelle la méthode appropriée sur le bus.
 */
public class UndoRedoController extends AbstractController {

    /**
     * Construit un contrôleur Undo/Redo.
     *
     * @param view la vue qui expose la Perspective active
     * @param bus  le bus responsable d'exécuter, annuler et refaire les commandes
     */
    public UndoRedoController(AbstractImageView view, CommandBus bus) {
        super(view, bus);
    }

    /**
     * Demande l’annulation de la dernière commande appliquée sur la Perspective active.
     *
     * Si aucune Perspective n'est active (cas improbable mais possible
     * si la vue n’a pas encore été initialisée), la méthode sort silencieusement.
     */
    public void handleUndo() {
        Perspective p = view.getActivePerspective();
        if (p != null) {
            bus.undo(p);
        }
    }

    /**
     * Demande de réappliquer une commande précédemment annulée.
     *
     * Là encore, l’opération est déléguée au CommandBus, qui gère
     * l’historique propre à chaque Perspective.
     */
    public void handleRedo() {
        Perspective p = view.getActivePerspective();
        if (p != null) {
            bus.redo(p);
        }
    }
}
