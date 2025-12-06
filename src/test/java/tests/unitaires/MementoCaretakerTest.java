package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.MementoCaretaker;
import model.Perspective;
import model.Perspective.Snapshot;

/**
 * Tests unitaires pour la classe MementoCaretaker.
 * Vérifie le fonctionnement de l'historique Undo/Redo avec snapshots.
 */
public class MementoCaretakerTest {

    private MementoCaretaker caretaker;
    private Perspective perspective;

    @BeforeEach
    public void setUp() {
        caretaker = new MementoCaretaker();
        perspective = new Perspective("Test");
    }

    // ========== TESTS SAVE MEMENTO ==========

    @Test
    public void testSaveMementoIncrementsUndoStack() {
        perspective.setScale(2.0);
        Snapshot snapshot = perspective.createSnapshot();
        caretaker.saveMemento(snapshot);
        
        assertEquals(1, caretaker.undoDepth(), "Undo stack should contain 1 snapshot");
    }

    @Test
    public void testSaveMentoMultipleSnapshots() {
        for (int i = 1; i <= 3; i++) {
            perspective.setScale(1.0 + i);
            caretaker.saveMemento(perspective.createSnapshot());
        }
        
        assertEquals(3, caretaker.undoDepth(), "Undo stack should contain 3 snapshots");
    }

    // ========== TESTS CAN UNDO ==========

    @Test
    public void testCanUndoReturnsFalseWhenEmpty() {
        assertFalse(caretaker.canUndo(), "canUndo should be false on empty stack");
    }

    @Test
    public void testCanUndoReturnsTrueAfterSave() {
        perspective.setScale(1.5);
        Snapshot snapshot = perspective.createSnapshot();
        caretaker.saveMemento(snapshot);
        
        assertTrue(caretaker.canUndo(), "canUndo should be true after saving");
    }

    // ========== TESTS POP UNDO ==========

    @Test
    public void testPopUndoReturnsSnapshot() {
        perspective.setScale(2.0);
        Snapshot snapshot = perspective.createSnapshot();
        caretaker.saveMemento(snapshot);
        
        Snapshot popped = caretaker.popUndo();
        assertNotNull(popped, "popUndo should return a snapshot");
    }

    @Test
    public void testPopUndoMovesSnapshotToRedoStack() {
        perspective.setScale(2.0);
        Snapshot snapshot = perspective.createSnapshot();
        caretaker.saveMemento(snapshot);
        
        caretaker.popUndo();
        
        assertEquals(1, caretaker.redoDepth(), "Redo stack should contain 1 snapshot");
        assertEquals(0, caretaker.undoDepth(), "Undo stack should be empty");
    }

    @Test
    public void testPopUndoReturnsNullWhenEmpty() {
        Snapshot result = caretaker.popUndo();
        assertNull(result, "popUndo should return null when empty");
    }

    // ========== TESTS CAN REDO ==========

    @Test
    public void testCanRedoReturnsFalseInitially() {
        assertFalse(caretaker.canRedo(), "canRedo should be false initially");
    }

    @Test
    public void testCanRedoReturnsTrueAfterUndo() {
        perspective.setScale(2.0);
        Snapshot snapshot = perspective.createSnapshot();
        caretaker.saveMemento(snapshot);
        caretaker.popUndo();
        
        assertTrue(caretaker.canRedo(), "canRedo should be true after popUndo");
    }

    // ========== TESTS POP REDO ==========

    @Test
    public void testPopRedoReturnsSnapshot() {
        perspective.setScale(2.0);
        Snapshot snapshot = perspective.createSnapshot();
        caretaker.saveMemento(snapshot);
        caretaker.popUndo();
        
        Snapshot redoSnapshot = caretaker.popRedo();
        assertNotNull(redoSnapshot, "popRedo should return a snapshot");
    }

    @Test
    public void testPopRedoMovesSnapshotBackToUndoStack() {
        perspective.setScale(2.0);
        Snapshot snapshot = perspective.createSnapshot();
        caretaker.saveMemento(snapshot);
        caretaker.popUndo();
        
        caretaker.popRedo();
        
        assertEquals(1, caretaker.undoDepth(), "Undo stack should contain 1 snapshot");
        assertEquals(0, caretaker.redoDepth(), "Redo stack should be empty");
    }

    @Test
    public void testPopRedoReturnsNullWhenEmpty() {
        Snapshot result = caretaker.popRedo();
        assertNull(result, "popRedo should return null when empty");
    }

    // ========== TESTS SAVE AFTER UNDO ==========

