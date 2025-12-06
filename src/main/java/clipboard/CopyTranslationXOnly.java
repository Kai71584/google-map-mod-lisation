package clipboard;

import java.awt.Point;

import model.Perspective;

/**
 * Stratégie copy/paste : copie uniquement la coordonnée X (horizontale) de la translation.
 * 
 * Cas d'usage :
 * - Copier la position horizontale d'une perspective
 * - Appliquer cette position X à une autre perspective
 * - Conserver la position Y et l'échelle inchangées
 */
public class CopyTranslationXOnly implements CopyStrategy {

    @Override
    public void apply(ClipboardMediator clipboard, Perspective target) {
        // Récupère la translation actuellement stockée dans le presse-papiers
        Point copiedTranslation = clipboard.getTranslation();
        
        if (copiedTranslation != null) {
            // Récupère la translation actuelle de la cible
            Point currentTranslation = target.getTranslation();
            
            // Crée une nouvelle translation avec :
            // - X du presse-papiers
            // - Y de la cible (inchangé)
            Point newTranslation = new Point(copiedTranslation.x, currentTranslation.y);
            target.setTranslation(newTranslation);
        }
    }
}
