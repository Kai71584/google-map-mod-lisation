package controller;

import command.CommandBus;
import command.ZoomCommand;
import model.Perspective;
import view.AbstractImageView;
import view.ImageViewListener;
import java.awt.Point;

public class ZoomController extends AbstractController implements ImageViewListener {

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
        if (p == null)
            return;
        bus.execute(new ZoomCommand(p, factor));
    }

    @Override
    public void onZoomRequested(double factor) {
        executeZoom(factor);
    }

    @Override
    public void onPanDelta(int dx, int dy) {
        // no-op for this controller
    }

    @Override
    public void onCopyRequested() {
        // no-op
    }

    @Override
    public void onPasteRequested() {
        // no-op
    }

    @Override
    public void onThumbnailClick(Point imagePoint) {
        // no-op
    }
}
