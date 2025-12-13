package tests.unitaires;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Perspective;
import model.Perspective.Snapshot;

/**
 * Tests unitaires pour la classe Perspective.
 * Vérifie le zoom, la translation, le snapshot (Memento) et les notifications.
 */
public class PerspectiveTest {

    private Perspective perspective;

    @BeforeEach
    public void setUp() {
        perspective = new Perspective("Test Perspective");
    }

    // ========== TESTS SCALE (ZOOM) ==========

    @Test
    public void testDefaultScaleIsOne() {
        // Arrange

        // Act
        double scale = perspective.getScale();

        // Assert
        assertEquals(1.0, scale, "Default scale should be 1.0");
    }

    @Test
    public void testSetScale() {
        // Arrange
        perspective.setScale(2.5);

        // Act
        double scale = perspective.getScale();

        // Assert
        assertEquals(2.5, scale, "Scale should be 2.5 after setScale");
    }

    @Test
    public void testSetScaleToZeroPointFive() {
        // Arrange
        perspective.setScale(0.5);

        // Act
        double scale = perspective.getScale();

        // Assert
        assertEquals(0.5, scale, "Scale should be 0.5 (zoom out)");
    }

    @Test
    public void testSetScaleToLargeValue() {
        // Arrange
        perspective.setScale(10.0);

        // Act
        double scale = perspective.getScale();

        // Assert
        assertEquals(10.0, scale, "Scale should be 10.0 (large zoom)");
    }

    // ========== TESTS TRANSLATION ==========

    @Test
    public void testDefaultTranslationIsOrigin() {
        // Arrange

        // Act
        Point translation = perspective.getTranslation();

        // Assert
        assertEquals(0, translation.x, "Default translation X should be 0");
        assertEquals(0, translation.y, "Default translation Y should be 0");
    }

    @Test
    public void testSetTranslation() {
        // Arrange
        perspective.setTranslation(new Point(100, 50));

        // Act
        Point translation = perspective.getTranslation();

        // Assert
        assertEquals(100, translation.x, "Translation X should be 100");
        assertEquals(50, translation.y, "Translation Y should be 50");
    }

    @Test
    public void testSetTranslationNegative() {
        // Arrange
        perspective.setTranslation(new Point(-50, -100));

        // Act
        Point translation = perspective.getTranslation();

        // Assert
        assertEquals(-50, translation.x, "Translation X should be -50");
        assertEquals(-100, translation.y, "Translation Y should be -100");
    }

    @Test
    public void testTranslationIsACopy() {
        // Arrange
        perspective.setTranslation(new Point(100, 50));

        // Act
        Point translation = perspective.getTranslation();
        translation.x = 999;
        Point translation2 = perspective.getTranslation();

        // Assert
        assertEquals(100, translation2.x, "Translation should not change (defensive copy)");
    }

    // ========== TESTS NAME ==========

    @Test
    public void testGetName() {
        // Arrange

        // Act
        String name = perspective.getName();

        // Assert
        assertEquals("Test Perspective", name, "Name should match");
    }

    @Test
    public void testSetName() {
        // Arrange
        perspective.setName("New Name");

        // Act
        String name = perspective.getName();

        // Assert
        assertEquals("New Name", name, "Name should be updated");
    }

    // ========== TESTS SNAPSHOT (MEMENTO) ==========

    @Test
    public void testCreateSnapshot() {
        // Arrange
        perspective.setScale(2.0);
        perspective.setTranslation(new Point(100, 50));

        // Act
        Snapshot snapshot = perspective.createSnapshot();

        // Assert
        assertNotNull(snapshot, "Snapshot should not be null");
    }

    @Test
    public void testSnapshotCapturesScale() {
        // Arrange
        perspective.setScale(1.5);
        Snapshot snapshot = perspective.createSnapshot();
        perspective.setScale(3.0);

        // Act
        double capturedScale = snapshot.getScale();

        // Assert
        assertEquals(1.5, capturedScale, "Snapshot should capture scale 1.5");
    }

    @Test
    public void testSnapshotCapturesTranslation() {
        // Arrange
        perspective.setTranslation(new Point(50, 75));
        Snapshot snapshot = perspective.createSnapshot();
        perspective.setTranslation(new Point(200, 300));

        // Act
        Point capturedTranslation = snapshot.getTranslation();

        // Assert
        assertEquals(50, capturedTranslation.x, "Snapshot should capture translation X=50");
        assertEquals(75, capturedTranslation.y, "Snapshot should capture translation Y=75");
    }

    @Test
    public void testRestoreFromSnapshot() {
        // Arrange
        perspective.setScale(2.0);
        perspective.setTranslation(new Point(100, 50));
        Snapshot snapshot = perspective.createSnapshot();
        perspective.setScale(3.0);
        perspective.setTranslation(new Point(200, 100));

        // Act
        perspective.restore(snapshot);
        double restoredScale = perspective.getScale();
        Point translation = perspective.getTranslation();

        // Assert
        assertEquals(2.0, restoredScale, "Scale should be restored to 2.0");
        assertEquals(100, translation.x, "Translation X should be restored to 100");
        assertEquals(50, translation.y, "Translation Y should be restored to 50");
    }

    @Test
    public void testMultipleSnapshots() {
        // Arrange
        perspective.setScale(1.5);
        Snapshot snapshot1 = perspective.createSnapshot();
        perspective.setScale(2.5);
        Snapshot snapshot2 = perspective.createSnapshot();

        // Act
        perspective.restore(snapshot1);
        double scaleAfterFirstRestore = perspective.getScale();
        perspective.restore(snapshot2);
        double scaleAfterSecondRestore = perspective.getScale();

        // Assert
        assertEquals(1.5, scaleAfterFirstRestore, "Should restore to scale 1.5");
        assertEquals(2.5, scaleAfterSecondRestore, "Should restore to scale 2.5");
    }

    @Test
    public void testToString() {
        // Arrange
        perspective.setScale(2.0);
        perspective.setTranslation(new Point(100, 50));

        // Act
        String str = perspective.toString();

        // Assert
        assertTrue(str.contains("Test Perspective"), "toString should contain name");
        assertTrue(str.contains("100"), "toString should contain translation X");
        assertTrue(str.contains("50"), "toString should contain translation Y");
        assertTrue(str.contains("2"), "toString should contain scale");
    }
}
