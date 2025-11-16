package persistence;

import model.ImageModel;
import model.Perspective;

import java.util.List;

public interface PersistenceManager {
    List<Perspective> loadAll(ImageModel model);
    void save(Perspective p);
}
