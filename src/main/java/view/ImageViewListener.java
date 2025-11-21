package view;

import java.awt.Point;

/**
 * Listener interface for user intents emitted by an ImageView.
 */
public interface ImageViewListener {
    void onZoomRequested(double factor);

    void onPanDelta(int dx, int dy);

    void onCopyRequested();

    void onPasteRequested();

    void onThumbnailClick(Point imagePoint);
}
