package command;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import model.Perspective;

/**
 * CommandHistory : gestion centralisée de l'historique Undo/Redo.
 *
 * Implémente un historique par Perspective :
 *   - Chaque Perspective possède sa pile d'undo
 *   - Chaque Perspective possède sa pile de redo
 *
 * Patron utilisés :
 *   - Singleton : un seul gestionnaire global pour toutes les commandes
 *   - Command : chaque action est un objet capable de s'exécuter et s'annuler
 *   - Memento : les commandes contiennent leur snapshot d'état
 *
 * Structure :
 *   undoStacks :  Map<Perspective, Deque<Command>>
 *   redoStacks :  Map<Perspective, Deque<Command>>
 *
 * Justification :
 *   - Lier l'historique à chaque Perspective permet de gérer plusieurs vues/
 *     images simultanément sans croiser leurs historiques.
 */
public class CommandHistory {

    /**
     * Instance unique du singleton.
     * Permet un accès global au gestionnaire d'historique.
     */
    private static final CommandHistory INSTANCE = new CommandHistory();

    /**
     * Piles d'undo pour chaque Perspective.
     * ArrayDeque utilisé pour performance (push/pop O(1)).
     */
    private final Map<Perspective, Deque<Command>> undoStacks = new HashMap<>();

    /**
     * Piles de redo pour chaque Perspective.
     */
    private final Map<Perspective, Deque<Command>> redoStacks = new HashMap<>();

    /**
     * Constructeur privé : impose le singleton.
     */
    private CommandHistory() { }

    /**
     * Accès global à l'unique instance.
     */
    public static CommandHistory getInstance() {
        return INSTANCE;
    }

    /**
     * Ajoute une nouvelle commande dans l'historique Undo
     * et efface le Redo associé.
     *
     * Étapes :
     *   1. La commande est poussée dans la pile Undo de sa Perspective
     *   2. La pile Redo correspondante est vidée
     *
     * Raison :
     *   Toute nouvelle action rend le redo obsolète (standard Undo/Redo).
     */
    public void push(Command cmd) {
        // Ajoute dans la pile Undo de la Perspective
        undoStacks
            .computeIfAbsent(cmd.target(), p -> new ArrayDeque<>())
            .push(cmd);

        // Efface la pile de redo, car nouvelle action => redo invalide
        Deque<Command> redo = redoStacks.get(cmd.target());
        if (redo != null) redo.clear();
    }

    /**
     * Effectue un Undo sur une Perspective donnée.
     *
     * Étapes :
     *   1. Récupère la pile Undo de la Perspective
     *   2. Pop de la commande la plus récente
     *   3. Appelle cmd.undo()
     *   4. Pousse la commande dans la pile Redo
     */
    public void undoLast(Perspective target) {
        Deque<Command> stack = undoStacks.get(target);

        // Rien à undo
        if (stack == null || stack.isEmpty()) return;

        // Récupération de la commande
        Command cmd = stack.pop();

        // Exécution de l'annulation
        cmd.undo();

        // Déplace la commande dans la pile de redo
        redoStacks
            .computeIfAbsent(target, p -> new ArrayDeque<>())
            .push(cmd);
    }

    /**
     * Effectue un Redo sur une Perspective donnée.
     *
     * Étapes :
     *   1. Récupère la pile Redo
     *   2. Pop de la commande annulée précédemment
     *   3. Ré-exécute cmd.execute()
     *   4. Replace la commande dans la pile Undo
     */
    public void redoLast(Perspective target) {
        Deque<Command> stack = redoStacks.get(target);

        // Rien à refaire
        if (stack == null || stack.isEmpty()) return;

        Command cmd = stack.pop();

        // Exécute à nouveau l'action
        cmd.execute();

        // Replace dans Undo
        undoStacks
            .computeIfAbsent(target, p -> new ArrayDeque<>())
            .push(cmd);
    }
}
