package tests.unitaires;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import command.TranslateCommand;
import model.Perspective;

/**
 * Tests unitaires pour la classe TranslateCommand.
 * Vérifie l'exécution et l'annulation des commandes de translation (pan).
 */
public class TranslateCommandTest {

    private Perspective perspective;
    private TranslateCommand command;

    @BeforeEach
    public void setUp() {
        perspective = new Perspective("Test Translate");
        perspective.setScale(1.0);
        perspective.setTranslation(new Point(0, 0));
    }

    // ========== TESTS EXECUTE ==========

    @Test
    public void testExecuteTranslatePositive() {
        // Arrange
        command = new TranslateCommand(perspective, 100, 50);

        // Act
        command.execute();
        Point translation = perspective.getTranslation();

        // Assert
        assertEquals(100, translation.x, "Translation X should be 100");
        assertEquals(50, translation.y, "Translation Y should be 50");
    }

    @Test
    public void testExecuteTranslateNegative() {
        // Arrange
        perspective.setTranslation(new Point(100, 100));
        command = new TranslateCommand(perspective, -50, -30);

        // Act
        command.execute();
        Point translation = perspective.getTranslation();

        // Assert
        assertEquals(50, translation.x, "Translation X should be 50 (100 - 50)");
        assertEquals(70, translation.y, "Translation Y should be 70 (100 - 30)");
    }

    @Test
    public void testExecuteTranslateFromNonOrigin() {
        // Arrange
        perspective.setTranslation(new Point(50, 75));
        command = new TranslateCommand(perspective, 25, 10);

        // Act
        command.execute();
        Point translation = perspective.getTranslation();

        // Assert
        assertEquals(75, translation.x, "Translation X should be 75 (50 + 25)");
        assertEquals(85, translation.y, "Translation Y should be 85 (75 + 10)");
    }

    @Test
    public void testExecuteTranslateZeroDeltas() {
        // Arrange
        perspective.setTranslation(new Point(100, 50));
        command = new TranslateCommand(perspective, 0, 0);

        // Act
        command.execute();
        Point translation = perspective.getTranslation();

        // Assert
        assertEquals(100, translation.x, "Translation X should remain 100");
        assertEquals(50, translation.y, "Translation Y should remain 50");
    }

    @Test
    public void testExecuteMultipleTranslates() {
        // Arrange
        TranslateCommand cmd1 = new TranslateCommand(perspective, 50, 25);
        TranslateCommand cmd2 = new TranslateCommand(perspective, 30, 20);

        // Act
        cmd1.execute();
        cmd2.execute();
        Point translation = perspective.getTranslation();

        // Assert
        assertEquals(80, translation.x, "Translation X should be 80 (50 + 30)");
        assertEquals(45, translation.y, "Translation Y should be 45 (25 + 20)");
    }

    @Test
    public void testExecuteDoesNotAffectScale() {
        // Arrange
        perspective.setScale(2.0);
        command = new TranslateCommand(perspective, 100, 50);

        // Act
        command.execute();
        double resultingScale = perspective.getScale();

        // Assert
        assertEquals(2.0, resultingScale, 0.001, "Scale should not change");
    }

    // ========== TESTS UNDO ==========

    @Test
    public void testUndoRestoresTranslation() {
        // Arrange
        command = new TranslateCommand(perspective, 100, 50);
        command.execute();

        // Act
        command.undo();
        Point translation = perspective.getTranslation();

        // Assert
        assertEquals(0, translation.x, "Translation X should be restored to 0");
        assertEquals(0, translation.y, "Translation Y should be restored to 0");
    }

    @Test
    public void testUndoFromNonOrigin() {
        // Arrange
        perspective.setTranslation(new Point(200, 150));
        command = new TranslateCommand(perspective, 50, 30);
        command.execute();

        // Act
        command.undo();
        Point translation = perspective.getTranslation();

        // Assert
        assertEquals(200, translation.x, "Translation X should be restored to 200");
        assertEquals(150, translation.y, "Translation Y should be restored to 150");
    }

    @Test
    public void testUndoDoesNotAffectScale() {
        // Arrange
        perspective.setScale(2.5);
        command = new TranslateCommand(perspective, 100, 50);
        command.execute();

        // Act
        command.undo();
        double resultingScale = perspective.getScale();

        // Assert
        assertEquals(2.5, resultingScale, 0.001, "Scale should not change");
    }

    // ========== TESTS REDO (via re-execute) ==========

    @Test
    public void testRedoByReexecute() {
        // Arrange
        command = new TranslateCommand(perspective, 100, 50);
        command.execute();
        command.undo();

        // Act
        command.execute();
        Point translation = perspective.getTranslation();

        // Assert
        assertEquals(100, translation.x, "Translation X should be 100 after redo");
        assertEquals(50, translation.y, "Translation Y should be 50 after redo");
    }

    @Test
    public void testMultipleExecuteUndoSequence() {
        // Arrange
        command = new TranslateCommand(perspective, 100, 50);

        // Act
        command.execute();
        Point t1 = perspective.getTranslation();
        command.undo();
        Point t2 = perspective.getTranslation();
        command.execute();
        Point t3 = perspective.getTranslation();

        // Assert
        assertEquals(100, t1.x);
        assertEquals(0, t2.x);
        assertEquals(100, t3.x);
    }

    // ========== TESTS TARGET ==========

    @Test
    public void testTargetReturnsCorrectPerspective() {
        // Arrange
        command = new TranslateCommand(perspective, 50, 25);

        // Act
        Perspective target = command.target();

        // Assert
        assertEquals(perspective, target, "target() should return the perspective");
    }

    // ========== TESTS WITH LARGE DELTAS ==========

    @Test
    public void testTranslateWithLargeDeltas() {
        // Arrange
        command = new TranslateCommand(perspective, 10000, 5000);

        // Act
        command.execute();
        Point translation = perspective.getTranslation();

        // Assert
        assertEquals(10000, translation.x);
        assertEquals(5000, translation.y);
    }

    @Test
    public void testTranslateWithNegativeAndPositiveMixed() {
        // Arrange
        command = new TranslateCommand(perspective, 100, -50);

        // Act
        command.execute();
        Point translation = perspective.getTranslation();

        // Assert
        assertEquals(100, translation.x, "Translation X should be 100");
        assertEquals(-50, translation.y, "Translation Y should be -50");
    }
}
