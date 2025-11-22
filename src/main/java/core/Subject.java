package core;

/**
 * Interface du patron Observateur (Observer Pattern).
 *
 * Un "Subject" — ou sujet/observable — représente un objet dont l'état
 * peut être observé par un ou plusieurs "Observer".
 *
 * Lorsqu’un changement d’état survient, le Subject appelle notifyObservers()
 * afin que tous les observers se mettent à jour automatiquement.
 *
 * Cette interface définit donc le contrat minimal pour tous les objets
 * observables du système (ex : Perspective, ImageModel).
 */
public interface Subject {

    /**
     * Enregistre un observer.
     *
     * @param o L'observer à ajouter.
     *
     * Un observer enregistré sera notifié à chaque appel de notifyObservers().
     */
    void addObserver(Observer o);

    /**
     * Retire un observer du sujet.
     *
     * @param o L'observer à retirer.
     *
     * Après suppression, cet observer ne recevra plus les notifications.
     */
    void removeObserver(Observer o);

    /**
     * Notifie tous les observers enregistrés.
     *
     * Cette méthode est généralement appelée après une modification interne
     * de l'état du Subject (ex : changement de zoom, mouvement de translation,
     * nouvelle perspective ajoutée, etc.).
     *
     * Typiquement, chaque observer implémentera update().
     */
    void notifyObservers();
}
