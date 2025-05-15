package AnimalRaceGame;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class MysteryBox extends JLabel {
    private final MysteryEffect effect;
    private static final Random random = new Random();

    public MysteryBox(JLabel player1, JLabel player2) {
        boolean isObstacle = random.nextBoolean();

        if (isObstacle) {
            ObstacleType[] types = ObstacleType.values();
            effect = types[random.nextInt(types.length)];
            setBackground(Color.BLUE);
            setText(effect.getEmoji());
        } else {
            PowerUpType[] types = PowerUpType.values();
            effect = types[random.nextInt(types.length)];
            setBackground(Color.ORANGE);
            setText(effect.getEmoji());
        }

        setOpaque(true);

        int offset = 100;

        int x1 = player1.getX() + offset;
        int y1 = player1.getY();
        int x2 = player2.getX() + offset;
        int y2 = player2.getY();

        if (random.nextBoolean()) {
            setBounds(x1, y1, 30, 30);
        } else {
            setBounds(x2, y2, 30, 30);
        }

        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setForeground(Color.WHITE);
    }

    public MysteryEffect getEffect() {
        return effect;
    }
}
