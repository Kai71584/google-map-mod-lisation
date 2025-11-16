package view;

import core.Observer;
import model.ImageModel;
import model.Perspective;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;

public abstract class AbstractImageView extends JPanel implements Observer {

    protected final ImageModel model;
    protected final Perspective perspective;

    protected AbstractImageView(ImageModel model, Perspective perspective) {
        this.model = model;
        this.perspective = perspective;
        this.perspective.addObserver(this);
    }

    public Perspective getActivePerspective() {
        return perspective;
    }

    @Override
    public void update() {
        repaint();
    }

    @Override
    protected final void paintComponent(Graphics g) {
        super.paintComponent(g);
        render((Graphics2D) g);
    }

    protected abstract void render(Graphics2D g2d);
}
