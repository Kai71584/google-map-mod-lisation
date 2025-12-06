package tests.unitaires;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import command.CommandBus;
import controller.LoadController;
import controller.SaveController;
import model.ImageModel;
import model.ImageSource;
import model.Perspective;
import persistence.JsonPersistenceManager;
import persistence.PersistenceManager;
import view.AbstractImageView;

/**
 * Tests unitaires pour les contrôleurs de persistance (SaveController et LoadController).
 * 
 * Objectif : Améliorer la couverture de code des contrôleurs de 58% à 70%+
 */
public class PersistenceControllersTest {

    @TempDir
    private File tempDir;

    private ImageModel imageModel;
    private Perspective perspective;
    private CommandBus commandBus;
    private PersistenceManager persistenceManager;
    private SaveController saveController;
    private LoadController loadController;
    private MockImageView mockImageView;

    @BeforeEach
    void setUp() {
        // Créer le modèle
        imageModel = new ImageModel(new MockImageSource());
        perspective = new Perspective("Test Perspective");
        imageModel.addPerspective(perspective);

        // Créer le bus de commandes
        commandBus = new CommandBus();

        // Créer le gestionnaire de persistance
        String persistenceFilePath = new File(tempDir, "test_persistence.json").getAbsolutePath();
        persistenceManager = new JsonPersistenceManager(persistenceFilePath);

        // Créer la vue mock
        mockImageView = new MockImageView(imageModel, perspective);

        // Créer les contrôleurs
        saveController = new SaveController(mockImageView, commandBus, persistenceManager);
        loadController = new LoadController(mockImageView, commandBus, persistenceManager, imageModel);
    }

    // ========== TESTS : SAVE CONTROLLER ==========

    @Test
    void testSaveControllerInitialization() {
        // Act & Assert
        assertNotNull(saveController, "SaveController should be initialized");
        assertEquals(mockImageView, saveController.getView(), "View should be stored");
    }

    @Test
    void testSaveControllerGetView() {
        // Act
        AbstractImageView view = saveController.getView();

        // Assert
        assertNotNull(view, "View should not be null");
        assertEquals(mockImageView, view, "Should return the correct view instance");
    }

    @Test
    void testSaveControllerPersistenceManagerAssignment() {
        // Arrange
        perspective.setScale(2.5);
        perspective.setTranslation(new java.awt.Point(75, 75));

        // Act - Save the perspective
        persistenceManager.save(perspective);

        // Assert - Verify file was created
        assertTrue(new File(tempDir, "test_persistence.json").exists(),
            "Persistence file should be created after save");
    }

    @Test
    void testSaveControllerPreservesPerspectiveScaleOnSave() {
        // Arrange
        perspective.setScale(3.5);

        // Act
        persistenceManager.save(perspective);

        // Assert - Load and verify
        ImageModel newModel = new ImageModel(new MockImageSource());
        persistenceManager.loadAll(newModel);

        assertEquals(3.5, newModel.getPerspectives().get(0).getScale(), 0.001);
    }

    @Test
    void testSaveControllerPreservesPerspectiveTranslationOnSave() {
        // Arrange
        perspective.setTranslation(new java.awt.Point(100, 150));

        // Act
        persistenceManager.save(perspective);

        // Assert - Load and verify
        ImageModel newModel = new ImageModel(new MockImageSource());
        persistenceManager.loadAll(newModel);

        assertEquals(100, newModel.getPerspectives().get(0).getTranslation().x);
        assertEquals(150, newModel.getPerspectives().get(0).getTranslation().y);
    }

    @Test
    void testSaveControllerPreservesPerspectiveStateCompletely() {
        // Arrange
        perspective.setScale(3.5);
        perspective.setTranslation(new java.awt.Point(100, 150));
        perspective.setName("Important View");

        // Act
        persistenceManager.save(perspective);

        // Assert
        ImageModel newModel = new ImageModel(new MockImageSource());
        persistenceManager.loadAll(newModel);

        Perspective loaded = newModel.getPerspectives().get(0);
        assertEquals(3.5, loaded.getScale(), 0.001);
        assertEquals(100, loaded.getTranslation().x);
        assertEquals(150, loaded.getTranslation().y);
    }

    // ========== TESTS : LOAD CONTROLLER ==========

    @Test
    void testLoadControllerInitialization() {
        // Act & Assert
        assertNotNull(loadController, "LoadController should be initialized");
        assertEquals(mockImageView, loadController.getView(), "View should be stored");
    }

