package command;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import model.Perspective;

public class CommandHistory {

    private static final CommandHistory INSTANCE = new CommandHistory();


    private final Map<Perspective, Deque<Command>> undoStacks = new HashMap<>();
    private final Map<Perspective, Deque<Command>> redoStacks = new HashMap<>();
    
    private CommandHistory() { }

    public static CommandHistory getInstance() {
        return INSTANCE;
    }

    public void push(Command cmd) {
        undoStacks.computeIfAbsent(cmd.target(), p -> new ArrayDeque<>()).push(cmd);
        Deque<Command> redo = redoStacks.get(cmd.target());
        if (redo != null) redo.clear();
    }

    public void undoLast(Perspective target) {
        Deque<Command> stack = undoStacks.get(target);
        if (stack == null || stack.isEmpty()) return;
        Command cmd = stack.pop();
        cmd.undo();
       
        
        redoStacks.computeIfAbsent(target, p -> new ArrayDeque<>()).push(cmd);
    }

    public void redoLast(Perspective target) {
        Deque<Command> stack = redoStacks.get(target);
        if (stack == null || stack.isEmpty()) return;
        Command cmd = stack.pop();
        cmd.execute();
      
        
        undoStacks.computeIfAbsent(target, p -> new ArrayDeque<>()).push(cmd);
    }
}
