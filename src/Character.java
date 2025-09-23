import java.awt.Point;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Character {
    private double movementSpeed, x, y, deltaX, deltaY;
    private StringBuilder logging;

    public Character () {
        movementSpeed = 0.025;
        x = y = this.deltaX = this.deltaY = 0;
        logging = new StringBuilder(1000);
    }

    public void move (int directionX, int directionY, int timeDeltaTime) {
        double changeX = directionX * timeDeltaTime * movementSpeed;
        double changeY = directionY * timeDeltaTime * movementSpeed;
//        System.out.println("changeX: "+changeX+", changeY: "+changeY+", timeDeltaTime: "+timeDeltaTime);

        logging.append("changeX: ");
        logging.append(changeX);
        logging.append(", changeY: ");
        logging.append(changeY);
        logging.append(", timeDeltaTime: ");
        logging.append(timeDeltaTime);
        logging.append('\n');

        deltaX += changeX;
        deltaY += changeY;
        x += changeX;
        y += changeY;
    }

    public Point getPos () {
        return new Point((int)x,(int)y);
    }

    public Point getDeltas () {
        Point deltas = new Point((int)deltaX,(int)deltaY);
        this.deltaX = 0;
        this.deltaY = 0;
        return deltas;
    }

    public void log () {
        System.out.println("test!");
        System.out.println(logging);
    }
}
