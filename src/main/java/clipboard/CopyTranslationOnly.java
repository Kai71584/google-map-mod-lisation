package clipboard;

import model.Perspective;

import java.awt.Point;

public class CopyTranslationOnly implements CopyStrategy {
    @Override
    public void apply(ClipboardMediator clipboard, Perspective target) {
        Point t = clipboard.getTranslation();
        if (t != null) {
            target.setTranslation(t);
        }
    }
}
