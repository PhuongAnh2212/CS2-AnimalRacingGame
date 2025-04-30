package AnimalRaceGame;
import javax.swing.*;

// import CheckWinner;
// import ObstacleManager;

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
    private Timer moveTimer;

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

                        moveTimer = new Timer(100, e1 -> {
                            if (!isPlayerFrozen(1)) {
                                player1Label.setLocation(player1Label.getX() + 5, player1Label.getY());
                            }
                            if (!isPlayerFrozen(2)) {
                                player2Label.setLocation(player2Label.getX() + 5, player2Label.getY());
                            }
                            checkWinner.check();
                        });
                        moveTimer.start();

                    });
                    removeLabelTimer.setRepeats(false);
                    removeLabelTimer.start();
                }
            }
        });

        countdownTimer.start();
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
}
