package tests.unitaires;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import command.CommandBus;
import command.ZoomCommand;
import controller.AbstractController;
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
        // Act & Assert
        assertNotNull(undoRedoController, "Controller should be initialized");
        assertEquals(mockImageView, undoRedoController.getView(), "View should be stored");
    }

    @Test
    void testAbstractControllerGetViewReturnsCorrectView() {
        // Act
        AbstractImageView view = undoRedoController.getView();

        // Assert
        assertNotNull(view, "View should not be null");
        assertEquals(mockImageView, view, "Should return the correct view instance");
    }

    // ========== TESTS : UNDO REDO CONTROLLER ==========

    @Test
    void testHandleUndoWithEmptyHistory() {
        // Act
        undoRedoController.handleUndo();

        // Assert
        // Should not throw and perspective should remain unchanged
        assertEquals(1.0, perspective.getScale(), 0.001);
    }

    @Test
    void testHandleRedoWithEmptyHistory() {
        // Act
        undoRedoController.handleRedo();

        // Assert
        // Should not throw and perspective should remain unchanged
        assertEquals(1.0, perspective.getScale(), 0.001);
    }

    @Test
    void testHandleUndoAfterCommand() {
        // Arrange - Exécuter une commande
        ZoomCommand zoomCommand = new ZoomCommand(perspective, 2.0);
        commandBus.execute(zoomCommand);
        assertEquals(2.0, perspective.getScale(), 0.001, "Zoom should be applied");

        // Act - Undo
        undoRedoController.handleUndo();

        // Assert
        assertEquals(1.0, perspective.getScale(), 0.001, "Undo should restore original scale");
    }

    @Test
    void testHandleRedoAfterUndo() {
        // Arrange - Exécuter une commande puis undo
        ZoomCommand zoomCommand = new ZoomCommand(perspective, 2.0);
        commandBus.execute(zoomCommand);
        undoRedoController.handleUndo();
        assertEquals(1.0, perspective.getScale(), 0.001);

        // Act - Redo
        undoRedoController.handleRedo();

        // Assert
        assertEquals(2.0, perspective.getScale(), 0.001, "Redo should reapply the command");
    }

    @Test
    void testHandleUndoMultipleCommands() {
        // Arrange - Exécuter plusieurs commandes
        commandBus.execute(new ZoomCommand(perspective, 2.0));
        double afterFirst = perspective.getScale();
        
        commandBus.execute(new ZoomCommand(perspective, 1.5));  // Multiplies: 2.0 * 1.5 = 3.0
        double afterSecond = perspective.getScale();
        
        commandBus.execute(new ZoomCommand(perspective, 1.3));  // Multiplies: 3.0 * 1.3 ≈ 3.9
        double afterThird = perspective.getScale();

        // Act - Undo once
        undoRedoController.handleUndo();
        double afterFirstUndo = perspective.getScale();

        // Assert
        assertNotEquals(afterThird, afterFirstUndo, "After first undo, scale should change");
        assertTrue(afterFirstUndo > 0, "Scale should remain positive after undo");
    }

    @Test
    void testHandleRedoMultipleCommands() {
        // Arrange - Exécuter plusieurs commandes, puis undo tout
        commandBus.execute(new ZoomCommand(perspective, 2.0));
        double scale1 = perspective.getScale();
        
        commandBus.execute(new ZoomCommand(perspective, 1.5));
        double scale2 = perspective.getScale();
        
        commandBus.execute(new ZoomCommand(perspective, 1.2));
        double scale3 = perspective.getScale();

        // Undo all
        undoRedoController.handleUndo();
        undoRedoController.handleUndo();
        undoRedoController.handleUndo();
        double afterAllUndo = perspective.getScale();

        // Act - Redo once
        undoRedoController.handleRedo();
        double afterFirstRedo = perspective.getScale();

        // Assert
        assertNotEquals(afterAllUndo, afterFirstRedo, "After first redo, scale should change");
        assertEquals(scale1, afterFirstRedo, 0.001, "After first redo should restore first zoom");
    }

    @Test
    void testUndoRedoAlternatingSequence() {
        // Arrange - Exécuter une commande
        commandBus.execute(new ZoomCommand(perspective, 2.5));

        // Act & Assert
        undoRedoController.handleUndo();
        assertEquals(1.0, perspective.getScale(), 0.001);

        undoRedoController.handleRedo();
        assertEquals(2.5, perspective.getScale(), 0.001);

        undoRedoController.handleUndo();
        assertEquals(1.0, perspective.getScale(), 0.001);

        undoRedoController.handleRedo();
        assertEquals(2.5, perspective.getScale(), 0.001);
    }

    @Test
    void testHandleUndoThenNewCommandClearsRedo() {
        // Arrange - Exécuter commande, undo
        commandBus.execute(new ZoomCommand(perspective, 2.0));
        undoRedoController.handleUndo();
        assertEquals(1.0, perspective.getScale(), 0.001);

        // Act - Exécuter une nouvelle commande
        commandBus.execute(new ZoomCommand(perspective, 3.0));

        // Assert - Redo ne devrait rien faire (l'historique redo est vidé)
        undoRedoController.handleRedo();
        assertEquals(3.0, perspective.getScale(), 0.001, "Redo should not change state after new command");
    }

    @Test
    void testUndoRedoWithLargeCommandSequence() {
        // Arrange - Exécuter 5 commandes
        for (int i = 1; i <= 5; i++) {
            commandBus.execute(new ZoomCommand(perspective, 1.1));
        }
        double scaleAfterCommands = perspective.getScale();

        // Act - Undo 5 times
        for (int i = 0; i < 5; i++) {
            undoRedoController.handleUndo();
        }
        double scaleAfterAllUndo = perspective.getScale();

        // Assert
        assertEquals(1.0, scaleAfterAllUndo, 0.001, "Should be back to 1.0");
        assertNotEquals(scaleAfterCommands, scaleAfterAllUndo);

        // Act - Redo all
        for (int i = 0; i < 5; i++) {
            undoRedoController.handleRedo();
        }
        double scaleAfterAllRedo = perspective.getScale();

        // Assert
        assertEquals(scaleAfterCommands, scaleAfterAllRedo, 0.001);
    }

    @Test
    void testAbstractControllerFieldAccessibility() {
        // Arrange
        AbstractImageView retrievedView = undoRedoController.getView();

        // Act & Assert
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
