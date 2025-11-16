package controller;

import command.CommandBus;
import command.TranslateCommand;
import model.Perspective;
import view.AbstractImageView;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanController extends AbstractController {

    private int lastX, lastY;

    public PanController(AbstractImageView view, CommandBus bus) {
        super(view, bus);
        attachListeners();
    }

    private void attachListeners() {
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }
        });

        view.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;
                lastX = e.getX();
                lastY = e.getY();
                Perspective p = view.getActivePerspective();
                if (p != null) {
                    bus.execute(new TranslateCommand(p, dx, dy));
                }
            }
        });
    }
}
