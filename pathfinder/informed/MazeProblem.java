package main.pathfinder.informed;

import java.util.*;

/**
 * Specifies the Maze Grid pathfinding problem including the actions, transitions,
 * goal test, and solution test. Can be fed as an input to a Search algorithm to
 * find and then test a solution.
 */
public class MazeProblem {

    // Public Fields
    // -----------------------------------------------------------------------------
    private final MazeState INITIAL_STATE, KEY_STATE;
    private final Set<MazeState> GOAL_STATES;
    
    // Private Fields
    // -----------------------------------------------------------------------------
    private String[] maze;
    private int rows, cols;
    
    // Private Static Vars
    // -----------------------------------------------------------------------------
    private static final Map<String, MazeState> TRANS_MAP = createTransitions();
    
    /**
     * @return Creates the transition map that maps String actions to 
     * MazeState offsets, of the format:
     * { "U": (0, -1), "D": (0, +1), "L": (-1, 0), "R": (+1, 0) }
     */
    private static final Map<String, MazeState> createTransitions () {
        Map<String, MazeState> result = new HashMap<>();
        result.put("U", new MazeState(0, -1));
        result.put("D", new MazeState(0,  1));
        result.put("L", new MazeState(-1, 0));
        result.put("R", new MazeState( 1, 0));
        return result;
    }
    
    
    // Constructor
    // -----------------------------------------------------------------------------
    
    /**
     * Constructs a new MazeProblem from the given maze; responsible for finding
     * the initial and goal states in the maze, and storing in the MazeProblem state.
     * 
     * @param maze An array of Strings in which characters represent the legal maze
     * entities, including:<br>
     * 'X': A wall, 'G': A goal, 'I': The initial state, '.': an open spot
     * For example, a valid maze might look like:
     * <pre>
     * String[] maze = {
     *     "XXXXXXX",
     *     "X.....X",
     *     "XIX.X.X",
     *     "XX.X..X",
     *     "XG....X",
     *     "XXXXXXX"
     * };
     * </pre>
     */
    public MazeProblem (String[] maze) {
        this.maze = maze;
        this.rows = maze.length;
        this.cols = (rows == 0) ? 0 : maze[0].length();
        MazeState foundInitial = null, foundKey = null;
        Set<MazeState> goals = new HashSet<>();
        
        // Find the initial and goal state in the given maze, and then
        // store in fields once found
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                switch (maze[row].charAt(col)) {
                case 'I':
                    foundInitial = new MazeState(col, row); break;
                case 'G':
                    goals.add(new MazeState(col, row)); break;
                case 'K':
                    foundKey = new MazeState(col, row); break;
                case '.':
                case 'X':
                case 'M':
                    break;
                default:
                    throw new IllegalArgumentException("Maze formatted invalidly");
                }
            }
        }
        INITIAL_STATE = foundInitial;
        KEY_STATE = foundKey;
        GOAL_STATES = goals;
    }
    
    
    // Methods
    // -----------------------------------------------------------------------------
    
    /**
     * Returns whether or not the given state is a Goal state.
     * 
     * @param state A MazeState (col, row) to test
     * @return Boolean of whether or not the given state is a Goal.
     */
    public boolean isGoalState (MazeState state) {
        return GOAL_STATES.contains(state);
    }
    
    /**
     * Returns the set of all 
     * 
     * @return The maze's initial state.
     */
    public Set<MazeState> getGoalStates () {
        return new HashSet<MazeState>(this.GOAL_STATES);
    }
    
    /**
     * Returns the initial state for this maze.
     * 
     * @return The maze's initial state.
     */
    public MazeState getInitialState () {
        return this.INITIAL_STATE.clone();
    }
    
    /**
     * Returns the key state for this maze, null if there is no key.
     * 
     * @return The maze's state containing a key.
     */
    public MazeState getKeyState () {
        return (this.KEY_STATE == null) ? null : this.KEY_STATE.clone();
    }
    
    /**
     * Returns the cost associated with entering the given state.
     * 
     * @param state state A MazeState in the maze.
     * @return The cost associated with moving into the given state.
     */
    public int getCost (MazeState state) {
        switch(maze[state.row].charAt(state.col)) {
            case 'M': return 3;
            default: return 1;
        }
    }
    
    /**
     * Returns a map of the states that can be reached from the given input
     * state using any of the available actions.
     * 
     * @param state A MazeState (col, row) representing the current state
     * from which actions can be taken
     * @return Map A map of actions to the states that they lead to, of the
     * format, for current MazeState (c, r):<br>
     * { "U": (c, r-1), "D": (c, r+1), "L": (c-1, r), "R": (c+1, r) }
     */
    public Map<String, MazeState> getTransitions (MazeState state) {
        // Store transitions as a Map between actions ("U", "D", ...) and
        // the MazeStates that they result in from state
        Map<String, MazeState> result = new HashMap<>();
        
        // For each of the possible directions (stored in TRANS_MAP), test
        // to see if it is a valid transition
        for (Map.Entry<String, MazeState> action : TRANS_MAP.entrySet()) {
            MazeState actionMod = action.getValue(),
                      newState  = new MazeState(state.col, state.row);
            newState.add(actionMod);
            
            // If the given state *is* a valid transition (i.e., within
            // map bounds and no wall at the position)...
            if (newState.row >= 0 && newState.row < rows &&
                newState.col >= 0 && newState.col < cols &&
                maze[newState.row].charAt(newState.col) != 'X') {
                // ...then add it to the result!
                result.put(action.getKey(), newState);
            }
        }
        return result;
    }
    
    /**
     * Given a possibleSoln, tests to ensure that it is indeed a solution to this MazeProblem,
     * as well as returning the cost.
     * 
     * @param possibleSoln A possible solution to test, which is a list of actions of the format:
     * ["U", "D", "D", "L", ...]
     * @return A MazeTestResult object with fields: IS_SOLUTION, determining whether or not the
     * given solution solves the maze, and COST, the total cost of the solution if so, -1 otherwise.
     */
    public MazeTestResult testSolution (ArrayList<String> possibleSoln) {
        // Update the "moving state" that begins at the start and is modified by the transitions
        MazeState movingState = new MazeState(INITIAL_STATE.col, INITIAL_STATE.row);
        int cost = 0;
        boolean hasKey = false;
        
        if (possibleSoln == null) { return new MazeTestResult(false, -1); }
        
        // For each action, modify the movingState, and then check that we have landed in
        // a legal position in this maze
        for (String action : possibleSoln) {
            MazeState actionMod = TRANS_MAP.get(action);
            movingState.add(actionMod);
            switch (maze[movingState.row].charAt(movingState.col)) {
            case 'X':
                return new MazeTestResult(false, -1);
            case 'K':
                hasKey = true; break;
            }
            cost += getCost(movingState);
        }
        return new MazeTestResult(isGoalState(movingState) && hasKey, cost);
    }
    
    
    /**
     * Public inner class serving as a tuple for the Maze Problem's testSolution return
     * value. Contains fields for both whether or not the provided solution actually
     * solves the maze, and the cost of the solution if so (-1 otherwise).
     */
    public static class MazeTestResult {
        
        public final boolean IS_SOLUTION;
        public final int COST;
        
        /**
         * Constructor for a MazeTestResult determining whether the result is a solution
         * or not and the associated cost of it if so.
         * @param isSoln Whether or not the provided solution solves the maze.
         * @param cost Total cost of the given solution if so, -1 otherwise.
         */
        public MazeTestResult (boolean isSoln, int cost) {
            this.IS_SOLUTION = isSoln;
            this.COST = cost;
        }
        
    }
    
}
