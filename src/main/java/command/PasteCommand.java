package command;

import clipboard.ClipboardMediator;
import clipboard.Colleague;
import clipboard.CopyStrategy;
import model.Perspective;

/**
 * Commande Paste : encapsule l'opération de collage.
 *
 * Cette commande combine plusieurs patrons :
 *
 *  - Command : permet d'exécuter / annuler le collage via Undo/Redo
 *  - Memento : snapshot capturant l'état avant collage
 *  - Mediator : le collage est réellement effectué par ClipboardMediator
 *  - Strategy : la méthode de collage dépend de la CopyStrategy active
 *  - Colleague : permet au médiateur de communiquer avec l'élément initiateur
 *
 * Le collage peut modifier plusieurs propriétés d'une Perspective
 * (ex : scale, translation). Toutes sont restaurées lors d'un undo.
 */
public class PasteCommand implements Command {

    /**
     * Perspective cible à laquelle le collage sera appliqué.
     */
    private final Perspective target;

    /**
     * Médiateur chargé de coordonner le collage en fonction de la stratégie.
     */
    private final ClipboardMediator clipboard;

    /**
     * Stratégie utilisée pour coller (ex : collage de scale, translation, etc.).
     */
    private final CopyStrategy strategy;

    /**
     * Le "colleague" initiateur du collage.
     * Sert d'interface entre le contrôleur et le ClipboardMediator.
     */
    private final Colleague colleague;

    /**
     * Memento : état initial de la Perspective, sauvegardé avant modification.
     */
    private Perspective.Snapshot stateBefore;

    /**
     * Constructeur : enregistre la cible, le médiateur, la stratégie et
     * l'initiateur du collage.
     */
    public PasteCommand(Perspective target,
                        ClipboardMediator clipboard,
                        CopyStrategy strategy,
                        Colleague colleague) {
        this.target = target;
        this.clipboard = clipboard;
        this.strategy = strategy;
        this.colleague = colleague;
    }

    @Override
    public void execute() {
        // Si aucun colleague n'est disponible, on ne peut pas coller
        if (colleague == null)
            return;

        // Sauvegarde de l'état AVANT la modification (pattern Memento)
        stateBefore = target.createSnapshot();

        // Le collage réel est délégué au médiateur
        clipboard.mediatePaste(colleague, strategy);
    }

    @Override
    public void undo() {
        // Restauration de l'état initial si disponible
        if (stateBefore != null) {
            target.restore(stateBefore);
        }
    }

    /**
     * Permet au CommandBus de savoir sur quelle Perspective cette commande agit.
     */
    @Override
    public Perspective target() {
        return target;
    }
}
