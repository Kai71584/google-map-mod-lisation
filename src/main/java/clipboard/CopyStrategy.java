package clipboard;

import model.Perspective;

public interface CopyStrategy {

    /**
     * Applique une opération de copie/collage sur une Perspective cible.
     *
     * @param clipboard Le médiateur contenant les valeurs copiées (zoom, translation, etc.).
     * @param target    La Perspective sur laquelle les données doivent être appliquées.
     *
     * Cette méthode définit *comment* les données copiées doivent être appliquées.
     * Chaque implémentation de CopyStrategy choisit quelles propriétés copier
     * (ex: seulement la translation, seulement l'échelle, ou les deux).
     */
    void apply(ClipboardMediator clipboard, Perspective target);
}
