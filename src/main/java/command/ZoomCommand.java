package command;

import model.Perspective;

/**
 * Commande Zoom : sauvegarde un snapshot de l'état avant modification
 * pour permettre l'undo/redo.
 */
public class ZoomCommand implements Command {

    private final Perspective target;
    private final double factor;
    private Perspective.Snapshot stateBefore;

    public ZoomCommand(Perspective target, double factor) {
        this.target = target;
        this.factor = factor;
    }

    @Override
    public void execute() {
        // Sauvegarde l'état AVANT la modification
        stateBefore = target.createSnapshot();

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
