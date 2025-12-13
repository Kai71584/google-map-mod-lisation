package tests.unitaires;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clipboard.ClipboardMediator;
import clipboard.Colleague;
import clipboard.CopyBoth;
import clipboard.CopyNone;
import clipboard.CopyScaleOnly;
import clipboard.CopyTranslationOnly;
import clipboard.CopyTranslationXOnly;
import command.PasteCommand;
import model.Perspective;

/**
 * Tests unitaires pour la classe PasteCommand.
 * Vérifie l'exécution et l'annulation des commandes de collage.
 * Teste aussi les différentes stratégies de copie.
 */
public class PasteCommandTest {

    private Perspective source;
    private Perspective target;
    private ClipboardMediator clipboard;
    private PasteCommand command;
    private MockColleague sourceColleague;
    private MockColleague targetColleague;

    @BeforeEach
    public void setUp() {
        source = new Perspective("Source");
        target = new Perspective("Target");
        clipboard = new ClipboardMediator();
        sourceColleague = new MockColleague(source);
        targetColleague = new MockColleague(target);
        
        // Initialize source with data to copy
        source.setScale(2.0);
        source.setTranslation(new Point(100, 50));
        
        // Initialize target with different values
        target.setScale(1.0);
        target.setTranslation(new Point(0, 0));
    }

    // ========== TESTS COPY BOTH (SCALE + TRANSLATION) ==========

    @Test
    public void testPasteBothScaleAndTranslation() {
        // Arrange
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyBoth(), targetColleague);

        // Act
        command.execute();
        double resultingScale = target.getScale();
        Point translation = target.getTranslation();

