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
import clipboard.CopyScaleOnly;
import clipboard.CopyTranslationOnly;
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

/**
 * Classe principale de l'application.
 * Initialise le modèle, les vues, les contrôleurs et l'interface graphique Swing.
 */
public class MainApp {

    public static void main(String[] args) {
        // Démarre l’interface dans le thread UI (Swing)
        SwingUtilities.invokeLater(() -> {
            try {
                new MainApp().start();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                    null,
                    "Erreur chargement image : " + e.getMessage()
                );
            }
        });
    }

    /**
     * Point d’entrée logique : instancie modèle, vues, contrôleurs et UI.
     */
    private void start() throws IOException {

        // Chemins des fichiers utilisés
        String imagePath = "w2.jpg";
        String jsonPath = "data.json";

        // Source d’image (pattern Strategy)
        ImageSource source = new FileImageSource(imagePath);

        // Modèle contenant l'image + liste des perspectives
        ImageModel model = new ImageModel(source);

        // Perspective principale utilisée par la fenêtre principale
        Perspective pMain = new Perspective("Vue principale");
        model.addPerspective(pMain);

        // --- Création des VUES ---

        // Vue d’image principale
        ImageView imageView = new ImageView(model, pMain);

        // Vue affichant les coordonnées (zoom, translation)
        CoordinatesView coordsView = new CoordinatesView(model, pMain);
        coordsView.setPreferredSize(new Dimension(300, 100));

        // Vue composite contenant plusieurs vues alignées par BorderLayout
        ViewComposite composite = new ViewComposite(model, pMain, new BorderLayout());
        composite.addView(imageView, BorderLayout.CENTER);
        composite.addView(coordsView, BorderLayout.SOUTH);

        // --- Infrastructure générale de l’application ---

        // Bus centralisé de commandes (pattern Command)
        CommandBus bus = new CommandBus();

        // Médiateur du presse-papiers (copy/paste)
        ClipboardMediator clipboard = new ClipboardMediator();

        // Gestionnaire de persistance JSON
        PersistenceManager persistence = new JsonPersistenceManager(jsonPath);

        // --- Contrôleurs (pattern MVC) ---

        // Contrôleur du zoom (écoute les événements souris de ImageView)
        ZoomController zoomCtrl = new ZoomController(imageView, bus);
        imageView.addImageViewListener(zoomCtrl);

        // Panning (déplacement image)
        PanController panCtrl = new PanController(imageView, bus);
        imageView.addImageViewListener(panCtrl);

        // Undo / Redo avec le CommandBus
        UndoRedoController undoCtrl = new UndoRedoController(imageView, bus);

        // Copier/coller de la perspective
        CopyPasteController copyPasteCtrl =
            new CopyPasteController(imageView, bus, clipboard);
        copyPasteCtrl.setStrategy(new CopyBoth()); // mode par défaut
        imageView.addImageViewListener(copyPasteCtrl);

        // Sauvegarde de toutes les perspectives vers le JSON
        SaveController saveCtrl = new SaveController(imageView, bus, persistence);

        // Chargement depuis JSON
        LoadController loadCtrl = new LoadController(imageView, bus, persistence, model);

        // --- Interface Graphique (Boutons & ComboBox) ---
        JFrame frame = new JFrame("Visualiseur multi-perspectives");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Boutons
        JButton btnPlus     = new JButton("+");     // zoom in
        JButton btnMinus    = new JButton("-");     // zoom out
        JButton btnUndo     = new JButton("Undo");
        JButton btnRedo     = new JButton("Redo");
        JButton btnCopy     = new JButton("Copy");
        JButton btnCopyX    = new JButton("Copy T"); // copy translation only
        JButton btnCopyZ    = new JButton("Copy Z"); // copy zoom only
        JButton btnPaste    = new JButton("Paste");
        JButton btnPasteX   = new JButton("Paste T");
        JButton btnPasteZ   = new JButton("Paste Z");
        JButton btnSave     = new JButton("Save");

        // Actions des boutons
        btnPlus.addActionListener(e -> zoomCtrl.handleZoomIn());
        btnMinus.addActionListener(e -> zoomCtrl.handleZoomOut());
        btnUndo.addActionListener(e -> undoCtrl.handleUndo());
        btnRedo.addActionListener(e -> undoCtrl.handleRedo());

        // Copier tout
        btnCopy.addActionListener(e -> {
            copyPasteCtrl.setStrategy(new CopyBoth());
            copyPasteCtrl.handleCopy();
        });

        // Copier uniquement le zoom
        btnCopyZ.addActionListener(e -> {
            copyPasteCtrl.setStrategy(new CopyScaleOnly());
            copyPasteCtrl.handleCopy();
        });

        // Coller uniquement le zoom
        btnPasteZ.addActionListener(e -> {
            copyPasteCtrl.setStrategy(new CopyScaleOnly());
            copyPasteCtrl.handlePaste();
        });

        // Copier uniquement la translation
        btnCopyX.addActionListener(e -> {
            copyPasteCtrl.setStrategy(new CopyTranslationOnly());
            copyPasteCtrl.handleCopy();
        });

        // Coller tout (stratégie courante)
        btnPaste.addActionListener(e -> copyPasteCtrl.handlePaste());

        // Coller uniquement translation
        btnPasteX.addActionListener(e -> {
            copyPasteCtrl.setStrategy(new CopyTranslationOnly());
            copyPasteCtrl.handlePaste();
        });

        // Sauvegarde JSON
        btnSave.addActionListener(e -> saveCtrl.handleSave());

        // Ajout des boutons au panneau
        controls.add(btnPlus);
        controls.add(btnMinus);
        controls.add(btnUndo);
        controls.add(btnRedo);
        controls.add(btnCopy);
        controls.add(btnCopyX);
        controls.add(btnCopyZ);
        controls.add(btnPaste);
        controls.add(btnPasteX);
        controls.add(btnPasteZ);
        controls.add(btnSave);

        // --- Chargement des perspectives depuis le JSON ---
        List<Perspective> loaded = loadCtrl.handleLoadAll();

        // ComboBox affichant toutes les perspectives
        JComboBox<Perspective> combo
            = new JComboBox<>(loaded.toArray(new Perspective[0]));

        // On insère la perspective principale en premier
        combo.insertItemAt(pMain, 0);
        combo.setSelectedIndex(0);

        // Lorsqu’on change de perspective dans la liste
        combo.addActionListener(e -> {
            Perspective selected = (Perspective) combo.getSelectedItem();
            if (selected != null && selected != pMain) {
                // On applique les paramètres dans la perspective principale
                pMain.setScale(selected.getScale());
                pMain.setTranslation(selected.getTranslation());
                pMain.setName(selected.getName());
            }
        });

        controls.add(new JLabel("Perspectives :"));
        controls.add(combo);

        // Ajout des vues à la fenêtre
        frame.add(controls, BorderLayout.NORTH);
        frame.add(composite, BorderLayout.CENTER);

        // Configuration de la fenêtre
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
