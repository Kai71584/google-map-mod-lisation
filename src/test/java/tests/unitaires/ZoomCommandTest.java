package tests;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import command.ZoomCommand;
import model.Perspective;

/**
 * Tests unitaires pour la classe ZoomCommand.
 * Vérifie l'exécution et l'annulation des commandes de zoom.
 */
public class ZoomCommandTest {

    private Perspective perspective;
    private ZoomCommand command;

    @BeforeEach
    public void setUp() {
        perspective = new Perspective("Test Zoom");
        perspective.setScale(1.0);
        perspective.setTranslation(new Point(0, 0));
    }

    // ========== TESTS EXECUTE ==========

    @Test
    public void testExecuteZoomIn() {
        double factor = 1.5;
        command = new ZoomCommand(perspective, factor);
        
        command.execute();
        
        assertEquals(1.5, perspective.getScale(), 0.001, "Scale should be 1.5 after zoom in");
    }

    @Test
    public void testExecuteZoomOut() {
        perspective.setScale(2.0);
        double factor = 0.5;
        command = new ZoomCommand(perspective, factor);
        
        command.execute();
        
        assertEquals(1.0, perspective.getScale(), 0.001, "Scale should be 1.0 (2.0 * 0.5)");
    }

    @Test
    public void testExecuteMultipleZooms() {
        ZoomCommand cmd1 = new ZoomCommand(perspective, 2.0);
        cmd1.execute();
        
        ZoomCommand cmd2 = new ZoomCommand(perspective, 1.5);
        cmd2.execute();
        
        assertEquals(3.0, perspective.getScale(), 0.001, "Scale should be 3.0 (1.0 * 2.0 * 1.5)");
    }

    @Test
    public void testExecuteDoesNotAffectTranslation() {
        perspective.setTranslation(new Point(100, 50));
        command = new ZoomCommand(perspective, 2.0);
        
        command.execute();
        
        Point translation = perspective.getTranslation();
        assertEquals(100, translation.x, "Translation X should not change");
        assertEquals(50, translation.y, "Translation Y should not change");
    }

    // ========== TESTS UNDO ==========

    @Test
    public void testUndoRestoresScale() {
        command = new ZoomCommand(perspective, 2.0);
        command.execute();
        
        assertEquals(2.0, perspective.getScale(), 0.001);
        
        command.undo();
        
        assertEquals(1.0, perspective.getScale(), 0.001, "Scale should be restored to 1.0");
    }

    @Test
    public void testUndoAfterZoomOut() {
        perspective.setScale(2.0);
        command = new ZoomCommand(perspective, 0.5);
        command.execute();
        
        assertEquals(1.0, perspective.getScale(), 0.001);
        
        command.undo();
        
        assertEquals(2.0, perspective.getScale(), 0.001, "Scale should be restored to 2.0");
    }

    @Test
    public void testUndoDoesNotAffectTranslation() {
        perspective.setTranslation(new Point(100, 50));
        command = new ZoomCommand(perspective, 2.0);
        command.execute();
        command.undo();
        
        Point translation = perspective.getTranslation();
        assertEquals(100, translation.x, "Translation X should not change");
        assertEquals(50, translation.y, "Translation Y should not change");
    }

    // ========== TESTS REDO (via re-execute) ==========

    @Test
    public void testRedoByReexecute() {
        command = new ZoomCommand(perspective, 2.0);
        command.execute();
        command.undo();
        
        assertEquals(1.0, perspective.getScale(), 0.001);
        
        command.execute();
        
        assertEquals(2.0, perspective.getScale(), 0.001, "Scale should be 2.0 after redo");
    }

    @Test
    public void testMultipleExecuteUndoSequence() {
        command = new ZoomCommand(perspective, 2.0);
        
        command.execute();
        assertEquals(2.0, perspective.getScale(), 0.001);
        
        command.undo();
        assertEquals(1.0, perspective.getScale(), 0.001);
        
        command.execute();
        assertEquals(2.0, perspective.getScale(), 0.001);
        
        command.undo();
        assertEquals(1.0, perspective.getScale(), 0.001);
    }

    // ========== TESTS TARGET ==========

    @Test
    public void testTargetReturnsCorrectPerspective() {
        command = new ZoomCommand(perspective, 2.0);
        
        assertEquals(perspective, command.target(), "target() should return the perspective");
    }

    // ========== TESTS WITH LARGE/SMALL FACTORS ==========

    @Test
    public void testZoomWithVeryLargeFactor() {
        command = new ZoomCommand(perspective, 10.0);
        command.execute();
        
        assertEquals(10.0, perspective.getScale(), 0.001);
    }

    @Test
    public void testZoomWithVerySmallFactor() {
        perspective.setScale(10.0);
        command = new ZoomCommand(perspective, 0.1);
        command.execute();
        
        assertEquals(1.0, perspective.getScale(), 0.001);
    }
}
