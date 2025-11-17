package command;

import java.awt.Point;

import model.MementoCaretaker;
import model.Perspective;
import model.PerspectiveMemento;

/**
 * Commande Translate utilisant le pattern Memento pour l'undo/redo.
 * Sauvegarde l'état avant modification via le Caretaker.
 */
public class TranslateCommand implements Command {

    private final Perspective target;
    private final int dx, dy;
    private final MementoCaretaker caretaker;
    private PerspectiveMemento stateBefore;

    public TranslateCommand(Perspective target, int dx, int dy, MementoCaretaker caretaker) {
        this.target = target;
        this.dx = dx;
        this.dy = dy;
        this.caretaker = caretaker;
    }

    @Override
    public void execute() {
        // Sauvegarde l'état AVANT la modification
        stateBefore = target.createMemento();
        caretaker.saveMemento(stateBefore);
        
        // Effectue l'action
        Point t = target.getTranslation();
        target.setTranslation(new Point(t.x + dx, t.y + dy));
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
