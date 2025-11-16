package controller;

import clipboard.ClipboardMediator;
import clipboard.CopyBoth;
import clipboard.CopyStrategy;
import command.CommandBus;
import command.PasteCommand;
import model.Perspective;
import view.AbstractImageView;

public class CopyPasteController extends AbstractController {

    private final ClipboardMediator clipboard;
    private CopyStrategy strategy = new CopyBoth(); // par défaut : copie tout

    public CopyPasteController(AbstractImageView view,
                               CommandBus bus,
                               ClipboardMediator clipboard) {
        super(view, bus);
        this.clipboard = clipboard;
    }

    public void setStrategy(CopyStrategy strategy) {
        this.strategy = strategy;
    }

    public void handleCopy() {
        Perspective p = view.getActivePerspective();
        if (p != null) {
            clipboard.storeFrom(p);
        }
    }

    public void handlePaste() {
        Perspective p = view.getActivePerspective();
        if (p != null) {
            bus.execute(new PasteCommand(p, clipboard, strategy));
        }
    }
}
