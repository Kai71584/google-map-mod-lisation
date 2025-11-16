package clipboard;

import model.Perspective;

import java.awt.Point;

public class CopyBoth implements CopyStrategy {
    @Override
    public void apply(ClipboardMediator clipboard, Perspective target) {
        if (clipboard.getScale() != null) {
            target.setScale(clipboard.getScale());
        }
        Point t = clipboard.getTranslation();
        if (t != null) {
            target.setTranslation(t);
        }
    }
}
