package tests.unitaires;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import core.Observer;
import model.ImageModel;
import model.ImageSource;
import model.Perspective;

/**
 * Tests unitaires pour le pattern Observer.
 * 
 * Valide :
 *   - Les notifications entre sujets découplés (ImageModel vs Perspective)
 *   - L'enregistrement/désenregistrement d'observers
 *   - La propagation correcte des mises à jour
 *   - L'isolation entre plusieurs observers et sujets
 */
public class ObserverPatternTest {

    private ImageModel imageModel;
    private Perspective perspective;
    private MockObserver mockObserverImage;
    private MockObserver mockObserverPerspective;

    @BeforeEach
    void setUp() {
        imageModel = new ImageModel(new MockImageSource());
        perspective = new Perspective("Test Perspective");
        mockObserverImage = new MockObserver();
        mockObserverPerspective = new MockObserver();
    }

    // ========== TESTS : SUBJECT IMAGEMODEL ==========

    @Test
    void testAddObserverToImageModel() {
        imageModel.addObserver(mockObserverImage);
        // Simply adding observer should not trigger notification
        assertEquals(0, mockObserverImage.getUpdateCount(), 
            "Adding observer alone should not trigger update");
    }

    @Test
    void testImageModelNotifiesObserverWhenPerspectiveAdded() {
        imageModel.addObserver(mockObserverImage);
        mockObserverImage.reset();
        
        imageModel.addPerspective(perspective);
        
        assertEquals(1, mockObserverImage.getUpdateCount(),
            "Observer should be notified when perspective is added");
    }

    @Test
    void testImageModelNotifiesMultipleObserversWhenPerspectiveAdded() {
        MockObserver observer2 = new MockObserver();
        MockObserver observer3 = new MockObserver();
        
        imageModel.addObserver(mockObserverImage);
        imageModel.addObserver(observer2);
        imageModel.addObserver(observer3);
        
        mockObserverImage.reset();
        observer2.reset();
        observer3.reset();
        
        imageModel.addPerspective(perspective);
        
        assertEquals(1, mockObserverImage.getUpdateCount());
        assertEquals(1, observer2.getUpdateCount());
        assertEquals(1, observer3.getUpdateCount());
    }

    @Test
    void testRemoveObserverFromImageModel() {
        imageModel.addObserver(mockObserverImage);
        mockObserverImage.reset();
        
        imageModel.removeObserver(mockObserverImage);
        imageModel.addPerspective(perspective);
        
        assertEquals(0, mockObserverImage.getUpdateCount(),
            "Removed observer should not receive updates");
    }

    @Test
    void testImageModelNotifiesOnEachPerspectiveAddition() {
        imageModel.addObserver(mockObserverImage);
        mockObserverImage.reset();
        
        imageModel.addPerspective(new Perspective("Perspective 1"));
        assertEquals(1, mockObserverImage.getUpdateCount());
        
        imageModel.addPerspective(new Perspective("Perspective 2"));
        assertEquals(2, mockObserverImage.getUpdateCount());
        
        imageModel.addPerspective(new Perspective("Perspective 3"));
        assertEquals(3, mockObserverImage.getUpdateCount());
    }

    // ========== TESTS : SUBJECT PERSPECTIVE ==========

    @Test
    void testAddObserverToPerspective() {
        perspective.addObserver(mockObserverPerspective);
        // Simply adding observer should not trigger notification
        assertEquals(0, mockObserverPerspective.getUpdateCount(),
            "Adding observer alone should not trigger update");
    }

    @Test
    void testPerspectiveNotifiesObserverWhenScaleChanges() {
        perspective.addObserver(mockObserverPerspective);
        mockObserverPerspective.reset();
        
        perspective.setScale(2.0);
        
        assertEquals(1, mockObserverPerspective.getUpdateCount(),
            "Observer should be notified when scale changes");
    }

    @Test
    void testPerspectiveNotifiesObserverWhenTranslationChanges() {
        perspective.addObserver(mockObserverPerspective);
        mockObserverPerspective.reset();
        
        perspective.setTranslation(new Point(10, 20));
        
        assertEquals(1, mockObserverPerspective.getUpdateCount(),
            "Observer should be notified when translation changes");
    }

    @Test
    void testPerspectiveNotifiesObserverWhenNameChanges() {
        perspective.addObserver(mockObserverPerspective);
        mockObserverPerspective.reset();
        
        perspective.setName("New Name");
        
        assertEquals(1, mockObserverPerspective.getUpdateCount(),
            "Observer should be notified when name changes");
    }

    @Test
    void testPerspectiveNotifiesMultipleObserversOnScaleChange() {
        MockObserver observer2 = new MockObserver();
        MockObserver observer3 = new MockObserver();
        
        perspective.addObserver(mockObserverPerspective);
        perspective.addObserver(observer2);
        perspective.addObserver(observer3);
        
        mockObserverPerspective.reset();
        observer2.reset();
        observer3.reset();
        
        perspective.setScale(1.5);
        
        assertEquals(1, mockObserverPerspective.getUpdateCount());
        assertEquals(1, observer2.getUpdateCount());
        assertEquals(1, observer3.getUpdateCount());
    }

    @Test
    void testRemoveObserverFromPerspective() {
        perspective.addObserver(mockObserverPerspective);
        mockObserverPerspective.reset();
        
        perspective.removeObserver(mockObserverPerspective);
        perspective.setScale(2.0);
        
        assertEquals(0, mockObserverPerspective.getUpdateCount(),
            "Removed observer should not receive updates from perspective");
    }

