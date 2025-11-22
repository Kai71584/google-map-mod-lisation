package controller;

import command.CommandBus;
import view.AbstractImageView;

/**
 * Classe abstraite représentant un contrôleur dans l’architecture MVC.
 * Un contrôleur sert d’intermédiaire entre la vue (ImageView)
 * et le système de commandes (CommandBus).
 *
 * Cette classe factorise les attributs et comportements communs
 * à tous les contrôleurs concrets (ex : ZoomController,
 * SaveController, LoadController, etc.).
 */
public abstract class AbstractController {

    /**
     * Référence vers la vue associée à ce contrôleur.
     * Cela permet au contrôleur de réagir aux actions utilisateur
     * (clics, scroll, boutons…) via la vue.
     */
    protected final AbstractImageView view;

    /**
     * Bus de commandes utilisé pour envoyer des commandes
     * (pattern Command). Le contrôleur ne modifie pas directement
     * le modèle : il envoie des commandes au CommandBus,
     * qui gère leur exécution et l’historique.
     */
    protected final CommandBus bus;

    /**
     * Constructeur commun à tous les contrôleurs.
     *
     * @param view la vue associée
     * @param bus le bus de commandes qui exécutera les actions
     */
    protected AbstractController(AbstractImageView view, CommandBus bus) {
        this.view = view;
        this.bus = bus;
    }

    /**
     * @return la vue contrôlée par ce contrôleur
     */
    public AbstractImageView getView() {
        return view;
    }

    /**
     * @return le bus de commandes utilisé pour exécuter
     *         les actions sur le modèle
     */
    public CommandBus getBus() {
        return bus;
    }
}
