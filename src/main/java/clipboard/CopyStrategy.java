package clipboard;

import model.Perspective;

public interface CopyStrategy {
    void apply(ClipboardMediator clipboard, Perspective target);
}
