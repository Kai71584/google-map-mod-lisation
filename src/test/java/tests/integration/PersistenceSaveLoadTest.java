package tests.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import model.ImageModel;
import model.ImageSource;
import model.Perspective;
import persistence.JsonPersistenceManager;
import persistence.PersistenceManager;

/**
 * Tests d'intégration pour la persistance (Save/Load).
 *
 * Objectif :
 *   Vérifier que Save → Load restaure exactement l'état du modèle
 *   avec plusieurs perspectives nommées, leurs zooms et translations.
 *
 * Patterns validés :
 *   - Persistence Layer (abstraction via PersistenceManager)
 *   - JSON Serialization (Jackson)
 *   - State Restoration (exact match après round-trip)
 *
 * Architecture testée :
 *   Modèle (Perspective) → Save → JSON → Load → Modèle (Perspective)
 *   ↓
 *   État initial == État restauré
 */
public class PersistenceSaveLoadTest {

    @TempDir
    private File tempDir;

    private ImageModel imageModel;
    private PersistenceManager persistence;
    private String persistenceFilePath;

    @BeforeEach
    void setUp() {
        // Créer un chemin de fichier temporaire
        persistenceFilePath = new File(tempDir, "test_perspectives.json").getAbsolutePath();

        // Créer le modèle avec ImageSource mock
        imageModel = new ImageModel(new MockImageSource());

        // Créer le gestionnaire de persistance
        persistence = new JsonPersistenceManager(persistenceFilePath);
    }

    // ========== TESTS : SAUVEGARDE ET CHARGEMENT SIMPLE ==========

    @Test
    void testSaveAndLoadSinglePerspective() {
        // Créer une perspective avec des paramètres spécifiques
        Perspective original = new Perspective("Vue Simple");
        original.setScale(2.5);
        original.setTranslation(new Point(100, 50));

        // Sauvegarder
        persistence.save(original);

        // Créer un nouveau modèle et charger
        ImageModel newModel = new ImageModel(new MockImageSource());
        persistence.loadAll(newModel);

        // Vérifications
        assertEquals(1, newModel.getPerspectives().size(),
            "Should have loaded 1 perspective");

        Perspective loaded = newModel.getPerspectives().get(0);
        assertEquals(original.getName(), loaded.getName(),
            "Name should match");
        assertEquals(original.getScale(), loaded.getScale(),
            "Scale should match exactly");
        assertEquals(original.getTranslation(), loaded.getTranslation(),
            "Translation should match exactly");
    }

    @Test
    void testSaveAndLoadMultiplePerspectives() {
        // Créer plusieurs perspectives
        Perspective p1 = new Perspective("Vue Zoomed");
        p1.setScale(3.0);
        p1.setTranslation(new Point(150, 200));

        Perspective p2 = new Perspective("Vue Normale");
        p2.setScale(1.0);
        p2.setTranslation(new Point(0, 0));

        Perspective p3 = new Perspective("Vue Dégrossie");
        p3.setScale(0.5);
        p3.setTranslation(new Point(-100, -50));

        // Sauvegarder toutes les perspectives
        persistence.save(p1);
        persistence.save(p2);
        persistence.save(p3);

        // Créer un nouveau modèle et charger
        ImageModel newModel = new ImageModel(new MockImageSource());
        persistence.loadAll(newModel);

        // Vérifications
        assertEquals(3, newModel.getPerspectives().size(),
            "Should have loaded 3 perspectives");

        Perspective loaded1 = newModel.getPerspectives().get(0);
        assertEquals("Vue Zoomed", loaded1.getName());
        assertEquals(3.0, loaded1.getScale());
        assertEquals(new Point(150, 200), loaded1.getTranslation());

        Perspective loaded2 = newModel.getPerspectives().get(1);
        assertEquals("Vue Normale", loaded2.getName());
        assertEquals(1.0, loaded2.getScale());
        assertEquals(new Point(0, 0), loaded2.getTranslation());

        Perspective loaded3 = newModel.getPerspectives().get(2);
        assertEquals("Vue Dégrossie", loaded3.getName());
        assertEquals(0.5, loaded3.getScale());
        assertEquals(new Point(-100, -50), loaded3.getTranslation());
    }

    // ========== TESTS : NOMS SPÉCIAUX ET VALEURS LIMITES ==========

    @Test
    void testSaveAndLoadPerspectiveWithSpecialCharacterName() {
        Perspective original = new Perspective("Vue ñ éàü 日本語 🎨");
        original.setScale(1.5);
        original.setTranslation(new Point(25, 35));

        persistence.save(original);

        ImageModel newModel = new ImageModel(new MockImageSource());
        persistence.loadAll(newModel);

        Perspective loaded = newModel.getPerspectives().get(0);
        assertEquals("Vue ñ éàü 日本語 🎨", loaded.getName(),
            "Special characters in name should be preserved");
    }

