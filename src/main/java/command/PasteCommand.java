package command;

import clipboard.ClipboardMediator;
import clipboard.Colleague;
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
    private final Colleague colleague;
    private Perspective.Snapshot stateBefore;

    public PasteCommand(Perspective target, ClipboardMediator clipboard, CopyStrategy strategy, Colleague colleague) {
        this.target = target;
        this.clipboard = clipboard;
        this.strategy = strategy;
        this.colleague = colleague;
    }

    @Override
    public void execute() {
        if (colleague == null)
            return;
        // Sauvegarde l'état AVANT la modification
        stateBefore = target.createSnapshot();

        // Effectue l'action via le médiateur
        clipboard.mediatePaste(colleague, strategy);
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
