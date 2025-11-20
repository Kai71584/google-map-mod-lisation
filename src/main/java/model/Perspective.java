package model;

import java.awt.Point;

import core.AbstractSubject;

public class Perspective extends AbstractSubject {

    private double scale = 1.0;
    private Point translation = new Point(0, 0);
    private String name;

    public Perspective(String name) {
        this.name = name;
    }

    /**
     * Create an opaque snapshot of the current state.
     * Snapshot is a nested public static class so callers may store it but
     * cannot modify its contents. Only Perspective restores from a Snapshot.
     */
    public Snapshot createSnapshot() {
        return new Snapshot(scale, new Point(translation));
    }

    public void restore(Snapshot snapshot) {
        this.scale = snapshot.scale;
        this.translation = new Point(snapshot.translation);
        notifyObservers();
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        notifyObservers();
    }

    public Point getTranslation() {
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

    /**
     * Snapshot representing an immutable state of a Perspective.
     * Nested class: created only by Perspective; callers may hold and pass it back
     * but cannot modify its contents.
     */
    public static final class Snapshot {
        private final double scale;
        private final Point translation;

        private Snapshot(double scale, Point translation) {
            this.scale = scale;
            this.translation = translation == null ? new Point(0, 0) : new Point(translation);
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
