package AnimalRaceGame;

import javax.swing.*;
import java.awt.*;

public class StartScreen extends JFrame {

    private Image backgroundImage;

    public StartScreen() {
        setTitle("Animal Race Game");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load background image
        backgroundImage = new ImageIcon("StartScreen.png").getImage();

        // Panel with background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null); // for absolute positioning

        JButton startButton = new JButton("Start") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 30;
                g2.setColor(Color.WHITE); // Button fill color
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#a9a6ff")); // Border color
                g2.setStroke(new BasicStroke(5)); // Border thickness
                int arc = 30;
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
                g2.dispose();
            }
        };

        startButton.setBounds(640, 700, 150, 50);
        startButton.setForeground(Color.BLACK);
        startButton.setContentAreaFilled(false);
        startButton.setFocusPainted(false);

        backgroundPanel.add(startButton);

        startButton.addActionListener(e -> {
            new ChooseAnimal();
            dispose();
        });

        setContentPane(backgroundPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GlobalUI.applyGlobalFont("RetroByte.ttf", 35f);
            new StartScreen();
        });
    }
}
