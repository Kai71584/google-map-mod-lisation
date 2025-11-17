import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import clipboard.ClipboardMediator;
import clipboard.CopyBoth;
import clipboard.CopyTranslationXOnly;
import command.CommandBus;
import controller.CopyPasteController;
import controller.LoadController;
import controller.PanController;
import controller.SaveController;
import controller.UndoRedoController;
import controller.ZoomController;
import model.FileImageSource;
import model.ImageModel;
import model.ImageSource;
import model.Perspective;
import persistence.JsonPersistenceManager;
import persistence.PersistenceManager;
import view.CoordinatesView;
import view.ImageView;
import view.ViewComposite;

public class MainApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MainApp().start();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Erreur chargement image : " + e.getMessage());
            }
        });
    }

    private void start() throws IOException {
        // ⚠️ À adapter à ton chemin d'image
        String imagePath = "C:\\Users\\salut\\Pictures\\Screenshots\\Capture d’écran 2025-11-14 131349.png";
        String jsonPath = "data.json";

        ImageSource source = new FileImageSource(imagePath);
        ImageModel model = new ImageModel(source);

        // Perspective principale
        Perspective pMain = new Perspective("Vue principale");
        model.addPerspective(pMain);

        // Vues
        ImageView imageView = new ImageView(model, pMain);
        CoordinatesView coordsView = new CoordinatesView(model, pMain);
        coordsView.setPreferredSize(new Dimension(300, 100));

        ViewComposite composite = new ViewComposite(model, pMain, new BorderLayout());
        composite.addView(imageView, BorderLayout.CENTER);
        composite.addView(coordsView, BorderLayout.SOUTH);

        // Infrastructure
        CommandBus bus = new CommandBus();
        ClipboardMediator clipboard = new ClipboardMediator();
        PersistenceManager persistence = new JsonPersistenceManager(jsonPath);

        // Contrôleurs
        ZoomController zoomCtrl = new ZoomController(imageView, bus);
        imageView.setZoomController(zoomCtrl); // Connecte le ZoomController à ImageView pour la roulette
        new PanController(imageView, bus); // s'accroche aux events souris
        UndoRedoController undoCtrl = new UndoRedoController(imageView, bus);
        CopyPasteController copyPasteCtrl = new CopyPasteController(imageView, bus, clipboard);
        copyPasteCtrl.setStrategy(new CopyBoth());
        SaveController saveCtrl = new SaveController(imageView, bus, persistence);
        LoadController loadCtrl = new LoadController(imageView, bus, persistence, model);

        // UI
        JFrame frame = new JFrame("Visualiseur multi-perspectives");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnPlus = new JButton("+");
        JButton btnMinus = new JButton("-");
        JButton btnUndo = new JButton("Undo");
        JButton btnRedo = new JButton("Redo");
        JButton btnCopy = new JButton("Copy");
        JButton btnCopyX = new JButton("Copy X");
        JButton btnPaste = new JButton("Paste");
        JButton btnPasteX = new JButton("Paste X");
        JButton btnSave = new JButton("Save");

        btnPlus.addActionListener(e -> zoomCtrl.handleZoomIn());
        btnMinus.addActionListener(e -> zoomCtrl.handleZoomOut());
        btnUndo.addActionListener(e -> undoCtrl.handleUndo());
        btnRedo.addActionListener(e -> undoCtrl.handleRedo());
        btnCopy.addActionListener(e -> copyPasteCtrl.handleCopy());
        btnCopyX.addActionListener(e -> {
            copyPasteCtrl.setStrategy(new CopyTranslationXOnly());
            copyPasteCtrl.handleCopy();
        });
        btnPaste.addActionListener(e -> copyPasteCtrl.handlePaste());
        btnPasteX.addActionListener(e -> {
            copyPasteCtrl.setStrategy(new CopyTranslationXOnly());
            copyPasteCtrl.handlePaste();
        });
        btnSave.addActionListener(e -> saveCtrl.handleSave());

        controls.add(btnPlus);
        controls.add(btnMinus);
        controls.add(btnUndo);
        controls.add(btnRedo);
        controls.add(btnCopy);
        controls.add(btnCopyX);
        controls.add(btnPaste);
        controls.add(btnPasteX);
        controls.add(btnSave);

        // Charger les perspectives depuis data.json
        List<Perspective> loaded = loadCtrl.handleLoadAll();
        JComboBox<Perspective> combo = new JComboBox<>(loaded.toArray(new Perspective[0]));
        combo.insertItemAt(pMain, 0);
        combo.setSelectedIndex(0);

        combo.addActionListener(e -> {
            Perspective selected = (Perspective) combo.getSelectedItem();
            if (selected != null && selected != pMain) {
                // Applique les paramètres de la perspective sélectionnée sur la principale
                pMain.setScale(selected.getScale());
                pMain.setTranslation(selected.getTranslation());
                pMain.setName(selected.getName());
            }
        });

        controls.add(new JLabel("Perspectives :"));
        controls.add(combo);

        frame.add(controls, BorderLayout.NORTH);
        frame.add(composite, BorderLayout.CENTER);

        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
