package view;

import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import model.ImageModel;
import model.Perspective;

/**
 * ViewComposite représente une vue conteneur capable d'afficher plusieurs
 * sous-vues AbstractImageView simultanément.
 *
 * C'est une implémentation du pattern COMPOSITE appliqué aux vues.
 * Elle hérite d'AbstractImageView pour rester cohérente dans la hiérarchie
 * et pour qu'un composite soit lui-même une "vue" compatible avec MVC.
 *
 * Elle repose aussi sur le Template Method :
 * - render() est vide car chaque sous-vue se dessine elle-même.
 */
public class ViewComposite extends AbstractImageView {

    // Liste interne des sous-vues enfants
    private final List<AbstractImageView> children = new ArrayList<>();

    /**
     * Constructeur.
     *
     * @param model       le modèle d’image partagé
     * @param perspective la perspective que toutes les sous-vues observent
     * @param layout      un LayoutManager Swing pour organiser les sous-vues
     *
     * On appelle le constructeur de AbstractImageView (super),
     * puis on définit le layout pour gérer la disposition des sous-vues.
     */
    public ViewComposite(ImageModel model, Perspective perspective, LayoutManager layout) {
        super(model, perspective);
        setLayout(layout);

        // Un composite n'a pas besoin de se peindre lui-même → transparence
        setOpaque(false);
    }

    /**
     * Ajoute une sous-vue au composite.
     *
     * @param v           une sous-vue qui étend AbstractImageView
     * @param constraints contrainte du layout (BorderLayout, etc.)
     *
     * On stocke la vue dans la liste interne pour suivre la hiérarchie,
     * puis on l'ajoute aussi au conteneur Swing (JPanel).
     */
    public void addView(AbstractImageView v, Object constraints) {
        children.add(v);
        add(v, constraints);
    }

    /**
     * Méthode appelée par AbstractImageView.repaintView().
     *
     * Ici elle est volontairement vide :
     * - Le composite ne rend rien lui-même.
     * - Chaque enfant AbstractImageView implémente son propre render().
     *
     * Ce comportement illustre le pattern TEMPLATE METHOD :
     * AbstractImageView appelle render(), mais ici, dans un composite,
     * on n'effectue aucun dessin.
     */
    @Override
    protected void render(Graphics2D g2d) {
        // Rien à dessiner.
        // Les sous-vues se dessinent elles-mêmes automatiquement.
    }
}
