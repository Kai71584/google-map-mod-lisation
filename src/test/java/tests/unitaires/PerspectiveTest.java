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
        assertEquals(1.0, perspective.getScale(), "Default scale should be 1.0");
    }

    @Test
    public void testSetScale() {
        perspective.setScale(2.5);
        assertEquals(2.5, perspective.getScale(), "Scale should be 2.5 after setScale");
    }

    @Test
    public void testSetScaleToZeroPointFive() {
        perspective.setScale(0.5);
        assertEquals(0.5, perspective.getScale(), "Scale should be 0.5 (zoom out)");
    }

    @Test
    public void testSetScaleToLargeValue() {
        perspective.setScale(10.0);
        assertEquals(10.0, perspective.getScale(), "Scale should be 10.0 (large zoom)");
    }

    // ========== TESTS TRANSLATION ==========

    @Test
    public void testDefaultTranslationIsOrigin() {
        Point translation = perspective.getTranslation();
        assertEquals(0, translation.x, "Default translation X should be 0");
        assertEquals(0, translation.y, "Default translation Y should be 0");
    }

    @Test
    public void testSetTranslation() {
        perspective.setTranslation(new Point(100, 50));
        Point translation = perspective.getTranslation();
        assertEquals(100, translation.x, "Translation X should be 100");
        assertEquals(50, translation.y, "Translation Y should be 50");
    }

    @Test
    public void testSetTranslationNegative() {
        perspective.setTranslation(new Point(-50, -100));
        Point translation = perspective.getTranslation();
        assertEquals(-50, translation.x, "Translation X should be -50");
        assertEquals(-100, translation.y, "Translation Y should be -100");
    }

    @Test
    public void testTranslationIsACopy() {
        perspective.setTranslation(new Point(100, 50));
        Point translation = perspective.getTranslation();
        translation.x = 999; // Modifier la copie retournée
        
        Point translation2 = perspective.getTranslation();
        assertEquals(100, translation2.x, "Translation should not change (defensive copy)");
    }

    // ========== TESTS NAME ==========

    @Test
    public void testGetName() {
        assertEquals("Test Perspective", perspective.getName(), "Name should match");
    }

    @Test
    public void testSetName() {
        perspective.setName("New Name");
        assertEquals("New Name", perspective.getName(), "Name should be updated");
    }

    // ========== TESTS SNAPSHOT (MEMENTO) ==========

    @Test
    public void testCreateSnapshot() {
        perspective.setScale(2.0);
        perspective.setTranslation(new Point(100, 50));
        
        Snapshot snapshot = perspective.createSnapshot();
        assertNotNull(snapshot, "Snapshot should not be null");
    }

    @Test
    public void testSnapshotCapturesScale() {
        perspective.setScale(1.5);
        Snapshot snapshot = perspective.createSnapshot();
        
        // Change the perspective
        perspective.setScale(3.0);
        
        // Snapshot should still contain the old scale
        assertEquals(1.5, snapshot.getScale(), "Snapshot should capture scale 1.5");
    }

    @Test
    public void testSnapshotCapturesTranslation() {
        perspective.setTranslation(new Point(50, 75));
        Snapshot snapshot = perspective.createSnapshot();
        
        // Change the perspective
        perspective.setTranslation(new Point(200, 300));
        
        // Snapshot should still contain the old translation
        assertEquals(50, snapshot.getTranslation().x, "Snapshot should capture translation X=50");
        assertEquals(75, snapshot.getTranslation().y, "Snapshot should capture translation Y=75");
    }

    @Test
    public void testRestoreFromSnapshot() {
        // Create initial state
        perspective.setScale(2.0);
        perspective.setTranslation(new Point(100, 50));
        Snapshot snapshot = perspective.createSnapshot();
        
        // Change the perspective
        perspective.setScale(3.0);
        perspective.setTranslation(new Point(200, 100));
        
        // Restore from snapshot
        perspective.restore(snapshot);
        
        assertEquals(2.0, perspective.getScale(), "Scale should be restored to 2.0");
        Point translation = perspective.getTranslation();
        assertEquals(100, translation.x, "Translation X should be restored to 100");
        assertEquals(50, translation.y, "Translation Y should be restored to 50");
    }

    @Test
    public void testMultipleSnapshots() {
        // Snapshot 1
        perspective.setScale(1.5);
        Snapshot snapshot1 = perspective.createSnapshot();
        
        // Snapshot 2
        perspective.setScale(2.5);
        Snapshot snapshot2 = perspective.createSnapshot();
        
        // Restore to snapshot 1
        perspective.restore(snapshot1);
        assertEquals(1.5, perspective.getScale(), "Should restore to scale 1.5");
        
        // Restore to snapshot 2
        perspective.restore(snapshot2);
        assertEquals(2.5, perspective.getScale(), "Should restore to scale 2.5");
    }

    @Test
    public void testToString() {
        perspective.setScale(2.0);
        perspective.setTranslation(new Point(100, 50));
        String str = perspective.toString();
        
        assertTrue(str.contains("Test Perspective"), "toString should contain name");
        assertTrue(str.contains("100"), "toString should contain translation X");
        assertTrue(str.contains("50"), "toString should contain translation Y");
        assertTrue(str.contains("2"), "toString should contain scale");
    }
}
