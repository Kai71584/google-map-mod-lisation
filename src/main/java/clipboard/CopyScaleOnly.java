package clipboard;

import model.Perspective;

public class CopyScaleOnly implements CopyStrategy {

    @Override
    public void apply(ClipboardMediator clipboard, Perspective target) {
        // Si le presse-papier contient une valeur d'échelle (zoom)
        if (clipboard.getScale() != null) {

            // Alors on applique cette échelle à la Perspective cible.
            // Cette stratégie ne touche PAS à la translation : elle ne modifie
            // que le zoom.
            target.setScale(clipboard.getScale());
        }
    }
}
