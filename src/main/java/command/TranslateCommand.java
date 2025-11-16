package command;

import model.Perspective;
import model.PerspectiveMemento;

import java.awt.Point;

public class TranslateCommand implements Command {

    private final Perspective target;
    private final int dx, dy;
    private PerspectiveMemento before;

    public TranslateCommand(Perspective target, int dx, int dy) {
        this.target = target;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void execute() {
        before = target.createMemento();
        Point t = target.getTranslation();
        target.setTranslation(new Point(t.x + dx, t.y + dy));
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
