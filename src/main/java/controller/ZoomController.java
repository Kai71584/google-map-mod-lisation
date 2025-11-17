package controller;

import command.CommandBus;
import command.ZoomCommand;
import model.MementoCaretaker;
import model.Perspective;
import view.AbstractImageView;

public class ZoomController extends AbstractController {

    private static final double DEFAULT_FACTOR = 1.1;
    private final MementoCaretaker caretaker;

    public ZoomController(AbstractImageView view, CommandBus bus, MementoCaretaker caretaker) {
        super(view, bus);
        this.caretaker = caretaker;
    }

    public void handleZoomIn() {
        executeZoom(DEFAULT_FACTOR);
    }

    public void handleZoomOut() {
        executeZoom(1.0 / DEFAULT_FACTOR);
    }

    private void executeZoom(double factor) {
        Perspective p = view.getActivePerspective();
        if (p == null) return;
        bus.execute(new ZoomCommand(p, factor, caretaker));
    }
}

