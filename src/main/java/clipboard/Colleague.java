package clipboard;

import model.Perspective;

/**
 * Interface Colleague pour le pattern Mediator.
 * Les collègues communiquent uniquement via le médiateur.
 */
public interface Colleague {
    /**
     * Envoie une demande de copie au médiateur
     */
    void requestCopy();

    /**
     * Envoie une demande de collage au médiateur
     */
    void requestPaste();

    /**
     * Reçoit les données copiées depuis le médiateur
     */
    void receiveCopyData(Double scale, java.awt.Point translation);

    /**
     * Récupère la perspective cible pour les opérations
     */
    Perspective getPerspective();
}
