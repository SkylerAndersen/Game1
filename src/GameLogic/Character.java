package GameLogic;

import Graphics.ImageUtilities;

import java.awt.*;

public class Character {
    public static final int BASE_POSE = 3, WALK_POSE = 4, ATTACK_POSE = 5, MAIN_CHARACTER = 6, ALT_CHARACTER = 7;
    private int pose;
    private boolean walking, flipped;
    private double movementSpeed, x, y;
    private StringBuilder logging;
    private long lastWalked, lastStood;

    public Character () {
        movementSpeed = 0.025;
        x = y = 0;
        logging = new StringBuilder(1000);
        pose = BASE_POSE;
        lastWalked = lastStood = 0;
    }

    public void setPose (int pose) {
        if (pose < 3 || pose > 7)
            return;
        this.pose = pose;
    }

    public int getPose () {
        return pose;
    }

    public void move (int directionX, int directionY, int timeDeltaTime) {
        double newX = directionX * timeDeltaTime * movementSpeed + x;
        double newY = directionY * timeDeltaTime * movementSpeed + y;

        Dimension characterSize = ImageUtilities.getCharacterScreenSize();
        Dimension backgroundSize = ImageUtilities.getBackgroundScreenSize();

        boolean validX = newX >= 0 && newX+characterSize.width < backgroundSize.width;
        boolean validY = newY >= 0 && newY+characterSize.height < backgroundSize.height;
        if (validX)
            x = newX;
        if (validY)
            y = newY;
    }

    public Point getPos () {
        return new Point((int)x,(int)y);
    }

    public void setPos (Point newPos) {
        x = newPos.x;
        y = newPos.y;
    }

    public void log () {
        System.out.println("test!");
        System.out.println(logging);
    }

    public void setIsWalking (boolean isWalking) {
        this.walking = isWalking;
    }

    public boolean isWalking () {
        return walking;
    }

    public void setLastWalked (long time) {
        this.lastWalked = time;
    }

    public long getLastWalked () {
        return lastWalked;
    }

    public void setLastStood (long time) {
        this.lastStood = time;
    }

    public long getLastStood () {
        return lastStood;
    }

    public void setFlipped (boolean flipped) {
        this.flipped = flipped;
    }

    public boolean getFlipped () {
        return flipped;
    }
}
