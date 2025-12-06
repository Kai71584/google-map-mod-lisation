package tests.e2e;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.io.TempDir;

import model.ImageModel;
import model.ImageSource;
import model.FileImageSource;
import model.Perspective;
import persistence.JsonPersistenceManager;
import persistence.PersistenceManager;

/**
 * Tests E2E : Persistance et redémarrage
 * 
 * Parcours C : Redémarrage + Load reproduit l'affichage attendu
 * 
 * @author Alex
 */
public class E2EPersistenceRestartTest {

    @TempDir
    Path tempDir;

    private ImageSource imageSource;
    private PersistenceManager persistence;

    @BeforeEach
    void setUp() throws IOException {
        // Configuration mode headless
        System.setProperty("java.awt.headless", "true");
        
        // Création de la source d'image
        imageSource = new FileImageSource("w2.jpg");
    }

    /**
     * Test : Cycle Save → Load → Vérifier identité
     */
    @Test
    void testSaveLoadRoundTrip() throws IOException {
        // Arrange : Créer une perspective avec des paramètres spécifiques
        ImageModel model1 = new ImageModel(imageSource);
        Perspective original = new Perspective("Test Perspective");
        original.setScale(2.5);
        original.setTranslation(new Point(100, 200));
        model1.addPerspective(original);
        
        // Créer le gestionnaire de persistance avec un fichier temporaire
        File tempFile = tempDir.resolve("test-persist.json").toFile();
        persistence = new JsonPersistenceManager(tempFile.getAbsolutePath());
        
        // Act : Sauvegarder
        persistence.save(original);
        
        // Simuler un redémarrage : créer un nouveau modèle et charger
        ImageModel model2 = new ImageModel(imageSource);
        List<Perspective> loaded = persistence.loadAll(model2);
        
        // Assert : Vérifier qu'une perspective a été chargée
        assertEquals(1, loaded.size(), "Une perspective doit être chargée");
        
        Perspective loadedPerspective = loaded.get(0);
        assertEquals("Test Perspective", loadedPerspective.getName());
        assertEquals(2.5, loadedPerspective.getScale(), 0.001, 
            "Le zoom doit être identique");
        assertEquals(new Point(100, 200), loadedPerspective.getTranslation(), 
            "La translation doit être identique");
    }

    /**
     * Test : Sauvegarder et charger plusieurs perspectives
     */
    @Test
    void testMultiplePerspectivesSaveLoad() throws IOException {
        // Arrange : Créer plusieurs perspectives
        ImageModel model1 = new ImageModel(imageSource);
        
        Perspective p1 = new Perspective("Perspective 1");
        p1.setScale(1.5);
        p1.setTranslation(new Point(10, 20));
        
        Perspective p2 = new Perspective("Perspective 2");
        p2.setScale(2.0);
        p2.setTranslation(new Point(30, 40));
        
        Perspective p3 = new Perspective("Perspective 3");
        p3.setScale(3.0);
        p3.setTranslation(new Point(50, 60));
        
        model1.addPerspective(p1);
        model1.addPerspective(p2);
        model1.addPerspective(p3);
        
        // Créer le gestionnaire de persistance
        File tempFile = tempDir.resolve("test-multiple.json").toFile();
        persistence = new JsonPersistenceManager(tempFile.getAbsolutePath());
        
        // Act : Sauvegarder toutes les perspectives
        persistence.save(p1);
        persistence.save(p2);
        persistence.save(p3);
        
        // Simuler redémarrage : charger
        ImageModel model2 = new ImageModel(imageSource);
        List<Perspective> loaded = persistence.loadAll(model2);
        
        // Assert : Vérifier que toutes les perspectives sont chargées
        assertEquals(3, loaded.size(), "Trois perspectives doivent être chargées");
        
        // Vérifier chaque perspective
        Perspective loadedP1 = findByName(loaded, "Perspective 1");
        assertNotNull(loadedP1);
        assertEquals(1.5, loadedP1.getScale(), 0.001);
        assertEquals(new Point(10, 20), loadedP1.getTranslation());
        
        Perspective loadedP2 = findByName(loaded, "Perspective 2");
        assertNotNull(loadedP2);
        assertEquals(2.0, loadedP2.getScale(), 0.001);
        assertEquals(new Point(30, 40), loadedP2.getTranslation());
        
        Perspective loadedP3 = findByName(loaded, "Perspective 3");
        assertNotNull(loadedP3);
        assertEquals(3.0, loadedP3.getScale(), 0.001);
        assertEquals(new Point(50, 60), loadedP3.getTranslation());
    }

