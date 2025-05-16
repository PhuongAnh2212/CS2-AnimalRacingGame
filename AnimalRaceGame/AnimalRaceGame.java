package AnimalRaceGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class AnimalRaceGame extends JFrame {
    private JLabel player1Label, player2Label, countdown;
    private CheckWinner checkWinner;
    private boolean gameStarted = false;
    private GamePanel gamePanel;

    private final int[] spawnThresholds = {600, 1800}; // X positions to trigger spawns

    // Freeze player
    private boolean p1Frozen = false;
    private boolean p2Frozen = false;
    private ArrayList<Obstacle> obstacles1;
    private ArrayList<Obstacle> obstacles2;


    private Random rand = new Random();
    private Image backgroundImage;
    private int raceLength;


    public AnimalRaceGame(String p1Animal, String p2Animal) {
        setTitle("Animal Race Game");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamePanel = new GamePanel(p1Animal, p2Animal);

        obstacles1 = new ArrayList<>();
        obstacles2 = new ArrayList<>();

        setContentPane(gamePanel);

        setVisible(true);

        //Time random for each player

//        Timer startSpawningTimer = new Timer(5000, e -> { // Wait for countdown (5s)
//            startObstacleSpawning();
//            ((Timer)e.getSource()).stop();
//        });

//
//        startSpawningTimer.setRepeats(false);
//        startSpawningTimer.start();
    }

    class GamePanel extends JPanel {
        private Image backgroundImage;
        private int raceLength = 2540; // Total race length (background width)
        private int viewWidth = 1920; // Window width
        private int backgroundX = 0; // Current background offset
        private Font retroByteFont;

        private boolean aPressed = false;
        private boolean lPressed = false;

        private int player1X = -100; // Starting X position
        private int player2X = -100; // Starting X position

        private boolean debugMode = true;
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
            // System.out.println("Player1 image: " + p1Animal + ", size: " + player1Icon.getIconWidth() + "x" + player1Icon.getIconHeight());
            // Set up player 2
            ImageIcon player2Icon = new ImageIcon("ingame/" + p2Animal.toLowerCase() + ".png");
            player2Label = new JLabel(player2Icon);
            player2Label.setBounds(50, 350, player2Icon.getIconWidth(), player2Icon.getIconHeight());
            // System.out.println("Player2 image: " + p2Animal + ", size: " + player2Icon.getIconWidth() + "x" + player2Icon.getIconHeight());
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

                    checkWinner = new CheckWinner(player1X, player2X, AnimalRaceGame.this);
                    checkWinner.check();

                    if (key == KeyEvent.VK_A && !aPressed && !isPlayerFrozen(1)) {
                        aPressed = true;
                        movePlayer(1, 10);
                        checkCollisions(1);
                    }

                    if (key == KeyEvent.VK_L && !lPressed && !isPlayerFrozen(2)) {
                        lPressed = true;
                        movePlayer(2, 10);
                        checkCollisions(2);
                    }
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

        private int getLeadPlayerX() {
            // Return the X position of the player who is ahead.
            return Math.max(player1X, player2X);
        }

        public void movePlayer(int playerNumber, int distance) {
            if (playerNumber == 1) {
                if (!p1Frozen) {
                    player1X = Math.max(0, Math.min(player1X + distance, raceLength - player1Label.getWidth()));
                    checkSpawnObstacle(1, player1X);
                }
            } else {
                if (!p2Frozen) {
                    player2X = Math.max(0, Math.min(player2X + distance, raceLength - player2Label.getWidth()));
                    checkSpawnObstacle(2, player2X);
                }
            }
            updateCamera(getLeadPlayerX());
            repaint();
        }

        private void checkCollisions(int playerNumber) {
            if (obstacles1 == null || obstacles2 == null) {
                System.err.println("Obstacles not initialized for collision check");
                return;
            }
            JLabel player = (playerNumber == 1) ? player1Label : player2Label;
            ArrayList<Obstacle> obstacles = (playerNumber == 1) ? obstacles1 : obstacles2;
            boolean frozen = (playerNumber == 1) ? p1Frozen : p2Frozen;
            int playerX = (playerNumber == 1) ? player1X : player2X;

            if (frozen) {
                System.out.println("Player " + playerNumber + " is already frozen");
                return;
            }

            // Use 200x200 hitbox centered in 512x512 player
            Rectangle playerBounds = new Rectangle(playerX + 156, player.getY() + 156, 200, 200);
            System.out.println("Checking collisions for Player " + playerNumber + " at " + playerBounds);

            for (Obstacle obs : obstacles) {
                Rectangle obsBounds = obs.getBounds();
                System.out.println("  Obstacle at " + obsBounds);
                if (playerBounds.intersects(obsBounds)) {
                    System.out.println("Collision detected for Player " + playerNumber + " at " + playerBounds + " with obstacle at " + obsBounds);
                    if (playerNumber == 1) {
                        p1Frozen = true;
                    } else {
                        p2Frozen = true;
                    }
                    Timer freezeTimer = new Timer(2000, e -> {
                        if (playerNumber == 1) {
                            p1Frozen = false;
                        } else {
                            p2Frozen = false;
                        }
                        System.out.println("Player " + playerNumber + " unfrozen");
                    });
                    freezeTimer.setRepeats(false);
                    freezeTimer.start();
                    obstacles.clear(); // Clear obstacle on collision
                    break;
                } else {
                    System.out.println("No collision: Player " + playerNumber + " bounds " + playerBounds + " does not intersect obstacle at " + obsBounds);
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.drawImage(backgroundImage, -backgroundX, 0, raceLength, getHeight(), this);

            for (Obstacle obs : obstacles1) {
                obs.draw(g2d, backgroundX);
                if (debugMode) {
                    Rectangle bounds = obs.getBounds();
                    g2d.setColor(Color.RED);
                    g2d.drawRect(bounds.x - backgroundX, bounds.y, bounds.width, bounds.height);
                }
            }
            for (Obstacle obs : obstacles2) {
                obs.draw(g2d, backgroundX);
                if (debugMode) {
                    Rectangle bounds = obs.getBounds();
                    g2d.setColor(Color.RED);
                    g2d.drawRect(bounds.x - backgroundX, bounds.y, bounds.width, bounds.height);
                }
            }
            if (debugMode) {
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                g2d.drawString("Obstacles1: " + obstacles1.size() + ", Obstacles2: " + obstacles2.size(), 10, 20);
                for (int i = 0; i < obstacles1.size(); i++) {
                    Obstacle obs = obstacles1.get(i);
//                    g2d.drawString("Obs1-" + i + ": (" + obs.getBounds().x + ", " + obs.getBounds().y + ")", 10, 40 + i * 20);
                }
                for (int i = 0; i < obstacles2.size(); i++) {
                    Obstacle obs = obstacles2.get(i);
//                    g2d.drawString("Obs2-" + i + ": (" + obs.getBounds().x + ", " + obs.getBounds().y + ")", 150, 40 + i * 20);
                }
            }
            g2d.drawImage(((ImageIcon) player1Label.getIcon()).getImage(), player1X - backgroundX, player1Label.getY(), this);
            g2d.drawImage(((ImageIcon) player2Label.getIcon()).getImage(), player2X - backgroundX, player2Label.getY(), this);
        }

    }

    public boolean isPlayerFrozen(int playerNum) {
        return (playerNum == 1) ? p1Frozen : p2Frozen;
    }

    private void spawnObstacle(int playerNum) {
        ArrayList<Obstacle> obstacles = (playerNum == 1) ? obstacles1 : obstacles2;
        int y = (playerNum == 1) ? 300 : 550;
        int playerX = (playerNum == 1) ? gamePanel.player1X : gamePanel.player2X;
        obstacles.clear();
        int x = playerX + rand.nextInt(500) - 400; // 400–900 ahead
        x = Math.max(x, playerX - 400); // Ensure ahead
        x = Math.min(x, raceLength + 200); // Adjust for obstacle width
        try {
            obstacles.add(new Obstacle(x, y));
            int distance = x - playerX;
            System.out.println("Spawned Obstacle" + playerNum + " at (" + x + ", " + y + "), Player" + playerNum + " at (" + playerX + "), Distance: " + distance);
        } catch (Exception e) {
            System.err.println("Failed to spawn obstacle for Player " + playerNum + " at (" + x + ", " + y + "): " + e.getMessage());
        }
        gamePanel.repaint();
    }

//    private void startObstacleSpawning() {
//        // Timer for Player 1 obstacles
//        Timer timer1 = new Timer(rand.nextInt(3000) + 2000, e -> { // 2-5 seconds
//            if (gameStarted) {
//                spawnObstacles(1);
//                ((Timer)e.getSource()).setDelay(rand.nextInt(3000) + 2000); // New random delay
//            }
//        });
//        timer1.setRepeats(true);
//        timer1.start();
//
//        // Timer for Player 2 obstacles
//        Timer timer2 = new Timer(rand.nextInt(3000) + 2000, e -> {
//            if (gameStarted) {
//                spawnObstacles(2);
//                ((Timer)e.getSource()).setDelay(rand.nextInt(3000) + 2000);
//            }
//        });
//        timer2.setRepeats(true);
//        timer2.start();
//    }

    private void checkSpawnObstacle(int playerNumber, int playerX) {
        if (playerNumber == 1 && playerX >= spawnThresholds[0]) {
            int y = player1Label.getY();  // Align obstacle with Player 1
            Obstacle obs = new Obstacle(playerX + 500, y);
            obstacles1.add(obs);
            spawnThresholds[0] += 1000; // Update threshold for next spawn
        } else if (playerNumber == 2 && playerX >= spawnThresholds[1]) {
            int y = player2Label.getY();  // Align obstacle with Player 2
            Obstacle obs = new Obstacle(playerX + 500, y);
            obstacles2.add(obs);
            spawnThresholds[1] += 1000; // Update threshold for next spawn
        }
    }

    public void checkObstaclePositions() {
        System.out.println("Checking obstacle positions...");
        if (obstacles1 == null || obstacles2 == null) {
            System.out.println("Obstacles not initialized!");
            return;
        }
        System.out.println("Obstacles1 (" + obstacles1.size() + "):");
        for (int i = 0; i < obstacles1.size(); i++) {
            Obstacle obs = obstacles1.get(i);
            System.out.println("  Obs1-" + i + ": (" + obs.getBounds().x + ", " + obs.getBounds().y + ")");
        }
        System.out.println("Obstacles2 (" + obstacles2.size() + "):");
        for (int i = 0; i < obstacles2.size(); i++) {
            Obstacle obs = obstacles2.get(i);
            System.out.println("  Obs2-" + i + ": (" + obs.getBounds().x + ", " + obs.getBounds().y + ")");
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AnimalRaceGame("cat", "dog"));
    }

}