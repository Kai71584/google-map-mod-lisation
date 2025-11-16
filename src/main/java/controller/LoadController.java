package controller;

import command.CommandBus;
import model.ImageModel;
import model.Perspective;
import persistence.PersistenceManager;
import view.AbstractImageView;

import java.util.List;

public class LoadController extends AbstractController {

    private final PersistenceManager persistence;
    private final ImageModel model;

    public LoadController(AbstractImageView view,
                          CommandBus bus,
                          PersistenceManager persistence,
                          ImageModel model) {
        super(view, bus);
        this.persistence = persistence;
        this.model = model;
    }

    public List<Perspective> handleLoadAll() {
        return persistence.loadAll(model);
    }
}
