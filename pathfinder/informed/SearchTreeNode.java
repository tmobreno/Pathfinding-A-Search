package main.pathfinder.informed;

/**
 * SearchTreeNode that is used in the Search algorithm to construct the Search
 * tree.
 */
class SearchTreeNode implements Comparable<SearchTreeNode> {    
    MazeState state;
    String action;
    SearchTreeNode parent;
    int costSoFar;
    int priority;

    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     * 
     * @param state The MazeState (row, col) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     */
    public SearchTreeNode (MazeState state, String action, SearchTreeNode parent, int priority, int costSoFar) {
        this.state = state;
        this.action = action;
        this.parent = parent;
        this.costSoFar = costSoFar;
        this.priority = priority;
    }
    
    
    public int compareTo (SearchTreeNode other){
        if (this.priority > other.priority){
            return 1;
        }
        if (this.priority <= other.priority){
            return -1;
        }else{
            return 0;
        }
    }
}