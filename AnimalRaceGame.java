import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AnimalRaceGame extends JFrame {
    // private JLabel Player_1;
    // private JLabel Player_2;
    private JLabel countdown;

    public AnimalRaceGame(String player_1_Animal, String player_2_Animal) {
        
        /**TASK: Set the window size, layout, and background color */
        setTitle("Animal Race Game");
        
        // window size
        setSize(1920, 1080);

        // Exit the program when close the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Default layout 
        setLayout(null); 

        // Background color 
        getContentPane().setBackground(Color.PINK);
    
        /**TASK: Add two JLabels to represent Player 1 and Player 2 with their selected animals and names */
        JLabel Player_1 = new JLabel("Player 1: " + player_1_Animal);
        Player_1.setFont(new Font("Arial", Font.PLAIN, 20));

        JLabel Player_2 = new JLabel("Player 2: " + player_2_Animal);
        Player_2.setFont(new Font("Arial", Font.PLAIN, 20));

        /**TASK: Set initial positions */
        Player_1.setBounds(50, 100, 200, 50);
        Player_2.setBounds(50, 150, 200, 50);

        // Add 2 players to the game
        add(Player_1);
        add(Player_2);

        /**TASK: Countdown 3 seconds before race starts */
        countdown = new JLabel("Ready?");
        countdown.setFont(new Font("Arial", Font.BOLD, 40));
        countdown.setBounds(300, 20, 300, 50);
        add(countdown);

        Queue<String> countdownQueue = new LinkedList<>();
        Collections.addAll(countdownQueue, "3", "2", "1", "Go!");

        setVisible(true);

        // Timer 
        Timer countdownTimer = new Timer(1000, null); // Wait 1s between actions
        countdownTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!countdownQueue.isEmpty()) {
                    String value = countdownQueue.poll();
                    countdown.setText(value);
                    repaint(); 

                } else {
                    countdownTimer.stop();

                    // Remove the countdown label out of the screen after 1s 
                    Timer removeLabelTimer = new Timer(1000, evt -> {
                        remove(countdown);
                        repaint();
                    });
                    removeLabelTimer.setRepeats(false);
                    removeLabelTimer.start();
                }
            }
        });

        // Countdown
        countdownTimer.start();
    }

    public static void main(String[] args) {
        new AnimalRaceGame("Cat", "Dog");
    }
}