    @Test
    void testSaveAndLoadPerspectiveWithZeroZoom() {
        Perspective original = new Perspective("Zoom Zéro");
        original.setScale(0.0);
        original.setTranslation(new Point(10, 20));

        persistence.save(original);

        ImageModel newModel = new ImageModel(new MockImageSource());
        persistence.loadAll(newModel);

        Perspective loaded = newModel.getPerspectives().get(0);
        assertEquals(0.0, loaded.getScale(),
            "Zero zoom should be preserved");
    }

    @Test
    void testSaveAndLoadPerspectiveWithNegativeTranslation() {
        Perspective original = new Perspective("Translation Négative");
        original.setScale(1.0);
        original.setTranslation(new Point(-500, -300));

        persistence.save(original);

        ImageModel newModel = new ImageModel(new MockImageSource());
        persistence.loadAll(newModel);

        Perspective loaded = newModel.getPerspectives().get(0);
        assertEquals(-500, loaded.getTranslation().x);
        assertEquals(-300, loaded.getTranslation().y);
    }

    @Test
    void testSaveAndLoadPerspectiveWithLargeZoomFactor() {
        Perspective original = new Perspective("Zoom Énorme");
        original.setScale(1000.0);
        original.setTranslation(new Point(1000, 1000));

        persistence.save(original);

        ImageModel newModel = new ImageModel(new MockImageSource());
        persistence.loadAll(newModel);

        Perspective loaded = newModel.getPerspectives().get(0);
        assertEquals(1000.0, loaded.getScale());
    }

    @Test
    void testSaveAndLoadPerspectiveWithSmallZoomFactor() {
        Perspective original = new Perspective("Zoom Minuscule");
        original.setScale(0.001);
        original.setTranslation(new Point(1, 1));

        persistence.save(original);

        ImageModel newModel = new ImageModel(new MockImageSource());
        persistence.loadAll(newModel);

        Perspective loaded = newModel.getPerspectives().get(0);
        assertEquals(0.001, loaded.getScale());
    }

    // ========== TESTS : ORDRE ET INTÉGRITÉ ==========

    @Test
    void testSaveAndLoadPreservesOrderOfPerspectives() {
        String[] names = {"Premier", "Deuxième", "Troisième", "Quatrième", "Cinquième"};

        for (String name : names) {
            Perspective p = new Perspective(name);
            p.setScale(Math.random() * 10);
            p.setTranslation(new Point((int) (Math.random() * 1000), 
                                       (int) (Math.random() * 1000)));
            persistence.save(p);
        }

        ImageModel newModel = new ImageModel(new MockImageSource());
        persistence.loadAll(newModel);

        assertEquals(5, newModel.getPerspectives().size());

        for (int i = 0; i < names.length; i++) {
            assertEquals(names[i], newModel.getPerspectives().get(i).getName(),
                "Order of perspectives should be preserved");
        }
    }

    @Test
    void testLoadFromNonExistentFileReturnEmptyList() {
        // Fichier n'existe pas encore
        ImageModel newModel = new ImageModel(new MockImageSource());
        persistence.loadAll(newModel);

        assertEquals(0, newModel.getPerspectives().size(),
            "Loading from non-existent file should return empty");
    }

    @Test
    void testMultipleSaveLoadCycles() {
        // Cycle 1
        Perspective p1 = new Perspective("Cycle 1");
        p1.setScale(2.0);
        persistence.save(p1);

        ImageModel model1 = new ImageModel(new MockImageSource());
        persistence.loadAll(model1);
        assertEquals(1, model1.getPerspectives().size());
        assertEquals(2.0, model1.getPerspectives().get(0).getScale());

        // Cycle 2 - ajouter une deuxième perspective
        Perspective p2 = new Perspective("Cycle 2");
        p2.setScale(3.0);
        persistence.save(p2);

        ImageModel model2 = new ImageModel(new MockImageSource());
        persistence.loadAll(model2);
        assertEquals(2, model2.getPerspectives().size());
        assertEquals("Cycle 1", model2.getPerspectives().get(0).getName());
        assertEquals("Cycle 2", model2.getPerspectives().get(1).getName());
    }

    // ========== TESTS : ÉTAT EXACT APRÈS ROUND-TRIP ==========

