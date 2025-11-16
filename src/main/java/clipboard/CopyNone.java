package clipboard;

import model.Perspective;

public class CopyNone implements CopyStrategy {
    @Override
    public void apply(ClipboardMediator clipboard, Perspective target) {
        // Ne fait rien (Null Object)
    }
}
