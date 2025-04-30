import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class AnimalRace {
    public static void main(String[] args) {
        new StartMenu();
    }
}

// Task 1: Start Menu
class StartMenu extends JFrame {
    private JComboBox<String> player1Combo, player2Combo;
    private static final String[] ANIMALS = { "🐱", "🐶", "🐢", "🐇", "🦊" };

    public StartMenu() {
        setTitle("Animal Race - Start Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1));

        player1Combo = new JComboBox<>(ANIMALS);
        player2Combo = new JComboBox<>(ANIMALS);

        add(new JLabel("Player 1 - Choose your animal:"));
        add(player1Combo);
        add(new JLabel("Player 2 - Choose your animal:"));
        add(player2Combo);

        JButton startButton = new JButton("Start Game");
        add(startButton);

        startButton.addActionListener(e -> {
            String p1Animal = (String) player1Combo.getSelectedItem();
            String p2Animal = (String) player2Combo.getSelectedItem();
            new AnimalRaceGame(p1Animal, p2Animal);
            dispose();
        });

        setVisible(true);
    }
}

// Tasks 2–4: Game logic
class AnimalRaceGame extends JFrame implements KeyListener {
    private JLabel player1Label, player2Label, countdown;
    private int p1X = 50, p2X = 50;
    private final int FINISH_LINE = 1600; // For 1920px screen
    private String p1Animal, p2Animal;

    public AnimalRaceGame(String p1Animal, String p2Animal) {
        this.p1Animal = p1Animal;
        this.p2Animal = p2Animal;

        setTitle("Animal Race Game");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.PINK);

        player1Label = new JLabel("Player 1: " + p1Animal);
        player1Label.setFont(new Font("Arial", Font.PLAIN, 30));
        player1Label.setBounds(p1X, 150, 200, 50);
        add(player1Label);

        player2Label = new JLabel("Player 2: " + p2Animal);
        player2Label.setFont(new Font("Arial", Font.PLAIN, 30));
        player2Label.setBounds(p2X, 250, 200, 50);
        add(player2Label);

        countdown = new JLabel("Ready?");
        countdown.setFont(new Font("Arial", Font.BOLD, 60));
        countdown.setBounds(800, 50, 300, 100);
        add(countdown);

        setVisible(true);
        setFocusable(true);
        addKeyListener(this);

        startCountdown();
    }

    private void startCountdown() {
        Queue<String> countdownQueue = new LinkedList<>();
        Collections.addAll(countdownQueue, "3", "2", "1", "Go!");

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
                    });
                    removeLabelTimer.setRepeats(false);
                    removeLabelTimer.start();
                }
            }
        });
        countdownTimer.start();
    }

    private void checkWinner() {
        if (p1X >= FINISH_LINE) {
            JOptionPane.showMessageDialog(this, "🎉 Player 1 wins!");
            resetGame();
        } else if (p2X >= FINISH_LINE) {
            JOptionPane.showMessageDialog(this, "🎉 Player 2 wins!");
            resetGame();
        }
    }

    private void resetGame() {
        dispose();
        new StartMenu();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (p1X >= FINISH_LINE || p2X >= FINISH_LINE)
            return;

        if (e.getKeyCode() == KeyEvent.VK_A) {
            p1X += 20;
            player1Label.setLocation(p1X, player1Label.getY());
        } else if (e.getKeyCode() == KeyEvent.VK_L) {
            p2X += 20;
            player2Label.setLocation(p2X, player2Label.getY());
        }
        checkWinner();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}