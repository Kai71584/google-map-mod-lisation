package controller;

import clipboard.ClipboardMediator;
import clipboard.Colleague;
import clipboard.CopyStrategy;
import command.CommandBus;
import command.PasteCommand;
import model.Perspective;
import view.AbstractImageView;
import view.ImageViewListener;
import java.awt.Point;

public class CopyPasteController extends AbstractController implements ImageViewListener, Colleague {

    private final ClipboardMediator clipboard;

    public CopyPasteController(AbstractImageView view,
            CommandBus bus,
            ClipboardMediator clipboard) {
        super(view, bus);
        this.clipboard = clipboard;
        this.clipboard.registerColleague(this);
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
        requestCopy();
    }

    /**
     * Demande de collage : délègue au médiateur et crée une commande
     */
    public void handlePaste() {
        requestPaste();
    }

    @Override
    public void onCopyRequested() {
        handleCopy();
    }

    @Override
    public void onPasteRequested() {
        handlePaste();
    }

    @Override
    public void onZoomRequested(double factor) {
        // no-op
    }

    @Override
    public void onPanDelta(int dx, int dy) {
        // no-op
    }

    @Override
    public void onThumbnailClick(Point imagePoint) {
        // no-op
    }

    @Override
    public void requestCopy() {
        Perspective p = getPerspective();
        if (p != null) {
            clipboard.mediateCopy(this);
        }
    }

    @Override
    public void requestPaste() {
        Perspective p = getPerspective();
        if (p == null || clipboard.isEmpty()) {
            return;
        }

        CopyStrategy snapshot = clipboard.getCurrentStrategy();
        if (snapshot == null) {
            return;
        }

        bus.execute(new PasteCommand(p, clipboard, snapshot, this));
    }

    @Override
    public void receiveCopyData(Double scale, Point translation) {
        // Pas d'action spécifique côté contrôleur pour l'instant
    }

    @Override
    public Perspective getPerspective() {
        return view.getActivePerspective();
    }
}
