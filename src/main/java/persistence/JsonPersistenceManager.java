package persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import model.ImageModel;
import model.Perspective;

/**
 * JsonPersistenceManager
 *
 * Implémentation de PersistenceManager utilisant Jackson pour stocker
 * les perspectives dans un fichier JSON.
 *
 * Le fichier contient un tableau JSON :
 *
 * [
 *   { "nom": "...", "translationX": ..., "translationY": ..., "zoom": ... },
 *   ...
 * ]
 *
 * Le chargement et la sauvegarde sont tolérants : si le fichier n'existe pas
 * ou est vide, le programme continue normalement.
 */
public class JsonPersistenceManager implements PersistenceManager {

    /** Fichier JSON de persistance. */
    private final File file;

    /** Mapper Jackson permettant lecture/écriture JSON. */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Constructeur.
     * @param path Chemin du fichier JSON contenant la liste de perspectives.
     */
    public JsonPersistenceManager(String path) {
        this.file = new File(path);
    }

    /**
     * Charge toutes les perspectives stockées dans le fichier JSON.
     *
     * @param model ImageModel dans lequel les perspectives doivent être ajoutées.
     * @return Liste des perspectives créées.
     */
    @Override
    public List<Perspective> loadAll(ImageModel model) {
        List<Perspective> result = new ArrayList<>();

        try {
            // Si aucun fichier ou fichier vide → rien à charger
            if (!file.exists() || file.length() == 0) {
                return result;
            }

            // Lecture du JSON racine
            JsonNode root = mapper.readTree(file);

            // On s'attend à un tableau JSON
            ArrayNode array;
            if (root.isArray()) {
                array = (ArrayNode) root;
            } else {
                // Format inattendu → on renvoie une liste vide
                return result;
            }

            // Lecture de chaque objet JSON représentant une perspective
            for (JsonNode obj : array) {

                // Lecture des champs, avec valeurs par défaut
                String nom = obj.has("nom") ? obj.get("nom").asText() : "Sans nom";
                int tx = obj.has("translationX") ? obj.get("translationX").asInt() : 0;
                int ty = obj.has("translationY") ? obj.get("translationY").asInt() : 0;

                // Compatibilité ancienne : "Zoom" ou "zoom"
                double zoom = obj.has("zoom") ? obj.get("zoom").asDouble()
                        : (obj.has("Zoom") ? obj.get("Zoom").asDouble() : 1.0);

                // Reconstruction de la perspective
                Perspective p = new Perspective(nom);
                p.setTranslation(new java.awt.Point(tx, ty));
                p.setScale(zoom);

                // On l'ajoute au modèle et au résultat
                model.addPerspective(p);
                result.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Enregistre une Perspective dans le fichier JSON.
     *
     * Le fichier est relu entièrement, la nouvelle perspective est ajoutée
     * au tableau, puis le tableau réécrit.
     *
     * @param p Perspective à sauvegarder.
     */
    @Override
    public void save(Perspective p) {
        try {
            ArrayNode array;

            // Si le fichier existe → on essaie de récupérer le tableau existant
            if (file.exists() && file.length() > 0) {
                JsonNode root = mapper.readTree(file);

                if (root.isArray()) {
                    array = (ArrayNode) root;
                } else {
                    array = mapper.createArrayNode(); // format invalide → repartir à zéro
                }
            } else {
                // Pas de fichier ou fichier vide → on crée un nouveau tableau
                array = mapper.createArrayNode();
            }

            // Création d'un nouvel objet JSON représentant la perspective
            ObjectNode node = mapper.createObjectNode();
            node.put("nom", p.getName());
            node.put("translationX", p.getTranslation().x);
            node.put("translationY", p.getTranslation().y);
            node.put("zoom", p.getScale());

            // Ajout au tableau
            array.add(node);

            // Réécriture du fichier avec jolie mise en forme
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, array);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
