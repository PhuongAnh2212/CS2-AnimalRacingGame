package AnimalRaceGame;
import java.awt.*;
import javax.swing.*;

// import AnimalRaceGame;
// import StartMenu;

public class StartMenu extends JFrame {

    private JComboBox<String> player1Combo;
    private JComboBox<String> player2Combo;
    private JButton startButton;

    public StartMenu() {
        setTitle("Animal Race - Start Menu");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        String[] animals = {"Tho", "Cho", "Meo"};

        player1Combo = new JComboBox<>(animals);
        player2Combo = new JComboBox<>(animals);
        startButton = new JButton("Start Game");

        setLayout(new GridLayout(3, 2, 10, 10));
        add(new JLabel("Player 1 Animal:"));
        add(player1Combo);
        add(new JLabel("Player 2 Animal:"));
        add(player2Combo);
        add(new JLabel()); // spacer
        add(startButton);

        startButton.addActionListener(e -> {
            String player1Animal = (String) player1Combo.getSelectedItem();
            String player2Animal = (String) player2Combo.getSelectedItem();
            new AnimalRaceGame(player1Animal, player2Animal);
            dispose();
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new StartMenu();
    }
}
