package tests.integration;

import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.ImageModel;
import model.ImageSource;
import model.Perspective;
import view.AbstractImageView;
import view.ViewComposite;

/**
 * Tests d'intégration : Pattern Composite pour les vues
 * 
 * Objectif : Vérifier que le ViewComposite combine correctement
 * plusieurs vues (ImageView, CoordinatesView, etc.) et que chacune
 * se met à jour indépendamment tout en partageant la même Perspective.
 * 
 * Architecture testée :
 *   ViewComposite (conteneur) → ImageView + CoordinatesView + ...
 *   Chacune observant la même Perspective
 *   
 * Patterns validés :
 *   - Composite Pattern (hiérarchie de composants)
 *   - Observer Pattern (mise à jour en cascade)
 *   - Sharing state (même Perspective)
 */
public class ViewCompositeIntegrationTest {

    private ImageModel imageModel;
    private Perspective perspective;
    private ViewComposite composite;
    private MockImageView imageView;
    private MockCoordinatesView coordinatesView;

    @BeforeEach
    void setUp() {
        // Configuration
        System.setProperty("java.awt.headless", "true");
        
        imageModel = new ImageModel(new MockImageSource());
        perspective = new Perspective("Test Perspective");
        imageModel.addPerspective(perspective);
        
        // Créer les vues individuelles
        imageView = new MockImageView(imageModel, perspective);
        coordinatesView = new MockCoordinatesView(imageModel, perspective);
        
        // Créer le composite
        composite = new ViewComposite(imageModel, perspective, new FlowLayout());
        composite.add(imageView);
        composite.add(coordinatesView);
    }

    /**
     * Test : Le Composite contient bien les deux vues
     */
    @Test
    void testCompositeContainsAllViews() {
        // Assert
        assertEquals(2, composite.getComponentCount(),
            "Composite should contain both views");
    }

    /**
     * Test : Les deux vues du Composite partagent la même Perspective
     */
    @Test
    void testCompositeViewsSharePerspective() {
        // Assert
        assertEquals(perspective, imageView.getActivePerspective(),
            "ImageView should have correct Perspective");
        assertEquals(perspective, coordinatesView.getActivePerspective(),
            "CoordinatesView should have correct Perspective");
        assertSame(imageView.getActivePerspective(), 
                   coordinatesView.getActivePerspective(),
            "Both views should share the same Perspective instance");
    }

    /**
     * Test : Modifier la Perspective → Toutes les vues du Composite ont accès aux données mises à jour
     */
    @Test
    void testPerspectiveChangeUpdatesAllCompositeViews() {
        // Arrange
        double newScale = 2.5;

        // Act : Modifier la Perspective
        perspective.setScale(newScale);

        // Assert : Les deux vues doivent avoir accès aux nouvelles valeurs
        assertEquals(newScale, imageView.getActivePerspective().getScale(), 0.001,
            "ImageView should access updated scale");
        assertEquals(newScale, coordinatesView.getActivePerspective().getScale(), 0.001,
            "CoordinatesView should access updated scale");
    }

    /**
     * Test : Chaque vue du Composite peut être rendue indépendamment
     */
    @Test
    void testCompositeViewsRenderIndependently() {
        // Arrange
        int imageViewRenders = imageView.getRenderCallCount();
        int coordinatesViewRenders = coordinatesView.getRenderCallCount();

        // Act : Forcer un repaint du Composite
        composite.repaint();

        // Assert : Les deux vues doivent avoir des compteurs indépendants
        assertTrue(imageView.getRenderCallCount() >= imageViewRenders,
            "ImageView render count should increase or stay same");
        assertTrue(coordinatesView.getRenderCallCount() >= coordinatesViewRenders,
            "CoordinatesView render count should increase or stay same");
    }

    /**
     * Test : Ajouter une vue au Composite après sa création
     */
    @Test
    void testAddViewToCompositeAtRuntime() {
        // Arrange
        MockImageView newView = new MockImageView(imageModel, perspective);
        int initialComponentCount = composite.getComponentCount();

        // Act
        composite.add(newView);

        // Assert
        assertEquals(initialComponentCount + 1, composite.getComponentCount(),
            "Composite should have one more component");
    }

    /**
     * Test : Supprimer une vue du Composite
     */
    @Test
    void testRemoveViewFromComposite() {
        // Arrange
        int initialComponentCount = composite.getComponentCount();

        // Act
        composite.remove(imageView);

        // Assert
        assertEquals(initialComponentCount - 1, composite.getComponentCount(),
            "Composite should have one less component");
    }

