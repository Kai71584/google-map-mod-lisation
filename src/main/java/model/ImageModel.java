package model;

import java.util.ArrayList;
import java.util.List;

import core.AbstractSubject;

/**
 * Modèle principal représentant l'image et l'ensemble des perspectives associées.
 *
 * Cette classe fait partie du modèle dans l'architecture MVC.
 * Elle hérite d'AbstractSubject pour pouvoir notifier les vues (Observers)
 * lorsqu’un changement survient (ex : ajout d'une perspective).
 */
public class ImageModel extends AbstractSubject {

    /** 
     * Source de l’image (fichier, URL, image générée, etc.).
     * Abstraction fournie par l’interface ImageSource.
     */
    private final ImageSource source;

    /**
     * Liste des perspectives enregistrées.
     * Une Perspective représente une vue sur l'image :
     *   - zoom
     *   - translation
     *   - nom
     *   - snapshot/restauration
     */
    private final List<Perspective> perspectives = new ArrayList<>();

    /**
     * Constructeur recevant la source d’image.
     * Le modèle ne s'occupe PAS de la façon dont l’image est chargée.
     */
    public ImageModel(ImageSource source) {
        this.source = source;
    }

    /**
     * Retourne l'objet source contenant l’image.
     */
    public ImageSource getSource() {
        return source;
    }

    /**
     * Ajoute une perspective au modèle.
     *
     * Chaque ajout notifie les observers (vues) pour qu’elles se mettent à jour :
     *   - mise à jour d’une liste UI
     *   - nouvelle miniature
     *   - changement dans un panneau latéral
     */
    public void addPerspective(Perspective p) {
        perspectives.add(p);
        notifyObservers();   // Indique que le modèle a changé
    }

    /**
     * Retourne toutes les perspectives associées à ce modèle.
     * La liste est mutable, mais fournie telle quelle.
     */
    public List<Perspective> getPerspectives() {
        return perspectives;
    }
}
