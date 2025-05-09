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
    private Timer mysteryBoxMoveTimer;
    private boolean p1Invincible = false;
    private boolean p2Invincible = false;
    private MysteryBox box1, box2;
    private boolean p1Jumping = false;
    private boolean p2Jumping = false;
    private boolean p1JumpKeyHeld = false; // Track if jump key is held for player 1
    private boolean p2JumpKeyHeld = false; // Track if jump key is held for player 2
    private long p1JumpStartTime = 0; // Track when player 1 started jumping
    private long p2JumpStartTime = 0; // Track when player 2 started jumping
    private final long MAX_JUMP_DURATION = 3000; // 3 seconds max jump duration
    private int jumpHeight = 50;
    private int jumpStep = 0;
    private boolean gameStarted = false;
    private final int PLAYER1_BASE_Y = 100; // Base y-position for player 1
    private final int PLAYER2_BASE_Y = 200; // Base y-position for player 2

    public AnimalRaceGame(String p1Animal, String p2Animal) {
        setTitle("Animal Race Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(Color.PINK);

        player1Label = new JLabel("player1 " + p1Animal);
        player1Label.setFont(new Font("Arial", Font.PLAIN, 20));
        player1Label.setBounds(50, PLAYER1_BASE_Y, 100, 50);
        add(player1Label);

        player2Label = new JLabel("player2 " + p2Animal);
        player2Label.setFont(new Font("Arial", Font.PLAIN, 20));
        player2Label.setBounds(50, PLAYER2_BASE_Y, 100, 50);
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

                if (key == KeyEvent.VK_W && !p1Jumping && !isPlayerFrozen(1)) {
                    p1Jumping = true;
                    p1JumpKeyHeld = true;
                    p1JumpStartTime = System.currentTimeMillis();
                    // Jump to peak immediately
                    player1Label.setLocation(player1Label.getX(), PLAYER1_BASE_Y - jumpHeight);
                }

                if (key == KeyEvent.VK_O && !p2Jumping && !isPlayerFrozen(2)) {
                    p2Jumping = true;
                    p2JumpKeyHeld = true;
                    p2JumpStartTime = System.currentTimeMillis();
                    // Jump to peak immediately
                    player2Label.setLocation(player2Label.getX(), PLAYER2_BASE_Y - jumpHeight);
                }

                if (!gameStarted) return;

                if (key == KeyEvent.VK_A && !isPlayerFrozen(1)) {
                    movePlayer(1, 10);
                }

                if (key == KeyEvent.VK_L && !isPlayerFrozen(2)) {
                    movePlayer(2, 10);
                }

                // Only check for mystery box collision if the player is not jumping
                if (box1 != null && player1Label.getBounds().intersects(box1.getBounds()) && !p1Jumping) {
                    applyMysteryEffect(1, box1);
                    remove(box1);
                    box1 = null;
                    repaint();
                }

                if (box2 != null && player2Label.getBounds().intersects(box2.getBounds()) && !p2Jumping) {
                    applyMysteryEffect(2, box2);
                    remove(box2);
                    box2 = null;
                    repaint();
                }

                checkWinner.check();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();

                if (key == KeyEvent.VK_W) {
                    p1JumpKeyHeld = false;
                }

                if (key == KeyEvent.VK_O) {
                    p2JumpKeyHeld = false;
                }
            }
        });

        Timer jumpTimer = new Timer(50, e -> {
            long currentTime = System.currentTimeMillis();

            if (p1Jumping) {
                long jumpDuration = currentTime - p1JumpStartTime;
                if (!p1JumpKeyHeld || jumpDuration >= MAX_JUMP_DURATION) {
                    // Descend back to base position
                    int currentY = player1Label.getY();
                    if (currentY < PLAYER1_BASE_Y) {
                        int newY = Math.min(currentY + jumpHeight / 5, PLAYER1_BASE_Y);
                        player1Label.setLocation(player1Label.getX(), newY);
                    }
                    if (player1Label.getY() >= PLAYER1_BASE_Y) {
                        p1Jumping = false;
                        p1JumpKeyHeld = false;
                    }
                }
            }

            if (p2Jumping) {
                long jumpDuration = currentTime - p2JumpStartTime;
                if (!p2JumpKeyHeld || jumpDuration >= MAX_JUMP_DURATION) {
                    // Descend back to base position
                    int currentY = player2Label.getY();
                    if (currentY < PLAYER2_BASE_Y) {
                        int newY = Math.min(currentY + jumpHeight / 5, PLAYER2_BASE_Y);
                        player2Label.setLocation(player2Label.getX(), newY);
                    }
                    if (player2Label.getY() >= PLAYER2_BASE_Y) {
                        p2Jumping = false;
                        p2JumpKeyHeld = false;
                    }
                }
            }

            repaint();
        });
        jumpTimer.start();

        setFocusable(true);
        requestFocusInWindow();
    }

    private void spawnMysteryBoxes() {
        if (box1 != null) {
            remove(box1);
            box1 = null;
        }
        if (box2 != null) {
            remove(box2);
            box2 = null;
        }

        Random rand = new Random();
        int numBoxes = rand.nextInt(3);

        int[] lanes = {100, 200};
        boolean[] usedLanes = {false, false};

        for (int i = 0; i < numBoxes; i++) {
            int laneIndex;
            do {
                laneIndex = rand.nextInt(lanes.length);
            } while (usedLanes[laneIndex]);
            usedLanes[laneIndex] = true;

            int y = lanes[laneIndex];
            MysteryBox box = new MysteryBox();
            box.setBounds(800, y, 30, 30);
            add(box);

            if (laneIndex == 0) {
                box1 = box;
            } else {
                box2 = box;
            }
        }

        repaint();
    }

    private void moveMysteryBoxes() {
        if (box1 != null) {
            box1.setLocation(box1.getX() - 5, box1.getY());
            if (box1.getX() < -50) {
                remove(box1);
                box1 = null;
                repaint();
            }
        }

        if (box2 != null) {
            box2.setLocation(box2.getX() - 5, box2.getY());
            if (box2.getX() < -50) {
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