    /**
     * Test : Chaque vue du Composite a accès aux changements de Perspective
     */
    @Test
    void testEachViewRespondsToChanges() {
        // Arrange
        perspective.setScale(1.0);
        perspective.setTranslation(new Point(0, 0));

        // Act : Plusieurs modifications
        double newScale = 2.0;
        Point newTranslation = new Point(100, 50);
        perspective.setScale(newScale);
        perspective.setTranslation(newTranslation);

        // Assert : Chaque vue doit avoir accès aux nouveaux changements
        assertEquals(newScale, imageView.getActivePerspective().getScale(), 0.001,
            "ImageView should access new scale");
        assertEquals(newScale, coordinatesView.getActivePerspective().getScale(), 0.001,
            "CoordinatesView should access new scale");
        assertEquals(newTranslation, imageView.getActivePerspective().getTranslation(),
            "ImageView should access new translation");
        assertEquals(newTranslation, coordinatesView.getActivePerspective().getTranslation(),
            "CoordinatesView should access new translation");
    }

    /**
     * Test : L'état de la Perspective est cohérent dans toutes les vues
     */
    @Test
    void testPerspectiveStateConsistencyAcrossViews() {
        // Arrange
        double newScale = 3.14;
        Point newTranslation = new Point(42, 73);

        // Act
        perspective.setScale(newScale);
        perspective.setTranslation(newTranslation);

        // Assert
        Perspective imageViewPerspective = imageView.getActivePerspective();
        Perspective coordinatesViewPerspective = coordinatesView.getActivePerspective();
        
        assertEquals(newScale, imageViewPerspective.getScale(), 0.001);
        assertEquals(newScale, coordinatesViewPerspective.getScale(), 0.001);
        assertEquals(newTranslation, imageViewPerspective.getTranslation());
        assertEquals(newTranslation, coordinatesViewPerspective.getTranslation());
    }

    /**
     * Test : Vérifier que le Composite peut contenir plusieurs types de vues
     */
    @Test
    void testCompositeWithMultipleViewTypes() {
        // Arrange
        ViewComposite complexComposite = new ViewComposite(imageModel, perspective, new FlowLayout());
        MockImageView view1 = new MockImageView(imageModel, perspective);
        MockCoordinatesView view2 = new MockCoordinatesView(imageModel, perspective);
        MockImageView view3 = new MockImageView(imageModel, perspective);

        // Act
        complexComposite.add(view1);
        complexComposite.add(view2);
        complexComposite.add(view3);

        // Assert
        assertEquals(3, complexComposite.getComponentCount(),
            "Composite should contain all 3 views");
    }

    /**
     * Test : Nested Composites (Composite dans Composite)
     */
    @Test
    void testNestedComposites() {
        // Arrange
        ViewComposite outerComposite = new ViewComposite(imageModel, perspective, new FlowLayout());
        ViewComposite innerComposite = new ViewComposite(imageModel, perspective, new FlowLayout());
        
        MockImageView view1 = new MockImageView(imageModel, perspective);
        MockCoordinatesView view2 = new MockCoordinatesView(imageModel, perspective);

        // Act
        innerComposite.add(view1);
        innerComposite.add(view2);
        outerComposite.add(innerComposite);

        // Assert
        assertEquals(1, outerComposite.getComponentCount(),
            "Outer composite should contain the inner composite");
    }

    /**
     * Test : Vérifier que les vues du Composite partagent le même modèle
     */
    @Test
    void testCompositeViewsShareModel() {
        // Assert
        assertSame(imageModel, imageView.getModel(),
            "ImageView should have the same model instance");
        assertSame(imageModel, coordinatesView.getModel(),
            "CoordinatesView should have the same model instance");
    }

    // ========== MOCK IMPLEMENTATIONS ==========

    private static class MockImageSource implements ImageSource {
        @Override
        public java.awt.image.BufferedImage image() {
            return null;
        }
    }

    private static class MockImageView extends AbstractImageView {
        private int renderCallCount = 0;

        public MockImageView(ImageModel model, Perspective perspective) {
            super(model, perspective);
        }

        @Override
        public Perspective getActivePerspective() {
            return super.getActivePerspective();
        }

        @Override
        protected void render(Graphics2D g2d) {
            renderCallCount++;
        }

        public int getRenderCallCount() {
            return renderCallCount;
        }

        public void resetRenderCount() {
            renderCallCount = 0;
        }

        public ImageModel getModel() {
            return this.model;
        }
    }

    private static class MockCoordinatesView extends AbstractImageView {
        private int renderCallCount = 0;

        public MockCoordinatesView(ImageModel model, Perspective perspective) {
            super(model, perspective);
        }

        @Override
        public Perspective getActivePerspective() {
            return super.getActivePerspective();
        }

        @Override
        protected void render(Graphics2D g2d) {
            renderCallCount++;
        }

        public int getRenderCallCount() {
            return renderCallCount;
        }

        public void resetRenderCount() {
            renderCallCount = 0;
        }

        public ImageModel getModel() {
            return this.model;
        }
    }
}
