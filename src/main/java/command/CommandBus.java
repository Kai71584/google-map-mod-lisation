package command;

import model.Perspective;

public class CommandBus {

    private final CommandHistory history = CommandHistory.getInstance();

    public void execute(Command cmd) {
        cmd.execute();
        history.push(cmd);
    }

    public void undo(Perspective p) {
        history.undoLast(p);
    }

    public void redo(Perspective p) {
        history.redoLast(p);
    }
}
