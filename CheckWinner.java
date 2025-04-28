import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class CheckWinner {
    private JLabel p1, p2;
    private JFrame frame;
    private final int finish_line = 600;

    public CheckWinner(JLabel p1, JLabel p2, JFrame frame) {
        this.p1 = p1;
        this.p2 = p2;
        this.frame = frame;
    }

    public void check() {
        if (p1.getX() >= finish_line) {
            JOptionPane.showMessageDialog(frame, "Player 1 Wins!");
            resetGame();
        } else if (p2.getX() >= finish_line) {
            JOptionPane.showMessageDialog(frame, "Player 2 Wins!");
            resetGame();
        }
    }

    private void resetGame() {
        frame.dispose();
        new StartMenu(); // Make sure StartMenu is properly implemented
    }
}
