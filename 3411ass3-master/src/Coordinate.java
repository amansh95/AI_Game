/**
 * Created by Rahul Gururaj on 5/27/2017.
 */
public class Coordinate {
    public int x, y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate(Coordinate c) {
        this.x = c.x;
        this.y = c.y;
    }

    public boolean equals(Coordinate c) {
        return this.x == c.x && this.y == c.y;
    }
}


