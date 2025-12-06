package model;

import java.awt.Point;

import core.AbstractSubject;

/**
 * Représente une perspective (zoom + translation) appliquée à une image.
 * 
 * - Hérite de AbstractSubject : donc observable (pattern Observer).
 * - Contient le zoom, la translation, et un nom.
 * - Fournit aussi un système de "Snapshot" -> pattern Memento :
 *   permet de sauvegarder/restaurer l’état interne sans fuite d'encapsulation.
 */
public class Perspective extends AbstractSubject {

    /** Niveau de zoom (1.0 = taille normale) */
    private double scale = 1.0;

    /** Translation de la vue (déplacement x,y dans l’image) */
    private Point translation = new Point(0, 0);

    /** Nom de la perspective (ex : "Vue utilisateur 1") */
    private String name;

    /**
     * Constructeur : une perspective doit toujours avoir un nom.
     */
    public Perspective(String name) {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    //  MÉMENTO (SNAPSHOT)
    // -------------------------------------------------------------------------

    /**
     * Crée un snapshot immuable de l’état courant.
     * 
     * ➜ Pattern Memento :
     *    - Snapshot est une classe statique interne (pas d'accès au parent).
     *    - Immuable : l’appelant ne peut pas modifier son contenu.
     *    - Permet de restaurer exactement l’état au moment de la capture.
     */
    public Snapshot createSnapshot() {
        // On copie la translation pour éviter les effets de bord
        return new Snapshot(scale, new Point(translation));
    }

    /**
     * Restaure cet objet à partir d’un snapshot.
     * Notifie ensuite les observers -> la vue se mettra automatiquement à jour.
     */
    public void restore(Snapshot snapshot) {
        this.scale = snapshot.scale;
        this.translation = new Point(snapshot.translation);
        notifyObservers();
    }

    // -------------------------------------------------------------------------
    //  GETTERS / SETTERS
    // -------------------------------------------------------------------------

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        notifyObservers(); // Toute modification doit rafraîchir la UI
    }

    public Point getTranslation() {
        // On renvoie une copie pour garantir l’encapsulation
        return new Point(translation);
    }

    public void setTranslation(Point translation) {
        this.translation = new Point(translation);
        notifyObservers();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyObservers();
    }

    @Override
    public String toString() {
        return name + " (x=" + translation.x + ", y=" + translation.y + ", zoom=" + scale + ")";
    }

    // -------------------------------------------------------------------------
    //  CLASSE INTERNE SNAPSHOT (MEMENTO)
    // -------------------------------------------------------------------------

    /**
     * Snapshot immuable représentant l’état complet d’une Perspective.
     * 
     * - Classe statique : n’a pas accès aux attributs du parent.
     * - Immutable : tous les champs sont final.
     * - Seul l’objet Perspective peut créer et utiliser ce snapshot.
     */
    public static final class Snapshot {
        private final double scale;
        private final Point translation;

        /**
         * Constructeur privé : seul Perspective peut créer un snapshot.
         */
        private Snapshot(double scale, Point translation) {
            this.scale = scale;
            // Copie défensive
            this.translation = translation == null ? new Point(0, 0) : new Point(translation);
        }

        public double getScale() {
            return scale;
        }

        public Point getTranslation() {
            return new Point(translation);
        }

        @Override
        public String toString() {
            return "Perspective.Snapshot{" +
                    "scale=" + scale +
                    ", translation=" + translation +
                    '}';
        }
    }
}
