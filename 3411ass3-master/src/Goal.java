/**
 * Created by Aman on 28/05/2017.
 */
import java.util.ArrayList;
public class Goal {
    private ArrayList<Point> finishList;

    private State s;
    private ArrayList<Point> availableItems;

    public Goal(State s) {
        this.s=s;
        finishList = new ArrayList<>();
        availableItems=new ArrayList<>();

        /**
         * For all the items remaining:
         * Check if we need it:
         * If yes, add to list
         * else move on
         */
        ArrayList<Point> itemsOnMap = s.map.findItemsOnMap();
        for (Point p : itemsOnMap) {
            if (p.symbol == Constants.Symbol.Axe && !s.hasAxe) {
                availableItems.add(p);
            } else if (p.symbol == Constants.Symbol.Key && !s.hasKey) {
                availableItems.add(p);
            } else if (p.symbol == Constants.Symbol.Dynamite) {
                availableItems.add(p);
            }
        }

    }

    public ArrayList<Point> getGoalList() {
        return finishList;
    }

    public void run() {

        // If gold is in view, we need to get there
        ArrayList<Point> goldPoints = s.map.getItemLocations(Constants.Symbol.Treasure);
        if (goldPoints.size() > 0) {

            finishList = pathToLocation(goldPoints.get(0));

        }
    }

    /**
     * Find all the item location and points required to reach the final state
     */
    private ArrayList<Point> pathToLocation(Point finish) {

        Astar pathFinder = new Astar(s, finish);
        ArrayList<ArrayList<Constants.Symbol>> itemsRequiredTESTED = new ArrayList<ArrayList<Constants.Symbol>>();
        ArrayList<Point> itemsRequiredPoints = new ArrayList<Point>();

        //Get a list of items required to reach the finish
        ArrayList<Constants.Symbol> itemsRequired = pathFinder.itemsRequired(availableItems);

        // Try all possible items combination to reach the finish
        while (itemsRequired != null) {

            // if no items required, go straight to finish
            if (itemsRequired.size() == 0) {
                return itemsRequiredPoints;
            }

            //Find points on the map for each item required
            ArrayList<Point> points = new ArrayList<Point>();

            for (Constants.Symbol s : itemsRequired) {

                for (Point p : availableItems) {
                    if (p.symbol == s) {
                        points.add(p);
                    }
                }
            }

            /**
             * For each item required, add to arraylist
             * find the path to item in the list
             *
             */
            for (Point p : points) {
                ArrayList<Point> temp;
                temp = pathToLocation(p);

                //If path found, add point
                if (temp != null) {
                    itemsRequiredPoints.add(p);
                    itemsRequiredPoints.addAll(temp);
                }
            }

            // check if a valid path to finish is found
            boolean isValid = false;

            for (Constants.Symbol s : itemsRequired) {
                isValid = false;

                for (Point p : itemsRequiredPoints) {
                    if (p.symbol == s) {
                        isValid = true;
                    }
                }
                //i path is not found
                if (!isValid) {
                    itemsRequiredTESTED.add(itemsRequired);
                    itemsRequired = pathFinder.itemsRequired(availableItems);
                    boolean alreadyTested = true;
                    while (alreadyTested) {
                        //Double check if we arent finding a path to a previously unreachable item
                        for (ArrayList<Constants.Symbol> testedList : itemsRequiredTESTED) {
                            alreadyTested = false;
                            if (itemsRequired.containsAll(testedList)) {
                                itemsRequired = pathFinder.itemsRequired(availableItems);

                                break;
                            }
                        }
                    }
                    break;
                }
            }
            // all items are reached
            if (isValid) {
                return itemsRequiredPoints;
            }
        }

        return null;
    }
}
