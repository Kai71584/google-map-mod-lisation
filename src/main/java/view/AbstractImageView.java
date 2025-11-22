package view;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import core.Observer;
import model.ImageModel;
import model.Perspective;

/**
 * AbstractImageView
 *
 * Classe de base pour toutes les vues graphiques dépendant d'une Perspective.
 *
 * Elle gère :
 *   - l'inscription en tant qu'observateur de la Perspective,
 *   - le mécanisme de repaint automatique via Observer.update(),
 *   - le pattern Template Method pour le rendu graphique (méthode render()).
 *
 * Les sous-classes n'ont PAS à redéfinir paintComponent() : elles redéfinissent
 * uniquement render(Graphics2D g2d), ce qui simplifie beaucoup leur code.
 */
public abstract class AbstractImageView extends JPanel implements Observer {

    /** Modèle d'image partagé (source + perspectives). */
    protected final ImageModel model;

    /** Perspective que cette vue affiche (scale, translation...). */
    protected final Perspective perspective;

    /**
     * Constructeur.
     *
     * Enregistre la vue comme Observateur de la Perspective :
     * dès que la Perspective change (zoom/pan), update() sera invoqué
     * et la vue se redessinera automatiquement.
     */
    protected AbstractImageView(ImageModel model, Perspective perspective) {
        this.model = model;
        this.perspective = perspective;

        // Inscription au pattern Observer
        this.perspective.addObserver(this);
    }

    /** Donne accès à la perspective affichée par cette vue. */
    public Perspective getActivePerspective() {
        return perspective;
    }

    /**
     * Méthode appelée automatiquement lorsque la Perspective notifie
     * un changement (pattern Observer).
     *
     * Ici, on se contente de redessiner la vue.
     */
    @Override
    public void update() {
        repaint();
    }

    /**
     * Méthode finale : les sous-classes ne doivent pas modifier paintComponent(),
     * elles doivent simplement implémenter render().
     *
     * On applique le pattern Template Method :
     *   paintComponent() est le template
     *   render() est l’étape personnalisée.
     */
    @Override
    protected final void paintComponent(Graphics g) {
        super.paintComponent(g);
        render((Graphics2D) g); // délégué à la sous-classe
    }

    /**
     * Méthode que les sous-classes doivent implémenter pour dessiner
     * leur contenu (image, overlay, textes, mini-map, etc.).
     */
    protected abstract void render(Graphics2D g2d);
}
