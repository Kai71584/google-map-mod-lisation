package controller;

import command.CommandBus;
import view.AbstractImageView;

public abstract class AbstractController {

    protected final AbstractImageView view;
    protected final CommandBus bus;

    protected AbstractController(AbstractImageView view, CommandBus bus) {
        this.view = view;
        this.bus = bus;
    }

    public AbstractImageView getView() {
        return view;
    }

    public CommandBus getBus() {
        return bus;
    }
}
