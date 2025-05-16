package AnimalRaceGame;

import javax.swing.*;
import java.awt.*;

public class CheckWinner {
    private int p1, p2;
    private JFrame frame;
    private final int finish_line = 2000;
    private boolean gameover = false;
    private long startTime;

    public CheckWinner(int p1, int p2, JFrame frame) {
        this.p1 = p1;
        this.p2 = p2;
        this.frame = frame;
        this.startTime = System.currentTimeMillis();
    }

    public void check() {
        if (gameover) {
            return;
        }

        //System.out.println("Player 1 X: " + p1 + " Player 2 X: " + p2);

        if (p1 >= finish_line) {
            gameover = true;
            JOptionPane.showMessageDialog(frame, "Player 1 Wins!");
            long finishTime = System.currentTimeMillis() - startTime;
            String name = JOptionPane.showInputDialog(frame, "Enter your name:");
            Leaderboard.recordResult(name, finishTime);
            JOptionPane.showMessageDialog(frame, "You finished in " + finishTime + " ms!");
            Leaderboard.showLeaderboard(frame);
            resetGame();
        } else if (p2 >= finish_line) {
            gameover = true;
            JOptionPane.showMessageDialog(frame, "Player 2 Wins!");
            long finishTime = System.currentTimeMillis() - startTime;
            String name = JOptionPane.showInputDialog(frame, "Enter your name:");
            Leaderboard.recordResult(name, finishTime);
            JOptionPane.showMessageDialog(frame, "You finished in " + finishTime + " ms!");
            Leaderboard.showLeaderboard(frame);
            resetGame();
        }
    }

    private void resetGame() {
        frame.dispose();
        new Leaderboard(); // Can optionally display the leaderboard here, or just restart the game
        new StartScreen();
    }
}
