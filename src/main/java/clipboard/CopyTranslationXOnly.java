package clipboard;

import java.awt.Point;

import model.Perspective;

public class CopyTranslationXOnly implements CopyStrategy {
    @Override
    public void apply(ClipboardMediator clipboard, Perspective target) {
        Point t = clipboard.getTranslation();
        if (t != null) {
            Point current = target.getTranslation();
            target.setTranslation(new Point(t.x, current.y));
        }
    }
}
