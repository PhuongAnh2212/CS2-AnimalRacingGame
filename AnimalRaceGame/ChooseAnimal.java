package AnimalRaceGame;
import AnimalRaceGame.GlobalUI;

import java.awt.*;
import java.io.File;
import java.util.HashMap;

import javax.swing.*;

// import AnimalRaceGame;
// import StartMenu;

public class ChooseAnimal extends JFrame {
    private JToggleButton[] player1Toggles;
    private JToggleButton[] player2Toggles;
    private JLabel player1Image;
    private JLabel player2Image;
    private JButton startButton;

    private JComboBox<String> player1Combo;
    private JComboBox<String> player2Combo;
    private final String[] animals = {"dog", "cat", "rabbit"};
    private final HashMap<String, ImageIcon> animalIcons = new HashMap<>();
    private Image backgroundImage;

    public ChooseAnimal() {
        setTitle("Animal Race - Start Menu");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        backgroundImage = new ImageIcon("ChoosePlayer.png").getImage();

        for (String animal : animals) {
            animalIcons.put(animal, loadAnimalIcon(animal));
        }
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        // Styling Player 1
        player1Image = new JLabel();
        player1Image.setBounds(200, 150, 300, 300);
        backgroundPanel.add(player1Image);
        JLabel player1Label = new JLabel("Just be a");
        player1Label.setForeground(Color.BLACK);
        player1Label.setBounds(250, 460, 200, 30);
        backgroundPanel.add(player1Label);
        player1Combo = new JComboBox<>(animals);
        player1Combo.setBounds(250, 490, 200, 30);
        backgroundPanel.add(player1Combo);

        //Styling Player 2
        player2Image = new JLabel();
        player2Image.setBounds(900, 150, 300, 300);
        backgroundPanel.add(player2Image);
        JLabel player2Label = new JLabel("Or be a");
        player2Label.setForeground(Color.BLACK); // Optional, for visibility on background
        player2Label.setBounds(950, 460, 200, 30);
        backgroundPanel.add(player2Label);
        player2Combo = new JComboBox<>(animals);
        player2Combo.setBounds(950, 490, 200, 30);
        backgroundPanel.add(player2Combo);

        player1Combo.addActionListener(e -> updateAnimalImage(player1Combo, player1Image));
        player2Combo.addActionListener(e -> updateAnimalImage(player2Combo, player2Image));

        startButton = new JButton("Start Game");
        startButton.setBounds(500, 550, 200, 50);
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setBackground(Color.WHITE);
        startButton.setForeground(new Color(0xa9a6ff));
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createLineBorder(new Color(0xa9a6ff), 5, true));
        backgroundPanel.add(startButton);

        startButton.addActionListener(e -> {
            String player1Animal = (String) player1Combo.getSelectedItem();
            String player2Animal = (String) player2Combo.getSelectedItem();
            new AnimalRaceGame(player1Animal, player2Animal);
            dispose();
        });

        updateAnimalImage(player1Combo, player1Image);
        updateAnimalImage(player2Combo, player2Image);

        setVisible(true);
    }

    private void updateAnimalImage(JComboBox<String> combo, JLabel imageLabel) {
        String selectedAnimal = (String) combo.getSelectedItem();
        ImageIcon icon = animalIcons.get(selectedAnimal);
        if (icon != null) {
            Image scaled = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
        } else {
            imageLabel.setIcon(null);
        }
    }

    private ImageIcon loadAnimalIcon(String animalName) {
        String basePath = "assets/";
        String[] extensions = {".png", ".jpg", ".jpeg", ".gif"};

        for (String ext : extensions) {
            File file = new File(basePath + animalName.toLowerCase() + ext);
            if (file.exists()) {
                return new ImageIcon(file.getAbsolutePath());
            }
        }

        return null;
    }

    public static void main(String[] args) {
        GlobalUI.applyGlobalFont("RetroByte.ttf", 24f);
        new ChooseAnimal();
    }
}
