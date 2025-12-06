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
        command = new TranslateCommand(perspective, 100, 50);
        command.execute();
        
        Point translation = perspective.getTranslation();
        assertEquals(100, translation.x, "Translation X should be 100");
        assertEquals(50, translation.y, "Translation Y should be 50");
    }

    @Test
    public void testExecuteTranslateNegative() {
        perspective.setTranslation(new Point(100, 100));
        command = new TranslateCommand(perspective, -50, -30);
        command.execute();
        
        Point translation = perspective.getTranslation();
        assertEquals(50, translation.x, "Translation X should be 50 (100 - 50)");
        assertEquals(70, translation.y, "Translation Y should be 70 (100 - 30)");
    }

    @Test
    public void testExecuteTranslateFromNonOrigin() {
        perspective.setTranslation(new Point(50, 75));
        command = new TranslateCommand(perspective, 25, 10);
        command.execute();
        
        Point translation = perspective.getTranslation();
        assertEquals(75, translation.x, "Translation X should be 75 (50 + 25)");
        assertEquals(85, translation.y, "Translation Y should be 85 (75 + 10)");
    }

    @Test
    public void testExecuteTranslateZeroDeltas() {
        perspective.setTranslation(new Point(100, 50));
        command = new TranslateCommand(perspective, 0, 0);
        command.execute();
        
        Point translation = perspective.getTranslation();
        assertEquals(100, translation.x, "Translation X should remain 100");
        assertEquals(50, translation.y, "Translation Y should remain 50");
    }

    @Test
    public void testExecuteMultipleTranslates() {
        TranslateCommand cmd1 = new TranslateCommand(perspective, 50, 25);
        cmd1.execute();
        
        TranslateCommand cmd2 = new TranslateCommand(perspective, 30, 20);
        cmd2.execute();
        
        Point translation = perspective.getTranslation();
        assertEquals(80, translation.x, "Translation X should be 80 (50 + 30)");
        assertEquals(45, translation.y, "Translation Y should be 45 (25 + 20)");
    }

    @Test
    public void testExecuteDoesNotAffectScale() {
        perspective.setScale(2.0);
        command = new TranslateCommand(perspective, 100, 50);
        command.execute();
        
        assertEquals(2.0, perspective.getScale(), 0.001, "Scale should not change");
    }

    // ========== TESTS UNDO ==========

    @Test
    public void testUndoRestoresTranslation() {
        command = new TranslateCommand(perspective, 100, 50);
        command.execute();
        
        Point translation = perspective.getTranslation();
        assertEquals(100, translation.x);
        assertEquals(50, translation.y);
        
        command.undo();
        
        translation = perspective.getTranslation();
        assertEquals(0, translation.x, "Translation X should be restored to 0");
        assertEquals(0, translation.y, "Translation Y should be restored to 0");
    }

    @Test
    public void testUndoFromNonOrigin() {
        perspective.setTranslation(new Point(200, 150));
        command = new TranslateCommand(perspective, 50, 30);
        command.execute();
        
        command.undo();
        
        Point translation = perspective.getTranslation();
        assertEquals(200, translation.x, "Translation X should be restored to 200");
        assertEquals(150, translation.y, "Translation Y should be restored to 150");
    }

    @Test
    public void testUndoDoesNotAffectScale() {
        perspective.setScale(2.5);
        command = new TranslateCommand(perspective, 100, 50);
        command.execute();
        command.undo();
        
        assertEquals(2.5, perspective.getScale(), 0.001, "Scale should not change");
    }

    // ========== TESTS REDO (via re-execute) ==========

    @Test
    public void testRedoByReexecute() {
        command = new TranslateCommand(perspective, 100, 50);
        command.execute();
        command.undo();
        
        Point translation = perspective.getTranslation();
        assertEquals(0, translation.x);
        assertEquals(0, translation.y);
        
        command.execute();
        
        translation = perspective.getTranslation();
        assertEquals(100, translation.x, "Translation X should be 100 after redo");
        assertEquals(50, translation.y, "Translation Y should be 50 after redo");
    }

    @Test
    public void testMultipleExecuteUndoSequence() {
        command = new TranslateCommand(perspective, 100, 50);
        
        command.execute();
        Point t1 = perspective.getTranslation();
        assertEquals(100, t1.x);
        
        command.undo();
        Point t2 = perspective.getTranslation();
        assertEquals(0, t2.x);
        
        command.execute();
        Point t3 = perspective.getTranslation();
        assertEquals(100, t3.x);
    }

    // ========== TESTS TARGET ==========

    @Test
    public void testTargetReturnsCorrectPerspective() {
        command = new TranslateCommand(perspective, 50, 25);
        
        assertEquals(perspective, command.target(), "target() should return the perspective");
    }

    // ========== TESTS WITH LARGE DELTAS ==========

    @Test
    public void testTranslateWithLargeDeltas() {
        command = new TranslateCommand(perspective, 10000, 5000);
        command.execute();
        
        Point translation = perspective.getTranslation();
        assertEquals(10000, translation.x);
        assertEquals(5000, translation.y);
    }

    @Test
    public void testTranslateWithNegativeAndPositiveMixed() {
        command = new TranslateCommand(perspective, 100, -50);
        command.execute();
        
        Point translation = perspective.getTranslation();
        assertEquals(100, translation.x, "Translation X should be 100");
        assertEquals(-50, translation.y, "Translation Y should be -50");
    }
}
