package AnimalRaceGame;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class MysteryBox extends JLabel {
    private final MysteryEffect effect;

    public MysteryBox() {
        // 50% random obstacle or power-up
        boolean isObstacle = new Random().nextBoolean();

        if (isObstacle) {
            ObstacleType[] types = ObstacleType.values();
            effect = types[new Random().nextInt(types.length)];
            setBackground(Color.BLUE);
            setText(((ObstacleType) effect).getEmoji());
        } else {
            PowerUpType[] types = PowerUpType.values();
            effect = types[new Random().nextInt(types.length)];
            setBackground(Color.ORANGE);
            setText(((PowerUpType) effect).getEmoji());
        }

        setOpaque(true);
        setBounds(new Random().nextInt(600) + 100, new Random().nextInt(200) + 200, 30, 30);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setForeground(Color.WHITE);
    }

    public MysteryEffect getEffect() {
        return effect;
    }
}
