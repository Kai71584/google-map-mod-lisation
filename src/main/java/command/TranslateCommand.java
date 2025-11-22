package command;

import java.awt.Point;

import model.Perspective;

/**
 * Commande Translate : représente un déplacement (pan) appliqué
 * à une Perspective.
 *
 * Cette commande suit le pattern Command :
 *  - execute() applique le déplacement
 *  - undo() restaure l'état initial
 *
 * Comme toutes les commandes modifiant l'état d'une Perspective,
 * elle utilise le pattern Memento :
 *   - Snapshot = capture de l'état avant la modification
 *   - undo() = restauration de cet état
 */
public class TranslateCommand implements Command {

    /**
     * La Perspective ciblée, contenant l'état de transformation :
     * translation, zoom, etc.
     */
    private final Perspective target;

    /**
     * Déplacement à appliquer (en pixels) :
     *  dx = déplacement horizontal
     *  dy = déplacement vertical
     */
    private final int dx, dy;

    /**
     * Memento de l'état avant modification.
     * Permet d'annuler le déplacement.
     */
    private Perspective.Snapshot stateBefore;

    /**
     * Constructeur : enregistre la cible et le delta de déplacement.
     *
     * @param target la Perspective à modifier
     * @param dx déplacement horizontal
     * @param dy déplacement vertical
     */
    public TranslateCommand(Perspective target, int dx, int dy) {
        this.target = target;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void execute() {
        // Sauvegarde de l'état initial (pattern Memento)
        stateBefore = target.createSnapshot();

        // Application du déplacement : on ajoute dx/dy à la translation actuelle
        Point t = target.getTranslation();
        target.setTranslation(new Point(t.x + dx, t.y + dy));
    }

    @Override
    public void undo() {
        // Restaure l'état précédent si disponible
        if (stateBefore != null) {
            target.restore(stateBefore);
        }
    }

    /**
     * Retourne la cible de la commande (utile pour CommandBus)
     */
    @Override
    public Perspective target() {
        return target;
    }
}
