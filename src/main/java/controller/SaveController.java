package controller;

import javax.swing.JOptionPane;

import command.CommandBus;
import model.Perspective;
import persistence.PersistenceManager;
import view.AbstractImageView;

/**
 * Contrôleur responsable de la sauvegarde d'une Perspective.
 *
 * Il interagit avec la vue pour récupérer la Perspective active,
 * demande un nom à l’utilisateur via une boîte de dialogue,
 * met à jour le nom de la Perspective et délègue la sauvegarde
 * au PersistenceManager.
 *
 * Ce contrôleur ne gère aucune logique de persistance lui-même :
 * il applique uniquement le workflow de sauvegarde déclenché par l’UI.
 */
public class SaveController extends AbstractController {

    /** Gestionnaire chargé de sauvegarder les Perspectives (JSON, BD, fichier…). */
    private final PersistenceManager persistence;

    /**
     * Construit un contrôleur de sauvegarde.
     *
     * @param view        vue contenant la Perspective active
     * @param bus         bus de commandes (non utilisé ici mais fourni par cohérence)
     * @param persistence gestionnaire de persistance chargé d’écrire la Perspective
     */
    public SaveController(AbstractImageView view,
                          CommandBus bus,
                          PersistenceManager persistence) {
        super(view, bus);
        this.persistence = persistence;
    }

    /**
     * Déclenche le processus de sauvegarde de la Perspective active.
     *
     * Étapes :
     *  1. Récupère la Perspective active.
     *  2. Demande à l'utilisateur de saisir un nom via un input dialog.
     *  3. Si la saisie est valide → met à jour le nom dans l’objet Perspective
     *     puis demande au PersistenceManager de la sauvegarder.
     *  4. Selon le cas, affiche un message de confirmation ou d’erreur.
     */
    public void handleSave() {
        // 1. Récupération de la Perspective active
        Perspective p = view.getActivePerspective();
        if (p != null) {

            // 2. Demander un nom à l'utilisateur (rempli avec le nom actuel)
            String currentName = p.getName();
            String newName = JOptionPane.showInputDialog(
                    view,
                    "Entrez le nom de la perspective :",
                    currentName
            );

            // 3. L’utilisateur a cliqué OK
            if (newName != null && !newName.trim().isEmpty()) {
                p.setName(newName.trim());     // mise à jour du nom
                persistence.save(p);           // délégation à la couche persistence
                JOptionPane.showMessageDialog(view,
                        "Perspective sauvegardée : " + newName);

            // 3b. L’utilisateur a cliqué OK mais a laissé le champ vide
            } else if (newName != null) {
                JOptionPane.showMessageDialog(
                        view,
                        "Erreur : le nom ne peut pas être vide.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }

            // 3c. newName == null → l’utilisateur a annulé, donc on ne fait rien.
        }
    }
}
