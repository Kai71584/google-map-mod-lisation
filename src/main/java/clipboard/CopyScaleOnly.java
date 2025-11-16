package clipboard;

import model.Perspective;

public class CopyScaleOnly implements CopyStrategy {
    @Override
    public void apply(ClipboardMediator clipboard, Perspective target) {
        if (clipboard.getScale() != null) {
            target.setScale(clipboard.getScale());
        }
    }
}