    @Test
    void testStateExactlyPreservedAfterRoundTrip() {
        // Créer un état complexe
        Perspective[] originals = new Perspective[5];
        double[] scales = {0.5, 1.0, 1.5, 2.0, 3.5};
        Point[] translations = {
            new Point(0, 0),
            new Point(100, 200),
            new Point(-150, 50),
            new Point(500, -300),
            new Point(-1000, -1000)
        };
        String[] names = {"A", "B", "C", "D", "E"};

        for (int i = 0; i < 5; i++) {
            originals[i] = new Perspective(names[i]);
            originals[i].setScale(scales[i]);
            originals[i].setTranslation(translations[i]);
            persistence.save(originals[i]);
        }

        // Charger dans un nouveau modèle
        ImageModel newModel = new ImageModel(new MockImageSource());
        persistence.loadAll(newModel);

        // Vérifier que tout est identique
        assertEquals(5, newModel.getPerspectives().size());

        for (int i = 0; i < 5; i++) {
            Perspective loaded = newModel.getPerspectives().get(i);
            assertEquals(names[i], loaded.getName(),
                "Name[" + i + "] mismatch");
            assertEquals(scales[i], loaded.getScale(), 0.0001,
                "Scale[" + i + "] mismatch");
            assertEquals(translations[i], loaded.getTranslation(),
                "Translation[" + i + "] mismatch");
        }
    }

    @Test
    void testPersistenceFileCreatedAfterSave() throws Exception {
        Perspective p = new Perspective("Test File Creation");
        p.setScale(1.5);
        p.setTranslation(new Point(50, 50));

        assertFalse(Files.exists(Paths.get(persistenceFilePath)),
            "File should not exist before save");

        persistence.save(p);

        assertTrue(Files.exists(Paths.get(persistenceFilePath)),
            "File should be created after save");
    }

    @Test
    void testPersistenceFileContainsValidJson() throws Exception {
        Perspective p1 = new Perspective("JSON Test 1");
        p1.setScale(2.0);

        Perspective p2 = new Perspective("JSON Test 2");
        p2.setScale(0.5);

        persistence.save(p1);
        persistence.save(p2);

        String fileContent = new String(Files.readAllBytes(Paths.get(persistenceFilePath)));

        assertTrue(fileContent.contains("\"nom\""),
            "JSON should contain 'nom' field");
        assertTrue(fileContent.contains("\"zoom\""),
            "JSON should contain 'zoom' field");
        assertTrue(fileContent.contains("\"translationX\""),
            "JSON should contain 'translationX' field");
        assertTrue(fileContent.contains("\"translationY\""),
            "JSON should contain 'translationY' field");
        assertTrue(fileContent.startsWith("["),
            "JSON should be an array");
        assertTrue(fileContent.endsWith("]"),
            "JSON should be a complete array");
    }

    // ========== TESTS : ISOLATION ET DÉCORUPLAGE ==========

    @Test
    void testDifferentModelsCanLoadSameData() {
        Perspective p = new Perspective("Shared Data");
        p.setScale(2.5);
        p.setTranslation(new Point(100, 100));
        persistence.save(p);

        // Charger dans deux modèles différents
        ImageModel model1 = new ImageModel(new MockImageSource());
        ImageModel model2 = new ImageModel(new MockImageSource());

        persistence.loadAll(model1);
        persistence.loadAll(model2);

        // Les deux modèles doivent avoir les mêmes données
        assertEquals(1, model1.getPerspectives().size());
        assertEquals(1, model2.getPerspectives().size());

        Perspective loaded1 = model1.getPerspectives().get(0);
        Perspective loaded2 = model2.getPerspectives().get(0);

        assertEquals(loaded1.getName(), loaded2.getName());
        assertEquals(loaded1.getScale(), loaded2.getScale());
        assertEquals(loaded1.getTranslation(), loaded2.getTranslation());
    }

    @Test
    void testModifyingLoadedPerspectiveDoesNotAffectFile() {
        Perspective original = new Perspective("Original");
        original.setScale(1.0);
        persistence.save(original);

        // Charger
        ImageModel model = new ImageModel(new MockImageSource());
        persistence.loadAll(model);

        // Modifier la perspective chargée
        Perspective loaded = model.getPerspectives().get(0);
        loaded.setScale(5.0);
        loaded.setTranslation(new Point(999, 999));

        // Charger à nouveau depuis le fichier
        ImageModel model2 = new ImageModel(new MockImageSource());
        persistence.loadAll(model2);

        // Le fichier devrait toujours contenir les valeurs originales
        Perspective reloaded = model2.getPerspectives().get(0);
        assertEquals(1.0, reloaded.getScale(),
            "File should not be affected by in-memory modifications");
        assertNotEquals(new Point(999, 999), reloaded.getTranslation());
    }

    // ========== MOCK IMPLEMENTATIONS ==========

    /**
     * Mock implementation of ImageSource for testing.
     */
    private static class MockImageSource implements ImageSource {
        @Override
        public java.awt.image.BufferedImage image() {
            return null;
        }
    }
}
