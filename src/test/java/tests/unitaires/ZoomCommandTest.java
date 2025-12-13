package tests.unitaires;

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
        // Arrange
        double factor = 1.5;

        // Act
        command = new ZoomCommand(perspective, factor);
        command.execute();
        double resultingScale = perspective.getScale();

        // Assert
        assertEquals(1.5, resultingScale, 0.001, "Scale should be 1.5 after zoom in");
    }

    @Test
    public void testExecuteZoomOut() {
        // Arrange
        perspective.setScale(2.0);
        double factor = 0.5;

        // Act
        command = new ZoomCommand(perspective, factor);
        command.execute();
        double resultingScale = perspective.getScale();

        // Assert
        assertEquals(1.0, resultingScale, 0.001, "Scale should be 1.0 (2.0 * 0.5)");
    }

    @Test
    public void testExecuteMultipleZooms() {
        // Arrange
        ZoomCommand cmd1 = new ZoomCommand(perspective, 2.0);
        ZoomCommand cmd2 = new ZoomCommand(perspective, 1.5);

        // Act
        cmd1.execute();
        cmd2.execute();
        double resultingScale = perspective.getScale();

        // Assert
        assertEquals(3.0, resultingScale, 0.001, "Scale should be 3.0 (1.0 * 2.0 * 1.5)");
    }

    @Test
    public void testExecuteDoesNotAffectTranslation() {
        // Arrange
        perspective.setTranslation(new Point(100, 50));

        // Act
        command = new ZoomCommand(perspective, 2.0);
        command.execute();
        Point translation = perspective.getTranslation();

        // Assert
        assertEquals(100, translation.x, "Translation X should not change");
        assertEquals(50, translation.y, "Translation Y should not change");
    }

    // ========== TESTS UNDO ==========

    @Test
    public void testUndoRestoresScale() {
        // Arrange
        command = new ZoomCommand(perspective, 2.0);
        command.execute();

        // Act
        command.undo();
        double resultingScale = perspective.getScale();

        // Assert
        assertEquals(1.0, resultingScale, 0.001, "Scale should be restored to 1.0");
    }

    @Test
    public void testUndoAfterZoomOut() {
        // Arrange
        perspective.setScale(2.0);
        command = new ZoomCommand(perspective, 0.5);
        command.execute();

        // Act
        command.undo();
        double resultingScale = perspective.getScale();

        // Assert
        assertEquals(2.0, resultingScale, 0.001, "Scale should be restored to 2.0");
    }

    @Test
    public void testUndoDoesNotAffectTranslation() {
        // Arrange
        perspective.setTranslation(new Point(100, 50));
        command = new ZoomCommand(perspective, 2.0);
        command.execute();

        // Act
        command.undo();
        Point translation = perspective.getTranslation();

        // Assert
        assertEquals(100, translation.x, "Translation X should not change");
        assertEquals(50, translation.y, "Translation Y should not change");
    }

    // ========== TESTS REDO (via re-execute) ==========

    @Test
    public void testRedoByReexecute() {
        // Arrange
        command = new ZoomCommand(perspective, 2.0);
        command.execute();
        command.undo();

        // Act
        command.execute();
        double resultingScale = perspective.getScale();

        // Assert
        assertEquals(2.0, resultingScale, 0.001, "Scale should be 2.0 after redo");
    }

    @Test
    public void testMultipleExecuteUndoSequence() {
        // Arrange
        command = new ZoomCommand(perspective, 2.0);

        // Act
        command.execute();
        double afterFirstExecute = perspective.getScale();
        command.undo();
        double afterFirstUndo = perspective.getScale();
        command.execute();
        double afterSecondExecute = perspective.getScale();
        command.undo();
        double afterSecondUndo = perspective.getScale();

        // Assert
        assertEquals(2.0, afterFirstExecute, 0.001);
        assertEquals(1.0, afterFirstUndo, 0.001);
        assertEquals(2.0, afterSecondExecute, 0.001);
        assertEquals(1.0, afterSecondUndo, 0.001);
    }

    // ========== TESTS TARGET ==========

    @Test
    public void testTargetReturnsCorrectPerspective() {
        // Arrange
        command = new ZoomCommand(perspective, 2.0);

        // Act
        Perspective target = command.target();

        // Assert
        assertEquals(perspective, target, "target() should return the perspective");
    }

    // ========== TESTS WITH LARGE/SMALL FACTORS ==========

    @Test
    public void testZoomWithVeryLargeFactor() {
        // Arrange
        double factor = 10.0;

        // Act
        command = new ZoomCommand(perspective, factor);
        command.execute();
        double resultingScale = perspective.getScale();

        // Assert
        assertEquals(10.0, resultingScale, 0.001);
    }

    @Test
    public void testZoomWithVerySmallFactor() {
        // Arrange
        perspective.setScale(10.0);
        double factor = 0.1;

        // Act
        command = new ZoomCommand(perspective, factor);
        command.execute();
        double resultingScale = perspective.getScale();

        // Assert
        assertEquals(1.0, resultingScale, 0.001);
    }
}
