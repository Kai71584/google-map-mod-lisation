package command;

import model.Perspective;

/**
 * CommandBus : point central d'exécution des commandes.
 *
 * Rôle :
 *   - Exécuter une commande
 *   - L'enregistrer dans l'historique Undo/Redo
 *   - Déléguer les opérations undo/redo au CommandHistory
 *
 * Design patterns utilisés :
 *   - Command
 *   - Memento (via Snapshot dans les commandes)
 *   - Mediator minimal (un bus qui centralise l’envoi des commandes)
 *
 * Avantages :
 *   - Les contrôleurs n'appellent jamais directement history
 *   - Toutes les commandes sont exécutées via ce bus → cohérence
 *   - Point unique pour instrumenter logs, analytics, sécurité
 */
public class CommandBus {

    /** 
     * Récupère l’instance unique du gestionnaire d'historique.
     * Permet d'empêcher plusieurs historiques concurrents.
     */
    private final CommandHistory history = CommandHistory.getInstance();

    /**
     * Exécute une commande.
     *
     * Étapes :
     *   1. cmd.execute() → applique l’action
     *   2. history.push(cmd) → stocke la commande pour undo/redo
     *
     * Important :
     *   - C'est le SEUL endroit où une commande est ajoutée à l'historique.
     *   - Garantit que toute commande exécutée peut être undo/redo.
     */
    public void execute(Command cmd) {
        cmd.execute();   // applique l'action
        history.push(cmd); // ajoute dans la pile Undo
    }

    /**
     * Demande un undo pour une Perspective donnée.
     *
     * Note :
     *   - Le bus ne fait qu’appeler l’historique
     *   - Cela garde les contrôleurs simples et cohérents
     */
    public void undo(Perspective p) {
        history.undoLast(p);
    }

    /**
     * Demande un redo pour une Perspective donnée.
     *
     * Symétrique à undo.
     */
    public void redo(Perspective p) {
        history.redoLast(p);
    }
}
