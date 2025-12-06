package tests.integration;

import java.awt.Point;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import command.CommandBus;
import controller.PanController;
import controller.ZoomController;
import model.ImageModel;
import model.ImageSource;
import model.Perspective;
import view.AbstractImageView;

/**
 * Tests d'intégration pour la substituabilité des contrôleurs.
 *
 * Objectif :
 *   Vérifier que différents contrôleurs (clics souris vs boutons menu)
 *   appliquent les mêmes effets sur le modèle (Perspective).
 *
 * Architecture testée :
 *   Contrôleur (souris/menu) → CommandBus → Command → Modèle (Perspective)
 *
 * Patterns validés :
 *   - Command Pattern (exécution cohérente)
 *   - Dependency Injection (vue, bus découplés)
 *   - Substituabilité (même résultat via différents chemins)
 */
public class ControllersInterchangeabilityTest {

    private ImageModel imageModel;
    private Perspective perspective;
    private CommandBus commandBus;
    private ZoomController zoomController;
    private PanController panController;
    private MockImageView mockView;

    @BeforeEach
    void setUp() {
        // Créer le modèle
        imageModel = new ImageModel(new MockImageSource());
        perspective = new Perspective("Test Perspective");
        imageModel.addPerspective(perspective);

        // Créer le bus de commandes
        commandBus = new CommandBus();

        // Créer la vue mock
        mockView = new MockImageView(imageModel, perspective);

        // Créer les contrôleurs
        zoomController = new ZoomController(mockView, commandBus);
        panController = new PanController(mockView, commandBus);
    }

    // ========== TESTS : INTERCHANGEABILITÉ ZOOM ==========

    @Test
    void testZoomInViaMenuButtonEqualsZoomViaMouse() {
        double initialScale = perspective.getScale();

        // Chemin 1 : Via bouton menu (handleZoomIn)
        zoomController.handleZoomIn();
        double scaleAfterMenuZoom = perspective.getScale();

        // Réinitialiser
        setUp();

        // Chemin 2 : Via molette souris (onZoomRequested)
        zoomController.onZoomRequested(1.1); // facteur 1.1 = DEFAULT_FACTOR
        double scaleAfterMouseZoom = perspective.getScale();

        assertEquals(scaleAfterMenuZoom, scaleAfterMouseZoom,
            "Menu zoom and mouse zoom should produce identical scale results");
        assertTrue(scaleAfterMenuZoom > initialScale,
            "Both zoom methods should increase scale");
    }

    @Test
    void testZoomOutViaMenuButtonEqualsZoomViaMouse() {
        double initialScale = perspective.getScale();

        // Chemin 1 : Via bouton menu (handleZoomOut)
        zoomController.handleZoomOut();
        double scaleAfterMenuZoom = perspective.getScale();

        // Réinitialiser
        setUp();

        // Chemin 2 : Via molette souris (onZoomRequested)
        zoomController.onZoomRequested(1.0 / 1.1); // facteur 1/1.1 = inverse
        double scaleAfterMouseZoom = perspective.getScale();

        assertEquals(scaleAfterMenuZoom, scaleAfterMouseZoom,
            "Menu zoom-out and mouse zoom-out should produce identical results");
        assertTrue(scaleAfterMenuZoom < initialScale,
            "Both zoom-out methods should decrease scale");
    }

    @Test
    void testMultipleZoomOperationsProduceSameResult() {
        // Chemin 1 : Menu zoom in + menu zoom in
        zoomController.handleZoomIn();
        zoomController.handleZoomIn();
        double scaleAfterMenuDoubleZoom = perspective.getScale();

        // Réinitialiser
        setUp();

        // Chemin 2 : Mouse zoom in + mouse zoom in
        zoomController.onZoomRequested(1.1);
        zoomController.onZoomRequested(1.1);
        double scaleAfterMouseDoubleZoom = perspective.getScale();

        assertEquals(scaleAfterMenuDoubleZoom, scaleAfterMouseDoubleZoom,
            "Multiple menu zooms should equal multiple mouse zooms");
    }

