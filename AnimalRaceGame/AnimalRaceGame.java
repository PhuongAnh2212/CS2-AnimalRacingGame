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
    private boolean p1Frozen = false;
    private boolean p2Frozen = false;
    private ObstacleManager obstacleManager;
    private CheckWinner checkWinner;
    private Timer mysteryBoxTimer;
    private boolean p1Invincible = false;
    private boolean p2Invincible = false;
    private MysteryBox box1, box2;
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

                            obstacleManager = new ObstacleManager(AnimalRaceGame.this);
                            obstacleManager.startObstacleTimer(player1Label, player2Label);

                            checkWinner = new CheckWinner(player1Label, player2Label, AnimalRaceGame.this);

                            mysteryBoxTimer = new Timer(3000, ev2 -> spawnMysteryBoxes());
                            mysteryBoxTimer.start();
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

                    if (key == KeyEvent.VK_A && !aPressed && !isPlayerFrozen(1)) {
                        aPressed = true;
                        movePlayer(1, 10);
                    }

                    if (key == KeyEvent.VK_L && !lPressed && !isPlayerFrozen(2)) {
                        lPressed = true;
                        movePlayer(2, 10);
                    }

                    if (box1 != null && player1Label.getBounds().intersects(box1.getBounds())) {
                        applyMysteryEffect(1, box1);
                        box1 = null;
                    }

                    if (box2 != null && player2Label.getBounds().intersects(box2.getBounds())) {
                        applyMysteryEffect(2, box2);
                        box2 = null;
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

    private void spawnMysteryBoxes() {
        if (box1 != null) remove(box1);
        if (box2 != null) remove(box2);

        int p1Y = player1Label.getY();
        box1 = new MysteryBox(player1Label, player2Label);
        box1.setLocation(Math.min(gamePanel.player1X + 500, raceLength - box1.getWidth()), p1Y + 200);
        add(box1);

        int p2Y = player2Label.getY();
        box2 = new MysteryBox(player1Label, player2Label);
        box2.setLocation(Math.min(gamePanel.player2X + 500, raceLength - box2.getWidth()), p2Y + 200);
        add(box2);

        repaint();
    }

    private void applyMysteryEffect(int playerNumber, MysteryBox box) {
        if (box == null) return;
        MysteryEffect effect = box.getEffect();
        effect.apply(this, playerNumber);
        remove(box);
        repaint();
    }

    public void freezePlayer(int playerNum) {
        if (playerNum == 1) p1Frozen = true;
        else if (playerNum == 2) p2Frozen = true;

        Timer t = new Timer(2000, e -> {
            if (playerNum == 1) p1Frozen = false;
            else if (playerNum == 2) p2Frozen = false;
        });
        t.setRepeats(false);
        t.start();
    }

    public boolean isPlayerFrozen(int playerNum) {
        return (playerNum == 1) ? p1Frozen : p2Frozen;
    }


    public void movePlayer(int playerNumber, int distance) {
        gamePanel.movePlayer(playerNumber, distance);
    }

    public void setPlayerInvincible(int playerNumber, boolean isInvincible) {
        if (playerNumber == 1) p1Invincible = isInvincible;
        else if (playerNumber == 2) p2Invincible = isInvincible;
    }

    public boolean isPlayerInvincible(int playerNumber) {
        return (playerNumber == 1) ? p1Invincible : p2Invincible;
    }
}