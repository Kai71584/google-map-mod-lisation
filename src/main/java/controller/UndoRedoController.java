package controller;

import command.CommandBus;
import model.Perspective;
import view.AbstractImageView;

public class UndoRedoController extends AbstractController {

    public UndoRedoController(AbstractImageView view, CommandBus bus) {
        super(view, bus);
    }

    public void handleUndo() {
        Perspective p = view.getActivePerspective();
        if (p != null) {
            bus.undo(p);
        }
    }

    public void handleRedo() {
        Perspective p = view.getActivePerspective();
        if (p != null) {
            bus.redo(p);
        }
    }
}
