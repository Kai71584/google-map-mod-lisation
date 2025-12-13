package tests.unitaires;

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
        // Arrange
        perspective.setScale(2.0);
        Snapshot snapshot = perspective.createSnapshot();

        // Act
        caretaker.saveMemento(snapshot);
        
        // Assert
        assertEquals(1, caretaker.undoDepth(), "Undo stack should contain 1 snapshot");
    }

    @Test
    public void testSaveMentoMultipleSnapshots() {
        // Arrange
        for (int i = 1; i <= 3; i++) {
            perspective.setScale(1.0 + i);
            caretaker.saveMemento(perspective.createSnapshot());
        }

        // Act
        int undoDepth = caretaker.undoDepth();

        // Assert
        assertEquals(3, undoDepth, "Undo stack should contain 3 snapshots");
    }

    // ========== TESTS CAN UNDO ==========

    @Test
    public void testCanUndoReturnsFalseWhenEmpty() {
        // Arrange

        // Act
        boolean canUndo = caretaker.canUndo();

        // Assert
        assertFalse(canUndo, "canUndo should be false on empty stack");
    }

    @Test
    public void testCanUndoReturnsTrueAfterSave() {
        // Arrange
        perspective.setScale(1.5);
        Snapshot snapshot = perspective.createSnapshot();

        // Act
        caretaker.saveMemento(snapshot);
        boolean canUndo = caretaker.canUndo();

        // Assert
        assertTrue(canUndo, "canUndo should be true after saving");
    }

    // ========== TESTS POP UNDO ==========

    @Test
    public void testPopUndoReturnsSnapshot() {
        // Arrange
        perspective.setScale(2.0);
        Snapshot snapshot = perspective.createSnapshot();
        caretaker.saveMemento(snapshot);
        
        // Act
        Snapshot popped = caretaker.popUndo();

        // Assert
        assertNotNull(popped, "popUndo should return a snapshot");
    }

    @Test
    public void testPopUndoMovesSnapshotToRedoStack() {
        // Arrange
        perspective.setScale(2.0);
        Snapshot snapshot = perspective.createSnapshot();
        caretaker.saveMemento(snapshot);
        
        // Act
        caretaker.popUndo();
        int redoDepth = caretaker.redoDepth();
        int undoDepth = caretaker.undoDepth();

        // Assert
        assertEquals(1, redoDepth, "Redo stack should contain 1 snapshot");
        assertEquals(0, undoDepth, "Undo stack should be empty");
    }

    @Test
    public void testPopUndoReturnsNullWhenEmpty() {
        // Arrange

        // Act
        Snapshot result = caretaker.popUndo();

        // Assert
        assertNull(result, "popUndo should return null when empty");
    }

    // ========== TESTS CAN REDO ==========

    @Test
    public void testCanRedoReturnsFalseInitially() {
        // Arrange

        // Act
        boolean canRedo = caretaker.canRedo();

        // Assert
        assertFalse(canRedo, "canRedo should be false initially");
    }

    @Test
    public void testCanRedoReturnsTrueAfterUndo() {
        // Arrange
        perspective.setScale(2.0);
        Snapshot snapshot = perspective.createSnapshot();
        caretaker.saveMemento(snapshot);
        caretaker.popUndo();

        // Act
        boolean canRedo = caretaker.canRedo();
        
        // Assert
        assertTrue(canRedo, "canRedo should be true after popUndo");
    }

    // ========== TESTS POP REDO ==========

    @Test
    public void testPopRedoReturnsSnapshot() {
        // Arrange
        perspective.setScale(2.0);
        Snapshot snapshot = perspective.createSnapshot();
        caretaker.saveMemento(snapshot);
        caretaker.popUndo();

        // Act
        Snapshot redoSnapshot = caretaker.popRedo();

        // Assert
        assertNotNull(redoSnapshot, "popRedo should return a snapshot");
    }

    @Test
    public void testPopRedoMovesSnapshotBackToUndoStack() {
        // Arrange
        perspective.setScale(2.0);
        Snapshot snapshot = perspective.createSnapshot();
        caretaker.saveMemento(snapshot);
        caretaker.popUndo();

        // Act
        caretaker.popRedo();

        // Assert
        assertEquals(1, caretaker.undoDepth(), "Undo stack should contain 1 snapshot");
        assertEquals(0, caretaker.redoDepth(), "Redo stack should be empty");
    }

    @Test
    public void testPopRedoReturnsNullWhenEmpty() {
        // Arrange

        // Act
        Snapshot result = caretaker.popRedo();

        // Assert
        assertNull(result, "popRedo should return null when empty");
    }

    // ========== TESTS SAVE AFTER UNDO ==========

    @Test
    public void testSaveMementoAfterUndoClearsRedoStack() {
        // Arrange
        perspective.setScale(2.0);
        Snapshot snapshot1 = perspective.createSnapshot();
        caretaker.saveMemento(snapshot1);
        
        perspective.setScale(2.5);
        Snapshot snapshot2 = perspective.createSnapshot();
        caretaker.saveMemento(snapshot2);
        caretaker.popUndo();

        // Act
        perspective.setScale(3.0);
        Snapshot snapshot3 = perspective.createSnapshot();
        caretaker.saveMemento(snapshot3);
        
        // Assert
        assertEquals(0, caretaker.redoDepth(), "Redo stack should be cleared");
        assertEquals(2, caretaker.undoDepth(), "Undo stack should have 2 snapshots (1 old + 1 new)");
    }

    // ========== TESTS CLEAR ==========

    @Test
    public void testClearRemovesAllSnapshots() {
        // Arrange
        perspective.setScale(2.0);
        Snapshot snapshot1 = perspective.createSnapshot();
        caretaker.saveMemento(snapshot1);
        perspective.setScale(3.0);
        Snapshot snapshot2 = perspective.createSnapshot();
        caretaker.saveMemento(snapshot2);

        // Act
        caretaker.clear();
        
        // Assert
        assertEquals(0, caretaker.undoDepth(), "Undo stack should be empty");
        assertEquals(0, caretaker.redoDepth(), "Redo stack should be empty");
        assertFalse(caretaker.canUndo(), "canUndo should be false after clear");
        assertFalse(caretaker.canRedo(), "canRedo should be false after clear");
    }

    // ========== TESTS UNDO/REDO SEQUENCE ==========

    @Test
    public void testUndoRedoSequence() {
        // Arrange
        for (int i = 1; i <= 3; i++) {
            perspective.setScale(1.0 + i);
            caretaker.saveMemento(perspective.createSnapshot());
        }

        // Act
        caretaker.popUndo();
        caretaker.popUndo();
        caretaker.popRedo();

        // Assert
        assertEquals(2, caretaker.undoDepth(), "Should have 2 snapshots after redo");
        assertEquals(1, caretaker.redoDepth(), "Should have 1 snapshot in redo");
    }

    @Test
    public void testComplexUndoRedoWithNewSave() {
        // Arrange
        perspective.setScale(1.5);
        caretaker.saveMemento(perspective.createSnapshot());
        perspective.setScale(2.0);
        caretaker.saveMemento(perspective.createSnapshot());
        perspective.setScale(2.5);
        caretaker.saveMemento(perspective.createSnapshot());

        // Act
        caretaker.popUndo();
        caretaker.popUndo();
        perspective.setScale(3.0);
        caretaker.saveMemento(perspective.createSnapshot());

        // Assert
        assertEquals(2, caretaker.undoDepth(), "Undo should have 2 after new save");
        assertEquals(0, caretaker.redoDepth(), "Redo should be cleared");
    }

    @Test
    public void testDepthTracking() {
        // Arrange

        // Act
        int initialUndoDepth = caretaker.undoDepth();
        int initialRedoDepth = caretaker.redoDepth();
        perspective.setScale(1.0);
        caretaker.saveMemento(perspective.createSnapshot());
        int undoDepthAfterSave = caretaker.undoDepth();
        int redoDepthAfterSave = caretaker.redoDepth();

        // Assert
        assertEquals(0, initialUndoDepth, "Initial undo depth should be 0");
        assertEquals(0, initialRedoDepth, "Initial redo depth should be 0");
        assertEquals(1, undoDepthAfterSave, "Undo depth should be 1");
        assertEquals(0, redoDepthAfterSave, "Redo depth should be 0");
    }
}
