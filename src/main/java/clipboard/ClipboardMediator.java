package clipboard;

import model.Perspective;

import java.awt.Point;

public class ClipboardMediator {

    private Double scale;
    private Point translation;

    public void storeFrom(Perspective p) {
        this.scale = p.getScale();
        this.translation = p.getTranslation();
    }

    public Double getScale() {
        return scale;
    }

    public Point getTranslation() {
        return translation == null ? null : new Point(translation);
    }

    public boolean isEmpty() {
        return scale == null && translation == null;
    }
}
