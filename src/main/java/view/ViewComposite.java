package view;

import model.ImageModel;
import model.Perspective;

import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

public class ViewComposite extends AbstractImageView {

    private final List<AbstractImageView> children = new ArrayList<>();

    public ViewComposite(ImageModel model, Perspective perspective, LayoutManager layout) {
        super(model, perspective);
        setLayout(layout);
        setOpaque(false);
    }

    public void addView(AbstractImageView v, Object constraints) {
        children.add(v);
        add(v, constraints);
    }

    @Override
    protected void render(Graphics2D g2d) {
        // Rien : chaque enfant se dessine lui-même (Composite + Template Method)
    }
}
