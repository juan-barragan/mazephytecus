package ibqpd.maze;

import java.util.ArrayList;
import java.util.Stack;

public class PathSolver {
	@SuppressWarnings("unchecked")
	public static ArrayList<CellPoint> getLonguestPath(CellPoint start, Maze l)
	{
		boolean visitedCell[][] = new boolean[l.getNumRows()][l.getNumColumns()];
		for(int i=0; i<l.getNumRows(); ++i)
			for(int j=0; j<l.getNumColumns(); ++j)
				visitedCell[i][j] = false;
		Stack<CellPoint> longuestPath = new Stack<CellPoint>();
		CellPoint pos = start;
		Stack<CellPoint> currentPath = new Stack<CellPoint>();
		currentPath.push(pos.clone());
		while (!currentPath.empty())
		{	
			pos = currentPath.peek().clone();
			visitedCell[pos.row_][pos.column_] = true;
			if(l.canMoveIn(pos.row_, pos.column_, Maze.DIRECTION_NORTH) && !visitedCell[pos.row_-1][pos.column_]) {
				--pos.row_;
				currentPath.push(pos.clone());
			} else if (l.canMoveIn(pos.row_, pos.column_, Maze.DIRECTION_EAST) && !visitedCell[pos.row_][pos.column_+1]) {
				++pos.column_;
				currentPath.push(pos.clone());
			} else if (l.canMoveIn(pos.row_, pos.column_, Maze.DIRECTION_SOUTH) && !visitedCell[pos.row_+1][pos.column_]) {
				++pos.row_;
				currentPath.push(pos.clone());
			} else if (l.canMoveIn(pos.row_, pos.column_, Maze.DIRECTION_WEST) && !visitedCell[pos.row_][pos.column_-1]) {
				--pos.column_;
				currentPath.push(pos.clone());
			} else {
				// This stack will no longer grow, check its size
				if (currentPath.size() > longuestPath.size()) {
					longuestPath = (Stack<CellPoint>) currentPath.clone();
				}
				pos = currentPath.pop();
			}	
		}

		ArrayList<CellPoint> ans = new ArrayList<CellPoint>();
		while(!longuestPath.empty())
			ans.add(longuestPath.pop());
		
		return ans;
	}
	
	public static final int VISITED = 1;
	public static final int NOT_VISITED = 0;
}

