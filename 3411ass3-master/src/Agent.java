/*********************************************
 *  Agent.java
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411 Artificial Intelligence
 *  UNSW Session 1, 2017
*/

/**
 * PHASE 1
 * Our program begins with the agent in the middle of a 160x160 map. Right now it can only see the nearest 5x5 points
 * Using the Pledge algorithm, it moves around gathering as much information as it can. It stores its initial state
 * and keeps a track of the moves made.
 * If during exploration, the agent sees an item, it will try to get it. The  path to that specific item is found using
 * Astar algorithm.
 *
 * PHASE 2
 * Now we need to get the treasure. Our Astar search will compute a path to get there and a list of items needed to get
 * there.
 * Now we recurse through all the items needed and see if we can get them
 *
 * PHASE 3
 * Finding a path to the treasure. Now that we have the item(s) needed to get to the goal, we use Astar to compute
 * the shortest path to reach the goal
 *
 * PHASE 4
 * Now that the gold is acquired, we set the destination to the original starting point[0,0] on the map and find the
 * shortest path to get to it.
 *
 * The Agent solves the relatively easier puzzles but is not able to run on the more complicated ones.
 */

import java.util.*;
import java.io.*;
import java.net.*;

public class Agent {
   public static final int PHASE_1_EXPLORE = 0;
   public static final int PHASE_2_SET_GOAL = 1;
   public static final int PHASE_3_FIND_PATH = 2;
   public static final int PHASE_4_RETURN = 3;

   public static final int MOVE_LIMIT = 10;

   //Class variables
   boolean gettingItem, keepExploring;
   ArrayList<Point> unreachable, itemsNearby;
   ArrayList<Character> path;
   State game;
   int phase;
   Point currentGoal;
   Pledge explore;

   public Agent() {

      unreachable = new ArrayList<>();
      keepExploring = false;
      game = new State();
      explore = new Pledge(game);
      path = new ArrayList<>();
      gettingItem = false;
      phase = PHASE_1_EXPLORE;
   }

   public char get_action( char view[][] ) {

       try {
           Thread.sleep(25);
       } catch (Exception e) {

       }

      game.map.updateMap(view,game);
      //PHASE 1 : Explore using Pledge algorithm and pick up any reachable item in proximity
       int stage = phase+1;
       //System.out.println("\nPHASE :"+stage+" location ="+game.location.x+" ,"+game.location.y);

       if(phase == PHASE_1_EXPLORE) {
         //Look for items in proximity
         itemsNearby = game.map.itemsInProximity(game.location);

         //Count number of reachable items
         int i;
         for (i = 0; i < itemsNearby.size(); i++) {
            Point item = itemsNearby.get(i);
            if (listContains(unreachable, item)) {
               i++;
            } else {
               break;
            }
         }
         // If there are items closeby that are possibly reachable try to get them
         if (itemsNearby.size() > 0 && i < itemsNearby.size()) {

            currentGoal = itemsNearby.get(i);
            // If the item is unreachable continue exploring
            if (!listContains(unreachable, currentGoal)) {
               gettingItem = true;
               phase = PHASE_3_FIND_PATH;
                System.out.println("SWITCHED TO PHASE 3");
                explore.leftHandOnWall = false;
            } else {
               return explore.explore();
            }
         }else {
            // set a boolean used for exploration cases
            if (keepExploring) {
               keepExploring = false;
            }
            // Explore
            char moveToMake = explore.explore();

            // If we have the gold, go to next phase which is return to start
            if (game.hasTreasure == true) {
               phase = PHASE_4_RETURN;
            } else if (!explore.stillExploring()) {
               phase = PHASE_2_SET_GOAL;
            }
            return moveToMake;
         }

      }
      // Phase 2: Next phase is finding the gold
      else if (phase == PHASE_2_SET_GOAL) {
          // System.out.println("INSIDE PHASE 2\n");
           if (!game.hasTreasure) {
            currentGoal = this.game.map.getItemLocations(Constants.Symbol.Treasure).get(0);

         } else {
            currentGoal = new Point(80, 80, Constants.Symbol.Empty);
           //  System.out.println("SET GOAL TO HOME\n");
         }
         phase++;
      }

      // Phase 3: Take the first goal and path to it
      // 	  Repeat until all goals have been made
      else if (phase == PHASE_3_FIND_PATH) {

         // If there are moves to perform, do them
         if (path.size() > 0) {
             char moveToMake = path.remove(0);
             game.move(moveToMake);

            // If we were picking up an item while exploring, go back to exploring
            if (path.size() == 0 && gettingItem) {
               phase = PHASE_1_EXPLORE;
               gettingItem = false;
               explore.markedSpots.clear();
               explore.addMarkOnWall = true;
               // System.out.println("Switching to 1");
            } else if (path.size() == 0){
               phase = PHASE_2_SET_GOAL;
              //  System.out.println("Switching to 2");
            }
            return moveToMake;
         } else {
             System.out.println("A* to :'"+currentGoal.symbol+"'");
            // Reset the past cost before performing A*
            State copy = new State(game);
            copy.pastCost = 0;
            Astar search = new Astar(copy, currentGoal);
            // If in the exploration stage still, limit the moves A* is allowed
            // This account for unreachable items
            if (gettingItem) {
               search.pastCostLimit = MOVE_LIMIT;
            }

            ArrayList<Character> moves = search.moves();
            search.printVisited();
            // If moves is not null the goal is reachable, add the moves to the list of moves to execute
            if (moves != null) {
                System.out.println("Adding path\n");
                path.addAll(moves);
               // If not, the item is unreachable, continue exploring
            } else if (moves == null && gettingItem) {
               unreachable.add(currentGoal);
               phase = PHASE_1_EXPLORE;
               gettingItem = false;
               keepExploring = true;
               explore.markedSpots.clear();
               explore.addMarkOnWall = true;
            }
         }
      }

      // Phase 4 :Return the start phase(starting point) [0,0]
      else if (phase == PHASE_4_RETURN) {
          // System.out.println("RETURNING\n");
          currentGoal = new Point(80, 80, Constants.Symbol.Empty);
          phase--;
      }
      return 0;
   }

