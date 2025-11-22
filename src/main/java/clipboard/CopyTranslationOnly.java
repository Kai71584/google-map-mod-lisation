package clipboard;

import java.awt.Point;

import model.Perspective;

public class CopyTranslationOnly implements CopyStrategy {

    @Override
    public void apply(ClipboardMediator clipboard, Perspective target) {
        // Récupère la translation actuellement stockée dans le presse-papiers.
        // clipboard.getTranslation() retourne une copie de l’objet Point (sécurité contre mutations).
        Point t = clipboard.getTranslation();

        // Si une translation existe dans le presse-papiers, on l'applique à la Perspective cible.
        // Cela signifie qu’on ne copie *que* le déplacement, pas l’échelle ou d’autres propriétés.
        if (t != null) {
            target.setTranslation(t);
        }
    }
}
