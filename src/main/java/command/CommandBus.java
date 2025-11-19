package command;

import model.Perspective;

public class CommandBus {

    private final CommandHistory history = CommandHistory.getInstance();
    private static final HistoryRegister register = HistoryRegister.getInstance();

    public void execute(Command cmd) {
        cmd.execute();
        history.push(cmd);
        register.push(cmd);
    }

    public void undo(Perspective p) { //revenir en arrière
        history.undoLast(p);
        //register.push(p);
    }

    public void redo(Perspective p) {//revenir en avant
        history.redoLast(p);
        // register.redoLast(p);
    }
}
