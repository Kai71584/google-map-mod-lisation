package controller;

import command.CommandBus;
import command.TranslateCommand;
import model.Perspective;
import view.AbstractImageView;
import view.ImageViewListener;
import java.awt.Point;

public class PanController extends AbstractController implements ImageViewListener {

    public PanController(AbstractImageView view, CommandBus bus) {
        super(view, bus);
    }

    @Override
    public void onPanDelta(int dx, int dy) {
        Perspective p = view.getActivePerspective();
        if (p != null) {
            bus.execute(new TranslateCommand(p, dx, dy));
        }
    }

    @Override
    public void onZoomRequested(double factor) {
        // no-op
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
