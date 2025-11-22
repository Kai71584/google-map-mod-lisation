package core;

/**
 * Interface du patron Observateur (Observer Pattern).
 *
 * Un "Observer" représente un objet qui souhaite être averti lorsqu’un
 * "Subject" (observable) change d’état.
 *
 * Cette interface est volontairement minimale : elle impose uniquement la
 * méthode update(), ce qui permet une grande flexibilité pour les implémentations.
 */
public interface Observer {

    /**
     * Méthode appelée automatiquement par le Subject lorsqu'il subit un changement d'état.
     *
     * Exemple :
     * - Une Perspective change de zoom → elle appelle notifyObservers()
     * - Tous les observers de cette Perspective reçoivent update()
     *
     * Dans votre projet, les vues (ex : AbstractImageView) implémentent Observer :
     *   → update() déclenche un repaint(), donc la vue se met à jour automatiquement.
     */
    void update();
}
