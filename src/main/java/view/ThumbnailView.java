package view;

import model.ImageModel;
import model.Perspective;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Thumbnail overlay view: draws a small version of the image and a rectangle
 * indicating the current viewport according to the given Perspective and the
 * main view component dimensions.
 */
public class ThumbnailView extends AbstractImageView {

    private final AbstractImageView mainView; // used to compute viewport
    private final int padding = 6;

    public ThumbnailView(ImageModel model, Perspective perspective, AbstractImageView mainView, Dimension preferred) {
        super(model, perspective);
        this.mainView = mainView;
        setOpaque(false);
        setPreferredSize(preferred);
        setSize(preferred);
        setMaximumSize(preferred);
        setMinimumSize(preferred);
        setFocusable(false);
        setEnabled(false);
    }

    @Override
    protected void render(Graphics2D g2d) {
        BufferedImage img = model.getSource().image();
        if (img == null)
            return;

        int w = getWidth();
        int h = getHeight();

        // draw background box
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(0, 0, w, h, 8, 8);

        // compute scale to fit inside thumbnail while preserving aspect
        double scaleX = (double) (w - 2 * padding) / img.getWidth();
        double scaleY = (double) (h - 2 * padding) / img.getHeight();
        double scale = Math.min(scaleX, scaleY);

        int drawW = (int) Math.round(img.getWidth() * scale);
        int drawH = (int) Math.round(img.getHeight() * scale);

        int offsetX = padding + (w - 2 * padding - drawW) / 2;
        int offsetY = padding + (h - 2 * padding - drawH) / 2;

        // draw scaled image
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(img, offsetX, offsetY, drawW, drawH, null);

        // compute visible image rectangle in image coordinates using perspective and
        // mainView size
        double s = perspective.getScale();
        int mainW = Math.max(1, mainView.getWidth());
        int mainH = Math.max(1, mainView.getHeight());

        // visible size in image coordinates
        double visImgW = mainW / s;
        double visImgH = mainH / s;

        // top-left image coord corresponding to main view's (0,0)
        double imgW = img.getWidth();
        double imgH = img.getHeight();

        double imgX0 = ((0 - mainW / 2.0 - perspective.getTranslation().x) / s) + imgW / 2.0;
        double imgY0 = ((0 - mainH / 2.0 - perspective.getTranslation().y) / s) + imgH / 2.0;

        // rectangle in thumbnail coords
        double rx = offsetX + (imgX0 * scale);
        double ry = offsetY + (imgY0 * scale);
        double rW = visImgW * scale;
        double rH = visImgH * scale;

        // draw viewport rectangle
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.drawRect((int) Math.round(rx), (int) Math.round(ry), Math.max(1, (int) Math.round(rW)),
                Math.max(1, (int) Math.round(rH)));

        // draw border
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
    }

    @Override
    public void update() {
        // repaint when perspective changes
        repaint();
    }
}
