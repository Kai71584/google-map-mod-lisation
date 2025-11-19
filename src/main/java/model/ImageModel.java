package model;

import java.util.ArrayList;
import java.util.List;

import core.AbstractSubject;

public class ImageModel extends AbstractSubject {

    private final ImageSource source;
    private final List<Perspective> perspectives = new ArrayList<>();

    public ImageModel(ImageSource source) {
        this.source = source;
    }

    public ImageSource getSource() {
        return source;
    }

    public void addPerspective(Perspective p) {
        perspectives.add(p);
        notifyObservers();
    }

    public List<Perspective> getPerspectives() {
        return perspectives;
    }
}
