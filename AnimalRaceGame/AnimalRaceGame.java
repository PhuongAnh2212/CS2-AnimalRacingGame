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
    private Timer mysteryBoxTimer;
    private boolean p1Invincible = false;
    private boolean p2Invincible = false;
    private MysteryBox box1, box2;

    private boolean gameStarted = false; // 🔧 Game start control

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

                        // 🔧 Start game now
                        gameStarted = true;

                        // Start game logic
                        obstacleManager = new ObstacleManager(AnimalRaceGame.this);
                        obstacleManager.startObstacleTimer(player1Label, player2Label);

                        checkWinner = new CheckWinner(player1Label, player2Label, AnimalRaceGame.this);

                        mysteryBoxTimer = new Timer(5000, ev2 -> spawnMysteryBoxes());
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
                if (!gameStarted) return; // 🔒 Block input before game starts

                int key = e.getKeyCode();

                if (key == KeyEvent.VK_A && !isPlayerFrozen(1)) {
                    movePlayer(1, 10);
                }

                if (key == KeyEvent.VK_L && !isPlayerFrozen(2)) {
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
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    private void spawnMysteryBoxes() {
        if (box1 != null) remove(box1);
        if (box2 != null) remove(box2);

        int p1X = player1Label.getX();
        int p1Y = player1Label.getY();
        box1 = new MysteryBox();
        box1.setLocation(Math.min(p1X + 150, 700), p1Y + 10);
        add(box1);

        int p2X = player2Label.getX();
        int p2Y = player2Label.getY();
        box2 = new MysteryBox();
        box2.setLocation(Math.min(p2X + 150, 700), p2Y + 10);
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
}
