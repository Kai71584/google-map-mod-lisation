package command;

import clipboard.ClipboardMediator;
import clipboard.CopyStrategy;
import model.Perspective;
import model.PerspectiveMemento;

public class PasteCommand implements Command {

    private final Perspective target;
    private final ClipboardMediator clipboard;
    private final CopyStrategy strategy;
    private PerspectiveMemento before;

    public PasteCommand(Perspective target, ClipboardMediator clipboard, CopyStrategy strategy) {
        this.target = target;
        this.clipboard = clipboard;
        this.strategy = strategy;
    }

    @Override
    public void execute() {
        before = target.createMemento();
        strategy.apply(clipboard, target);
    }

    @Override
    public void undo() {
        if (before != null) {
            target.restore(before);
        }
    }

    @Override
    public Perspective target() {
        return target;
    }
}
