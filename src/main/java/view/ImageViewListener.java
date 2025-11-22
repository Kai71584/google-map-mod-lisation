package view;

import java.awt.Point;

/**
 * ImageViewListener
 *
 * Interface destinée à capter les intentions de l’utilisateur exprimées via une ImageView.
 * 
 * L’idée est que la vue (ImageView) ne contient pas de logique métier : elle envoie simplement 
 * des « intentions utilisateur » vers un contrôleur. Ce listener joue donc le rôle 
 * d'intermédiaire propre entre la vue et le contrôleur (pattern MVC).
 */
public interface ImageViewListener {

    /**
     * Signal qu’un zoom est demandé par l’utilisateur.
     *
     * @param factor facteur de zoom ( >1 = zoom avant, <1 = zoom arrière )
     *
     * Exemples :
     *   - factor = 1.1  → petit zoom avant
     *   - factor = 0.9  → petit zoom arrière
     *
     * Généralement déclenché par :
     *   - molette de la souris
     *   - raccourcis clavier (+/-)
     *   - boutons dans l’UI
     */
    void onZoomRequested(double factor);

    /**
     * Indique que l’utilisateur a déplacé (panné) l’image.
     *
     * @param dx déplacement horizontal en pixels (positif = droite)
     * @param dy déplacement vertical en pixels (positif = bas)
     *
     * Déclenché par :
     *   - glisser-déposer (drag) de la souris
     *   - déplacement tactile
     */
    void onPanDelta(int dx, int dy);

    /**
     * Demande de copier la perspective courante (position + zoom).
     *
     * Déclenché par :
     *   - CTRL+C
     *   - un bouton “Copy”
     *
     * Le contrôleur copie généralement un objet Perspective dans un “ClipboardMediator”.
     */
    void onCopyRequested();

    /**
     * Demande de coller une perspective précédemment copiée.
     *
     * Déclenché par :
     *   - CTRL+V
     *   - un bouton “Paste”
     *
     * Le contrôleur récupère la perspective du presse-papier et l’applique.
     */
    void onPasteRequested();

    /**
     * Notification qu’un clic a été fait sur une vignette (thumbnail).
     *
     * @param imagePoint coordonnées du clic dans l'espace de l’image miniature
     *
     * Utilisé notamment pour :
     *   - recentrer la grande image sur une zone choisie
     *   - sélectionner une portion de l’image via la miniature
     */
    void onThumbnailClick(Point imagePoint);
}
