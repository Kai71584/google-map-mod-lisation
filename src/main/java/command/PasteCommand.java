package command;

import clipboard.ClipboardMediator;
import clipboard.CopyStrategy;
import model.MementoCaretaker;
import model.Perspective;
import model.PerspectiveMemento;

/**
 * Commande Paste utilisant le pattern Memento pour l'undo/redo.
 * Sauvegarde l'état avant modification via le Caretaker.
 */
public class PasteCommand implements Command {

    private final Perspective target;
    private final ClipboardMediator clipboard;
    private final CopyStrategy strategy;
    private final MementoCaretaker caretaker;
    private PerspectiveMemento stateBefore;

    public PasteCommand(Perspective target, ClipboardMediator clipboard, CopyStrategy strategy, MementoCaretaker caretaker) {
        this.target = target;
        this.clipboard = clipboard;
        this.strategy = strategy;
        this.caretaker = caretaker;
    }

    @Override
    public void execute() {
        // Sauvegarde l'état AVANT la modification
        stateBefore = target.createMemento();
        caretaker.saveMemento(stateBefore);
        
        // Effectue l'action
        strategy.apply(clipboard, target);
    }

    @Override
    public void undo() {
        if (stateBefore != null) {
            target.restore(stateBefore);
        }
    }

    @Override
    public Perspective target() {
        return target;
    }
}
