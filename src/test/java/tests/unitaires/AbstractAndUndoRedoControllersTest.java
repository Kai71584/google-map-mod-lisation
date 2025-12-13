package tests.unitaires;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import command.CommandBus;
import command.ZoomCommand;
import controller.UndoRedoController;
import model.ImageModel;
import model.ImageSource;
import model.Perspective;
import view.AbstractImageView;

/**
 * Tests unitaires pour AbstractController et UndoRedoController.
 * Objectif : Améliorer la couverture de 60%/59% vers 70%+
 */
public class AbstractAndUndoRedoControllersTest {

    private ImageModel imageModel;
    private Perspective perspective;
    private CommandBus commandBus;
    private MockImageView mockImageView;
    private UndoRedoController undoRedoController;

    @BeforeEach
    void setUp() {
        imageModel = new ImageModel(new MockImageSource());
        perspective = new Perspective("Test Perspective");
        imageModel.addPerspective(perspective);

        commandBus = new CommandBus();
        mockImageView = new MockImageView(imageModel, perspective);
        undoRedoController = new UndoRedoController(mockImageView, commandBus);
    }

    // ========== TESTS : ABSTRACT CONTROLLER ==========

    @Test
    void testAbstractControllerInitializationWithValidParameters() {
        // Arrange

        // Act
        AbstractImageView view = undoRedoController.getView();

        // Assert
        assertNotNull(undoRedoController, "Controller should be initialized");
        assertEquals(mockImageView, view, "View should be stored");
    }

    @Test
    void testAbstractControllerGetViewReturnsCorrectView() {
        // Arrange

        // Act
        AbstractImageView view = undoRedoController.getView();

        // Assert
        assertNotNull(view, "View should not be null");
        assertEquals(mockImageView, view, "Should return the correct view instance");
    }

    // ========== TESTS : UNDO REDO CONTROLLER ==========

    @Test
    void testHandleUndoWithEmptyHistory() {
        // Arrange

        // Act
        undoRedoController.handleUndo();

        // Assert
        assertEquals(1.0, perspective.getScale(), 0.001, "Undo on empty history should keep scale");
    }

    @Test
    void testHandleRedoWithEmptyHistory() {
        // Arrange

        // Act
        undoRedoController.handleRedo();

        // Assert
        assertEquals(1.0, perspective.getScale(), 0.001, "Redo on empty history should keep scale");
    }

    @Test
    void testHandleUndoAfterCommand() {
        // Arrange
        ZoomCommand zoomCommand = new ZoomCommand(perspective, 2.0);
        commandBus.execute(zoomCommand);

        // Act
        undoRedoController.handleUndo();

        // Assert
        assertEquals(1.0, perspective.getScale(), 0.001, "Undo should restore original scale");
    }

    @Test
    void testHandleRedoAfterUndo() {
        // Arrange
        ZoomCommand zoomCommand = new ZoomCommand(perspective, 2.0);
        commandBus.execute(zoomCommand);
        undoRedoController.handleUndo();

        // Act
        undoRedoController.handleRedo();

        // Assert
        assertEquals(2.0, perspective.getScale(), 0.001, "Redo should reapply the command");
    }

    @Test
    void testHandleUndoMultipleCommands() {
        // Arrange
        commandBus.execute(new ZoomCommand(perspective, 2.0));
        double afterFirst = perspective.getScale();
        
        commandBus.execute(new ZoomCommand(perspective, 1.5));  // Multiplies: 2.0 * 1.5 = 3.0
        double afterSecond = perspective.getScale();
        
        commandBus.execute(new ZoomCommand(perspective, 1.3));  // Multiplies: 3.0 * 1.3 ≈ 3.9
        double afterThird = perspective.getScale();

        // Act
        undoRedoController.handleUndo();
        double afterFirstUndo = perspective.getScale();

        // Assert
        assertNotEquals(afterThird, afterFirstUndo, "After first undo, scale should change");
        assertTrue(afterFirstUndo > 0, "Scale should remain positive after undo");
    }

