package AnimalRaceGame;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.awt.event.*;
import java.io.IOException;

public class AnimalRaceGame extends JFrame {
    private JLabel player1Label, player2Label, countdown;
    private CheckWinner checkWinner;
    private boolean gameStarted = false;
    private int raceLength = 3840;
    private GamePanel gamePanel;


    public AnimalRaceGame(String p1Animal, String p2Animal) {
        setTitle("Animal Race Game");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamePanel = new GamePanel(p1Animal, p2Animal);

        setContentPane(gamePanel);

        setVisible(true);
    }

    class GamePanel extends JPanel {
        private Image backgroundImage;
        private int raceLength = 2540; // Total race length (background width)
        private int viewWidth = 1920; // Window width
        private int backgroundX = 0; // Current background offset
        private Font retroByteFont;

        private boolean aPressed = false;
        private boolean lPressed = false;

        private void updateCamera(int playerX) {
            // Use the lead player X position to calculate the center.
            int centerX = getLeadPlayerX() - viewWidth / 2;
            backgroundX = Math.min(Math.max(centerX, 0), raceLength - viewWidth);

            // Move players to the correct positions based on the background's current offset.
            int p1ScreenX = player1Label.getX() - backgroundX;
            int p2ScreenX = player2Label.getX() - backgroundX;
            player1Label.setLocation(p1ScreenX, player1Label.getY());
            player2Label.setLocation(p2ScreenX, player2Label.getY());
        }


        public GamePanel(String p1Animal, String p2Animal) {
            setLayout(null);
            setFocusable(true);
            backgroundImage = new ImageIcon("ingame/background.jpg").getImage();

            try {
                File fontFile = new File("resources/fonts/retrobyte.ttf");  // Update path as needed
                retroByteFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(40f); // You can adjust font size here
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(retroByteFont); // Register the font
            } catch (FontFormatException | IOException e) {
                e.printStackTrace(); // Handle the exception
            }

            // Set up player 1
            ImageIcon player1Icon = new ImageIcon("ingame/" + p1Animal.toLowerCase() + ".png");
            player1Label = new JLabel(player1Icon);
            player1Label.setBounds(50, 100, player1Icon.getIconWidth(), player1Icon.getIconHeight());

            // Set up player 2
            ImageIcon player2Icon = new ImageIcon("ingame/" + p2Animal.toLowerCase() + ".png");
            player2Label = new JLabel(player2Icon);
            player2Label.setBounds(50, 350, player2Icon.getIconWidth(), player2Icon.getIconHeight());

            // Set up countdown label
            countdown = new JLabel("Ready?");
            countdown.setFont(retroByteFont);
            countdown.setBounds(300, 20, 300, 50);
            add(countdown);

            Queue<String> countdownQueue = new LinkedList<>();
            Collections.addAll(countdownQueue, "3", "2", "1", "Go!");

            Timer countdownTimer = new Timer(1000, null);
            countdownTimer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!countdownQueue.isEmpty()) {
                        countdown.setText(countdownQueue.poll());
                        repaint();
                    } else {
                        countdownTimer.stop();
                        Timer removeLabelTimer = new Timer(1000, evt -> {
                            remove(countdown);
                            repaint();

                            gameStarted = true;

                            checkWinner = new CheckWinner(player1Label, player2Label, AnimalRaceGame.this);

                        });
                        removeLabelTimer.setRepeats(false);
                        removeLabelTimer.start();
                    }
                }
            });
            countdownTimer.start();

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (!gameStarted) return;

                    int key = e.getKeyCode();

                    if (key == KeyEvent.VK_A && !aPressed) {
                        aPressed = true;
                        movePlayer(1, 10);
                    }

                    if (key == KeyEvent.VK_L && !lPressed) {
                        lPressed = true;
                        movePlayer(2, 10);
                    }

                    checkWinner.check();
                }
                public void keyReleased(KeyEvent e) {
                    int key = e.getKeyCode();

                    if (key == KeyEvent.VK_A) {
                        aPressed = false;
                    }

                    if (key == KeyEvent.VK_L) {
                        lPressed = false;
                    }
                }
            });

            requestFocusInWindow();
        }
        private int player1X = -100;
        private int player2X = -100;

        private int getLeadPlayerX() {
            // Return the X position of the player who is ahead.
            return Math.max(player1X, player2X);
        }

        public void movePlayer(int playerNumber, int distance) {
            if (playerNumber == 1) {
                player1X = Math.max(0, Math.min(player1X + distance, raceLength - player1Label.getWidth()));
            } else {
                player2X = Math.max(0, Math.min(player2X + distance, raceLength - player2Label.getWidth()));
            }

            updateCamera(getLeadPlayerX());
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.drawImage(backgroundImage, -backgroundX, 0, raceLength, getHeight(), this);

            g2d.drawImage(((ImageIcon) player1Label.getIcon()).getImage(), player1X - backgroundX, player1Label.getY(), this);
            g2d.drawImage(((ImageIcon) player2Label.getIcon()).getImage(), player2X - backgroundX, player2Label.getY(), this);
        }

    }

    public void movePlayer(int playerNumber, int distance) {
        gamePanel.movePlayer(playerNumber, distance);
    }
}