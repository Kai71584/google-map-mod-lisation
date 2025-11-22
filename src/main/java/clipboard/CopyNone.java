package clipboard;

import model.Perspective;

/**
 * Implémentation de CopyStrategy qui ne copie absolument rien.
 *
 * Cette classe représente le pattern "Null Object".
 * Elle est utilisée lorsqu'on souhaite désactiver toute opération de copie
 * sans avoir à gérer des conditions spéciales ailleurs dans le code.
 */
public class CopyNone implements CopyStrategy {

    @Override
    public void apply(ClipboardMediator clipboard, Perspective target) {
        // Ne fait rien du tout.
        // Cette stratégie est utile pour éviter des check null ou if inutiles,
        // et pour représenter explicitement une absence d'action.
    }
}
