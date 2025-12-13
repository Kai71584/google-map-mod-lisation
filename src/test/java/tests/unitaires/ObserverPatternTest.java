package tests.unitaires;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

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
        // Arrange
        imageModel.addObserver(mockObserverImage);

        // Act
        int updateCount = mockObserverImage.getUpdateCount();

        // Assert
        assertEquals(0, updateCount,
            "Adding observer alone should not trigger update");
    }

    @Test
    void testImageModelNotifiesObserverWhenPerspectiveAdded() {
        // Arrange
        imageModel.addObserver(mockObserverImage);
        mockObserverImage.reset();

        // Act
        imageModel.addPerspective(perspective);
        int updateCount = mockObserverImage.getUpdateCount();

        // Assert
        assertEquals(1, updateCount,
            "Observer should be notified when perspective is added");
    }

    @Test
    void testImageModelNotifiesMultipleObserversWhenPerspectiveAdded() {
        // Arrange
        MockObserver observer2 = new MockObserver();
        MockObserver observer3 = new MockObserver();
        
        imageModel.addObserver(mockObserverImage);
        imageModel.addObserver(observer2);
        imageModel.addObserver(observer3);
        
        mockObserverImage.reset();
        observer2.reset();
        observer3.reset();

        // Act
        imageModel.addPerspective(perspective);
        int updates1 = mockObserverImage.getUpdateCount();
        int updates2 = observer2.getUpdateCount();
        int updates3 = observer3.getUpdateCount();

        // Assert
        assertEquals(1, updates1);
        assertEquals(1, updates2);
        assertEquals(1, updates3);
    }

    @Test
    void testRemoveObserverFromImageModel() {
        // Arrange
        imageModel.addObserver(mockObserverImage);
        mockObserverImage.reset();
        imageModel.removeObserver(mockObserverImage);
        
        // Act
        imageModel.addPerspective(perspective);
        int updateCount = mockObserverImage.getUpdateCount();

        // Assert
        assertEquals(0, updateCount,
            "Removed observer should not receive updates");
    }

    @Test
    void testImageModelNotifiesOnEachPerspectiveAddition() {
        // Arrange
        imageModel.addObserver(mockObserverImage);
        mockObserverImage.reset();

        // Act
        imageModel.addPerspective(new Perspective("Perspective 1"));
        int afterFirst = mockObserverImage.getUpdateCount();
        imageModel.addPerspective(new Perspective("Perspective 2"));
        int afterSecond = mockObserverImage.getUpdateCount();
        imageModel.addPerspective(new Perspective("Perspective 3"));
        int afterThird = mockObserverImage.getUpdateCount();

        // Assert
        assertEquals(1, afterFirst);
        assertEquals(2, afterSecond);
        assertEquals(3, afterThird);
    }

    // ========== TESTS : SUBJECT PERSPECTIVE ==========

    @Test
    void testAddObserverToPerspective() {
        // Arrange
        perspective.addObserver(mockObserverPerspective);

        // Act
        int updateCount = mockObserverPerspective.getUpdateCount();

        // Assert
        assertEquals(0, updateCount,
            "Adding observer alone should not trigger update");
    }

    @Test
    void testPerspectiveNotifiesObserverWhenScaleChanges() {
        // Arrange
        perspective.addObserver(mockObserverPerspective);
        mockObserverPerspective.reset();

        // Act
        perspective.setScale(2.0);
        int updateCount = mockObserverPerspective.getUpdateCount();

        // Assert
        assertEquals(1, updateCount,
            "Observer should be notified when scale changes");
    }

    @Test
    void testPerspectiveNotifiesObserverWhenTranslationChanges() {
        // Arrange
        perspective.addObserver(mockObserverPerspective);
        mockObserverPerspective.reset();

        // Act
        perspective.setTranslation(new Point(10, 20));
        int updateCount = mockObserverPerspective.getUpdateCount();

        // Assert
        assertEquals(1, updateCount,
            "Observer should be notified when translation changes");
    }

    @Test
    void testPerspectiveNotifiesObserverWhenNameChanges() {
        // Arrange
        perspective.addObserver(mockObserverPerspective);
        mockObserverPerspective.reset();

        // Act
        perspective.setName("New Name");
        int updateCount = mockObserverPerspective.getUpdateCount();

        // Assert
        assertEquals(1, updateCount,
            "Observer should be notified when name changes");
    }

    @Test
    void testPerspectiveNotifiesMultipleObserversOnScaleChange() {
        // Arrange
        MockObserver observer2 = new MockObserver();
        MockObserver observer3 = new MockObserver();
        
        perspective.addObserver(mockObserverPerspective);
        perspective.addObserver(observer2);
        perspective.addObserver(observer3);
        
        mockObserverPerspective.reset();
        observer2.reset();
        observer3.reset();

        // Act
        perspective.setScale(1.5);
        int count1 = mockObserverPerspective.getUpdateCount();
        int count2 = observer2.getUpdateCount();
        int count3 = observer3.getUpdateCount();

        // Assert
        assertEquals(1, count1);
        assertEquals(1, count2);
        assertEquals(1, count3);
    }

    @Test
    void testRemoveObserverFromPerspective() {
        // Arrange
        perspective.addObserver(mockObserverPerspective);
        mockObserverPerspective.reset();
        perspective.removeObserver(mockObserverPerspective);

        // Act
        perspective.setScale(2.0);
        int updateCount = mockObserverPerspective.getUpdateCount();

        // Assert
        assertEquals(0, updateCount,
            "Removed observer should not receive updates from perspective");
    }

    @Test
    void testPerspectiveNotifiesOnEachStateChange() {
        // Arrange
        perspective.addObserver(mockObserverPerspective);
        mockObserverPerspective.reset();

        // Act
        perspective.setScale(1.5);
        int afterScale = mockObserverPerspective.getUpdateCount();
        perspective.setTranslation(new Point(5, 5));
        int afterTranslation = mockObserverPerspective.getUpdateCount();
        perspective.setName("Updated");
        int afterName = mockObserverPerspective.getUpdateCount();

        // Assert
        assertEquals(1, afterScale);
        assertEquals(2, afterTranslation);
        assertEquals(3, afterName);
    }

    // ========== TESTS : ISOLATION ENTRE SUJETS DÉCOUPLÉS ==========

    @Test
    void testImageModelAndPerspectiveAreDecoupled() {
        // Arrange
        imageModel.addObserver(mockObserverImage);
        perspective.addObserver(mockObserverPerspective);
        
        mockObserverImage.reset();
        mockObserverPerspective.reset();

        // Act
        imageModel.addPerspective(perspective);
        int imageUpdates = mockObserverImage.getUpdateCount();
        int perspectiveUpdates = mockObserverPerspective.getUpdateCount();

        // Assert
        assertEquals(1, imageUpdates,
            "ImageModel observers should be notified");
        assertEquals(0, perspectiveUpdates,
            "Perspective observers should NOT be notified by ImageModel change");
    }

    @Test
    void testPerspectiveChangeDoesNotNotifyImageModelObservers() {
        // Arrange
        imageModel.addObserver(mockObserverImage);
        perspective.addObserver(mockObserverPerspective);
        
        mockObserverImage.reset();
        mockObserverPerspective.reset();

        // Act
        perspective.setScale(2.0);
        int imageUpdates = mockObserverImage.getUpdateCount();
        int perspectiveUpdates = mockObserverPerspective.getUpdateCount();

        // Assert
        assertEquals(0, imageUpdates,
            "ImageModel observers should NOT be notified by Perspective change");
        assertEquals(1, perspectiveUpdates,
            "Perspective observers should be notified");
    }

    @Test
    void testMultiplePerspectivesNotifyIndependently() {
        // Arrange
        Perspective perspective2 = new Perspective("Perspective 2");
        MockObserver observer1 = new MockObserver();
        MockObserver observer2 = new MockObserver();
        
        perspective.addObserver(observer1);
        perspective2.addObserver(observer2);
        
        observer1.reset();
        observer2.reset();

        // Act
        perspective.setScale(2.0);
        int updatesPerspective1 = observer1.getUpdateCount();
        int updatesPerspective2 = observer2.getUpdateCount();

        // Assert
        assertEquals(1, updatesPerspective1,
            "Observer of Perspective 1 should be notified");
        assertEquals(0, updatesPerspective2,
            "Observer of Perspective 2 should NOT be notified");
    }

    @Test
    void testObserverRegisteredToMultipleSubjectsReceivesAllNotifications() {
        // Arrange
        imageModel.addObserver(mockObserverImage);
        perspective.addObserver(mockObserverImage);
        
        mockObserverImage.reset();

        // Act
        imageModel.addPerspective(perspective);
        int updateCountAfterImageModel = mockObserverImage.getUpdateCount();
        perspective.setScale(1.5);
        int updateCountAfterPerspective = mockObserverImage.getUpdateCount();

        // Assert
        assertEquals(1, updateCountAfterImageModel, "ImageModel notification counted");
        assertEquals(2, updateCountAfterPerspective, "Both notifications counted");
    }

    // ========== TESTS : EDGE CASES ==========

    @Test
    void testRemoveNonExistentObserverDoesNotCauseProblem() {
        // Arrange
        MockObserver otherObserver = new MockObserver();
        perspective.addObserver(mockObserverPerspective);

        // Act
        Executable removalAttempt = () -> perspective.removeObserver(otherObserver);

        // Assert
        assertDoesNotThrow(removalAttempt,
            "Removing non-existent observer should not throw");
    }

    @Test
    void testAddSameObserverMultipleTimes() {
        // Arrange
        perspective.addObserver(mockObserverPerspective);
        perspective.addObserver(mockObserverPerspective);
        
        mockObserverPerspective.reset();
        
        // Act
        perspective.setScale(2.0);
        int updateCount = mockObserverPerspective.getUpdateCount();

        // Assert
        assertEquals(2, updateCount,
            "Same observer added twice should receive 2 notifications");
    }

    @Test
    void testNotifyObserversWithNoObserversRegistered() {
        // Arrange

        // Act
        Executable notifyWithNoObservers = () -> perspective.setScale(2.0);

        // Assert
        assertDoesNotThrow(notifyWithNoObservers,
            "Notifying with no observers should not throw");
    }

    @Test
    void testObserverStillNotifiedAfterRemovingAnotherObserver() {
        // Arrange
        MockObserver observer2 = new MockObserver();
        
        perspective.addObserver(mockObserverPerspective);
        perspective.addObserver(observer2);
        
        perspective.removeObserver(observer2);
        
        mockObserverPerspective.reset();

        // Act
        perspective.setScale(2.0);
        int updateCount = mockObserverPerspective.getUpdateCount();

        // Assert
        assertEquals(1, updateCount,
            "First observer should still be notified after removing second");
    }

    @Test
    void testPerspectiveNotifiesObserverWhenRestored() {
        // Arrange
        perspective.addObserver(mockObserverPerspective);
        
        // Créer un snapshot
        Perspective.Snapshot snapshot = perspective.createSnapshot();
        
        // Modifier la perspective
        perspective.setScale(2.0);
        perspective.setTranslation(new Point(10, 10));
        mockObserverPerspective.reset();

        // Act
        perspective.restore(snapshot);
        int updateCount = mockObserverPerspective.getUpdateCount();

        // Assert
        assertEquals(1, updateCount,
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
