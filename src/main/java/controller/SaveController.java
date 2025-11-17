package controller;

import command.CommandBus;
import model.Perspective;
import persistence.PersistenceManager;
import view.AbstractImageView;

import javax.swing.JOptionPane;

public class SaveController extends AbstractController {

    private final PersistenceManager persistence;

    public SaveController(AbstractImageView view,
                          CommandBus bus,
                          PersistenceManager persistence) {
        super(view, bus);
        this.persistence = persistence;
    }

    public void handleSave() {
        Perspective p = view.getActivePerspective();
        if (p != null) {
            // Demander à l'utilisateur de saisir un nom
            String currentName = p.getName();
            String newName = JOptionPane.showInputDialog(
                    view,
                    "Entrez le nom de la perspective :",
                    currentName
            );

            // Si l'utilisateur a cliqué "OK" et n'a pas laissé vide
            if (newName != null && !newName.trim().isEmpty()) {
                p.setName(newName.trim());
                persistence.save(p);
                JOptionPane.showMessageDialog(view, "Perspective sauvegardée : " + newName);
            } else if (newName != null) {
                JOptionPane.showMessageDialog(view, "Erreur : le nom ne peut pas être vide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            // Si annulation (newName == null), on ne fait rien
        }
    }
}