        // Assert
        assertEquals(2.0, resultingScale, 0.001, "Scale should be copied to 2.0");
        assertEquals(100, translation.x, "Translation X should be copied to 100");
        assertEquals(50, translation.y, "Translation Y should be copied to 50");
    }

    @Test
    public void testPasteBothAndUndo() {
        // Arrange
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyBoth(), targetColleague);
        command.execute();

        // Act
        command.undo();
        double resultingScale = target.getScale();
        Point translation = target.getTranslation();

        // Assert
        assertEquals(1.0, resultingScale, 0.001, "Scale should be restored to 1.0");
        assertEquals(0, translation.x, "Translation X should be restored to 0");
        assertEquals(0, translation.y, "Translation Y should be restored to 0");
    }

    // ========== TESTS COPY SCALE ONLY ==========

    @Test
    public void testPasteScaleOnly() {
        // Arrange
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyScaleOnly(), targetColleague);

        // Act
        command.execute();
        double resultingScale = target.getScale();
        Point translation = target.getTranslation();

        // Assert
        assertEquals(2.0, resultingScale, 0.001, "Scale should be copied to 2.0");
        assertEquals(0, translation.x, "Translation X should remain 0");
        assertEquals(0, translation.y, "Translation Y should remain 0");
    }

    @Test
    public void testPasteScaleOnlyAndUndo() {
        // Arrange
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyScaleOnly(), targetColleague);
        command.execute();

        // Act
        command.undo();
        double resultingScale = target.getScale();

        // Assert
        assertEquals(1.0, resultingScale, 0.001, "Scale should be restored to 1.0");
    }

    @Test
    public void testPasteScaleOnlyPreservesTranslation() {
        // Arrange
        target.setTranslation(new Point(50, 75));
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyScaleOnly(), targetColleague);

        // Act
        command.execute();
        Point translation = target.getTranslation();

        // Assert
        assertEquals(50, translation.x, "Translation X should remain 50");
        assertEquals(75, translation.y, "Translation Y should remain 75");
    }

    // ========== TESTS COPY TRANSLATION ONLY ==========

    @Test
    public void testPasteTranslationOnly() {
        // Arrange
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyTranslationOnly(), targetColleague);

        // Act
        command.execute();
        double resultingScale = target.getScale();
        Point translation = target.getTranslation();

        // Assert
        assertEquals(1.0, resultingScale, 0.001, "Scale should remain 1.0");
        assertEquals(100, translation.x, "Translation X should be copied to 100");
        assertEquals(50, translation.y, "Translation Y should be copied to 50");
    }

    @Test
    public void testPasteTranslationOnlyAndUndo() {
        // Arrange
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyTranslationOnly(), targetColleague);
        command.execute();

        // Act
        command.undo();
        Point translation = target.getTranslation();

        // Assert
        assertEquals(0, translation.x, "Translation X should be restored to 0");
        assertEquals(0, translation.y, "Translation Y should be restored to 0");
    }

    @Test
    public void testPasteTranslationOnlyPreservesScale() {
        // Arrange
        target.setScale(2.5);
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyTranslationOnly(), targetColleague);

        // Act
        command.execute();
        double resultingScale = target.getScale();

        // Assert
        assertEquals(2.5, resultingScale, 0.001, "Scale should remain 2.5");
    }

    // ========== TESTS COPY TRANSLATION X ONLY ==========

    @Test
    public void testPasteTranslationXOnly() {
        // Arrange
        target.setTranslation(new Point(0, 75));
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyTranslationXOnly(), targetColleague);

        // Act
        command.execute();
        Point translation = target.getTranslation();

        // Assert
        assertEquals(100, translation.x, "Translation X should be copied to 100");
        assertEquals(75, translation.y, "Translation Y should remain 75");
    }

    @Test
    public void testPasteTranslationXOnlyAndUndo() {
        // Arrange
        target.setTranslation(new Point(0, 75));
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyTranslationXOnly(), targetColleague);
        command.execute();

        // Act
        command.undo();
        Point translation = target.getTranslation();

        // Assert
        assertEquals(0, translation.x, "Translation X should be restored to 0");
        assertEquals(75, translation.y, "Translation Y should remain 75");
    }

    @Test
    public void testPasteTranslationXOnlyPreservesScaleAndY() {
        // Arrange
        target.setScale(2.5);
        target.setTranslation(new Point(50, 100));
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyTranslationXOnly(), targetColleague);

        // Act
        command.execute();
        double resultingScale = target.getScale();
        Point translation = target.getTranslation();

        // Assert
        assertEquals(2.5, resultingScale, 0.001, "Scale should remain 2.5");
        assertEquals(100, translation.x, "Translation X should be 100");
        assertEquals(100, translation.y, "Translation Y should remain 100");
    }

    // ========== TESTS COPY NONE ==========

    @Test
    public void testPasteNone() {
        // Arrange
        target.setScale(2.5);
        target.setTranslation(new Point(75, 100));
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyNone(), targetColleague);

        // Act
        command.execute();
        double resultingScale = target.getScale();
        Point translation = target.getTranslation();

        // Assert
        assertEquals(2.5, resultingScale, 0.001, "Scale should remain unchanged");
        assertEquals(75, translation.x, "Translation X should remain unchanged");
        assertEquals(100, translation.y, "Translation Y should remain unchanged");
    }

    @Test
    public void testPasteNoneAndUndo() {
        // Arrange
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyNone(), targetColleague);
        command.execute();

        // Act
        command.undo();
        double resultingScale = target.getScale();

        // Assert
        assertEquals(1.0, resultingScale, 0.001, "Scale should be 1.0");
    }

    // ========== TESTS TARGET ==========

    @Test
    public void testTargetReturnsCorrectPerspective() {
        // Arrange
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyBoth(), targetColleague);

        // Act
        Perspective result = command.target();

        // Assert
        assertEquals(target, result, "target() should return the target perspective");
    }

    // ========== TESTS MULTIPLE PASTES ==========

    @Test
    public void testMultiplePastesWithDifferentStrategies() {
        // Arrange
        source.setScale(3.0);
        source.setTranslation(new Point(200, 100));
        clipboard.mediateCopy(sourceColleague);

        // Act
        command = new PasteCommand(target, clipboard, new CopyScaleOnly(), targetColleague);
        command.execute();
        clipboard.mediateCopy(sourceColleague);
        PasteCommand command2 = new PasteCommand(target, clipboard, new CopyTranslationOnly(), targetColleague);
        command2.execute();
        double resultingScale = target.getScale();
        Point translation = target.getTranslation();

        // Assert
        assertEquals(3.0, resultingScale, 0.001, "Scale should remain 3.0");
        assertEquals(200, translation.x, "Translation X should be 200");
        assertEquals(100, translation.y, "Translation Y should be 100");
    }

    @Test
    public void testPasteAndUndoSequence() {
        // Arrange
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyBoth(), targetColleague);

        // Act
        command.execute();
        double afterFirstExecute = target.getScale();
        command.undo();
        double afterUndo = target.getScale();
        command.execute();
        double afterSecondExecute = target.getScale();

        // Assert
        assertEquals(2.0, afterFirstExecute, 0.001);
        assertEquals(1.0, afterUndo, 0.001);
        assertEquals(2.0, afterSecondExecute, 0.001);
    }

    // ========== MOCK COLLEAGUE HELPER ==========

    /**
     * Mock implementation of Colleague for testing purposes.
     */
    private static class MockColleague implements Colleague {
        private final Perspective perspective;

        public MockColleague(Perspective perspective) {
            this.perspective = perspective;
        }

        @Override
        public Perspective getPerspective() {
            return perspective;
        }

        @Override
        public void receiveCopyData(Double scale, Point translation) {
            // Mock implementation: do nothing for testing
        }

        @Override
        public void requestCopy() {
            // Mock implementation: do nothing for testing
        }

        @Override
        public void requestPaste() {
            // Mock implementation: do nothing for testing
        }
    }
}
