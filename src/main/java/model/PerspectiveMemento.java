package model;

import java.awt.Point;

/**
 * Memento : Représentation immuable de l'état d'une Perspective.
 * Opaque : accessible uniquement par Perspective et MementoCaretaker.
 */
public class PerspectiveMemento {
    private final double scale;
    private final Point translation;

    /**
     * Constructeur package-private : seule Perspective peut créer des mementos
     */
    PerspectiveMemento(double scale, Point translation) {
        this.scale = scale;
        this.translation = translation == null ? new Point(0, 0) : new Point(translation);
    }

    /**
     * Accesseurs package-private : accessibles uniquement dans le même package
     */
    double getScale() {
        return scale;
    }

    Point getTranslation() {
        return new Point(translation);
    }

    @Override
    public String toString() {
        return "PerspectiveMemento{" +
                "scale=" + scale +
                ", translation=" + translation +
                '}';
    }
}