    @Test
    public void testSaveMementoAfterUndoClearsRedoStack() {
        perspective.setScale(2.0);
        Snapshot snapshot1 = perspective.createSnapshot();
        caretaker.saveMemento(snapshot1);
        
        perspective.setScale(2.5);
        Snapshot snapshot2 = perspective.createSnapshot();
        caretaker.saveMemento(snapshot2);
        
        // After 2 saves, we have 2 in undo
        assertEquals(2, caretaker.undoDepth(), "Should have 2 in undo before popUndo");
        
        caretaker.popUndo(); // Move snapshot2 to redo, now undo has 1
        
        assertEquals(1, caretaker.undoDepth(), "Should have 1 in undo after popUndo");
        assertEquals(1, caretaker.redoDepth(), "Should have 1 in redo after popUndo");
        
        // Save a new snapshot
        perspective.setScale(3.0);
        Snapshot snapshot3 = perspective.createSnapshot();
        caretaker.saveMemento(snapshot3);
        
        assertEquals(0, caretaker.redoDepth(), "Redo stack should be cleared");
        assertEquals(2, caretaker.undoDepth(), "Undo stack should have 2 snapshots (1 old + 1 new)");
    }

    // ========== TESTS CLEAR ==========

    @Test
    public void testClearRemovesAllSnapshots() {
        perspective.setScale(2.0);
        Snapshot snapshot1 = perspective.createSnapshot();
        caretaker.saveMemento(snapshot1);
        
        perspective.setScale(3.0);
        Snapshot snapshot2 = perspective.createSnapshot();
        caretaker.saveMemento(snapshot2);
        
        caretaker.clear();
        
        assertEquals(0, caretaker.undoDepth(), "Undo stack should be empty");
        assertEquals(0, caretaker.redoDepth(), "Redo stack should be empty");
        assertFalse(caretaker.canUndo(), "canUndo should be false after clear");
        assertFalse(caretaker.canRedo(), "canRedo should be false after clear");
    }

    // ========== TESTS UNDO/REDO SEQUENCE ==========

    @Test
    public void testUndoRedoSequence() {
        // Save 3 snapshots
        for (int i = 1; i <= 3; i++) {
            perspective.setScale(1.0 + i);
            caretaker.saveMemento(perspective.createSnapshot());
        }
        
        // Undo once
        caretaker.popUndo();
        assertEquals(2, caretaker.undoDepth(), "Should have 2 snapshots after first undo");
        assertEquals(1, caretaker.redoDepth(), "Should have 1 snapshot in redo");
        
        // Undo again
        caretaker.popUndo();
        assertEquals(1, caretaker.undoDepth(), "Should have 1 snapshot after second undo");
        assertEquals(2, caretaker.redoDepth(), "Should have 2 snapshots in redo");
        
        // Redo once
        caretaker.popRedo();
        assertEquals(2, caretaker.undoDepth(), "Should have 2 snapshots after redo");
        assertEquals(1, caretaker.redoDepth(), "Should have 1 snapshot in redo");
    }

    @Test
    public void testComplexUndoRedoWithNewSave() {
        // Save snapshot 1
        perspective.setScale(1.5);
        caretaker.saveMemento(perspective.createSnapshot());
        
        // Save snapshot 2
        perspective.setScale(2.0);
        caretaker.saveMemento(perspective.createSnapshot());
        
        // Save snapshot 3
        perspective.setScale(2.5);
        caretaker.saveMemento(perspective.createSnapshot());
        
        // Undo twice
        caretaker.popUndo();
        caretaker.popUndo();
        assertEquals(1, caretaker.undoDepth());
        assertEquals(2, caretaker.redoDepth());
        
        // Save a new snapshot (should clear redo)
        perspective.setScale(3.0);
        caretaker.saveMemento(perspective.createSnapshot());
        
        assertEquals(2, caretaker.undoDepth(), "Undo should have 2 after new save");
        assertEquals(0, caretaker.redoDepth(), "Redo should be cleared");
    }

    @Test
    public void testDepthTracking() {
        assertEquals(0, caretaker.undoDepth(), "Initial undo depth should be 0");
        assertEquals(0, caretaker.redoDepth(), "Initial redo depth should be 0");
        
        perspective.setScale(1.0);
        caretaker.saveMemento(perspective.createSnapshot());
        
        assertEquals(1, caretaker.undoDepth(), "Undo depth should be 1");
        assertEquals(0, caretaker.redoDepth(), "Redo depth should be 0");
    }
}
