package AnimalRaceGame;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.awt.event.*;

public class AnimalRaceGame extends JFrame {
    private JLabel player1Label, player2Label, countdown;
    private boolean p1Frozen = false;
    private boolean p2Frozen = false;
    private ObstacleManager obstacleManager;
    private CheckWinner checkWinner;
    private MysteryBox mysteryBox;
    private Timer mysteryBoxTimer;
    private boolean p1Invincible = false;
    private boolean p2Invincible = false;

    public AnimalRaceGame(String p1Animal, String p2Animal) {
        setTitle("Animal Race Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(Color.PINK);

        player1Label = new JLabel("Player 1: " + p1Animal);
        player1Label.setFont(new Font("Arial", Font.PLAIN, 20));
        player1Label.setBounds(50, 100, 200, 50);
        add(player1Label);

        player2Label = new JLabel("Player 2: " + p2Animal);
        player2Label.setFont(new Font("Arial", Font.PLAIN, 20));
        player2Label.setBounds(50, 150, 200, 50);
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
                    String value = countdownQueue.poll();
                    countdown.setText(value);
                    repaint();
                } else {
                    countdownTimer.stop();
                    Timer removeLabelTimer = new Timer(1000, evt -> {
                        remove(countdown);
                        repaint();

                        obstacleManager = new ObstacleManager(AnimalRaceGame.this);
                        obstacleManager.startObstacleTimer(player1Label, player2Label);

                        Timer spawnTimer = new Timer(3000, ev -> obstacleManager.spawnObstacle());
                        spawnTimer.start();

                        // Bắt đầu timer di chuyển
                        checkWinner = new CheckWinner(player1Label, player2Label, AnimalRaceGame.this);
                        mysteryBoxTimer = new Timer(5000, ev2 -> spawnMysteryBox());
                        mysteryBoxTimer.start();
                    });
                    removeLabelTimer.setRepeats(false);
                    removeLabelTimer.start();
                }
            }
        });

        countdownTimer.start();

        // Add this after spawnTimer and before keyListener
        mysteryBoxTimer = new Timer(5000, ev -> spawnMysteryBox());
        mysteryBoxTimer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                // Player 1: dùng phím A
                if (key == KeyEvent.VK_A && !isPlayerFrozen(1)) {
                    movePlayer(1, 10); // Move Player 1
                }

                // Player 2: dùng phím L
                if (key == KeyEvent.VK_L && !isPlayerFrozen(2)) {
                    movePlayer(2, 10); // Move Player 2
                }
                if (mysteryBox != null) {
                    if (player1Label.getBounds().intersects(mysteryBox.getBounds())) {
                        applyMysteryEffect(1);
                    } else if (player2Label.getBounds().intersects(mysteryBox.getBounds())) {
                        applyMysteryEffect(2);
                    }
                }

                checkWinner.check();
            }
        });
        setFocusable(true);
        requestFocusInWindow();
    }

    private void spawnMysteryBox() {
        if (mysteryBox != null) {
            remove(mysteryBox);
        }

        mysteryBox = new MysteryBox();
        add(mysteryBox);
        repaint();
    }

    private void applyMysteryEffect(int playerNumber) {
        if (mysteryBox == null) return;

        MysteryEffect effect = mysteryBox.getEffect();
        effect.apply(this, playerNumber);

        remove(mysteryBox);
        mysteryBox = null;
        repaint();
    }

    public void freezePlayer(int playerNum) {
        if (playerNum == 1) p1Frozen = true;
        if (playerNum == 2) p2Frozen = true;

        Timer t = new Timer(2000, e -> {
            if (playerNum == 1) p1Frozen = false;
            if (playerNum == 2) p2Frozen = false;
        });
        t.setRepeats(false);
        t.start();
    }

    public boolean isPlayerFrozen(int playerNum) {
        return (playerNum == 1) ? p1Frozen : p2Frozen;
    }

    // Move player method (for power-ups like speed boost)
    public void movePlayer(int playerNumber, int distance) {
        if (playerNumber == 1) {
            player1Label.setLocation(player1Label.getX() + distance, player1Label.getY());
        } else if (playerNumber == 2) {
            player2Label.setLocation(player2Label.getX() + distance, player2Label.getY());
        }
    }

    // Set player invincible method (for invincibility power-up)
    public void setPlayerInvincible(int playerNumber, boolean isInvincible) {
        if (playerNumber == 1) {
            p1Invincible = isInvincible;
            // You can add additional logic to change the appearance of Player 1 (e.g., change the label color, etc.)
        } else if (playerNumber == 2) {
            p2Invincible = isInvincible;
            // Similarly, you can modify Player 2's appearance or behavior
        }
    }

    // Additional methods to check if players are invincible or frozen (if needed)
    public boolean isPlayerInvincible(int playerNumber) {
        return (playerNumber == 1) ? p1Invincible : p2Invincible;
    }
}