   private boolean listContains(ArrayList<Point> list, Point p) {
      for (Point z : list) {
         if (p.equals(z)) {
            return true;
         }
      }
      return false;
   }

   void print_view( char view[][] )
   {
      int i,j;
      System.out.println("\n+-----+");
      for( i=0; i < 5; i++ ) {
         System.out.print("|");
         for( j=0; j < 5; j++ ) {
            if(( i == 2 )&&( j == 2 )) {
               System.out.print('^');
            }
            else {
               System.out.print( view[i][j] );
            }
         }
         System.out.println("|");
      }
      System.out.println("+-----+");
   }

   public static void main( String[] args )
   {
      InputStream in  = null;
      OutputStream out= null;
      Socket socket   = null;
      Agent  agent    = new Agent();
      char   view[][] = new char[5][5];
      char   action   = 'F';
      int port;
      int ch;
      int i,j;

      if( args.length < 2 ) {
         System.out.println("Usage: java Agent -p <port>\n");
         System.exit(-1);
      }

      port = Integer.parseInt( args[1] );

      try { // open socket to Game Engine
         socket = new Socket( "localhost", port );
         in  = socket.getInputStream();
         out = socket.getOutputStream();
      }
      catch( IOException e ) {
         System.out.println("Could not bind to port: "+port);
         System.exit(-1);
      }

      try { // scan 5-by-5 wintow around current location
         while( true ) {
            for( i=0; i < 5; i++ ) {
               for( j=0; j < 5; j++ ) {
                  if( !(( i == 2 )&&( j == 2 ))) {
                     ch = in.read();
                     if( ch == -1 ) {
                        System.exit(-1);
                     }
                     view[i][j] = (char) ch;
                  }
               }
            }
            //agent.print_view( view ); // COMMENT THIS OUT BEFORE SUBMISSION
            action = agent.get_action( view );
            out.write( action );
         }
      }
      catch( IOException e ) {
         System.out.println("Lost connection to port: "+ port );
         System.exit(-1);
      }
      finally {
         try {
            socket.close();
         }
         catch( IOException e ) {}
      }
   }
}