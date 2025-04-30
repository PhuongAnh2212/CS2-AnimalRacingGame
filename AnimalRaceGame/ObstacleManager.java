package AnimalRaceGame;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.Timer;

public class ObstacleManager {

    private AnimalRaceGame game;  
    private Queue<JLabel> obstacles;
    private Timer moveTimer;

    public ObstacleManager(AnimalRaceGame game) {
        this.obstacles = new LinkedList<>();
        this.game = game;
    }

    public void spawnObstacle() {
        ObstacleType[] types = ObstacleType.values();
        ObstacleType type = types[new Random().nextInt(types.length)];

        JLabel ob = new JLabel(type.getEmoji());
        int[] lanes = {100, 150};
        int y = lanes[new Random().nextInt(lanes.length)];

        // Size of the obstacles
        ob.setBounds(1000, y, 50, 50);
        ob.putClientProperty("type", type);

        game.add(ob);
        obstacles.add(ob);
    }

    public void startObstacleTimer(JLabel player1, JLabel player2) {
        moveTimer = new Timer(30, ev -> {
            List<JLabel> toRemove = new ArrayList<>();

            for (JLabel ob : obstacles) {
                ob.setLocation(ob.getX() - 5, ob.getY());
                ObstacleType type = (ObstacleType) ob.getClientProperty("type");

                // Collision Player 1
                if (!game.isPlayerFrozen(1) && ob.getBounds().intersects(player1.getBounds())) {
                    applyEffect(type, 1);
                    toRemove.add(ob);
                }
                // Collision Player 2
                else if (!game.isPlayerFrozen(2) && ob.getBounds().intersects(player2.getBounds())) {
                    applyEffect(type, 2);
                    toRemove.add(ob);
                }
                // Off-screen
                else if (ob.getX() < 0) {
                    toRemove.add(ob);
                }
            }

            for (JLabel ob : toRemove) {
                game.remove(ob);
                obstacles.remove(ob);
            }

            game.repaint();
        });

        moveTimer.start();
    }

    // Freeze the player after intersecting the obstacle
    private void applyEffect(ObstacleType type, int playerNumber) {
        game.freezePlayer(playerNumber);
    }
}
