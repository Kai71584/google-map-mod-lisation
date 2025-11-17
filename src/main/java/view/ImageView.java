package view;

import controller.ZoomController;
import model.ImageModel;
import model.Perspective;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageView extends AbstractImageView {

    private ZoomController zoomController;

    public ImageView(ImageModel model, Perspective perspective) {
        super(model, perspective);

        // 🎯 Ajout du zoom à la roulette de la souris
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (zoomController == null) return;

                if (e.getPreciseWheelRotation() < 0) {
                    // roule vers le haut → zoom IN
                    zoomController.handleZoomIn();
                } else {
                    // roule vers le bas → zoom OUT
                    zoomController.handleZoomOut();
                }
            }
        });
    }

    // 🎯 Setter pour connecter le ZoomController
    public void setZoomController(ZoomController controller) {
        this.zoomController = controller;
    }

    @Override
    protected void render(Graphics2D g2d) {
        BufferedImage img = model.getSource().image();
        if (img == null) return;

        double s = perspective.getScale();
        var t = perspective.getTranslation();

        AffineTransform at = new AffineTransform();
        at.translate(getWidth() / 2.0 + t.x, getHeight() / 2.0 + t.y);
        at.scale(s, s);
        at.translate(-img.getWidth() / 2.0, -img.getHeight() / 2.0);

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(img, at, null);
    }
}
