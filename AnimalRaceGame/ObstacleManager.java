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
        this.game = game;
        this.obstacles = new LinkedList<>();
    }

    public void spawnObstacle() {
        ObstacleType[] types = ObstacleType.values();
        ObstacleType type = types[new Random().nextInt(types.length)];

        JLabel ob = new JLabel(type.getEmoji());
        int[] lanes = {100, 200}; // Align with player1 (y=100) and player2 (y=200)
        int y = lanes[new Random().nextInt(lanes.length)];

        // Spawn at right edge of the screen
        ob.setBounds(800, y, 50, 50);
        ob.putClientProperty("type", type);

        game.add(ob);
        obstacles.add(ob);
        game.repaint();
    }

    public void startObstacleTimer(JLabel player1, JLabel player2) {
        moveTimer = new Timer(30, ev -> {
            List<JLabel> toRemove = new ArrayList<>();

            for (JLabel ob : obstacles) {
                ob.setLocation(ob.getX() - 5, ob.getY());
                ObstacleType type = (ObstacleType) ob.getClientProperty("type");

                // Collision with Player 1
                if (!game.isPlayerFrozen(1) && !game.isPlayerInvincible(1) &&
                    ob.getBounds().intersects(player1.getBounds()) && !game.isPlayerJumping(1)) {
                    applyEffect(type, 1);
                    toRemove.add(ob);
                }
                // Collision with Player 2
                else if (!game.isPlayerFrozen(2) && !game.isPlayerInvincible(2) &&
                         ob.getBounds().intersects(player2.getBounds()) && !game.isPlayerJumping(2)) {
                    applyEffect(type, 2);
                    toRemove.add(ob);
                }
                // Remove if off-screen
                else if (ob.getX() < -50) {
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

    private void applyEffect(ObstacleType type, int playerNumber) {
        game.freezePlayer(playerNumber);
    }

    public Queue<JLabel> getObstacles() {
        return obstacles;
    }
}
