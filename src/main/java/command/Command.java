package command;

import model.Perspective;

public interface Command {
    void execute();
    void undo();
    Perspective target();
}
