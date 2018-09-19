import java.util.ArrayList;

/**
 * Created by Rahul Gururaj on 5/28/2017.
 */
public class Pledge {
    public State state;
    //Variables needed for the pledge
    public boolean leftHandOnWall;
    public boolean axeNeeded, keyNeeded;
    private int turnBalance;
    private Constants.Direction initialDirection;
    private ArrayList<Coordinate> visited;
    public boolean addMarkOnWall;
    public ArrayList<MarkOnWall> markedSpots;
    private boolean explore;

    public Pledge(State currentState) {
        state = currentState;
        initialDirection = currentState.currentDirection;
        leftHandOnWall = axeNeeded = keyNeeded = false;
        turnBalance = 0;
        visited = new ArrayList<>();
        visited.add(state.location);
        addMarkOnWall = false;
        markedSpots = new ArrayList<>();
        explore = true;
    }

    public char explore(){
        char move = '#';
        resetIfItemFound();
        if(explore) {
            //If aren't following along a wall yet
            if (!leftHandOnWall) {
                //Try and go forward until you hit a wall
                if (canGoForward())
                    move = goForward();
                    //once you hit the wall, turn right and follow the wall with
                    //your left hand against the wall
                    //Also mark the wall as your starting position on the next move
                else {
                    move = turnRight();
                    leftHandOnWall = true;
                    addMarkOnWall = true;
                }
            } else {
                //If you just cut a tree or opened a door, go forward
                char lastmove = state.previousMove();
                if (lastmove == 'o' || lastmove == 'c')
                    move = goForward();

                    //With your left hand on the wall, keep following along the wall and
                    //Try to go to the left (Unless you just turned left)
                else if (lastmove != 'l' && canGoLeft())
                    move = turnLeft();

                /*Keep following the wall
                If we have to mark the wall, mark it
                If there's already a mark on the wall, we have returned to the position
                We initially started from.*/
                else if (canGoForward()) {
                    move = goForward();

                    //Check if we need to make a mark on the wall
                    if (addMarkOnWall) {
                        MarkOnWall mark = new MarkOnWall(visited);
                        markedSpots.add(mark);
                        addMarkOnWall = false;
                    }

                    //Check if there already is a mark on the wall
                    else if (isWallMarked())
                        explore = false;
                }
                //If we can't go left or forward, go right
                else
                    move = turnRight();

                //If the number of left turns = number of right turns,
                // and we're facing the same direction as we did initially, stop following the wall
                if(initialDirection == state.currentDirection && turnBalance == 0) {
                    leftHandOnWall = false;
                }
            }
        }
        return move;
    }

    public boolean stillExploring() {return explore;}

    //Update item requirements if a door or tree is found
    private void updateItemRequirements(Constants.Symbol destination) {
        switch (destination){
            case Door: keyNeeded = true; break;
            case Tree: axeNeeded = true; break;
        }
    }

    //If we found an item, we need to start exploring again by
    // erasing all the marks we made and reseting item requirements
    private void resetIfItemFound() {
        if (state.hasAxe && axeNeeded) {
            markedSpots.clear();
            axeNeeded = false;
        }
        if(state.hasKey && keyNeeded) {
            markedSpots.clear();
            keyNeeded = false;
        }

    }

    //Check if our current position along the wall has a mark
    private boolean isWallMarked() {
        boolean isWallMarked = false;
        for(MarkOnWall markOnWall: markedSpots)
            if(markOnWall.isMarkVisited(visited))
                isWallMarked = true;
        return isWallMarked;
    }


    //CHECK IF MOVEMENT IN A DIRECTION IS POSSIBLE
    //Check if we can go forward
    public boolean canGoForward() {
        Constants.Symbol destination = state.map.getSymbolAtCoordinate(state.coordInFront());
        boolean isValid = state.isValidMove(destination);
        if (!isValid)
            updateItemRequirements(destination);
        return isValid;
    }

    //Check if we can go Left
    public boolean canGoLeft() {
        Constants.Symbol destination = state.map.getSymbolAtCoordinate(state.coordOnLeft());
        boolean isValid = state.isValidMove(destination);
        if (!isValid)
            updateItemRequirements(destination);
        return isValid;
    }

    //Check if we can go Right
    public boolean canGoRight() {
        Constants.Symbol destination = state.map.getSymbolAtCoordinate(state.coordOnRight());
        boolean isValid = state.isValidMove(destination);
        if (!isValid)
            updateItemRequirements(destination);
        return isValid;
    }


    //MOVEMENT
    //Update turnBalance and return the character that represents the move
    private char goForward(){
        //Attempt to go forward
//        System.out.print("Moving Forward");
        char move = state.goForward();

        //If we moved, mark the new location as visited
        if(move == 'f')
            visited.add(state.location);

        //If we did something other than go forward, make a new mark on the wall
        else {
            addMarkOnWall = true;
            markedSpots.clear();
        }
        return move;
    }

    private char turnLeft(){
        state.turnLeft();
        turnBalance--;
        return 'l';
    }

    private char turnRight(){
        state.turnRight();
        turnBalance++;
        return 'r';
    }

    /*This class is made so we can store the co ordinates we started at and the next
    move made in one object*/
    private class MarkOnWall {
        public Coordinate firstWallContact;
        public Coordinate nextMove;

        public MarkOnWall(ArrayList<Coordinate> visited) {
            if (visited.size() > 1) {
                firstWallContact = visited.get(visited.size()-2);
                nextMove = visited.get(visited.size()-1);
            }
        }

        public boolean isMarkVisited(ArrayList<Coordinate> visited) {
            boolean hasReturnedToMark = false;
            if (visited.size() > 1)
                if(firstWallContact.equals(visited.get(visited.size()-2)) &&
                        nextMove.equals(visited.get(visited.size()-1)) )
                    hasReturnedToMark = true;
            return hasReturnedToMark;
        }
    }
}
