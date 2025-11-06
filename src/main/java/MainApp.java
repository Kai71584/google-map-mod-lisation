import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

// ----------------- DEUXIÈME PAGE -----------------
class ImageViewer2 extends JPanel {

    private BufferedImage image;
    private double zoom = 1.0;
    private double zoomStep = 0.1;
    private double minZoom = 0.2, maxZoom = 5.0;
    private int translateX = 0;
    private int translateY = 0;
    private int lastDragX, lastDragY;
    private String nomEnregistre = ""; // variable pour stocker le nom

    private JTextField positionField;

    public ImageViewer2(String imagePath, JTextField positionField, int x, int y, double zoomInitial) {
        this.positionField = positionField;
        this.translateX = x;
        this.translateY = y;
        this.zoom = zoomInitial;

        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Impossible de charger l'image : " + e.getMessage());
        }

        addMouseWheelListener(e -> {
            if (e.getPreciseWheelRotation() < 0)
                zoom = Math.min(zoom + zoomStep, maxZoom);
            else
                zoom = Math.max(zoom - zoomStep, minZoom);
            updatePositionField();
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastDragX = e.getX();
                lastDragY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastDragX;
                int dy = e.getY() - lastDragY;
                translateX += dx;
                translateY += dy;
                lastDragX = e.getX();
                lastDragY = e.getY();
                updatePositionField();
                repaint();
            }
        });
    }

    private void updatePositionField() {
        if (positionField != null) {
            positionField.setText("X: " + translateX + " | Y: " + translateY + " | Zoom: " + String.format("%.2f", zoom));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            AffineTransform transform = new AffineTransform();
            transform.translate(translateX, translateY);
            transform.scale(zoom, zoom);
            g2d.drawImage(image, transform, null);
        }
    }

    public void zoomIn() { zoom = Math.min(zoom + zoomStep, maxZoom); updatePositionField(); repaint(); }
    public void zoomOut() { zoom = Math.max(zoom - zoomStep, minZoom); updatePositionField(); repaint(); }
    public void resetView() { zoom = 1.0; translateX = 0; translateY = 0; updatePositionField(); repaint(); }

    private void ouvrirFenetreSecondaire() {
        JDialog fenetreSecondaire = new JDialog((Frame) null, "Saisie du nom", true);
        fenetreSecondaire.setSize(300, 120);
        fenetreSecondaire.setLayout(new FlowLayout());

        JTextField champNom = new JTextField(15);
        JButton boutonValider = new JButton("Valider");

        boutonValider.addActionListener(e -> {
            String texte = champNom.getText().trim();
            if (!texte.isEmpty()) {
                nomEnregistre = texte;
                fenetreSecondaire.dispose();
            } else {
                JOptionPane.showMessageDialog(fenetreSecondaire, "Veuillez saisir un nom !");
            }
        });

        fenetreSecondaire.add(new JLabel("Entrez votre nom :"));
        fenetreSecondaire.add(champNom);
        fenetreSecondaire.add(boutonValider);

        fenetreSecondaire.setLocationRelativeTo(null);
        fenetreSecondaire.setVisible(true);
    }

    public void ajoutinfo() {
        ouvrirFenetreSecondaire();

        ObjectMapper mapper = new ObjectMapper();
        File fichier = new File("data.json");

        try {
            ArrayNode personnes;

            if (fichier.exists() && fichier.length() > 0) {
                JsonNode racine = mapper.readTree(fichier);

                if (racine.isArray()) {
                    personnes = (ArrayNode) racine;
                } else if (racine.isObject()) {
                    personnes = mapper.createArrayNode();
                    personnes.add(racine);
                } else {
                    personnes = mapper.createArrayNode();
                }
            } else {
                personnes = mapper.createArrayNode();
            }

            ObjectNode personne = mapper.createObjectNode();
            personne.put("nom", nomEnregistre);
            personne.put("translationX", translateX);
            personne.put("translationY", translateY);
            personne.put("zoom", zoom);

            personnes.add(personne);

            mapper.writerWithDefaultPrettyPrinter().writeValue(fichier, personnes);
            JOptionPane.showMessageDialog(this, "Informations ajoutées dans data.json !");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement : " + e.getMessage());
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (image == null) return new Dimension(800, 600);
        return new Dimension(image.getWidth(), image.getHeight());
    }
}