    @Test
    void testPerspectiveNotifiesOnEachStateChange() {
        perspective.addObserver(mockObserverPerspective);
        mockObserverPerspective.reset();
        
        perspective.setScale(1.5);
        assertEquals(1, mockObserverPerspective.getUpdateCount());
        
        perspective.setTranslation(new Point(5, 5));
        assertEquals(2, mockObserverPerspective.getUpdateCount());
        
        perspective.setName("Updated");
        assertEquals(3, mockObserverPerspective.getUpdateCount());
    }

    // ========== TESTS : ISOLATION ENTRE SUJETS DÉCOUPLÉS ==========

    @Test
    void testImageModelAndPerspectiveAreDecoupled() {
        imageModel.addObserver(mockObserverImage);
        perspective.addObserver(mockObserverPerspective);
        
        mockObserverImage.reset();
        mockObserverPerspective.reset();
        
        // Notification du modèle
        imageModel.addPerspective(perspective);
        
        assertEquals(1, mockObserverImage.getUpdateCount(),
            "ImageModel observers should be notified");
        assertEquals(0, mockObserverPerspective.getUpdateCount(),
            "Perspective observers should NOT be notified by ImageModel change");
    }

    @Test
    void testPerspectiveChangeDoesNotNotifyImageModelObservers() {
        imageModel.addObserver(mockObserverImage);
        perspective.addObserver(mockObserverPerspective);
        
        mockObserverImage.reset();
        mockObserverPerspective.reset();
        
        // Notification de la perspective
        perspective.setScale(2.0);
        
        assertEquals(0, mockObserverImage.getUpdateCount(),
            "ImageModel observers should NOT be notified by Perspective change");
        assertEquals(1, mockObserverPerspective.getUpdateCount(),
            "Perspective observers should be notified");
    }

    @Test
    void testMultiplePerspectivesNotifyIndependently() {
        Perspective perspective2 = new Perspective("Perspective 2");
        MockObserver observer1 = new MockObserver();
        MockObserver observer2 = new MockObserver();
        
        perspective.addObserver(observer1);
        perspective2.addObserver(observer2);
        
        observer1.reset();
        observer2.reset();
        
        perspective.setScale(2.0);
        
        assertEquals(1, observer1.getUpdateCount(),
            "Observer of Perspective 1 should be notified");
        assertEquals(0, observer2.getUpdateCount(),
            "Observer of Perspective 2 should NOT be notified");
    }

    @Test
    void testObserverRegisteredToMultipleSubjectsReceivesAllNotifications() {
        imageModel.addObserver(mockObserverImage);
        perspective.addObserver(mockObserverImage);
        
        mockObserverImage.reset();
        
        imageModel.addPerspective(perspective);
        int updateCountAfterImageModel = mockObserverImage.getUpdateCount();
        
        perspective.setScale(1.5);
        int updateCountAfterPerspective = mockObserverImage.getUpdateCount();
        
        assertEquals(1, updateCountAfterImageModel, "ImageModel notification counted");
        assertEquals(2, updateCountAfterPerspective, "Both notifications counted");
    }

    // ========== TESTS : EDGE CASES ==========

    @Test
    void testRemoveNonExistentObserverDoesNotCauseProblem() {
        MockObserver otherObserver = new MockObserver();
        perspective.addObserver(mockObserverPerspective);
        
        assertDoesNotThrow(() -> perspective.removeObserver(otherObserver),
            "Removing non-existent observer should not throw");
    }

    @Test
    void testAddSameObserverMultipleTimes() {
        perspective.addObserver(mockObserverPerspective);
        perspective.addObserver(mockObserverPerspective);
        
        mockObserverPerspective.reset();
        perspective.setScale(2.0);
        
        assertEquals(2, mockObserverPerspective.getUpdateCount(),
            "Same observer added twice should receive 2 notifications");
    }

    @Test
    void testNotifyObserversWithNoObserversRegistered() {
        assertDoesNotThrow(() -> perspective.setScale(2.0),
            "Notifying with no observers should not throw");
    }

    @Test
    void testObserverStillNotifiedAfterRemovingAnotherObserver() {
        MockObserver observer2 = new MockObserver();
        
        perspective.addObserver(mockObserverPerspective);
        perspective.addObserver(observer2);
        
        perspective.removeObserver(observer2);
        
        mockObserverPerspective.reset();
        perspective.setScale(2.0);
        
        assertEquals(1, mockObserverPerspective.getUpdateCount(),
            "First observer should still be notified after removing second");
    }

    @Test
    void testPerspectiveNotifiesObserverWhenRestored() {
        perspective.addObserver(mockObserverPerspective);
        
        // Créer un snapshot
        Perspective.Snapshot snapshot = perspective.createSnapshot();
        
        // Modifier la perspective
        perspective.setScale(2.0);
        perspective.setTranslation(new Point(10, 10));
        mockObserverPerspective.reset();
        
        // Restaurer depuis le snapshot
        perspective.restore(snapshot);
        
        assertEquals(1, mockObserverPerspective.getUpdateCount(),
            "Observer should be notified when perspective is restored");
    }

    // ========== MOCK OBSERVER HELPER ==========

    /**
     * Mock implementation of Observer for testing.
     * Tracks the number of times update() is called.
     */
    private static class MockObserver implements Observer {
        private int updateCount = 0;

        @Override
        public void update() {
            updateCount++;
        }

        public int getUpdateCount() {
            return updateCount;
        }

        public void reset() {
            updateCount = 0;
        }
    }

    /**
     * Mock implementation of ImageSource for testing.
     * Returns null image since we only test Observer pattern, not image rendering.
     */
    private static class MockImageSource implements ImageSource {
        @Override
        public java.awt.image.BufferedImage image() {
            return null; // Not needed for Observer tests
        }
    }
}
