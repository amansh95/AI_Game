import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Rahul Gururaj on 5/27/2017.
 */
public class Map implements Iterable<Point>{

    public static final int MAX_HEIGHT = 160;
    public static final int MAX_WIDTH = 160;

    //This will have everything we have seen so far in the form of 'Points' on the map
    Point[][] map;

    public Map() {
        map = new Point[MAX_WIDTH][MAX_HEIGHT];

        //Map is initially set to out Null symbol, '#'
        for(int x=0; x<MAX_WIDTH; x++)
            for(int y=0; y<MAX_HEIGHT; y++)
                map[x][y] = new Point(x,y, Constants.Symbol.Null);
    }

    public Map(Map m){
        map = new Point[MAX_WIDTH][MAX_HEIGHT];

        for(int x=0; x<MAX_WIDTH; x++)
            for(int y=0; y<MAX_HEIGHT; y++)
                map[x][y] = new Point(m.map[x][y]);
    }

    //Updates whatever is in view of the player in his version of the map
    public void updateMap(char view[][], State state) {

        /*int map_x = state.location.x - view.length/2;
        for (int x = 0; x < view.length; x++) {
            int map_y = state.location.y - view.length/2;
            for (int y = 0; y <view[y].length; y++) {
                //Put empty cell at player's current location
                if ( map_x == state.location.x && map_y == state.location.y)
                    map[map_x][map_y].symbol = Constants.Symbol.Empty;
                //Put whatever is seen in the 5x5 char block into our map
                else
                    map[map_x][map_y].symbol = Constants.charToConstant(view[x][y]);
                map_y++;
            }
            map_x++;
        }*/

        if (state.currentDirection == Constants.Direction.North) {

            int x_m = state.location.x - view.length/2;
            for (int x_v=0; x_v < view.length; x_v++) {

                int y_m = state.location.y - view[0].length/2;

                for (int y_v=0; y_v < view[0].length; y_v++) {

                    if (x_m == state.location.x && y_m == state.location.y) {
                        map[x_m][y_m].symbol = Constants.Symbol.Empty;
                    } else {
                        map[x_m][y_m].symbol = Constants.charToConstant(view[y_v][x_v]);
                    }

                    y_m++;
                }

                x_m++;
            }
        }

        // SOUTH
        else if (state.currentDirection == Constants.Direction.South) {

            int x_m = state.location.x - view.length/2;
            for (int x_v=view.length-1; x_v >= 0; x_v--) {

                int y_m = state.location.y - view[0].length/2;
                for (int y_v=view[0].length-1; y_v >= 0; y_v--) {

                    if (x_m == state.location.x && y_m == state.location.y) {
                        map[x_m][y_m].symbol = Constants.Symbol.Empty;
                    } else {
                        map[x_m][y_m].symbol = Constants.charToConstant(view[y_v][x_v]);
                    }

                    y_m++;
                }

                x_m++;
            }
        }

        // EAST
        else if (state.currentDirection == Constants.Direction.East) {

            int x_m = state.location.x - view.length/2;

            for (int y_v=view[0].length-1; y_v >= 0; y_v--) {

                int y_m = state.location.y - view[0].length/2;
                for (int x_v=0; x_v < view.length; x_v++) {
                    if (x_m == state.location.x && y_m == state.location.y) {
                        map[x_m][y_m].symbol = Constants.Symbol.Empty;
                    } else {
                        map[x_m][y_m].symbol = Constants.charToConstant(view[y_v][x_v]);
                    }

                    y_m++;
                }

                x_m++;
            }
        }

        // WEST
        else if (state.currentDirection == Constants.Direction.West) {

            int x_m = state.location.x - view.length/2;
            for (int y_v=0; y_v < view[0].length; y_v++) {


                int y_m = state.location.y - view[0].length/2;
                for (int x_v=view.length-1; x_v >= 0; x_v--) {
                    if (x_m == state.location.x && y_m == state.location.y) {
                        map[x_m][y_m].symbol = Constants.Symbol.Empty;
                    } else {
                        map[x_m][y_m].symbol = Constants.charToConstant(view[y_v][x_v]);
                    }

                    y_m++;
                }

                x_m++;
            }
        }
        /*for (int y=0; y<5; y++) {
            for (int x=0; x<5; x++) {
                System.out.print(view[x][y]);
            }
            System.out.println();
        }*/
       // printMap(state.location,state.currentDirection);
    }


    public void printMap(Coordinate c, Constants.Direction d) {
        System.out.println("\nMap that's seen: "+d+"\n");
        // Set all coordinated of the map to UNKNOWN at the start
        for (int y=65; y < 100; y++) {
            for (int x=45; x < 105; x++) {
                if (x == c.x && y == c.y) {
                    System.out.print(Constants.playerDirection(d));
                } else {
                    System.out.print(map[x][y].symbol);
                }
            }
            System.out.println();
        }
    }

    //Returns what's at a certain co ordinate on the player's map
    public Constants.Symbol getSymbolAtCoordinate(Coordinate c) {
        return map[c.x][c.y].symbol;
    }

    //Gives us all the points at which have a specific item
    public ArrayList<Point> getItemLocations(Constants.Symbol item){
        ArrayList<Point> items = new ArrayList<>();
        //This uses the iterator() defined at the end
        for (Point point: this)
            if (point.symbol == item)
                items.add(point);
        return items;
    }

    // Return a list of all Points on the map containing a key/dynamite/axe
    public ArrayList<Point> findItemsOnMap() {
        ArrayList<Point> items = new ArrayList<>();
        for (Point point : this) {
            if  (point.symbol == Constants.Symbol.Dynamite ||
                 point.symbol == Constants.Symbol.Dynamite ||
                 point.symbol == Constants.Symbol.Axe)
                items.add(point);
        }
        return items;
    }

    //Finds Collectibles in our proximity. We'll use this when exploring
    public ArrayList<Point> itemsInProximity(Coordinate c) {
        ArrayList<Point> items = new ArrayList<Point>();

        for (int y=c.y-2; y <= c.y+2; y++) {
            for (int x=c.x-2; x <= c.x+2; x++) {
                if (    map[x][y].symbol == Constants.Symbol.Treasure ||
                        map[x][y].symbol == Constants.Symbol.Key      ||
                        map[x][y].symbol == Constants.Symbol.Axe      ||
                        map[x][y].symbol == Constants.Symbol.Dynamite) {
                    items.add(new Point(map[x][y]));
                }
            }
        }

        return items;
    }

    //Allows us to iterate through all the the points on map[][]
    //Iterates across the x axis before moving to the row below it
    @Override
    public Iterator<Point> iterator() {
        ArrayList<Point> list = new ArrayList<Point>();

        for (Point[] pointOnY : this.map) {
            for (Point pointOnX : pointOnY) {
                list.add(pointOnX);
            }
        }
        return list.iterator();
    }
}