// ----------------- PREMIÈRE PAGE -----------------
class PremierePage extends JFrame {

    public PremierePage(String jsonPath, String imagePath) {
        setTitle("Sélection de l'entrée JSON");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new GridLayout(0, 1));

        try {
            ObjectMapper mapper = new ObjectMapper();
            File fichier = new File(jsonPath);

            if (!fichier.exists()) {
                JOptionPane.showMessageDialog(this, "Le fichier JSON est introuvable !");
                return;
            }

            JsonNode racine = mapper.readTree(fichier);
            if (!racine.isArray()) {
                JOptionPane.showMessageDialog(this, "Le JSON n'est pas un tableau !");
                return;
            }

            ArrayNode array = (ArrayNode) racine;

            for (JsonNode obj : array) {
                String nom = obj.has("nom") ? obj.get("nom").asText() : "Sans nom";
                int translationX = obj.has("translationX") ? obj.get("translationX").asInt() : 0;
                int translationY = obj.has("translationY") ? obj.get("translationY").asInt() : 0;
                double zoom = 1.0;
                if (obj.has("zoom")) zoom = obj.get("zoom").asDouble();
                else if (obj.has("Zoom")) zoom = obj.get("Zoom").asDouble();
                zoom = zoom;
                JButton bouton = new JButton(nom);
                double finalZoom = zoom;
                bouton.addActionListener(e -> {
                    ouvrirDeuxiemePage(imagePath, translationX, translationY, finalZoom);
                    dispose();
                });

                add(bouton);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la lecture du JSON : " + e.getMessage());
        }

        setVisible(true);
    }

    private void ouvrirDeuxiemePage(String imagePath, int x, int y, double zoom) {
        JFrame frame = new JFrame("Visualiseur d'image avec boutons et position");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField positionField = new JTextField();
        positionField.setEditable(false);
        positionField.setColumns(20);
        positionField.setOpaque(true);
        positionField.setBackground(new Color(255, 255, 255, 180));

        ImageViewer2 viewer = new ImageViewer2(imagePath, positionField, x, y, zoom);

        JPanel boutonPanel = new JPanel();
        boutonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        boutonPanel.setOpaque(false);

        JButton zoomPlus = new JButton("+");
        JButton zoomMoins = new JButton("-");
        JButton reset = new JButton("⟳");
        JButton enregistrer = new JButton("enregistrer");

        Dimension size = new Dimension(50, 30);
        zoomPlus.setPreferredSize(size);
        zoomMoins.setPreferredSize(size);
        reset.setPreferredSize(size);

        zoomPlus.addActionListener(e -> viewer.zoomIn());
        zoomMoins.addActionListener(e -> viewer.zoomOut());
        reset.addActionListener(e -> viewer.resetView());
        enregistrer.addActionListener(e -> viewer.ajoutinfo());

        boutonPanel.add(zoomPlus);
        boutonPanel.add(zoomMoins);
        boutonPanel.add(reset);
        boutonPanel.add(enregistrer);

        JLayeredPane layeredPane = new JLayeredPane();
        JScrollPane scrollPane = new JScrollPane(viewer);
        scrollPane.setBounds(0, 0, 1000, 800);

        boutonPanel.setBounds(20, 20, 300, 50);
        positionField.setBounds(20, 80, 220, 30);

        layeredPane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(boutonPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(positionField, JLayeredPane.PALETTE_LAYER);
        //allo

        frame.setContentPane(layeredPane);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

// ----------------- MAIN -----------------
public class MainApp {
    public static void main(String[] args) {
        String cheminImage = "C:\\Users\\salut\\Pictures\\Screenshots\\Capture d’écran 2025-11-04 102530.png";
        String cheminJson = "data.json";
        SwingUtilities.invokeLater(() -> new PremierePage(cheminJson, cheminImage));
    }
}