    @Test
    void testZoomFactorAppliedIdentically() {
        double factor = 1.5;

        // Chemin 1 : Via onZoomRequested
        zoomController.onZoomRequested(factor);
        double scaleAfterFirstPath = perspective.getScale();

        // Réinitialiser
        setUp();

        // Chemin 2 : Via onZoomRequested (même facteur)
        zoomController.onZoomRequested(factor);
        double scaleAfterSecondPath = perspective.getScale();

        assertEquals(scaleAfterFirstPath, scaleAfterSecondPath,
            "Same zoom factor should produce identical scale");
        assertEquals(factor, scaleAfterFirstPath, 0.0001,
            "Scale should be exactly the zoom factor");
    }

    // ========== TESTS : INTERCHANGEABILITÉ PAN (TRANSLATION) ==========

    @Test
    void testPanViaControllerProducesTranslation() {
        Point initialTranslation = perspective.getTranslation();

        int dx = 50;
        int dy = 30;

        panController.onPanDelta(dx, dy);

        Point newTranslation = perspective.getTranslation();

        assertEquals(initialTranslation.x + dx, newTranslation.x,
            "Translation X should be updated by dx");
        assertEquals(initialTranslation.y + dy, newTranslation.y,
            "Translation Y should be updated by dy");
    }

    @Test
    void testMultiplePanOperationsAccumulate() {
        int dx1 = 20, dy1 = 15;
        int dx2 = 30, dy2 = 25;

        panController.onPanDelta(dx1, dy1);
        panController.onPanDelta(dx2, dy2);

        Point translation = perspective.getTranslation();

        assertEquals(dx1 + dx2, translation.x,
            "Multiple pans should accumulate in X");
        assertEquals(dy1 + dy2, translation.y,
            "Multiple pans should accumulate in Y");
    }

    @Test
    void testPanWithNegativeValuesDecreasesTranslation() {
        // Pan positif d'abord
        panController.onPanDelta(50, 50);
        Point afterPositivePan = perspective.getTranslation();

        // Pan négatif
        panController.onPanDelta(-30, -20);
        Point afterNegativePan = perspective.getTranslation();

        assertEquals(afterPositivePan.x - 30, afterNegativePan.x);
        assertEquals(afterPositivePan.y - 20, afterNegativePan.y);
    }

    // ========== TESTS : COMBINAISON ZOOM + PAN ==========

    @Test
    void testZoomAndPanAreMutuallyIndependent() {
        double initialScale = perspective.getScale();
        Point initialTranslation = perspective.getTranslation();

        // Appliquer zoom
        zoomController.handleZoomIn();
        double scaleAfterZoom = perspective.getScale();
        Point translationAfterZoom = perspective.getTranslation();

        // Vérifier que le zoom n'affecte pas la translation
        assertEquals(initialTranslation, translationAfterZoom,
            "Zoom should not affect translation");

        // Appliquer pan
        panController.onPanDelta(20, 15);
        Point translationAfterPan = perspective.getTranslation();
        double scaleAfterPan = perspective.getScale();

        // Vérifier que le pan n'affecte pas le zoom
        assertEquals(scaleAfterZoom, scaleAfterPan,
            "Pan should not affect scale");
        assertEquals(new Point(20, 15), translationAfterPan,
            "Pan should update translation independently");
    }

    @Test
    void testComplexSequenceOfZoomAndPan() {
        // Séquence complexe : zoom in → pan → zoom in → pan
        zoomController.handleZoomIn(); // scale *= 1.1
        double scaleAfterFirstZoom = perspective.getScale();

        panController.onPanDelta(25, 25); // translation += (25, 25)
        Point translationAfterFirstPan = perspective.getTranslation();

        zoomController.handleZoomIn(); // scale *= 1.1
        double scaleAfterSecondZoom = perspective.getScale();

        panController.onPanDelta(15, -10); // translation += (15, -10)
        Point translationAfterSecondPan = perspective.getTranslation();

        // Vérifications
        assertTrue(scaleAfterFirstZoom < scaleAfterSecondZoom,
            "Second zoom should increase scale further");
        assertEquals(new Point(40, 15), translationAfterSecondPan,
            "Translation should reflect both pans");
    }

