package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstraite fournissant une implémentation de base du patron Observateur.
 *
 * Elle implémente Subject et gère automatiquement :
 *   - la liste des observers
 *   - l’ajout / suppression d'observateurs
 *   - la notification de chaque observer lors d’un changement
 *
 * Les classes du modèle comme Perspective ou ImageModel héritent de cette classe
 * et appellent notifyObservers() dès qu’un changement doit être communiqué aux vues.
 */
public abstract class AbstractSubject implements Subject {

    /**
     * Liste des objets qui observent ce Subject.
     * Chaque observer implémente la méthode update().
     */
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Ajoute un observer à la liste.
     * Utilisé par les vues (ex : AbstractImageView) pour s’enregistrer
     * auprès d’un modèle ou d’une perspective.
     */
    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    /**
     * Retire un observer de la liste.
     * Rarement utilisé mais important lorsque des vues disparaissent
     * pour éviter des fuites mémoire.
     */
    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    /**
     * Notifie tous les observers qu'un changement est survenu.
     * Chaque observer voit sa méthode update() appelée.
     *
     * Exemple :
     *   perspective.setScale(2.0);
     *   → notifyObservers()
     *   → chaque vue liée à la perspective redessine automatiquement son contenu.
     */
    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update();
        }
    }
}
