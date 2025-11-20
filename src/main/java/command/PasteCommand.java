package command;

import clipboard.ClipboardMediator;
import clipboard.CopyStrategy;
import model.Perspective;

/**
 * Commande Paste : sauvegarde un snapshot de l'état avant modification
 * pour permettre l'undo/redo.
 */
public class PasteCommand implements Command {

    private final Perspective target;
    private final ClipboardMediator clipboard;
    private final CopyStrategy strategy;
    private Perspective.Snapshot stateBefore;

    public PasteCommand(Perspective target, ClipboardMediator clipboard, CopyStrategy strategy) {
        this.target = target;
        this.clipboard = clipboard;
        this.strategy = strategy;
    }

    @Override
    public void execute() {
        // Sauvegarde l'état AVANT la modification
        stateBefore = target.createSnapshot();

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
