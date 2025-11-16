package view;

import model.ImageModel;
import model.Perspective;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class CoordinatesView extends AbstractImageView {

    public CoordinatesView(ImageModel model, Perspective perspective) {
        super(model, perspective);
    }

    @Override
    protected void render(Graphics2D g2d) {
        var t = perspective.getTranslation();
        double s = perspective.getScale();

        g2d.setColor(new Color(255, 255, 255, 210));
        g2d.fillRoundRect(5, 5, 220, 50, 10, 10);

        g2d.setColor(Color.BLACK);
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 12f));

        String txt1 = "X: " + t.x + " | Y: " + t.y;
        String txt2 = String.format("Zoom: %.2f", s);

        g2d.drawString(txt1, 15, 25);
        g2d.drawString(txt2, 15, 40);
    }
}
