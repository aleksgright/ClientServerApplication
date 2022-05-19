package moviesApp.utils;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private final double x;
    private final double y;

    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return x + " " + y;
    }
}
