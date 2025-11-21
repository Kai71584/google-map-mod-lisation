package view;

import model.ImageModel;
import model.Perspective;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public class ImageView extends AbstractImageView {

    private final List<ImageViewListener> listeners = new ArrayList<>();

    public ImageView(ImageModel model, Perspective perspective) {
        super(model, perspective);
        // make layout null so we can position overlay thumbnail absolutely
        setLayout(null);

        // create thumbnail overlay and add it
        Dimension thumbPref = new Dimension(200, 140);
        ThumbnailView thumb = new ThumbnailView(model, perspective, this, thumbPref);
        add(thumb);
        // initial placement (will be corrected on first resize)
        thumb.setBounds(Math.max(0, getWidth() - thumbPref.width - 10), 10, thumbPref.width, thumbPref.height);

        // reposition thumbnail when the main view is resized
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int x = Math.max(6, getWidth() - thumbPref.width - 10);
                int y = 10;
                thumb.setBounds(x, y, thumbPref.width, thumbPref.height);
            }
        });

        // mouse wheel -> emit zoom event
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double factor = (e.getPreciseWheelRotation() < 0) ? 1.1 : (1.0 / 1.1);
                for (ImageViewListener l : listeners) {
                    l.onZoomRequested(factor);
                }
            }
        });

        // drag -> emit pan deltas
        MouseAdapter dragAdapter = new MouseAdapter() {
            private int lastX, lastY;

            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;
                lastX = e.getX();
                lastY = e.getY();
                for (ImageViewListener l : listeners) {
                    l.onPanDelta(dx, dy);
                }
            }
        };
        addMouseListener(dragAdapter);
        addMouseMotionListener(dragAdapter);
    }

    // 🎯 Setter pour connecter le ZoomController
    /**
     * Register a listener to receive view events (zoom, pan, copy, paste).
     */
    public void addImageViewListener(ImageViewListener listener) {
        if (listener != null)
            listeners.add(listener);
    }

    public void removeImageViewListener(ImageViewListener listener) {
        listeners.remove(listener);
    }

    @Override
    protected void render(Graphics2D g2d) {
        BufferedImage img = model.getSource().image();
        if (img == null)
            return;

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
