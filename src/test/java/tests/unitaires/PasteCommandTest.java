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
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyBoth(), targetColleague);
        command.execute();
        
        assertEquals(2.0, target.getScale(), 0.001, "Scale should be copied to 2.0");
        Point translation = target.getTranslation();
        assertEquals(100, translation.x, "Translation X should be copied to 100");
        assertEquals(50, translation.y, "Translation Y should be copied to 50");
    }

    @Test
    public void testPasteBothAndUndo() {
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyBoth(), targetColleague);
        command.execute();
        command.undo();
        
        assertEquals(1.0, target.getScale(), 0.001, "Scale should be restored to 1.0");
        Point translation = target.getTranslation();
        assertEquals(0, translation.x, "Translation X should be restored to 0");
        assertEquals(0, translation.y, "Translation Y should be restored to 0");
    }

    // ========== TESTS COPY SCALE ONLY ==========

    @Test
    public void testPasteScaleOnly() {
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyScaleOnly(), targetColleague);
        command.execute();
        
        assertEquals(2.0, target.getScale(), 0.001, "Scale should be copied to 2.0");
        Point translation = target.getTranslation();
        assertEquals(0, translation.x, "Translation X should remain 0");
        assertEquals(0, translation.y, "Translation Y should remain 0");
    }

    @Test
    public void testPasteScaleOnlyAndUndo() {
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyScaleOnly(), targetColleague);
        command.execute();
        command.undo();
        
        assertEquals(1.0, target.getScale(), 0.001, "Scale should be restored to 1.0");
    }

    @Test
    public void testPasteScaleOnlyPreservesTranslation() {
        target.setTranslation(new Point(50, 75));
        
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyScaleOnly(), targetColleague);
        command.execute();
        
        Point translation = target.getTranslation();
        assertEquals(50, translation.x, "Translation X should remain 50");
        assertEquals(75, translation.y, "Translation Y should remain 75");
    }

    // ========== TESTS COPY TRANSLATION ONLY ==========

    @Test
    public void testPasteTranslationOnly() {
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyTranslationOnly(), targetColleague);
        command.execute();
        
        assertEquals(1.0, target.getScale(), 0.001, "Scale should remain 1.0");
        Point translation = target.getTranslation();
        assertEquals(100, translation.x, "Translation X should be copied to 100");
        assertEquals(50, translation.y, "Translation Y should be copied to 50");
    }

    @Test
    public void testPasteTranslationOnlyAndUndo() {
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyTranslationOnly(), targetColleague);
        command.execute();
        command.undo();
        
        Point translation = target.getTranslation();
        assertEquals(0, translation.x, "Translation X should be restored to 0");
        assertEquals(0, translation.y, "Translation Y should be restored to 0");
    }

    @Test
    public void testPasteTranslationOnlyPreservesScale() {
        target.setScale(2.5);
        
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyTranslationOnly(), targetColleague);
        command.execute();
        
        assertEquals(2.5, target.getScale(), 0.001, "Scale should remain 2.5");
    }

    // ========== TESTS COPY TRANSLATION X ONLY ==========

    @Test
    public void testPasteTranslationXOnly() {
        target.setTranslation(new Point(0, 75));
        
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyTranslationXOnly(), targetColleague);
        command.execute();
        
        Point translation = target.getTranslation();
        assertEquals(100, translation.x, "Translation X should be copied to 100");
        assertEquals(75, translation.y, "Translation Y should remain 75");
    }

    @Test
    public void testPasteTranslationXOnlyAndUndo() {
        target.setTranslation(new Point(0, 75));
        
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyTranslationXOnly(), targetColleague);
        command.execute();
        command.undo();
        
        Point translation = target.getTranslation();
        assertEquals(0, translation.x, "Translation X should be restored to 0");
        assertEquals(75, translation.y, "Translation Y should remain 75");
    }

    @Test
    public void testPasteTranslationXOnlyPreservesScaleAndY() {
        target.setScale(2.5);
        target.setTranslation(new Point(50, 100));
        
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyTranslationXOnly(), targetColleague);
        command.execute();
        
        assertEquals(2.5, target.getScale(), 0.001, "Scale should remain 2.5");
        Point translation = target.getTranslation();
        assertEquals(100, translation.x, "Translation X should be 100");
        assertEquals(100, translation.y, "Translation Y should remain 100");
    }

    // ========== TESTS COPY NONE ==========

    @Test
    public void testPasteNone() {
        target.setScale(2.5);
        target.setTranslation(new Point(75, 100));
        
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyNone(), targetColleague);
        command.execute();
        
        assertEquals(2.5, target.getScale(), 0.001, "Scale should remain unchanged");
        Point translation = target.getTranslation();
        assertEquals(75, translation.x, "Translation X should remain unchanged");
        assertEquals(100, translation.y, "Translation Y should remain unchanged");
    }

    @Test
    public void testPasteNoneAndUndo() {
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyNone(), targetColleague);
        command.execute();
        command.undo();
        
        // Should be same as before since nothing was changed
        assertEquals(1.0, target.getScale(), 0.001, "Scale should be 1.0");
    }

    // ========== TESTS TARGET ==========

    @Test
    public void testTargetReturnsCorrectPerspective() {
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyBoth(), targetColleague);
        
        assertEquals(target, command.target(), "target() should return the target perspective");
    }

    // ========== TESTS MULTIPLE PASTES ==========

    @Test
    public void testMultiplePastesWithDifferentStrategies() {
        source.setScale(3.0);
        source.setTranslation(new Point(200, 100));
        
        clipboard.mediateCopy(sourceColleague);
        
        // First paste: copy scale only
        command = new PasteCommand(target, clipboard, new CopyScaleOnly(), targetColleague);
        command.execute();
        assertEquals(3.0, target.getScale(), 0.001);
        
        // Second paste: copy translation only (on same target)
        clipboard.mediateCopy(sourceColleague);
        PasteCommand command2 = new PasteCommand(target, clipboard, new CopyTranslationOnly(), targetColleague);
        command2.execute();
        
        assertEquals(3.0, target.getScale(), 0.001, "Scale should remain 3.0");
        Point translation = target.getTranslation();
        assertEquals(200, translation.x, "Translation X should be 200");
        assertEquals(100, translation.y, "Translation Y should be 100");
    }

    @Test
    public void testPasteAndUndoSequence() {
        clipboard.mediateCopy(sourceColleague);
        command = new PasteCommand(target, clipboard, new CopyBoth(), targetColleague);
        
        command.execute();
        assertEquals(2.0, target.getScale(), 0.001);
        
        command.undo();
        assertEquals(1.0, target.getScale(), 0.001);
        
        command.execute();
        assertEquals(2.0, target.getScale(), 0.001);
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
