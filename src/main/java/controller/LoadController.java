package controller;

import java.util.List;

import command.CommandBus;
import model.ImageModel;
import model.Perspective;
import persistence.PersistenceManager;
import view.AbstractImageView;

/**
 * LoadController
 * ----------------
 * Contrôleur responsable du chargement des perspectives sauvegardées.
 *
 * Il utilise un PersistenceManager (ex. JsonPersistenceManager)
 * pour lire depuis un stockage externe (fichier JSON, DB, etc.)
 * et recharger toutes les perspectives associées à un ImageModel.
 *
 * Contrairement à d'autres contrôleurs, il ne déclenche aucune commande
 * undo/redo : il effectue directement une opération de chargement.
 */
public class LoadController extends AbstractController {

    /** Objet de persistance chargé de lire les perspectives (JSON, etc.) */
    private final PersistenceManager persistence;

    /** Le modèle d’image où les perspectives doivent être réintégrées */
    private final ImageModel model;

    /**
     * Constructeur du contrôleur.
     *
     * @param view         Vue contenant une perspective active
     * @param bus          Bus de commandes (non utilisé ici, mais hérité)
     * @param persistence  Gestionnaire de persistance servant à charger les données
     * @param model        Modèle contenant l’image et la liste des perspectives
     */
    public LoadController(AbstractImageView view,
                          CommandBus bus,
                          PersistenceManager persistence,
                          ImageModel model) {
        super(view, bus);
        this.persistence = persistence;
        this.model = model;
    }

    /**
     * Charge toutes les perspectives disponibles dans la source externe
     * (ex : fichier JSON) et les ajoute dans le modèle.
     *
     * @return la liste des perspectives qui ont été chargées.
     *
     * Responsabilité :
     * - demande au PersistenceManager de charger
     * - les perspectives sont ajoutées directement dans le modèle (side-effect)
     *
     * Cette méthode ne montre pas de popup, ne gère pas de UI :
     * elle fournit simplement les données.
     */
    public List<Perspective> handleLoadAll() {
        return persistence.loadAll(model);
    }
}
