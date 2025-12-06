package tests.e2e;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import model.ImageModel;
import model.ImageSource;
import model.FileImageSource;
import model.Perspective;
import command.CommandBus;
import command.ZoomCommand;
import command.TranslateCommand;
import controller.UndoRedoController;
import view.AbstractImageView;
import view.ImageView;

import java.io.IOException;

/**
 * Tests E2E : Isolation Undo/Redo par perspective
 * 
 * Parcours A : Vérifier que Undo agit seulement sur la vue active
 * 
 * @author Alex
 */
public class E2EUndoIsolationTest {

    private ImageModel model;
    private Perspective perspectiveA;
    private Perspective perspectiveB;
    private CommandBus bus;
    private UndoRedoController undoCtrl;
    private AbstractImageView viewA;
    private AbstractImageView viewB;
    private ImageSource imageSource;

    @BeforeEach
    void setUp() throws IOException {
        // Configuration mode headless pour Swing
        System.setProperty("java.awt.headless", "true");
        
        // Création d'une source d'image de test
        // TODO: Utiliser une image de test réelle dans src/test/resources/test/
        imageSource = new FileImageSource("w2.jpg");
        
        // Création du modèle
        model = new ImageModel(imageSource);
        
        // Création de deux perspectives distinctes
        perspectiveA = new Perspective("Perspective A");
        perspectiveB = new Perspective("Perspective B");
        model.addPerspective(perspectiveA);
        model.addPerspective(perspectiveB);
        
        // Création des vues
        viewA = new ImageView(model, perspectiveA);
        viewB = new ImageView(model, perspectiveB);
        
        // Création du bus de commandes
        bus = new CommandBus();
        
        // Création du contrôleur Undo/Redo
        undoCtrl = new UndoRedoController(viewA, bus);
    }

    @AfterEach
    void tearDown() {
        // Nettoyage si nécessaire
        // Réinitialisation des singletons si nécessaire
    }

    /**
     * Test : Undo n'affecte que la perspective active
     * 
     * Scénario :
     * 1. Appliquer des transformations sur A
     * 2. Appliquer des transformations sur B
     * 3. Faire Undo sur A
     * 4. Vérifier que seule A est affectée
     */
    @Test
    void testUndoOnlyAffectsActivePerspective() {
        // Arrange
        double initialScaleA = perspectiveA.getScale();
        
        // Appliquer zoom sur A
        bus.execute(new ZoomCommand(perspectiveA, 2.0));
        
        // Appliquer zoom sur B
        bus.execute(new ZoomCommand(perspectiveB, 1.5));
        double scaleAfterZoomB = perspectiveB.getScale();
        
        // Act : Undo sur A (via le contrôleur qui utilise viewA)
        undoCtrl.handleUndo();
        
        // Assert : Seule A doit être affectée
        assertEquals(initialScaleA, perspectiveA.getScale(), 0.001, 
            "Undo doit restaurer A à son état initial");
        assertEquals(scaleAfterZoomB, perspectiveB.getScale(), 0.001, 
            "B ne doit pas être affecté par l'undo sur A");
    }

    /**
     * Test : Isolation complète entre les historiques de A et B
     */
    @Test
    void testUndoRedoIsolationBetweenPerspectives() {
        // Arrange : Actions multiples sur A et B
        bus.execute(new ZoomCommand(perspectiveA, 2.0));
        bus.execute(new TranslateCommand(perspectiveA, 10, 20));
        bus.execute(new ZoomCommand(perspectiveA, 3.0)); // Dernier zoom sur A
        
        bus.execute(new ZoomCommand(perspectiveB, 1.5));
        bus.execute(new TranslateCommand(perspectiveB, 30, 40));
        
        // Capturer les états après actions
        double scaleAAfterZoom = perspectiveA.getScale(); // 3.0
        double scaleBAfterZoom = perspectiveB.getScale(); // 1.5
        
        // Act : Undo sur A (doit annuler le dernier ZoomCommand sur A)
        undoCtrl.handleUndo();
        
        // Assert : Seule A change (revient au zoom précédent)
        assertEquals(2.0, perspectiveA.getScale(), 0.001,
            "A doit revenir au zoom précédent après undo");
        assertNotEquals(scaleAAfterZoom, perspectiveA.getScale(),
            "A doit avoir changé après undo");
        assertEquals(scaleBAfterZoom, perspectiveB.getScale(), 0.001, 
            "B ne doit pas changer");
    }

    /**
     * Test : Vérification que chaque perspective a sa propre pile d'historique
     */
    @Test
    void testCommandHistoryPerPerspective() {
        // Arrange : Actions sur A et B
        bus.execute(new ZoomCommand(perspectiveA, 2.0));
        bus.execute(new ZoomCommand(perspectiveB, 1.5));
        bus.execute(new ZoomCommand(perspectiveA, 3.0));
        
        // Act : Undo sur A (doit annuler le dernier zoom de A, pas celui de B)
        undoCtrl.handleUndo();
        
        // Assert : A revient à 2.0, B reste à 1.5
        assertEquals(2.0, perspectiveA.getScale(), 0.001);
        assertEquals(1.5, perspectiveB.getScale(), 0.001);
    }

    /**
     * Test : Changement de perspective puis undo
     */
    @Test
    void testUndoAfterPerspectiveSwitch() {
        // Arrange : Actions sur A
        bus.execute(new ZoomCommand(perspectiveA, 2.0));
        bus.execute(new TranslateCommand(perspectiveA, 10, 20));
        
        // Actions sur B
        bus.execute(new ZoomCommand(perspectiveB, 1.5));
        
        // Changer de perspective active (simuler en changeant la vue du contrôleur)
        UndoRedoController undoCtrlB = new UndoRedoController(viewB, bus);
        
        // Act : Undo sur B
        undoCtrlB.handleUndo();
        
        // Assert : B revient à 1.0, A reste à 2.0
        assertEquals(1.0, perspectiveB.getScale(), 0.001);
        assertEquals(2.0, perspectiveA.getScale(), 0.001);
    }
}

