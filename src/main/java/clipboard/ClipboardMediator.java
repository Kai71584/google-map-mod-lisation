package clipboard;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import model.Perspective;

/**
 * Médiateur pour gérer les opérations de copie/collage.
 * Orchestre la communication entre les collègues (Perspectives et contrôleurs).
 * Centralise les données et les stratégies de copie.
 */
public class ClipboardMediator {

    private Double scale;
    private Point translation;
    private CopyStrategy currentStrategy;
    private final List<Colleague> colleagues = new ArrayList<>();

    public ClipboardMediator() {
        this.currentStrategy = new CopyBoth(); // Stratégie par défaut
    }

    /**
     * Enregistre un collègue auprès du médiateur
     */
    public void registerColleague(Colleague colleague) {
        if (!colleagues.contains(colleague)) {
            colleagues.add(colleague);
        }
    }

    /**
     * Désenregistre un collègue
     */
    public void unregisterColleague(Colleague colleague) {
        colleagues.remove(colleague);
    }

    /**
     * Change la stratégie de copie/collage
     */
    public void setStrategy(CopyStrategy strategy) {
        this.currentStrategy = strategy != null ? strategy : this.currentStrategy;
    }

    /**
     * Médiation d'une demande de copie depuis une perspective
     */
    public void mediateCopy(Colleague source) {
        if (source == null)
            return;
        Perspective perspective = source.getPerspective();
        if (perspective == null)
            return;

        this.scale = perspective.getScale();
        this.translation = perspective.getTranslation();
        notifyCopyDistributed(source);
    }

    /**
     * Médiation d'une demande de collage vers une perspective
     */
    public void mediatePaste(Colleague target, CopyStrategy strategy) {
        if (target == null || isEmpty())
            return;

        Perspective perspective = target.getPerspective();
        if (perspective == null)
            return;

        CopyStrategy effective = strategy != null ? strategy : currentStrategy;
        if (effective == null)
            return;

        effective.apply(this, perspective);
        target.receiveCopyData(scale, getTranslation());
    }

    /**
     * Données accessibles aux stratégies via le médiateur
     */
    public Double getScale() {
        return scale;
    }

    public Point getTranslation() {
        return translation == null ? null : new Point(translation);
    }

    public CopyStrategy getCurrentStrategy() {
        return currentStrategy;
    }

    public boolean isEmpty() {
        return scale == null && translation == null;
    }

    private void notifyCopyDistributed(Colleague origin) {
        for (Colleague colleague : colleagues) {
            if (colleague != origin) {
                colleague.receiveCopyData(scale, getTranslation());
            }
        }
    }
}

