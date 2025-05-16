package AnimalRaceGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

public class Freeze {
    private int x, y;
    private int width = 80, height = 80;
    private int speed = 5;
    private Image sprite;

    private boolean isFrozen = false;
    private long freezeEndTime = 0;

    public Freeze(int startX, int startY, Image sprite) {
        this.x = startX;
        this.y = startY;
        this.sprite = sprite;
    }

    public void update() {
        if (isFrozen && System.currentTimeMillis() >= freezeEndTime) {
            isFrozen = false;
        }
    }

    public void moveRight() {
        if (!isFrozen) {
            x += speed;
        }
    }

    public void moveLeft() {
        if (!isFrozen) {
            x -= speed;
        }
    }

    public void freeze(int milliseconds) {
        isFrozen = true;
        freezeEndTime = System.currentTimeMillis() + milliseconds;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void draw(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
        } else {
            // fallback if no sprite is loaded
            g.setColor(isFrozen ? Color.BLUE : Color.GREEN);
            g.fillRect(x, y, width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Optional setters or position reset
    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }
}
