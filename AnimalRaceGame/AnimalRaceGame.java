package AnimalRaceGame;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.awt.event.*;

public class AnimalRaceGame extends JFrame {
    private JLabel player1Label, player2Label, countdown;
    private boolean p1Frozen = false;
    private boolean p2Frozen = false;
    private ObstacleManager obstacleManager;
    private CheckWinner checkWinner;
    private Timer mysteryBoxTimer;
    private Timer mysteryBoxMoveTimer; // New timer for moving mystery boxes
    private boolean p1Invincible = false;
    private boolean p2Invincible = false;
    private MysteryBox box1, box2;
    private boolean p1Jumping = false;
    private boolean p2Jumping = false;
    private int jumpHeight = 50;
    private int jumpStep = 0;
    private boolean gameStarted = false;

    public AnimalRaceGame(String p1Animal, String p2Animal) {
        setTitle("Animal Race Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(Color.PINK);

        player1Label = new JLabel("player1 " + p1Animal);
        player1Label.setFont(new Font("Arial", Font.PLAIN, 20));
        player1Label.setBounds(50, 100, 100, 50);
        add(player1Label);

        player2Label = new JLabel("player2 " + p2Animal);
        player2Label.setFont(new Font("Arial", Font.PLAIN, 20));
        player2Label.setBounds(50, 200, 100, 50);
        add(player2Label);

        countdown = new JLabel("Ready?");
        countdown.setFont(new Font("Arial", Font.BOLD, 40));
        countdown.setBounds(300, 20, 300, 50);
        add(countdown);

        Queue<String> countdownQueue = new LinkedList<>();
        Collections.addAll(countdownQueue, "3", "2", "1", "Go!");

        setVisible(true);

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

                        mysteryBoxTimer = new Timer(5000, ev2 -> spawnMysteryBoxes());
                        mysteryBoxTimer.start();

                        // Start the movement timer for mystery boxes
                        mysteryBoxMoveTimer = new Timer(30, ev3 -> moveMysteryBoxes());
                        mysteryBoxMoveTimer.start();
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
                int key = e.getKeyCode();

                if (key == KeyEvent.VK_W && !p1Jumping) {
                    p1Jumping = true;
                    jumpStep = 0;
                }

                if (key == KeyEvent.VK_O && !p2Jumping) {
                    p2Jumping = true;
                    jumpStep = 0;
                }

                if (!gameStarted) return;

                if (key == KeyEvent.VK_A && !isPlayerFrozen(1)) {
                    movePlayer(1, 10);
                }

                if (key == KeyEvent.VK_L && !isPlayerFrozen(2)) {
                    movePlayer(2, 10);
                }

                if (box1 != null && player1Label.getBounds().intersects(box1.getBounds())) {
                    applyMysteryEffect(1, box1);
                    remove(box1);
                    box1 = null;
                    repaint();
                }

                if (box2 != null && player2Label.getBounds().intersects(box2.getBounds())) {
                    applyMysteryEffect(2, box2);
                    remove(box2);
                    box2 = null;
                    repaint();
                }

                checkWinner.check();
            }
        });

        Timer jumpTimer = new Timer(50, e -> {
            if (p1Jumping) {
                if (jumpStep < 5) {
                    player1Label.setLocation(player1Label.getX(), player1Label.getY() - jumpHeight / 5);
                } else if (jumpStep < 10) {
                    player1Label.setLocation(player1Label.getX(), player1Label.getY() + jumpHeight / 5);
                } else {
                    p1Jumping = false;
                }
                jumpStep++;
            }

            if (p2Jumping) {
                if (jumpStep < 5) {
                    player2Label.setLocation(player2Label.getX(), player2Label.getY() - jumpHeight / 5);
                } else if (jumpStep < 10) {
                    player2Label.setLocation(player2Label.getX(), player2Label.getY() + jumpHeight / 5);
                } else {
                    p2Jumping = false;
                }
                jumpStep++;
            }

            repaint();
        });
        jumpTimer.start();

        setFocusable(true);
        requestFocusInWindow();
    }

    private void spawnMysteryBoxes() {
        // Remove existing boxes if they weren't collected
        if (box1 != null) {
            remove(box1);
            box1 = null;
        }
        if (box2 != null) {
            remove(box2);
            box2 = null;
        }

        // Randomly decide how many boxes to spawn (0, 1, or 2)
        Random rand = new Random();
        int numBoxes = rand.nextInt(3); // 0, 1, or 2 boxes

        int[] lanes = {100, 200}; // Lanes for player 1 and player 2
        boolean[] usedLanes = {false, false}; // Track which lanes are used

        for (int i = 0; i < numBoxes; i++) {
            // Pick a random lane that hasn't been used yet
            int laneIndex;
            do {
                laneIndex = rand.nextInt(lanes.length);
            } while (usedLanes[laneIndex]);
            usedLanes[laneIndex] = true;

            int y = lanes[laneIndex];
            MysteryBox box = new MysteryBox();
            box.setBounds(800, y, 30, 30); // Spawn at right edge
            add(box);

            if (laneIndex == 0) {
                box1 = box; // Assign to lane 1 (y=100)
            } else {
                box2 = box; // Assign to lane 2 (y=200)
            }
        }

        repaint();
    }

    private void moveMysteryBoxes() {
        // Move box1 if it exists
        if (box1 != null) {
            box1.setLocation(box1.getX() - 5, box1.getY());
            if (box1.getX() < -50) { // Remove if off-screen
                remove(box1);
                box1 = null;
                repaint();
            }
        }

        // Move box2 if it exists
        if (box2 != null) {
            box2.setLocation(box2.getX() - 5, box2.getY());
            if (box2.getX() < -50) { // Remove if off-screen
                remove(box2);
                box2 = null;
                repaint();
            }
        }
    }

    private void applyMysteryEffect(int playerNumber, MysteryBox box) {
        if (box == null) return;
        MysteryEffect effect = box.getEffect();
        effect.apply(this, playerNumber);
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
        if (playerNumber == 1) {
            player1Label.setLocation(player1Label.getX() + distance, player1Label.getY());
        } else if (playerNumber == 2) {
            player2Label.setLocation(player2Label.getX() + distance, player2Label.getY());
        }
    }

    public void setPlayerInvincible(int playerNumber, boolean isInvincible) {
        if (playerNumber == 1) p1Invincible = isInvincible;
        else if (playerNumber == 2) p2Invincible = isInvincible;
    }

    public boolean isPlayerInvincible(int playerNumber) {
        return (playerNumber == 1) ? p1Invincible : p2Invincible;
    }

    public boolean isPlayerJumping(int playerNumber) {
        return (playerNumber == 1) ? p1Jumping : p2Jumping;
    }
}