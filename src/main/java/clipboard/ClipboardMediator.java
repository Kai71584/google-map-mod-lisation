package clipboard;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import model.Perspective;

/**
 * Médiateur pour gérer les opérations de copie/collage.
 *
 * Ce composant centralise :
 * - les données copiées (scale, translation)
 * - les stratégies de copie (CopyStrategy)
 * - la communication entre les collègues (Colleague)
 *
 * Il empêche les contrôleurs et les perspectives d'interagir directement,
 * ce qui réduit les dépendances et facilite l’extension du comportement.
 */
public class ClipboardMediator {

    // Données copiées du dernier copy()
    private Double scale;
    private Point translation;

    // Stratégie active (CopyBoth par défaut)
    private CopyStrategy currentStrategy;

    // Liste des objets intéressés par les changements du presse-papier
    private final List<Colleague> colleagues = new ArrayList<>();

    public ClipboardMediator() {
        // Stratégie par défaut : copie complète (scale + translation)
        this.currentStrategy = new CopyBoth();
    }

    /**
     * Enregistre un collègue auprès du médiateur.
     * Le collègue pourra recevoir les notifications lors d'un COPY.
     */
    public void registerColleague(Colleague colleague) {
        if (!colleagues.contains(colleague)) {
            colleagues.add(colleague);
        }
    }

    /**
     * Retire un collègue du médiateur.
     */
    public void unregisterColleague(Colleague colleague) {
        colleagues.remove(colleague);
    }

    /**
     * Permet de changer dynamiquement la stratégie de copie/collage
     * (ex. : CopyScaleOnly, CopyTranslationOnly, CopyNone...).
     */
    public void setStrategy(CopyStrategy strategy) {
        this.currentStrategy = strategy != null ? strategy : this.currentStrategy;
    }

    /**
     * Médiation d'une demande de copie.
     * - le médiateur récupère l'état d'une Perspective (scale + translation)
     * - il stocke ces valeurs
     * - il notifie les autres collègues de la mise à jour du clipboard
     */
    public void mediateCopy(Colleague source) {
        if (source == null)
            return;

        Perspective perspective = source.getPerspective();
        if (perspective == null)
            return;

        // Stockage des données copiées
        this.scale = perspective.getScale();
        this.translation = perspective.getTranslation();

        // Assure la propagation de la mise à jour aux autres collègues
        notifyCopyDistributed(source);
    }

    /**
     * Médiation d'une demande de collage.
     *
     * Le médiateur :
     * - vérifie que le presse-papier contient des données
     * - choisit la stratégie à appliquer (celle passée en paramètre ou celle active)
     * - applique cette stratégie sur la perspective ciblée
     * - retourne les données appliquées au collègue via receiveCopyData()
     */
    public void mediatePaste(Colleague target, CopyStrategy strategy) {
        if (target == null || isEmpty())
            return;

        Perspective perspective = target.getPerspective();
        if (perspective == null)
            return;

        // Sélection de la stratégie utilisée (paramètre > stratégie courante)
        CopyStrategy effective = strategy != null ? strategy : currentStrategy;
        if (effective == null)
            return;

        // Application de la stratégie sur la perspective
        effective.apply(this, perspective);

        // Notifie le collègue des données réellement appliquées
        target.receiveCopyData(scale, getTranslation());
    }

    /**
     * Accès aux données copiées pour les stratégies.
     */
    public Double getScale() {
        return scale;
    }

    public Point getTranslation() {
        // Retourne une copie défensive pour éviter les modifications externes
        return translation == null ? null : new Point(translation);
    }

    public CopyStrategy getCurrentStrategy() {
        return currentStrategy;
    }

    /**
     * Indique si le presse-papier contient des données exploitables.
     */
    public boolean isEmpty() {
        return scale == null && translation == null;
    }

    /**
     * Notifie tous les collègues — sauf celui qui a initié la copie —
     * que de nouvelles données sont disponibles dans le presse-papier.
     *
     * Permet à l’UI ou d’autres contrôleurs de réagir automatiquement.
     */
    private void notifyCopyDistributed(Colleague origin) {
        for (Colleague colleague : colleagues) {
            if (colleague != origin) {
                colleague.receiveCopyData(scale, getTranslation());
            }
        }
    }
}
