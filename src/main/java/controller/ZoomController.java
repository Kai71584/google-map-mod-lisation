package controller;

import command.CommandBus;
import command.ZoomCommand;
import model.Perspective;
import view.AbstractImageView;

public class ZoomController extends AbstractController {

    private static final double DEFAULT_FACTOR = 1.1;

    public ZoomController(AbstractImageView view, CommandBus bus) {
        super(view, bus);
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
        bus.execute(new ZoomCommand(p, factor));
    }
}