    // ========== TESTS : UNDO/REDO CONSISTENCY ==========

    @Test
    void testZoomCommandCanBeUndone() {
        double initialScale = perspective.getScale();

        // Zoom in
        zoomController.handleZoomIn();
        double scaleAfterZoom = perspective.getScale();
        assertTrue(scaleAfterZoom > initialScale);

        // Undo
        commandBus.undo(perspective);
        double scaleAfterUndo = perspective.getScale();

        assertEquals(initialScale, scaleAfterUndo,
            "Undo should restore initial scale");
    }

    @Test
    void testPanCommandCanBeUndone() {
        Point initialTranslation = perspective.getTranslation();

        // Pan
        panController.onPanDelta(50, 30);
        Point translationAfterPan = perspective.getTranslation();
        assertNotEquals(initialTranslation, translationAfterPan);

        // Undo
        commandBus.undo(perspective);
        Point translationAfterUndo = perspective.getTranslation();

        assertEquals(initialTranslation, translationAfterUndo,
            "Undo should restore initial translation");
    }

    @Test
    void testRedoRestoresStateAfterUndo() {
        double initialScale = perspective.getScale();

        // Zoom in
        zoomController.handleZoomIn();
        double scaleAfterZoom = perspective.getScale();

        // Undo
        commandBus.undo(perspective);

        // Redo
        commandBus.redo(perspective);
        double scaleAfterRedo = perspective.getScale();

        assertEquals(scaleAfterZoom, scaleAfterRedo,
            "Redo should restore zoomed state");
    }

    // ========== TESTS : EDGE CASES ==========

    @Test
    void testZoomWithNullPerspectiveDoesNotThrow() {
        mockView.setActivePerspectiveNull();

        assertDoesNotThrow(() -> zoomController.handleZoomIn(),
            "ZoomController should handle null perspective gracefully");
    }

    @Test
    void testPanWithNullPerspectiveDoesNotThrow() {
        mockView.setActivePerspectiveNull();

        assertDoesNotThrow(() -> panController.onPanDelta(10, 10),
            "PanController should handle null perspective gracefully");
    }

    @Test
    void testZoomWithZeroFactorIsNoOp() {
        double initialScale = perspective.getScale();

        zoomController.onZoomRequested(0.0);

        double scaleAfterZoom = perspective.getScale();
        // Scale should become 0 (zero factor applied)
        assertEquals(0.0, scaleAfterZoom);
    }

    @Test
    void testPanWithZeroDeltaIsNoOp() {
        Point initialTranslation = perspective.getTranslation();

        panController.onPanDelta(0, 0);

        Point translationAfterPan = perspective.getTranslation();
        assertEquals(initialTranslation, translationAfterPan,
            "Pan with zero delta should not change translation");
    }

    // ========== MOCK IMPLEMENTATIONS ==========

    /**
     * Mock implementation of ImageSource.
     */
    private static class MockImageSource implements ImageSource {
        @Override
        public BufferedImage image() {
            return null; // Not needed for controller tests
        }
    }

    /**
     * Mock implementation of AbstractImageView for testing.
     */
    private static class MockImageView extends AbstractImageView {
        private Perspective mockActivePerspective;

        public MockImageView(ImageModel model, Perspective perspective) {
            super(model, perspective);
            this.mockActivePerspective = perspective;
        }

        @Override
        public Perspective getActivePerspective() {
            return mockActivePerspective;
        }

        @Override
        protected void render(java.awt.Graphics2D g2d) {
            // No-op for tests - we don't need to render anything
        }

        public void setActivePerspectiveNull() {
            this.mockActivePerspective = null;
        }
    }
}
