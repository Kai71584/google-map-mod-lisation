package command;

import java.awt.Point;

import model.Perspective;

/**
 * Commande Translate : sauvegarde un snapshot de l'état avant modification
 * pour permettre l'undo/redo.
 */
public class TranslateCommand implements Command {

    private final Perspective target;
    private final int dx, dy;
    private Perspective.Snapshot stateBefore;

    public TranslateCommand(Perspective target, int dx, int dy) {
        this.target = target;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void execute() {
        // Sauvegarde l'état AVANT la modification
        stateBefore = target.createSnapshot();

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
