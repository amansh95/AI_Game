/**
 * Created by Rahul Gururaj on 5/27/2017.
 */
public class Constants {

    public enum Direction {
        North, South, East, West
    }

    public enum Symbol {
        PlayerNorth {
            public String toString() {return "^";}
        },

        PlayerSouth {
            public String toString() {return "v";}
        },

        PlayerEast {
            public String toString() {return ">";}
        },

        PlayerWest {
            public String toString() {return "<";}
        },

        Tree {
            public String toString() {return "T";}
        },

        Dynamite {
            public String toString() {return "d";}
        },

        Wall {
            public String toString() {return "*";}
        },

        Door {
            public String toString() {return "-";}
        },

        Axe {
            public String toString() {return "a";}
        },

        Empty {
            public String toString() {return " ";}
        },

        Water {
            public String toString() {return "~";}
        },

        Treasure {
            public String toString() {return "$";}
        },

        Key {
            public String toString() {return "k";}
        },

        Null {
            public String toString() {return "#";}
        },

    }

    public static Symbol charToConstant(char c) {
        switch (c) {
            case 'k':
                return Symbol.Key;

            case 'd':
                return Symbol.Dynamite;

            case '~':
                return Symbol.Water;

            case '$':
                return Symbol.Treasure;

            case '#':
                return Symbol.Null;

            case 'T':
                return Symbol.Tree;

            case 'a':
                return Symbol.Axe;

            case '-':
                return Symbol.Door;

            case ' ':
                return Symbol.Empty;
        }
        //If nothing is passed/ garbage values
        return Symbol.Null;
    }

    public static Symbol playerDirection (Direction d) {

        switch (d) {
            case North:
                return Symbol.PlayerNorth;

            case South:
                return Symbol.PlayerSouth;

            case East:
                return Symbol.PlayerEast;

            case West:
                return Symbol.PlayerWest;
        }

        //Shouldn't happen
        return Symbol.PlayerSouth;
    }

}
