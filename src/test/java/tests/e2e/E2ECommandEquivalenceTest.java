package tests.e2e;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;

import model.ImageModel;
import model.ImageSource;
import model.FileImageSource;
import model.Perspective;
import command.CommandBus;
import controller.ZoomController;
import controller.PanController;
import view.ImageView;

import java.io.IOException;

/**
 * Tests E2E : Équivalence menu vs gestes souris
 * 
 * Parcours D : Menu et geste souris déclenchent la même commande
 * 
 * @author Alex
 */
public class E2ECommandEquivalenceTest {

    private ImageModel model;
    private Perspective perspective;
    private CommandBus bus;
    private ImageView view;
    private ZoomController zoomCtrl;
    private PanController panCtrl;
    private ImageSource imageSource;

    @BeforeEach
    void setUp() throws IOException {
        // Configuration mode headless
        System.setProperty("java.awt.headless", "true");
        
        // Création de la source d'image
        imageSource = new FileImageSource("w2.jpg");
        model = new ImageModel(imageSource);
        
        // Création d'une perspective
        perspective = new Perspective("Test Perspective");
        model.addPerspective(perspective);
        
        // Création de la vue
        view = new ImageView(model, perspective);
        
        // Création du bus de commandes
        bus = new CommandBus();
        
        // Création des contrôleurs
        zoomCtrl = new ZoomController(view, bus);
        panCtrl = new PanController(view, bus);
        
        // Enregistrer les contrôleurs comme listeners
        view.addImageViewListener(zoomCtrl);
        view.addImageViewListener(panCtrl);
    }

    @AfterEach
    void tearDown() {
        // Nettoyage si nécessaire
    }

    /**
     * Test : Zoom via bouton "+" vs molette souris → même état final
     */
    @Test
    void testZoomButtonAndMouseWheelEquivalent() {
        // Arrange : État initial
        double initialScale = perspective.getScale();
        
        // Act 1 : Zoom via bouton "+"
        zoomCtrl.handleZoomIn();
        double scaleAfterButton = perspective.getScale();
        
        // Réinitialiser
        perspective.setScale(initialScale);
        
        // Act 2 : Zoom via molette (simuler onZoomRequested avec facteur 1.1)
        zoomCtrl.onZoomRequested(1.1); // Facteur équivalent au bouton
        double scaleAfterWheel = perspective.getScale();
        
        // Assert : Les deux doivent produire le même résultat
        assertEquals(scaleAfterButton, scaleAfterWheel, 0.001, 
            "Zoom bouton et molette doivent produire le même résultat");
    }

    /**
     * Test : Pan via drag souris → vérifier état
     */
    @Test
    void testPanDragEquivalent() {
        // Arrange : État initial
        Point initialTranslation = perspective.getTranslation();
        
        // Act : Simuler un drag (pan delta)
        panCtrl.onPanDelta(50, 75);
        
        // Assert : La translation doit avoir changé
        Point newTranslation = perspective.getTranslation();
        assertNotEquals(initialTranslation, newTranslation, 
            "La translation doit changer après un pan");
        
        // Vérifier les valeurs exactes
        assertEquals(50, newTranslation.x, 
            "Translation X doit être égale au delta X");
        assertEquals(75, newTranslation.y, 
            "Translation Y doit être égale au delta Y");
    }

    /**
     * Test : Vérifier que tous les chemins passent par CommandBus
     */
    @Test
    void testCommandBusUnifiedExecution() {
        // Arrange : État initial
        double initialScale = perspective.getScale();
        Point initialTranslation = perspective.getTranslation();
        
        // Act 1 : Zoom via contrôleur (passe par CommandBus)
        zoomCtrl.handleZoomIn();
        
        // Act 2 : Pan via contrôleur (passe par CommandBus)
        panCtrl.onPanDelta(10, 20);
        
        // Assert : Les deux actions doivent être undoables via le bus
        bus.undo(perspective);
        
        // Après undo, la translation doit être restaurée mais le zoom aussi
        // (car undo annule la dernière commande, qui était le pan)
        assertEquals(initialTranslation, perspective.getTranslation(), 
            "Undo doit restaurer la translation");
        
        // Undo encore pour restaurer le zoom
        bus.undo(perspective);
        assertEquals(initialScale, perspective.getScale(), 0.001, 
            "Undo doit restaurer le zoom");
    }

    /**
     * Test : Undo fonctionne après les deux types d'interaction
     */
    @Test
    void testUndoWorksAfterBothInteractionTypes() {
        // Arrange : État initial
        double initialScale = perspective.getScale();
        Point initialTranslation = perspective.getTranslation();
        
        // Act : Zoom via bouton
        zoomCtrl.handleZoomIn();
        double scaleAfterZoom = perspective.getScale();
        
        // Pan via drag
        panCtrl.onPanDelta(30, 40);
        Point translationAfterPan = perspective.getTranslation();
        
        // Assert : Les deux actions sont enregistrées
        assertNotEquals(initialScale, scaleAfterZoom);
        assertNotEquals(initialTranslation, translationAfterPan);
        
        // Undo 1 : Annule le pan
        bus.undo(perspective);
        assertEquals(scaleAfterZoom, perspective.getScale(), 0.001, 
            "Le zoom doit être préservé après undo du pan");
        assertEquals(initialTranslation, perspective.getTranslation(), 
            "La translation doit être restaurée");
        
        // Undo 2 : Annule le zoom
        bus.undo(perspective);
        assertEquals(initialScale, perspective.getScale(), 0.001, 
            "Le zoom doit être restauré");
    }

    /**
     * Test : Vérifier que zoom in et zoom out sont symétriques
     */
    @Test
    void testZoomInOutSymmetry() {
        // Arrange
        double initialScale = perspective.getScale();
        
        // Act : Zoom in puis zoom out
        zoomCtrl.handleZoomIn();
        
        zoomCtrl.handleZoomOut();
        double scaleAfterOut = perspective.getScale();
        
        // Assert : Doit revenir proche de l'état initial
        assertEquals(initialScale, scaleAfterOut, 0.001, 
            "Zoom in puis zoom out doit revenir à l'état initial");
    }
}

