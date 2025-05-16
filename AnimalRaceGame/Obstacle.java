package AnimalRaceGame;

import javax.swing.*;
import java.awt.*;

public class Obstacle {
    private int x;
    private int y;
    private int width = 200;
    private int height = 200;
    private Image image;

    public Obstacle(int x, int y) {
        this.x = x;
        this.y = y;
        try {
            ImageIcon icon = new ImageIcon("ingame/obstacle.png");
            image = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            if (image == null) {
                throw new RuntimeException("ImageIcon returned null image");
            }
            System.out.println("Loaded assets/obstacle.png successfully at (" + x + ", " + this.y + "), scaled to 200x200");
        } catch (Exception e) {
            System.err.println("Failed to load assets/obstacle.png at (" + x + ", " + this.y + "): " + e.getMessage());
            this.image = null;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics g, int backgroundX) {
        if (image != null) {
            g.drawImage(image, x - backgroundX, y, width, height, null);
        }
        // No red rectangle fallback
    }
}