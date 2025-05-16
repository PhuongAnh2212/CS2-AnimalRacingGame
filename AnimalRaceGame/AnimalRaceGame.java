package AnimalRaceGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;


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
            {800, 256},  // Obstacle 1 for Player 1
            {1200, 505},  // Obstacle 1 for Player 2
    };

    private boolean p1Immune = false;
    private boolean p2Immune = false;
    private Set<Integer> spawnedPositionsP1 = new HashSet<>();
    private Set<Integer> spawnedPositionsP2 = new HashSet<>();

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
        private int raceLength = 2040; // Total race length
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
                        movePlayer(1, 50);
                        checkCollisions(1);
                    }
                    if (key == KeyEvent.VK_L && !lPressed && !isPlayerFrozen(2)) {
                        lPressed = true;
                        movePlayer(2, 50);
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
            int yPos = (playerNumber == 1) ? 256 : 505;
            int[] positions = new int[]{800};

            Set<Integer> spawnedPositions = (playerNumber == 1) ? spawnedPositionsP1 : spawnedPositionsP2;

            for (int xPos : positions) {
                if (playerX >= xPos - 500 && playerX <= xPos && !spawnedPositions.contains(xPos)) {
                    try {
                        obstacles.add(new Obstacle(xPos, yPos));
                        spawnedPositions.add(xPos); // Mark as spawned
                        System.out.println("Spawned obstacle for Player " + playerNumber + " at (" + xPos + ", " + yPos + ")");
                    } catch (Exception e) {
                        System.err.println("Failed to spawn obstacle: " + e.getMessage());
                    }
                }
            }
        }

//                       _oo0oo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                      0\  =  /0
//                    ___/`---'\___
//                  .' \\|     |// '.
//                 / \\|||  :  |||// \
//                / _||||| -:- |||||- \
//               |   | \\\  -  /// |   |
//               | \_|  ''\---/''  |_/ |
//               \  .-\__  '-'  ___/-. /
//             ___'. .'  /--.--\  `. .'___
//          ."" '<  `.___\_<|>_/___.' >' "".
//         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
//         \  \ `_.   \_ __\ /__ _/   .-` /  /
//     =====`-.____`.___ \_____/___.-`___.-'=====
//                       `=---='
//
//
//     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//
//               佛祖保佑         永无BUG
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
            boolean immune = (playerNumber == 1) ? p1Immune : p2Immune;

            if (frozen || immune) return; // Don't check if player is frozen or immune

            Rectangle playerBounds = new Rectangle(playerX + 156, player.getY() + 156, 200, 200);

            Iterator<Obstacle> iterator = obstacles.iterator();
            while (iterator.hasNext()) {
                Obstacle obs = iterator.next();
                Rectangle obsBounds = obs.getBounds();
                if (playerBounds.intersects(obsBounds)) {
                    System.out.println("Collision detected for Player " + playerNumber + " with obstacle at " + obsBounds);

                    // Freeze the player and make them immune
                    if (playerNumber == 1) {
                        p1Frozen = true;
                        p1Immune = true;
                    } else {
                        p2Frozen = true;
                        p2Immune = true;
                    }

                    // Start freeze timer
                    Timer freezeTimer = new Timer(2000, e -> {
                        if (playerNumber == 1) {
                            p1Frozen = false;
                            System.out.println("Player 1 unfrozen");
                            // Start immunity timer
                            Timer immuneTimer = new Timer(1000, ev -> {
                                p1Immune = false;
                                System.out.println("Player 1 is no longer immune");
                            });
                            immuneTimer.setRepeats(false);
                            immuneTimer.start();
                        } else {
                            p2Frozen = false;
                            System.out.println("Player 2 unfrozen");
                            Timer immuneTimer = new Timer(1000, ev -> {
                                p2Immune = false;
                                System.out.println("Player 2 is no longer immune");
                            });
                            immuneTimer.setRepeats(false);
                            immuneTimer.start();
                        }
                    });
                    freezeTimer.setRepeats(false);
                    freezeTimer.start();

                    iterator.remove(); // Remove the obstacle after collision
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