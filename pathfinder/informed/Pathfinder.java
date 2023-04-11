package main.pathfinder.informed;

import java.util.*;

/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, breadth-first tree search.
 */
public class Pathfinder {
    
    /**
     * Given a MazeProblem, which specifies the actions and transitions available in the
     * search, returns a solution to the problem as a sequence of actions that leads from
     * the initial to a goal state.
     * 
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @return An ArrayList of Strings representing actions that lead from the initial to
     * the goal state, of the format: ["R", "R", "L", ...]
     */
    public static ArrayList<String> solve (MazeProblem problem) {
        // initializes graveyard, priority queue, and first node
        Set <MazeState> graveyard = new HashSet<>();
        PriorityQueue<SearchTreeNode> frontier = new PriorityQueue<>();
        SearchTreeNode first = new SearchTreeNode(problem.getInitialState(), null, null, 0, 0);
        SearchTreeNode keyState = null;

        // adds proper initial node to frontier and graveyard
        frontier.add(first);
        graveyard.add(first.state);

        // sets the keyState if it isn't null in the puzzle, initialized earlier so it can be used outside if statement
        if (problem.getKeyState() != null){
            keyState = keyFind(problem, graveyard, frontier);
        }

        // returns null if the keyState cannot be found
        if(keyState == null || problem.getKeyState() == null){
            return null;
        }

        // clears the fronier and graveyard for when we search for the goal
        frontier.clear();
        graveyard.clear();
        frontier.add(keyState);

        // finds the final goal
        return goalFind(problem, graveyard, frontier);
    }

    /**
     * Given the problem, returns the node storing the path to the key state.
     * @param problem A MazeProblem to be navigated.
     * @param graveyard The graveyard of states that have been searched so that they are not repeated.
     * @param frontier The next node to search, ordered by priority, which is calculated using Manhattan distance + next space cost.
     * @return A SearchTreeNode storing the most efficient path from the initial to the key.
     */
    public static SearchTreeNode keyFind (MazeProblem problem, Set<MazeState> graveyard, Queue<SearchTreeNode> frontier){
        if(!frontier.isEmpty()){
            SearchTreeNode expanding = frontier.poll();
            Map<String, MazeState> currentTransitions = problem.getTransitions(expanding.state);

            for (Map.Entry<String, MazeState> action : currentTransitions.entrySet()){
                if(!graveyard.contains(action.getValue())){
                    // adds the current state
                    graveyard.add(expanding.state);

                    // deals with priority, I kept them separate to prevent the lines from getting to long
                    int nextSpaceCost = problem.getCost(action.getValue());
                    int distanceFromObjectiveC = Math.abs(problem.getKeyState().col - action.getValue().col);
                    int distanceFromObjectiveR = Math.abs(problem.getKeyState().row - action.getValue().row);
                    int priority = expanding.costSoFar + distanceFromObjectiveC + distanceFromObjectiveR + nextSpaceCost;
                    int newCostSoFar = expanding.costSoFar + nextSpaceCost;

                    SearchTreeNode child = new SearchTreeNode(action.getValue(), action.getKey(), expanding, priority, newCostSoFar);

                    if (child.state.equals(problem.getKeyState())){
                        return child;
                    }
                    frontier.add(child);
                }
            }
        }
        else if (frontier.isEmpty()){
            return null;
        }
    return keyFind(problem, graveyard, frontier);
    }

    /**
     * Given the key state (and path to the key state), finds the nearest and most efficient path
     * to a goal, if one is possible.
     * @param problem A MazeProblem to be navigated.
     * @param graveyard The graveyard of states that have been searched so that they are not repeated.
     * @param frontier The next node to search, ordered by priority, which is calculated using Manhattan distance + next space cost.
     * @return A complete solution in an ArrayList
     */
    public static ArrayList<String> goalFind (MazeProblem problem, Set<MazeState> graveyard, Queue<SearchTreeNode> frontier){
        if (!frontier.isEmpty()){
            SearchTreeNode expanding = frontier.poll();
            Map<String, MazeState> currentTransitions = problem.getTransitions(expanding.state);

            for (Map.Entry<String, MazeState> action : currentTransitions.entrySet()){
                if(!graveyard.contains(action.getValue())){
                    // adds the current state to the graveyard
                    graveyard.add(expanding.state);

                    // deals with priority, initiates the distances as -1 because every distance will be greater
                    int distanceFromObjectiveC = -1, distanceFromObjectiveR = -1, nextSpaceCost = problem.getCost(action.getValue());
                    for (MazeState mazeState : problem.getGoalStates()) {
                        if (distanceFromObjectiveC > Math.abs(mazeState.col - action.getValue().col) || distanceFromObjectiveC == -1){
                            distanceFromObjectiveC = Math.abs(mazeState.col - action.getValue().col);
                        }
                        if (distanceFromObjectiveR > Math.abs(mazeState.row - action.getValue().row) || distanceFromObjectiveR == -1){
                            distanceFromObjectiveR = Math.abs(mazeState.row - action.getValue().row);
                        }
                    }

                    int priority = expanding.costSoFar + distanceFromObjectiveC + distanceFromObjectiveR + nextSpaceCost;
                    int newCostSoFar = expanding.costSoFar + nextSpaceCost;
                    SearchTreeNode child = new SearchTreeNode(action.getValue(), action.getKey(), expanding, priority, newCostSoFar);

                    if (problem.isGoalState(child.state)){
                        ArrayList<String> solution = new ArrayList<String>();
                        return completeSolution(child, solution);
                    }
                    frontier.add(child);
                }else if (frontier.isEmpty()){
                    return null;
                }
            }
        }
    return goalFind(problem, graveyard, frontier);
    }

    /**
     * Given the final node, this will follow the path back up and return
     * the path to the key, then goal, from the initial, returning the actions to
     * get through to the goal state.
     * @param kiddo A final node that has the path stored.
     * @param solved The storage for the path.
     * @return An ArrayList showing the actions that lead through the maze.
     */
    public static ArrayList<String> completeSolution (SearchTreeNode kiddo, ArrayList<String> solved){
        String movement = kiddo.action;
        if (kiddo.parent != null){
            solved.add(0, movement);
            completeSolution(kiddo.parent, solved);
        }
        return solved;
    }  
}
