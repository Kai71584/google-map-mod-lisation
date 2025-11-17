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
        this.currentStrategy = strategy;
    }

    /**
     * Médiation d'une demande de copie depuis une perspective
     */
    public void mediateCopy(Perspective source) {
        // Le médiateur orchestre : il récupère les données
        this.scale = source.getScale();
        this.translation = source.getTranslation();
    }

    /**
     * Médiation d'une demande de collage vers une perspective
     */
    public void mediatePaste(Perspective target) {
        // Le médiateur orchestre : il applique la stratégie
        if (this.scale != null || this.translation != null) {
            currentStrategy.apply(this, target);
        }
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
}

