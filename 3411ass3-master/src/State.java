import java.util.ArrayList;

/**
 * Created by Rahul Gururaj on 5/27/2017.
 */
public class State implements Comparable<State>{

    // Class variables
    public int dynamite;

    public boolean hasKey;
    public boolean hasAxe;
    public boolean hasTreasure;

    public Constants.Direction currentDirection;

    public Coordinate location;

    public int pastCost;
    public int costToGoal;

    public int moveCount;

    public ArrayList<Character> movesMade;

    public Map map;

    // Class Constants
    public static final int COST_ITEM = 1;
    public static final int COST_MOVE = 1;
    public static final int COST_TURN = 1;
    public static final int COST_BOMB = 2;

    public State(){
        hasAxe = hasKey = hasAxe = hasTreasure = false;
        moveCount = dynamite = pastCost = 0;
        currentDirection = Constants.Direction.North;
        //We assume we're in the middle of the map and build it from there
        location = new Coordinate(Map.MAX_WIDTH/2,Map.MAX_HEIGHT/2);
        movesMade = new ArrayList<>();
        map = new Map();
    }

    public State (State s) {
        this.movesMade = new ArrayList<>(s.movesMade);
        this.location = new Coordinate(s.location.x,s.location.y);
        this.currentDirection = s.currentDirection;
        this.hasAxe = s.hasAxe;
        this.hasKey = s.hasKey;
        this.hasTreasure = s.hasTreasure;
        this.dynamite = s.dynamite;
        this.pastCost = s.pastCost;
        this.moveCount = s.moveCount;
        this.map = new Map(s.map);
    }

    //MOVEMENT
    //For every move, update moveCount, pastCost, Movesmade
    //And items if any are picked

    public char goForward() {
        Constants.Symbol inFront = map.getSymbolAtCoordinate(coordInFront());
        char move;

        //If there's an obstacle but we have the item to deal with it
        if (hasKey && inFront == Constants.Symbol.Door) {
            move = 'o';
            pastCost += COST_ITEM;
            moveCount++;
            movesMade.add(move);

        } else if (hasAxe && inFront == Constants.Symbol.Tree) {
            move = 'c';
            pastCost += COST_ITEM;
            moveCount++;
            movesMade.add(move);

        } else if (dynamite >0 && (inFront == Constants.Symbol.Wall ||
                                   inFront == Constants.Symbol.Tree ||
                                   inFront == Constants.Symbol.Door) ){
            move = 'b';
            dynamite--;
            pastCost += COST_BOMB;
            moveCount++;
            movesMade.add(move);

        //If there's empty space move forward/ update collectibles if there is an item
        } else {
            move = 'f';
            pastCost += COST_MOVE;
            location = coordInFront();
            moveCount++;
            movesMade.add(move);

            switch (inFront) {
                //If you moved into empty point, do nothing
                case Empty: break;
                //If you picked up an item, update state
                case Axe: hasAxe = true; break;
                case Key: hasKey = true; break;
                case Treasure: hasTreasure = true; break;
                case Dynamite: dynamite++; break;
            }
        }
        return move;
    }

    public void turnLeft() {
        switch (currentDirection){
            case North: currentDirection = Constants.Direction.West; break;
            case East: currentDirection = Constants.Direction.North; break;
            case South: currentDirection = Constants.Direction.East; break;
            case West: currentDirection = Constants.Direction.South; break;
        }
        moveCount++;
        movesMade.add('l');
        pastCost += COST_TURN;
    }

    public void turnRight() {
        switch (currentDirection){
            case North: currentDirection = Constants.Direction.East; break;
            case East: currentDirection = Constants.Direction.South; break;
            case South: currentDirection = Constants.Direction.West; break;
            case West: currentDirection = Constants.Direction.North; break;
        }
        moveCount++;
        movesMade.add('r');
        pastCost += COST_TURN;
    }

    //Find what's in front of a player.
    //Depends on which direction the player Is currently facing.
    public Coordinate coordInFront() {
        Coordinate coordInfront = new Coordinate(location);
        switch (currentDirection) {
            case North: coordInfront.y--; break;
            case South: coordInfront.y++; break;
            case East: coordInfront.x++; break;
            case West: coordInfront.x--; break;
        }
        return coordInfront;
    }

    //Find what's to the back of a player.
    public Coordinate coordOnBack() {
        Coordinate coordOnBack = new Coordinate(location);
        switch (currentDirection) {
            case North: coordOnBack.y++; break;
            case South: coordOnBack.y--; break;
            case East: coordOnBack.x--; break;
            case West: coordOnBack.x++; break;
        }
        return coordOnBack;
    }

    //Find what's to the left of a player.
    public Coordinate coordOnLeft() {
        Coordinate coordOnLeft = new Coordinate(location);
        switch (currentDirection) {
            case North: coordOnLeft.x--; break;
            case South: coordOnLeft.x++; break;
            case East: coordOnLeft.y--; break;
            case West: coordOnLeft.y++; break;
        }
        return coordOnLeft;
    }

