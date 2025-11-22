package command;

import model.Perspective;

/**
 * Interface de base du patron Command.
 *
 * Une Command représente une action exécutable sur une Perspective :
 *   - zoom
 *   - translation
 *   - collage
 *   - etc.
 *
 * Chaque commande :
 *   1. sait s'exécuter (execute)
 *   2. sait s'annuler (undo) — grâce à un snapshot capturé avant exécution
 *   3. connaît la Perspective sur laquelle elle agit (target)
 *
 * Cela permet :
 *   - Undo/Redo propre et isolé
 *   - Historique par Perspective
 *   - Découplage total entre contrôleurs et logique métier
 */
public interface Command {

    /**
     * Exécute l'action.
     *
     * C'est ici que la commande applique sa modification :
     *   - ZoomCommand → modifie l'échelle
     *   - TranslateCommand → modifie la translation
     *   - PasteCommand → applique la stratégie du presse-papier
     *
     * Important :
     *   - execute() doit TOUJOURS capturer un état avant la modification
     *     (via createSnapshot()) dans la commande concrète.
     */
    void execute();

    /**
     * Annule l'action précédemment exécutée.
     *
     * C'est possible grâce au Snapshot de Perspective que la commande
     * a enregistré avant l'exécution.
     *
     * Undo doit remettre la Perspective EXACTEMENT dans l’état sauvegardé.
     */
    void undo();

    /**
     * Retourne la perspective sur laquelle la commande agit.
     *
     * Pourquoi ?
     *   - L'historique (CommandHistory) classe les commandes par Perspective.
     *   - Undo/Redo doivent être indépendants selon la vue active.
     *
     * Exemple :
     *   Undo pour la Perspective A ne doit pas impacte B.
     *
     * Ce lien explicite permet au CommandBus d'être générique et robuste.
     */
    Perspective target();
}
