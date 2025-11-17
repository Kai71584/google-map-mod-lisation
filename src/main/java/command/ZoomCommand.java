package command;

import model.MementoCaretaker;
import model.Perspective;
import model.PerspectiveMemento;

/**
 * Commande Zoom utilisant le pattern Memento pour l'undo/redo.
 * Sauvegarde l'état avant modification via le Caretaker.
 */
public class ZoomCommand implements Command {

    private final Perspective target;
    private final double factor;
    private final MementoCaretaker caretaker;
    private PerspectiveMemento stateBefore;

    public ZoomCommand(Perspective target, double factor, MementoCaretaker caretaker) {
        this.target = target;
        this.factor = factor;
        this.caretaker = caretaker;
    }

    @Override
    public void execute() {
        // Sauvegarde l'état AVANT la modification
        stateBefore = target.createMemento();
        caretaker.saveMemento(stateBefore);
        
        // Effectue l'action
        double newScale = target.getScale() * factor;
        target.setScale(newScale);
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

