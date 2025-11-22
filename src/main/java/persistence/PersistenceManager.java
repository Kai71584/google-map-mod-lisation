package persistence;

import java.util.List;

import model.ImageModel;
import model.Perspective;

/**
 * PersistenceManager
 *
 * Interface définissant le contrat de persistance des perspectives.
 *
 * Elle permet :
 *   - de charger toutes les perspectives enregistrées (loadAll),
 *   - d'enregistrer une perspective spécifique (save).
 *
 * Cette abstraction permet de changer facilement le backend de persistance
 * (JSON, XML, base de données, fichier binaire, etc.) sans modifier le reste
 * du code.
 */
public interface PersistenceManager {

    /**
     * Charge toutes les perspectives enregistrées dans le support de
     * persistance.
     *
     * @param model L'ImageModel dans lequel insérer les perspectives chargées.
     *              Le PersistenceManager peut utiliser le modèle si nécessaire,
     *              par exemple pour associer la perspective à la bonne image
     *              ou l'ajouter directement au modèle.
     *
     * @return Une liste d'objets Perspective restaurés à partir du stockage.
     */
    List<Perspective> loadAll(ImageModel model);

    /**
     * Sauvegarde une perspective dans le support de persistance.
     *
     * @param p La Perspective à enregistrer.
     *          Elle peut contenir : nom, scale, translation, ou d'autres
     *          paramètres ajoutés plus tard.
     */
    void save(Perspective p);
}
