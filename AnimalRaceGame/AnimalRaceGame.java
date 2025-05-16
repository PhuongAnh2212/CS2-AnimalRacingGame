package AnimalRaceGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.io.IOException;

public class AnimalRaceGame extends JFrame {
    private JLabel player1Label, player2Label, countdown;
    private CheckWinner checkWinner;
    private boolean gameStarted = false;
    private GamePanel gamePanel;
    private boolean p1Frozen = false;
    private boolean p2Frozen = false;
    private ArrayList<Obstacle> obstacles1;
    private ArrayList<Obstacle> obstacles2;

    private int[][] obstaclePositions = {
            {800, 100},  // Obstacle 1 for Player 1
            {1400, 100}, // Obstacle 2 for Player 1
            {2000, 100}, // Obstacle 3 for Player 1
            {800, 350},  // Obstacle 1 for Player 2
            {1400, 350}, // Obstacle 2 for Player 2
            {2000, 350}  // Obstacle 3 for Player 2
    };

    public AnimalRaceGame(String p1Animal, String p2Animal) {
        setTitle("Animal Race Game");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        obstacles1 = new ArrayList<>();
        obstacles2 = new ArrayList<>();
        gamePanel = new GamePanel(p1Animal, p2Animal);
        setContentPane(gamePanel);
        setVisible(true);
    }

    class GamePanel extends JPanel {
        private Image backgroundImage;
        private int raceLength = 2340; // Total race length
        private int viewWidth = 1920; // Window width
        private int backgroundX = 0; // Background offset
        private Font retroByteFont;
        private boolean aPressed = false;
        private boolean lPressed = false;
        private int player1X = 50; // Starting X position
        private int player2X = 50; // Starting X position
        private boolean debugMode = true;

        public GamePanel(String p1Animal, String p2Animal) {
            setLayout(null);
            setFocusable(true);
            backgroundImage = new ImageIcon("ingame/background.jpg").getImage();

            try {
                File fontFile = new File("resources/fonts/retrobyte.ttf");
                retroByteFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(40f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(retroByteFont);
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
            }

            ImageIcon player1Icon = new ImageIcon("ingame/" + p1Animal.toLowerCase() + ".png");
            player1Label = new JLabel(player1Icon);
            player1Label.setBounds(50, 100, player1Icon.getIconWidth(), player1Icon.getIconHeight());

            ImageIcon player2Icon = new ImageIcon("ingame/" + p2Animal.toLowerCase() + ".png");
            player2Label = new JLabel(player2Icon);
            player2Label.setBounds(50, 350, player2Icon.getIconWidth(), player2Icon.getIconHeight());

            countdown = new JLabel("Ready?");
            countdown.setFont(retroByteFont);
            countdown.setBounds(300, 20, 300, 50);
            add(countdown);

            add(player1Label);
            add(player2Label);

            String[] countdownText = {"3", "2", "1", "Go!"};
            final int[] count = {0};
            Timer countdownTimer = new Timer(1000, e -> {
                if (count[0] < countdownText.length) {
                    countdown.setText(countdownText[count[0]]);
                    count[0]++;
                    repaint();
                } else {
                    ((Timer)e.getSource()).stop();
                    remove(countdown);
                    gameStarted = true;
                    repaint();
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

                @Override
                public void keyReleased(KeyEvent e) {
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_A) aPressed = false;
                    if (key == KeyEvent.VK_L) lPressed = false;
                }
            });
            requestFocusInWindow();
        }

        private void updateCamera(int playerX) {
            int centerX = getLeadPlayerX() - viewWidth / 2;
            backgroundX = Math.min(Math.max(centerX, 0), raceLength - viewWidth);
            player1Label.setLocation(player1X - backgroundX, player1Label.getY());
            player2Label.setLocation(player2X - backgroundX, player2Label.getY());
        }

        private int getLeadPlayerX() {
            return Math.max(player1X, player2X);
        }

        public void movePlayer(int playerNumber, int distance) {
            if (playerNumber == 1 && !p1Frozen) {
                player1X = Math.max(0, Math.min(player1X + distance, raceLength - player1Label.getWidth()));
                checkSpawnObstacle(1, player1X);
            } else if (playerNumber == 2 && !p2Frozen) {
                player2X = Math.max(0, Math.min(player2X + distance, raceLength - player2Label.getWidth()));
                checkSpawnObstacle(2, player2X);
            }
            updateCamera(getLeadPlayerX());
            repaint();
        }

        private void checkSpawnObstacle(int playerNumber, int playerX) {
            ArrayList<Obstacle> obstacles = (playerNumber == 1) ? obstacles1 : obstacles2;
            int yPos = (playerNumber == 1) ? 100 : 350;
            int[] positions = (playerNumber == 1) ? new int[]{800, 1400, 2000} : new int[]{800, 1400, 2000};

            for (int xPos : positions) {
                if (playerX >= xPos - 500 && playerX <= xPos && !obstacleExists(obstacles, xPos)) {
                    try {
                        obstacles.add(new Obstacle(xPos, yPos));
                        System.out.println("Spawned obstacle for Player " + playerNumber + " at (" + xPos + ", " + yPos + ")");
                    } catch (Exception e) {
                        System.err.println("Failed to spawn obstacle: " + e.getMessage());
                    }
                }
            }
        }

        private boolean obstacleExists(ArrayList<Obstacle> obstacles, int xPos) {
            for (Obstacle obs : obstacles) {
                if (obs.getBounds().x == xPos) return true;
            }
            return false;
        }

        private void checkCollisions(int playerNumber) {
            ArrayList<Obstacle> obstacles = (playerNumber == 1) ? obstacles1 : obstacles2;
            JLabel player = (playerNumber == 1) ? player1Label : player2Label;
            int playerX = (playerNumber == 1) ? player1X : player2X;
            boolean frozen = (playerNumber == 1) ? p1Frozen : p2Frozen;

            if (frozen) return;

            Rectangle playerBounds = new Rectangle(playerX + 156, player.getY() + 156, 200, 200);

            for (Obstacle obs : obstacles) {
                Rectangle obsBounds = obs.getBounds();
                if (playerBounds.intersects(obsBounds)) {
                    System.out.println("Collision detected for Player " + playerNumber + " with obstacle at " + obsBounds);
                    if (playerNumber == 1) p1Frozen = true;
                    else p2Frozen = true;

                    Timer freezeTimer = new Timer(2000, e -> {
                        if (playerNumber == 1) p1Frozen = false;
                        else p2Frozen = false;
                        System.out.println("Player " + playerNumber + " unfrozen");
                    });
                    freezeTimer.setRepeats(false);
                    freezeTimer.start();
                    obstacles.remove(obs);
                    break;
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
                g2d.drawString("Obstacles1: " + obstacles1.size() + ", Obstacles2: " + obstacles2.size(), 10, 20);
            }
            g2d.drawImage(((ImageIcon) player1Label.getIcon()).getImage(), player1X - backgroundX, player1Label.getY(), this);
            g2d.drawImage(((ImageIcon) player2Label.getIcon()).getImage(), player2X - backgroundX, player2Label.getY(), this);
        }
    }

    public boolean isPlayerFrozen(int playerNum) {
        return (playerNum == 1) ? p1Frozen : p2Frozen;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AnimalRaceGame("cat", "dog"));
    }
}