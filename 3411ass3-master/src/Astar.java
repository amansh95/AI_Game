/**
 * Created by Aman Shekhar on 28/05/2017.
 */
import java.util.*;
public class Astar {
    private PriorityQueue<State> unVisited;
    private ArrayList<State> visited;
    private State start;
    private Point finish;
    private boolean firstCall;
    private int Dynamites;
    public int pastCostLimit;


    public Astar(State start, Point finish) {
        this.start= start;
        this.finish = finish;
        unVisited = new PriorityQueue<State>();
        visited = new ArrayList<State>();
        start.movesMade.clear();
        start.manhattanDistanceTo(finish);
        unVisited.add(start);
        firstCall=true;
        Dynamites = 0;
        pastCostLimit = Integer.MAX_VALUE;
    }

    //Returns a set of movements required to reach the final state
    public ArrayList<Character> moves() {
        State current;

        //While any unvisited states exist
        while (unVisited.size() > 0) {
            current=unVisited.poll();

            //We can control the search radius by limiting pastCostLimit
            if (current.pastCost > pastCostLimit)
                return null;
            visited.add(current);

            //If current state is final state
            if(current.location.x == finish.x && current.location.y == finish.y)
                return current.movesMade;

            ArrayList<State> children = current.getChildren(finish);
            for (State c: children) {
                if (!(listContains(visited, c) || listContains(unVisited, c))) {
                    unVisited.offer(c);
                }

            }
        }
        return null;
    }

    public void printVisited() {
        System.out.println();
        for (State p : visited){
            System.out.print("("+p.location.x+","+p.location.y+") ");
        }
    }
    private boolean listContains(Iterable<State> I, State s) {
        for (State current : I) {
            if (current.isEqual(s))
                return true;
        }
        return false;
    }

    public ArrayList<Constants.Symbol> itemsRequired(ArrayList<Point> itemsAvailable ) {
        /**
         * If an item is required in getting to the finish state,
         * check if we have that item. If we dont have the item, add to the list
         * Continue till finish state is reached and return the list of items needed to reach the final state
         */

        if (firstCall) {

            for (Point p : itemsAvailable) {
                switch (p.symbol){
                    case Axe: start.hasAxe = true;
                         break;
                    case Dynamite: Dynamites++;
                         start.dynamite++;
                         break;
                    case Key: start.hasKey = true;
                         break;
                }
            }
            firstCall = false;
        }

        ArrayList<Character> moves = moves();

        if (moves == null) {
            return null;
        }

        // To see what items were used for the set of given movements
        ArrayList<Constants.Symbol> itemsRequired = new ArrayList<>();
        for (Character c : moves) {

            if (c == 'c' && containsItem(Constants.Symbol.Axe, itemsAvailable)) {
                itemsRequired.add(Constants.Symbol.Axe);

            } else if (c == 'o' && containsItem(Constants.Symbol.Key, itemsAvailable)) {
                itemsRequired.add(Constants.Symbol.Key);

            } else if (c == 'b') {
                if (Dynamites != 0) {
                    itemsRequired.add(Constants.Symbol.Dynamite);
                    Dynamites--;
                }
            }
        }

        return itemsRequired;
    }

    // Return whether a given symbol is in an arraylist of points
    private boolean containsItem(Constants.Symbol s, ArrayList<Point> list) {
        for (Point p : list) {
            if (p.symbol == s) {
                return true;
            }
        }
        return false;
    }
}
