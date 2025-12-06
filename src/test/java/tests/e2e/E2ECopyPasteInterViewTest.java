package tests.e2e;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;

import model.ImageModel;
import model.ImageSource;
import model.FileImageSource;
import model.Perspective;
import command.CommandBus;
import clipboard.ClipboardMediator;
import clipboard.CopyBoth;
import clipboard.CopyScaleOnly;
import clipboard.CopyTranslationOnly;
import controller.CopyPasteController;
import view.AbstractImageView;
import view.ImageView;

import java.io.IOException;

/**
 * Tests E2E : Copy/Paste entre perspectives différentes
 * 
 * Parcours B : Copier sur vue A / Coller sur vue B
 * 
 * @author Alex
 */
public class E2ECopyPasteInterViewTest {

    private ImageModel model;
    private Perspective perspectiveA;
    private Perspective perspectiveB;
    private CommandBus bus;
    private ClipboardMediator clipboard;
    private CopyPasteController copyPasteCtrlA;
    private CopyPasteController copyPasteCtrlB;
    private AbstractImageView viewA;
    private AbstractImageView viewB;
    private ImageSource imageSource;

    @BeforeEach
    void setUp() throws IOException {
        // Configuration mode headless
        System.setProperty("java.awt.headless", "true");
        
        // Création de la source d'image
        imageSource = new FileImageSource("w2.jpg");
        model = new ImageModel(imageSource);
        
        // Création de deux perspectives avec états différents
        perspectiveA = new Perspective("Perspective A");
        perspectiveB = new Perspective("Perspective B");
        
        // Configuration initiale de A
        perspectiveA.setScale(2.0);
        perspectiveA.setTranslation(new Point(100, 50));
        
        // Configuration initiale de B
        perspectiveB.setScale(1.0);
        perspectiveB.setTranslation(new Point(0, 0));
        
        model.addPerspective(perspectiveA);
        model.addPerspective(perspectiveB);
        
        // Création des vues
        viewA = new ImageView(model, perspectiveA);
        viewB = new ImageView(model, perspectiveB);
        
        // Création du bus et du clipboard
        bus = new CommandBus();
        clipboard = new ClipboardMediator();
        
        // Création des contrôleurs copy/paste
        copyPasteCtrlA = new CopyPasteController(viewA, bus, clipboard);
        copyPasteCtrlB = new CopyPasteController(viewB, bus, clipboard);
    }

    @AfterEach
    void tearDown() {
        // Nettoyage si nécessaire
    }

    /**
     * Test : Copier A → Coller sur B → Vérifier que B = A
     */
    @Test
    void testCopyPasteBetweenDifferentPerspectives() {
        // Arrange : Capturer l'état initial de A
        double scaleA = perspectiveA.getScale();
        Point translationA = perspectiveA.getTranslation();
        
        // Act : Copier A puis coller sur B
        copyPasteCtrlA.setStrategy(new CopyBoth());
        copyPasteCtrlA.handleCopy();
        copyPasteCtrlB.handlePaste();
        
        // Assert : B doit avoir exactement les mêmes paramètres que A
        assertEquals(scaleA, perspectiveB.getScale(), 0.001, 
            "B doit avoir le même zoom que A");
        assertEquals(translationA, perspectiveB.getTranslation(), 
            "B doit avoir la même translation que A");
        
        // Vérifier que A n'a pas changé
        assertEquals(scaleA, perspectiveA.getScale(), 0.001, 
            "A ne doit pas être modifié");
        assertEquals(translationA, perspectiveA.getTranslation(), 
            "A ne doit pas être modifié");
    }

    /**
     * Test : Copy/Paste avec stratégie CopyBoth (zoom + translation)
     */
    @Test
    void testCopyPasteWithCopyBothStrategy() {
        // Arrange
        perspectiveA.setScale(2.5);
        perspectiveA.setTranslation(new Point(150, 75));
        
        // Act
        copyPasteCtrlA.setStrategy(new CopyBoth());
        copyPasteCtrlA.handleCopy();
        copyPasteCtrlB.handlePaste();
        
        // Assert
        assertEquals(2.5, perspectiveB.getScale(), 0.001);
        assertEquals(new Point(150, 75), perspectiveB.getTranslation());
    }

