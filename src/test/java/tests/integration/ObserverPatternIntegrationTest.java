package tests.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import command.ZoomCommand;
import core.Observer;
import model.ImageModel;
import model.ImageSource;
import model.Perspective;
import view.AbstractImageView;

/**
 * Tests d'intégration : Pattern Observer
 * 
 * Objectif : Vérifier que les vues se mettent à jour automatiquement
 * quand l'état d'une Perspective change (via le pattern Observer).
 * 
 * Architecture testée :
 *   Perspective (Subject) → notifyObservers() → Vue.update()
 *   
 * Patterns validés :
 *   - Observer Pattern (couplage faible)
 *   - Auto-refresh des vues (repaint automatique)
 *   - Notification en cascade
 */
public class ObserverPatternIntegrationTest {

    private ImageModel imageModel;
    private Perspective perspective;
    private MockUpdateTrackingView view;
    private MockUpdateTrackingView view2;

    @BeforeEach
    void setUp() {
        imageModel = new ImageModel(new MockImageSource());
        perspective = new Perspective("Test Perspective");
        imageModel.addPerspective(perspective);
        
        view = new MockUpdateTrackingView(imageModel, perspective);
        view2 = new MockUpdateTrackingView(imageModel, perspective);
    }

    /**
     * Test : Modification de la Perspective → Observer.update() appelé sur les vues
     */
    @Test
    void testPerspectiveChangeNotifiesObservers() {
        // Arrange
        int initialUpdateCount = view.getUpdateCallCount();

        // Act : Modifier la Perspective
        perspective.setScale(2.5);

        // Assert : Observer.update() doit avoir été appelé
        assertEquals(initialUpdateCount + 1, view.getUpdateCallCount(),
            "Observer.update() should be called when Perspective changes");
    }

    /**
     * Test : Plusieurs vues observent la même Perspective
     */
    @Test
    void testMultipleViewsObservePerspective() {
        // Arrange
        int view1InitialCount = view.getUpdateCallCount();
        int view2InitialCount = view2.getUpdateCallCount();

        // Act : Modifier la Perspective
        perspective.setTranslation(new Point(100, 50));

        // Assert : Les deux vues doivent être notifiées
        assertEquals(view1InitialCount + 1, view.getUpdateCallCount(),
            "View1 should be notified");
        assertEquals(view2InitialCount + 1, view2.getUpdateCallCount(),
            "View2 should be notified");
    }

    /**
     * Test : Après changement, la vue a accès aux nouvelles valeurs
     */
    @Test
    void testViewAccessesUpdatedPerspectiveValues() {
        // Arrange
        double newScale = 3.7;
        Point newTranslation = new Point(150, 200);

        // Act : Modifier la Perspective
        perspective.setScale(newScale);
        perspective.setTranslation(newTranslation);

        // Assert : La vue doit pouvoir accéder aux nouvelles valeurs
        assertEquals(newScale, view.getActivePerspective().getScale(), 0.001);
        assertEquals(newTranslation, view.getActivePerspective().getTranslation());
    }

    /**
     * Test : Retrait d'un observateur → Il n'est plus notifié
     */
    @Test
    void testRemoveObserverStopsNotifications() {
        // Arrange
        int initialUpdateCount = view.getUpdateCallCount();
        perspective.removeObserver(view);

        // Act : Modifier la Perspective
        perspective.setScale(2.0);

        // Assert : La vue ne doit pas être notifiée (count reste le même)
        assertEquals(initialUpdateCount, view.getUpdateCallCount(),
            "Removed observer should not be notified");
    }

    /**
     * Test : Deux modifications rapides → Deux notifications
     */
    @Test
    void testMultiplePerspectiveChanges() {
        // Arrange
        int initialCount = view.getUpdateCallCount();

        // Act : Deux modifications rapides
        perspective.setScale(1.5);
        perspective.setTranslation(new Point(10, 20));

        // Assert : Deux notifications doivent avoir lieu
        assertEquals(initialCount + 2, view.getUpdateCallCount(),
            "Each perspective change should trigger one notification");
    }

    /**
     * Test : Command → Perspective → Observer.update()
     * (End-to-end du pattern Observer avec Command)
     */
    @Test
    void testCommandExecutionTriggersObserverNotification() {
        // Arrange
        int initialCount = view.getUpdateCallCount();
        ZoomCommand zoomCmd = new ZoomCommand(perspective, 2.0);

        // Act : Exécuter la commande (qui modifie la Perspective)
        zoomCmd.execute();

        // Assert : Observer doit être notifié
        assertEquals(initialCount + 1, view.getUpdateCallCount(),
            "Command execution should trigger observer notification");
    }

    /**
     * Test : Undo d'une commande → Perspective change → Observer notifié
     */
    @Test
    void testCommandUndoTriggersObserverNotification() {
        // Arrange
        ZoomCommand zoomCmd = new ZoomCommand(perspective, 2.0);
        zoomCmd.execute();
        int countAfterExecute = view.getUpdateCallCount();

        // Act : Undo la commande
        zoomCmd.undo();

        // Assert : Observer doit être notifié lors du undo
        assertEquals(countAfterExecute + 1, view.getUpdateCallCount(),
            "Command undo should trigger observer notification");
    }

    /**
     * Test : Vérifier que update() est appelé avec la bonne fréquence
     */
    @Test
    void testObserverUpdateFrequency() {
        // Arrange
        int initialCount = view.getUpdateCallCount();

        // Act : Faire 5 modifications
        for (int i = 0; i < 5; i++) {
            perspective.setScale(1.0 + i * 0.5);
        }

        // Assert : 5 appels à update()
        assertEquals(initialCount + 5, view.getUpdateCallCount(),
            "Observer should be notified 5 times");
    }

    /**
     * Test : Observable n'a pas d'observateurs au départ
     */
    @Test
    void testPerspectiveWithoutObservers() {
        // Arrange
        Perspective isolatedPerspective = new Perspective("Isolated");
        
        // Act : Modifier sans observateurs
        assertDoesNotThrow(() -> {
            isolatedPerspective.setScale(2.0);
            isolatedPerspective.setTranslation(new Point(50, 50));
        }, "Should not throw when notifying zero observers");
    }

    // ========== MOCK IMPLEMENTATIONS ==========

    private static class MockImageSource implements ImageSource {
        @Override
        public java.awt.image.BufferedImage image() {
            return null;
        }
    }

    /**
     * Vue mock qui track les appels à update()
     */
    private static class MockUpdateTrackingView extends AbstractImageView {
        private int updateCallCount = 0;

        public MockUpdateTrackingView(ImageModel model, Perspective perspective) {
            super(model, perspective);
        }

        @Override
        public void update() {
            updateCallCount++;
            super.update();
        }

        @Override
        public Perspective getActivePerspective() {
            return super.getActivePerspective();
        }

        @Override
        protected void render(Graphics2D g2d) {
            // No-op for tests
        }

        public int getUpdateCallCount() {
            return updateCallCount;
        }
    }
}
