import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AnimalRaceGame extends JFrame {
    private JLabel player1Label, player2Label, countdown;
    private boolean p1Frozen = false;
    private boolean p2Frozen = false;
    private ObstacleManager obstacleManager;

    public AnimalRaceGame(String p1Animal, String p2Animal) {
        
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
    
        /**TASK: Add two JLabels & set position to represent Player 1 and Player 2 with their selected animals and names */
        player1Label = new JLabel("Player 1: " + p1Animal);
        player1Label.setFont(new Font("Arial", Font.PLAIN, 20));
        player1Label.setBounds(50, 100, 200, 50);
        add(player1Label);

        player2Label = new JLabel("Player 2: " + p2Animal);
        player2Label.setFont(new Font("Arial", Font.PLAIN, 20));
        player2Label.setBounds(50, 150, 200, 50);
        add(player2Label);


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

                        obstacleManager = new ObstacleManager(AnimalRaceGame.this);
                        obstacleManager.startObstacleTimer(player1Label, player2Label);
                        Timer spawnTimer = new Timer(3000, ev -> obstacleManager.spawnObstacle());
                        spawnTimer.start();

                    });
                    removeLabelTimer.setRepeats(false);
                    removeLabelTimer.start();
                }
            }
        });

        // Countdown
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
        


    public static void main(String[] args) {
        new AnimalRaceGame("Cat", "Dog");
    }
}