    /**
     * Test : Simuler un redémarrage complet de l'application
     */
    @Test
    void testLoadAfterRestartReproducesState() throws IOException {
        // Arrange : État initial avec plusieurs perspectives
        ImageModel model1 = new ImageModel(imageSource);
        
        Perspective main = new Perspective("Vue principale");
        main.setScale(1.8);
        main.setTranslation(new Point(75, 125));
        
        Perspective user1 = new Perspective("William");
        user1.setScale(1.2);
        user1.setTranslation(new Point(134, 154));
        
        model1.addPerspective(main);
        model1.addPerspective(user1);
        
        File tempFile = tempDir.resolve("test-restart.json").toFile();
        persistence = new JsonPersistenceManager(tempFile.getAbsolutePath());
        
        // Sauvegarder
        persistence.save(main);
        persistence.save(user1);
        
        // Act : Simuler redémarrage (nouvelle instance)
        ImageModel model2 = new ImageModel(imageSource);
        PersistenceManager newPersistence = new JsonPersistenceManager(tempFile.getAbsolutePath());
        List<Perspective> loaded = newPersistence.loadAll(model2);
        
        // Assert : Vérifier que l'état est reproduit
        assertEquals(2, loaded.size());
        
        Perspective loadedMain = findByName(loaded, "Vue principale");
        assertNotNull(loadedMain);
        assertEquals(1.8, loadedMain.getScale(), 0.001);
        assertEquals(new Point(75, 125), loadedMain.getTranslation());
        
        Perspective loadedUser1 = findByName(loaded, "William");
        assertNotNull(loadedUser1);
        assertEquals(1.2, loadedUser1.getScale(), 0.001);
        assertEquals(new Point(134, 154), loadedUser1.getTranslation());
    }

    /**
     * Test : Sauvegarder et charger avec caractères spéciaux dans le nom
     */
    @Test
    void testSaveLoadWithSpecialCharacters() throws IOException {
        // Arrange
        ImageModel model1 = new ImageModel(imageSource);
        Perspective p = new Perspective("Test & Émojis 🎨");
        p.setScale(2.0);
        p.setTranslation(new Point(100, 100));
        model1.addPerspective(p);
        
        File tempFile = tempDir.resolve("test-special.json").toFile();
        persistence = new JsonPersistenceManager(tempFile.getAbsolutePath());
        
        // Act
        persistence.save(p);
        ImageModel model2 = new ImageModel(imageSource);
        List<Perspective> loaded = persistence.loadAll(model2);
        
        // Assert
        assertEquals(1, loaded.size());
        Perspective loadedP = loaded.get(0);
        assertEquals("Test & Émojis 🎨", loadedP.getName());
        assertEquals(2.0, loadedP.getScale(), 0.001);
    }

    /**
     * Test : Valeurs limites (zoom très grand/petit)
     */
    @Test
    void testSaveLoadWithExtremeValues() throws IOException {
        // Arrange
        ImageModel model1 = new ImageModel(imageSource);
        
        Perspective p1 = new Perspective("Very Small");
        p1.setScale(0.1);
        p1.setTranslation(new Point(-1000, -1000));
        
        Perspective p2 = new Perspective("Very Large");
        p2.setScale(10.0);
        p2.setTranslation(new Point(1000, 1000));
        
        model1.addPerspective(p1);
        model1.addPerspective(p2);
        
        File tempFile = tempDir.resolve("test-extreme.json").toFile();
        persistence = new JsonPersistenceManager(tempFile.getAbsolutePath());
        
        // Act
        persistence.save(p1);
        persistence.save(p2);
        ImageModel model2 = new ImageModel(imageSource);
        List<Perspective> loaded = persistence.loadAll(model2);
        
        // Assert
        assertEquals(2, loaded.size());
        
        Perspective loadedP1 = findByName(loaded, "Very Small");
        assertEquals(0.1, loadedP1.getScale(), 0.001);
        assertEquals(new Point(-1000, -1000), loadedP1.getTranslation());
        
        Perspective loadedP2 = findByName(loaded, "Very Large");
        assertEquals(10.0, loadedP2.getScale(), 0.001);
        assertEquals(new Point(1000, 1000), loadedP2.getTranslation());
    }

    /**
     * Test : Charger un fichier vide doit gérer gracieusement
     */
    @Test
    void testLoadEmptyFileHandlesGracefully() throws IOException {
        // Arrange : Créer un fichier vide
        File emptyFile = tempDir.resolve("empty.json").toFile();
        emptyFile.createNewFile();
        
        PersistenceManager persistence = new JsonPersistenceManager(emptyFile.getAbsolutePath());
        ImageModel model = new ImageModel(imageSource);
        
        // Act : Charger depuis un fichier vide
        List<Perspective> loaded = persistence.loadAll(model);
        
        // Assert : Ne doit pas planter, retourne une liste vide
        assertNotNull(loaded);
        assertEquals(0, loaded.size(), "Un fichier vide doit retourner une liste vide");
    }

    // Méthode utilitaire pour trouver une perspective par nom
    private Perspective findByName(List<Perspective> perspectives, String name) {
        return perspectives.stream()
            .filter(p -> p.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
}