    //Find what's to the right of a player.
    public Coordinate coordOnRight() {
        Coordinate coordOnRight = new Coordinate(location);
        switch (currentDirection) {
            case North: coordOnRight.x++; break;
            case South: coordOnRight.x--; break;
            case East: coordOnRight.y++; break;
            case West: coordOnRight.y--; break;
        }
        return coordOnRight;
    }

    //Pass the next symbol you're attempting to step on to check if it's a valid move
    public boolean isValidMove(Constants.Symbol nextMove) {
        //Start by assuming it's an invalid move
        boolean isValidMove = false;

        //Valid if the next move is to an empty space
        if (nextMove == Constants.Symbol.Empty)
            isValidMove = true;

        //Valid if the next move is a tree or a door and you have the items to deal with it
        else if (hasAxe && nextMove == Constants.Symbol.Tree ||
                 hasKey && nextMove == Constants.Symbol.Door )
            isValidMove = true;

        //Valid if the next move is a collectible
        else if (nextMove == Constants.Symbol.Axe ||
                 nextMove == Constants.Symbol.Dynamite ||
                 nextMove == Constants.Symbol.Treasure ||
                 nextMove == Constants.Symbol.Key)
            isValidMove = true;
        return  isValidMove;

        //ToDo: Figure out if we need to set walls&trees as a valid move if we have dynamite
    }

    //Pass character commands to make the move it represents
    public void move(char c) {
        switch (c){
            case 'f': goForward(); break;
            case 'l': turnLeft(); break;
            case 'r': turnRight(); break;
            default: goForward(); break;
        }
    }

    //Makes sure that a given symbol would make a valid point we can move into
    public boolean isValidChild(Constants.Symbol nextMove) {
        //Start by assuming it's an invalid move
        boolean isValidMove = false;

        //Valid if the next move is to an empty space
        if (nextMove == Constants.Symbol.Empty)
            isValidMove = true;

        //Valid if the next move is a tree or a door and you have the items to deal with it
        else if (hasAxe && nextMove == Constants.Symbol.Tree ||
                hasKey && nextMove == Constants.Symbol.Door )
            isValidMove = true;

        //Valid if the next move is a collectible
        else if (nextMove == Constants.Symbol.Axe ||
                nextMove == Constants.Symbol.Dynamite ||
                nextMove == Constants.Symbol.Treasure ||
                nextMove == Constants.Symbol.Key)
            isValidMove = true;

        //Valid if the there's a wall or a tree and we have dynamite
        else if ( dynamite > 0 && nextMove != Constants.Symbol.Water)
            isValidMove = true;
        return  isValidMove;

    }

    //Sets costToGoal to the manhattan distance to goal
    public void manhattanDistanceTo(Point destination) {
        costToGoal = Math.abs((destination.x - location.x)+(destination.y - location.y));
    }

    //Returns sum of cost so far + manhattan distance cost
    public int totalCost() {
        return pastCost+costToGoal;
    }

    //Returns previous Move made
    public char previousMove() {
        return movesMade.get(movesMade.size()-1);
    }

    //Compare two states to see which state is favourable
    @Override
    public int compareTo(State state) {
        return totalCost() - state.totalCost();
    }

    //Compare two states on basis of direction and location
    public boolean isEqual(State state) {
        boolean isEqual = false;
        if (location.equals(state.location) &&
                currentDirection == state.currentDirection)
            isEqual = true;
        return isEqual;
    }

    //Gives us all the possible states we can immediately move into
    public ArrayList<State> getChildren(Point destination){
        ArrayList<State> children = new ArrayList<>();
        Constants.Symbol childSymbol;

        //Top Child
        childSymbol  = map.getSymbolAtCoordinate(coordInFront());
        if(isValidChild(childSymbol)) {
            State child = new State(this);
            child.goForward();
            if(child.previousMove() != 'f')
                child.goForward();
            child.manhattanDistanceTo(destination);
            children.add(child);
        }

        //Bottom Child
        childSymbol  = map.getSymbolAtCoordinate(coordOnBack());
        if(isValidChild(childSymbol)) {
            State child = new State(this);
            child.turnRight();
            child.turnRight();
            child.goForward();
            if(child.previousMove() != 'f')
                child.goForward();
            child.manhattanDistanceTo(destination);
            children.add(child);
        }

        //Left Child
        childSymbol  = map.getSymbolAtCoordinate(coordOnLeft());
        if(isValidChild(childSymbol)) {
            State child = new State(this);
            child.turnLeft();
            child.goForward();
            if(child.previousMove() != 'f')
                child.goForward();
            child.manhattanDistanceTo(destination);
            children.add(child);
        }

        //Right Child
        childSymbol  = map.getSymbolAtCoordinate(coordOnRight());
        if(isValidChild(childSymbol)) {
            State child = new State(this);
            child.turnRight();
            child.goForward();
            if(child.previousMove() != 'f')
                child.goForward();
            child.manhattanDistanceTo(destination);
            children.add(child);
        }
        return children;
    }


}