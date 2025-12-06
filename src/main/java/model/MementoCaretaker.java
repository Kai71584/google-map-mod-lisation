package model;

import java.util.Stack;

/**
 * Caretaker pour le pattern Memento.
 * Gère l'historique des snapshots pour l'undo/redo.
 * 
 * Pattern : Memento
 * - Originator : Perspective
 * - Memento : Perspective.Snapshot
 * - Caretaker : MementoCaretaker (cette classe)
 */
public class MementoCaretaker {
    private Stack<Perspective.Snapshot> undoStack = new Stack<>();
    private Stack<Perspective.Snapshot> redoStack = new Stack<>();

    /**
     * Sauvegarde un snapshot en mémoire (undo stack).
     * Réinitialise la pile redo.
     */
    public void saveMemento(Perspective.Snapshot snapshot) {
        undoStack.push(snapshot);
        redoStack.clear(); // Nouvelle action annule redo
    }

    /**
     * Récupère le dernier snapshot pour l'undo.
     * Déplace le snapshot vers la pile redo.
     */
    public Perspective.Snapshot popUndo() {
        if (!canUndo()) {
            return null;
        }
        Perspective.Snapshot memento = undoStack.pop();
        redoStack.push(memento);
        return memento;
    }

    /**
     * Récupère le dernier snapshot pour le redo.
     * Déplace le snapshot vers la pile undo.
     */
    public Perspective.Snapshot popRedo() {
        if (!canRedo()) {
            return null;
        }
        Perspective.Snapshot memento = redoStack.pop();
        undoStack.push(memento);
        return memento;
    }

    /**
     * Vérifie s'il y a une action à annuler.
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Vérifie s'il y a une action à refaire.
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Vide l'historique.
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    /**
     * Retourne la profondeur de la pile undo (pour logs/debug).
     */
    public int undoDepth() {
        return undoStack.size();
    }

    /**
     * Retourne la profondeur de la pile redo (pour logs/debug).
     */
    public int redoDepth() {
        return redoStack.size();
    }
}
