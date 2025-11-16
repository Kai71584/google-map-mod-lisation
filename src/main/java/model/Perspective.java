package model;

import core.AbstractSubject;

import java.awt.Point;

public class Perspective extends AbstractSubject {

    private double scale = 1.0;
    private Point translation = new Point(0, 0);
    private String name;

    public Perspective(String name) {
        this.name = name;
    }

    public PerspectiveMemento createMemento() {
        return new PerspectiveMemento(scale, new Point(translation));
    }

    public void restore(PerspectiveMemento memento) {
        this.scale = memento.scale();
        this.translation = new Point(memento.translation());
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
}
