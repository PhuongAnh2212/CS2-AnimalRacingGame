package AnimalRaceGame;

public 
package AnimalRaceGame;

import java.awt.*;

public class Animal {
    private String type;
    private int x, y;
    private int jumpHeight = 50;
    private boolean jumping = false;
    private int groundY;
    private int jumpFrame = 0;

    private int leftKey, jumpKey;

    public Animal(String type, int x, int y, int leftKey, int jumpKey) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.leftKey = leftKey;
        this.jumpKey = jumpKey;
        this.groundY = y;
    }

    public void moveForward() {
        x += 10;
    }

    public void jump() {
        if (!jumping) {
            jumping = true;
            jumpFrame = 0;
        }
    }

    public void update() {
        if (jumping) {
            if (jumpFrame < 10) {
                y = groundY - jumpHeight;
            } else if (jumpFrame < 20) {
                y = groundY;
            } else {
                jumping = false;
            }
            jumpFrame++;
        }
    }

    public void draw(Graphics g) {
        g.setColor(type.equals("cho") ? Color.ORANGE : (type.equals("meo") ? Color.PINK : Color.GRAY));
        g.fillOval(x, y, 30, 30);
        g.drawString(type, x, y - 5);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 30, 30);
    }

    public int getX() {
        return x;
    }

    public int getJumpKey() {
        return jumpKey;
    }
}
 {
    
}
