package controller;

import command.CommandBus;
import model.Perspective;
import persistence.PersistenceManager;
import view.AbstractImageView;

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
            persistence.save(p);
        }
    }
}
