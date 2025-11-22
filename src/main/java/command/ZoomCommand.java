package command;

import model.Perspective;

/**
 * Commande Zoom : représente une action de zoom appliquée à un objet Perspective.
 * 
 * Cette commande utilise le pattern Command :
 *  - execute() applique l’action (ici : changer l’échelle)
 *  - undo() restaure l’état précédent
 *  
 * Elle utilise aussi le pattern Memento indirectement :
 *  - Perspective.Snapshot joue le rôle de Memento
 *  - La commande enregistre l’état avant l’action afin de permettre undo().
 */
public class ZoomCommand implements Command {

    /**
     * La cible sur laquelle l’action de zoom sera appliquée.
     * Ici, la Perspective contient l’état de transformation
     * (échelle, translation...).
     */
    private final Perspective target;

    /**
     * Le facteur de zoom : >1 pour zoomer, <1 pour dézoomer.
     */
    private final double factor;

    /**
     * Snapshot contenant l’état avant modification.
     * Permet de restaurer l’état initial lors d’un undo().
     */
    private Perspective.Snapshot stateBefore;

    /**
     * Constructeur : on configure la cible et le facteur de zoom.
     *
     * @param target l'objet Perspective à modifier
     * @param factor le facteur de zoom (1.1, 0.9, etc.)
     */
    public ZoomCommand(Perspective target, double factor) {
        this.target = target;
        this.factor = factor;
    }

    @Override
    public void execute() {
        // Sauvegarde de l'état avant la modification (pattern Memento)
        stateBefore = target.createSnapshot();

        // Application du zoom : on multiplie l’échelle actuelle par le facteur
        double newScale = target.getScale() * factor;
        target.setScale(newScale);
    }

    @Override
    public void undo() {
        // Restaure l'état si un snapshot a été enregistré
        if (stateBefore != null) {
            target.restore(stateBefore);
        }
    }

    /**
     * @return la cible de cette commande (utile pour le CommandBus)
     */
    @Override
    public Perspective target() {
        return target;
    }
}
