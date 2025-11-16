package command;

import model.Perspective;
import model.PerspectiveMemento;

public class ZoomCommand implements Command {

    private final Perspective target;
    private final double factor;
    private PerspectiveMemento before;

    public ZoomCommand(Perspective target, double factor) {
        this.target = target;
        this.factor = factor;
    }

    @Override
    public void execute() {
        before = target.createMemento();
        double newScale = target.getScale() * factor;
        target.setScale(newScale);
    }

    @Override
    public void undo() {
        if (before != null) {
            target.restore(before);
        }
    }

    @Override
    public Perspective target() {
        return target;
    }
}
