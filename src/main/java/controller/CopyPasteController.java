package controller;

import clipboard.ClipboardMediator;
import clipboard.CopyStrategy;
import command.CommandBus;
import command.PasteCommand;
import model.MementoCaretaker;
import model.Perspective;
import view.AbstractImageView;

public class CopyPasteController extends AbstractController {

    private final ClipboardMediator clipboard;
    private final MementoCaretaker caretaker;

    public CopyPasteController(AbstractImageView view,
                               CommandBus bus,
                               ClipboardMediator clipboard,
                               MementoCaretaker caretaker) {
        super(view, bus);
        this.clipboard = clipboard;
        this.caretaker = caretaker;
        // Stratégie par défaut gérée par le médiateur
    }

    /**
     * Change la stratégie de copie/collage via le médiateur
     */
    public void setStrategy(CopyStrategy strategy) {
        clipboard.setStrategy(strategy);
    }

    /**
     * Demande de copie : délègue au médiateur
     */
    public void handleCopy() {
        Perspective p = view.getActivePerspective();
        if (p != null) {
            // Le médiateur orchestre : il prend les données de la perspective
            clipboard.mediateCopy(p);
        }
    }

    /**
     * Demande de collage : délègue au médiateur et crée une commande
     */
    public void handlePaste() {
        Perspective p = view.getActivePerspective();
        if (p != null) {
            // Le médiateur orchestre : il applique la stratégie
            bus.execute(new PasteCommand(p, clipboard, clipboard.getCurrentStrategy(), caretaker));
        }
    }
}

