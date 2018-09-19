/**
 * Created by Rahul Gururaj on 5/27/2017.
 */
public class Point {
   public int x,y;
   public Constants.Symbol symbol;

   public Point(int x, int y, Constants.Symbol symbol) {
       this.x = x;
       this.y = y;
       this.symbol = symbol;
   }

    public Point(Point point) {
        this(point.x, point.y, point.symbol);
    }

    public boolean equals(Point p) {
       boolean equals = false;
       if(x == p.x && y == p.y)
           equals = true;
       return equals;
    }

    public Point(Coordinate c, Constants.Symbol symbol) {
       this(c.x, c.y, symbol);
    }

    public Coordinate toCoordinate(Point point) {
       return new Coordinate(x,y);
    }
}
