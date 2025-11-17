package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Caretaker : Conserve les mementos pour l'historique Undo/Redo.
 * Responsable de sauvegarder et restaurer l'état des perspectives.
 */
public class MementoCaretaker {

    private final List<PerspectiveMemento> undoStack = new ArrayList<>();
    private final List<PerspectiveMemento> redoStack = new ArrayList<>();

    /**
     * Sauvegarde un memento dans la pile undo
     */
    public void saveMemento(PerspectiveMemento memento) {
        undoStack.add(memento);
        // Vider la pile redo quand une nouvelle action est effectuée
        redoStack.clear();
    }

    /**
     * Récupère et retire le dernier memento undo
     */
    public PerspectiveMemento popUndo() {
        if (undoStack.isEmpty()) {
            return null;
        }
        return undoStack.remove(undoStack.size() - 1);
    }

    /**
     * Récupère et retire le dernier memento redo
     */
    public PerspectiveMemento popRedo() {
        if (redoStack.isEmpty()) {
            return null;
        }
        return redoStack.remove(redoStack.size() - 1);
    }

    /**
     * Ajoute un memento à la pile redo
     */
    public void pushRedo(PerspectiveMemento memento) {
        redoStack.add(memento);
    }

    /**
     * Ajoute un memento à la pile undo
     */
    public void pushUndo(PerspectiveMemento memento) {
        undoStack.add(memento);
    }

    /**
     * Vérifie s'il y a des actions à annuler
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Vérifie s'il y a des actions à refaire
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Vide les deux piles (reset)
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
