package clipboard;

import java.awt.Point;

import model.Perspective;

/**
 * Stratégie de copie complète : copie à la fois l'échelle (scale)
 * et la translation (position) depuis le presse-papier vers la Perspective.
 *
 * Cette stratégie est utilisée lorsque l'utilisateur veut coller
 * l'état complet (zoom + position) sauvegardé dans le ClipboardMediator.
 */
public class CopyBoth implements CopyStrategy {

    @Override
    public void apply(ClipboardMediator clipboard, Perspective target) {

        // Copie l'échelle si elle est présente dans le presse-papier.
        if (clipboard.getScale() != null) {
            target.setScale(clipboard.getScale());
        }

        // Copie la translation si elle existe.
        Point t = clipboard.getTranslation();
        if (t != null) {
            target.setTranslation(t);
        }
    }
}