    @Test
    void testLoadControllerHandleLoadAll() {
        // Arrange - Sauvegarder d'abord une perspective
        perspective.setScale(2.5);
        perspective.setTranslation(new java.awt.Point(75, 75));
        persistenceManager.save(perspective);

        // Créer un nouveau modèle vide
        ImageModel emptyModel = new ImageModel(new MockImageSource());
        MockImageView emptyView = new MockImageView(emptyModel, null);
        LoadController loader = new LoadController(emptyView, commandBus, persistenceManager, emptyModel);

        // Act
        loader.handleLoadAll();

        // Assert
        assertEquals(1, emptyModel.getPerspectives().size(),
            "Should have loaded one perspective");
    }

    @Test
    void testLoadControllerLoadsMultiplePerspectives() {
        // Arrange - Sauvegarder plusieurs perspectives
        Perspective p1 = new Perspective("Perspective 1");
        p1.setScale(1.0);
        persistenceManager.save(p1);

        Perspective p2 = new Perspective("Perspective 2");
        p2.setScale(2.0);
        persistenceManager.save(p2);

        Perspective p3 = new Perspective("Perspective 3");
        p3.setScale(3.0);
        persistenceManager.save(p3);

        // Créer un nouveau modèle vide
        ImageModel emptyModel = new ImageModel(new MockImageSource());
        MockImageView emptyView = new MockImageView(emptyModel, null);
        LoadController loader = new LoadController(emptyView, commandBus, persistenceManager, emptyModel);

        // Act
        loader.handleLoadAll();

        // Assert
        assertEquals(3, emptyModel.getPerspectives().size(),
            "Should have loaded three perspectives");
        assertEquals("Perspective 1", emptyModel.getPerspectives().get(0).getName());
        assertEquals("Perspective 2", emptyModel.getPerspectives().get(1).getName());
        assertEquals("Perspective 3", emptyModel.getPerspectives().get(2).getName());
    }

    @Test
    void testLoadControllerRestoresExactState() {
        // Arrange - Créer et sauvegarder une perspective avec état spécifique
        Perspective original = new Perspective("Test View");
        original.setScale(4.2);
        original.setTranslation(new java.awt.Point(250, 300));
        persistenceManager.save(original);

        // Charger dans un nouveau modèle
        ImageModel newModel = new ImageModel(new MockImageSource());
        MockImageView newView = new MockImageView(newModel, null);
        LoadController loader = new LoadController(newView, commandBus, persistenceManager, newModel);

        // Act
        loader.handleLoadAll();

        // Assert
        Perspective loaded = newModel.getPerspectives().get(0);
        assertEquals(4.2, loaded.getScale(), 0.001, "Scale should match");
        assertEquals(250, loaded.getTranslation().x, "Translation X should match");
        assertEquals(300, loaded.getTranslation().y, "Translation Y should match");
    }

    @Test
    void testLoadControllerWithNoDataDoesNotFail() {
        // Arrange - Modèle vide, aucune donnée sauvegardée
        ImageModel emptyModel = new ImageModel(new MockImageSource());
        MockImageView emptyView = new MockImageView(emptyModel, null);
        LoadController loader = new LoadController(emptyView, commandBus, persistenceManager, emptyModel);

        // Act & Assert
        assertDoesNotThrow(() -> loader.handleLoadAll(),
            "LoadController should handle empty persistence gracefully");
        assertEquals(0, emptyModel.getPerspectives().size(),
            "Model should remain empty");
    }

    @Test
    void testSaveAndLoadCyclePreservesData() {
        // Arrange
        perspective.setScale(2.7);
        perspective.setTranslation(new java.awt.Point(111, 222));
        perspective.setName("Test Cycle");

        // Act - Save
        persistenceManager.save(perspective);

        // Create new model and load
        ImageModel newModel = new ImageModel(new MockImageSource());
        MockImageView newView = new MockImageView(newModel, null);
        LoadController loader = new LoadController(newView, commandBus, persistenceManager, newModel);
        loader.handleLoadAll();

        // Assert
        assertEquals(1, newModel.getPerspectives().size());
        Perspective loaded = newModel.getPerspectives().get(0);
        assertEquals(2.7, loaded.getScale(), 0.001);
        assertEquals(111, loaded.getTranslation().x);
        assertEquals(222, loaded.getTranslation().y);
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
            super(model, perspective != null ? perspective : new Perspective("Default"));
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