    @Test
    void testHandleRedoMultipleCommands() {
        // Arrange
        commandBus.execute(new ZoomCommand(perspective, 2.0));
        double scale1 = perspective.getScale();
        
        commandBus.execute(new ZoomCommand(perspective, 1.5));
        double scale2 = perspective.getScale();
        
        commandBus.execute(new ZoomCommand(perspective, 1.2));
        double scale3 = perspective.getScale();

        // Act
        undoRedoController.handleUndo();
        undoRedoController.handleUndo();
        undoRedoController.handleUndo();
        double afterAllUndo = perspective.getScale();

        undoRedoController.handleRedo();
        double afterFirstRedo = perspective.getScale();

        // Assert
        assertNotEquals(afterAllUndo, afterFirstRedo, "After first redo, scale should change");
        assertEquals(scale1, afterFirstRedo, 0.001, "After first redo should restore first zoom");
    }

    @Test
    void testUndoRedoAlternatingSequence() {
        // Arrange
        commandBus.execute(new ZoomCommand(perspective, 2.5));

        // Act
        undoRedoController.handleUndo();
        double afterFirstUndo = perspective.getScale();
        undoRedoController.handleRedo();
        double afterFirstRedo = perspective.getScale();
        undoRedoController.handleUndo();
        double afterSecondUndo = perspective.getScale();
        undoRedoController.handleRedo();
        double finalScale = perspective.getScale();

        // Assert
        assertEquals(1.0, afterFirstUndo, 0.001);
        assertEquals(2.5, afterFirstRedo, 0.001);
        assertEquals(1.0, afterSecondUndo, 0.001);
        assertEquals(2.5, finalScale, 0.001, "Final redo should reapply zoom");
    }

    @Test
    void testHandleUndoThenNewCommandClearsRedo() {
        // Arrange
        commandBus.execute(new ZoomCommand(perspective, 2.0));
        undoRedoController.handleUndo();

        // Act
        commandBus.execute(new ZoomCommand(perspective, 3.0));
        undoRedoController.handleRedo();

        // Assert
        assertEquals(3.0, perspective.getScale(), 0.001, "Redo should not change state after new command");
    }

    @Test
    void testUndoRedoWithLargeCommandSequence() {
        // Arrange
        for (int i = 1; i <= 5; i++) {
            commandBus.execute(new ZoomCommand(perspective, 1.1));
        }
        double scaleAfterCommands = perspective.getScale();

        // Act
        for (int i = 0; i < 5; i++) {
            undoRedoController.handleUndo();
        }
        double scaleAfterAllUndo = perspective.getScale();

        for (int i = 0; i < 5; i++) {
            undoRedoController.handleRedo();
        }
        double scaleAfterAllRedo = perspective.getScale();

        // Assert
        assertEquals(1.0, scaleAfterAllUndo, 0.001, "Should be back to 1.0");
        assertNotEquals(scaleAfterCommands, scaleAfterAllUndo);
        assertEquals(scaleAfterCommands, scaleAfterAllRedo, 0.001);
    }

    @Test
    void testAbstractControllerFieldAccessibility() {
        // Arrange

        // Act
        AbstractImageView retrievedView = undoRedoController.getView();

        // Assert
        assertSame(mockImageView, retrievedView, "Should return the same view instance");
    }

    // ========== MOCK IMPLEMENTATIONS ==========

    private static class MockImageSource implements ImageSource {
        @Override
        public java.awt.image.BufferedImage image() {
            return null;
        }
    }

    private static class MockImageView extends AbstractImageView {
        public MockImageView(ImageModel model, Perspective perspective) {
            super(model, perspective);
        }

        @Override
        public Perspective getActivePerspective() {
            return super.getActivePerspective();
        }

        @Override
        protected void render(java.awt.Graphics2D g2d) {
            // No-op for tests
        }
    }
}