    /**
     * Test : Copy/Paste avec stratégie CopyScaleOnly (zoom uniquement)
     */
    @Test
    void testCopyPasteWithCopyScaleOnlyStrategy() {
        // Arrange
        Point originalTranslationB = perspectiveB.getTranslation();
        
        perspectiveA.setScale(3.0);
        perspectiveA.setTranslation(new Point(200, 100));
        
        // Act : Copier seulement le zoom
        copyPasteCtrlA.setStrategy(new CopyScaleOnly());
        copyPasteCtrlA.handleCopy();
        copyPasteCtrlB.setStrategy(new CopyScaleOnly());
        copyPasteCtrlB.handlePaste();
        
        // Assert : Seul le zoom doit être copié
        assertEquals(3.0, perspectiveB.getScale(), 0.001, 
            "Le zoom doit être copié");
        assertEquals(originalTranslationB, perspectiveB.getTranslation(), 
            "La translation ne doit pas être modifiée");
    }

    /**
     * Test : Copy/Paste avec stratégie CopyTranslationOnly (translation uniquement)
     */
    @Test
    void testCopyPasteWithCopyTranslationOnlyStrategy() {
        // Arrange
        double originalScaleB = perspectiveB.getScale();
        Point newTranslation = new Point(300, 200);
        
        perspectiveA.setTranslation(newTranslation);
        
        // Act
        copyPasteCtrlA.setStrategy(new CopyTranslationOnly());
        copyPasteCtrlA.handleCopy();
        copyPasteCtrlB.setStrategy(new CopyTranslationOnly());
        copyPasteCtrlB.handlePaste();
        
        // Assert : Seule la translation doit être copiée
        assertEquals(originalScaleB, perspectiveB.getScale(), 0.001, 
            "Le zoom ne doit pas être modifié");
        assertEquals(newTranslation, perspectiveB.getTranslation(), 
            "La translation doit être copiée");
    }

    /**
     * Test : Vérifier que la perspective source n'est pas affectée par la copie
     */
    @Test
    void testCopyPasteDoesNotAffectSourcePerspective() {
        // Arrange : Capturer l'état initial de A
        double scaleA = perspectiveA.getScale();
        Point translationA = perspectiveA.getTranslation();
        
        // Act : Copier A
        copyPasteCtrlA.handleCopy();
        
        // Assert : A ne doit pas avoir changé
        assertEquals(scaleA, perspectiveA.getScale(), 0.001);
        assertEquals(translationA, perspectiveA.getTranslation());
        
        // Coller sur B
        copyPasteCtrlB.handlePaste();
        
        // Assert : A ne doit toujours pas avoir changé
        assertEquals(scaleA, perspectiveA.getScale(), 0.001);
        assertEquals(translationA, perspectiveA.getTranslation());
    }

    /**
     * Test : Undo après Paste doit restaurer l'état précédent de B
     */
    @Test
    void testUndoAfterPasteRestoresPreviousState() {
        // Arrange : État initial de B
        double scaleBeforePaste = 1.5;
        Point translationBeforePaste = new Point(50, 25);
        
        // Modifier B avant le paste
        perspectiveB.setScale(scaleBeforePaste);
        perspectiveB.setTranslation(translationBeforePaste);
        
        // Copier A et coller sur B
        copyPasteCtrlA.handleCopy();
        copyPasteCtrlB.handlePaste();
        
        // Vérifier que B a changé
        assertNotEquals(scaleBeforePaste, perspectiveB.getScale());
        
        // Act : Undo
        bus.undo(perspectiveB);
        
        // Assert : B doit revenir à l'état avant le paste
        assertEquals(scaleBeforePaste, perspectiveB.getScale(), 0.001);
        assertEquals(translationBeforePaste, perspectiveB.getTranslation());
    }

    /**
     * Test : Paste sur un clipboard vide ne doit rien faire
     */
    @Test
    void testEmptyClipboardPasteDoesNothing() {
        // Arrange : État initial de B
        double initialScaleB = perspectiveB.getScale();
        Point initialTranslationB = perspectiveB.getTranslation();
        
        // Act : Paste sans avoir copié
        copyPasteCtrlB.handlePaste();
        
        // Assert : B ne doit pas avoir changé
        assertEquals(initialScaleB, perspectiveB.getScale(), 0.001);
        assertEquals(initialTranslationB, perspectiveB.getTranslation());
    }
}

