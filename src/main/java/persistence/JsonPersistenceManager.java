package persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.ImageModel;
import model.Perspective;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JsonPersistenceManager implements PersistenceManager {

    private final File file;
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonPersistenceManager(String path) {
        this.file = new File(path);
    }

    @Override
    public List<Perspective> loadAll(ImageModel model) {
        List<Perspective> result = new ArrayList<>();
        try {
            if (!file.exists() || file.length() == 0) {
                return result;
            }
            JsonNode root = mapper.readTree(file);
            ArrayNode array;
            if (root.isArray()) {
                array = (ArrayNode) root;
            } else {
                return result;
            }

            for (JsonNode obj : array) {
                String nom = obj.has("nom") ? obj.get("nom").asText() : "Sans nom";
                int tx = obj.has("translationX") ? obj.get("translationX").asInt() : 0;
                int ty = obj.has("translationY") ? obj.get("translationY").asInt() : 0;
                double zoom = obj.has("zoom") ? obj.get("zoom").asDouble()
                        : (obj.has("Zoom") ? obj.get("Zoom").asDouble() : 1.0);

                Perspective p = new Perspective(nom);
                p.setTranslation(new java.awt.Point(tx, ty));
                p.setScale(zoom);
                model.addPerspective(p);
                result.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(Perspective p) {
        try {
            ArrayNode array;
            if (file.exists() && file.length() > 0) {
                JsonNode root = mapper.readTree(file);
                if (root.isArray()) {
                    array = (ArrayNode) root;
                } else {
                    array = mapper.createArrayNode();
                }
            } else {
                array = mapper.createArrayNode();
            }

            ObjectNode node = mapper.createObjectNode();
            node.put("nom", p.getName());
            node.put("translationX", p.getTranslation().x);
            node.put("translationY", p.getTranslation().y);
            node.put("zoom", p.getScale());

            array.add(node);

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, array);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
