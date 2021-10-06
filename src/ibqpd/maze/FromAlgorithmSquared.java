package ibqpd.maze;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

public class FromAlgorithmSquared implements MazeBuilder {
	public FromAlgorithmSquared()
	{
	}
	public void initSquared(int r, int c)
	{
		beingBuild_ = new Maze(r,c);
		beingBuild_.initFullMazeGrid();
	}
	
	// The number of rows comes from the number of elements in bits
	public void setParametersForBitWiseMode(int[] bits, int columns)
	{
		beingBuild_ = new Maze(bits.length, columns);
		beingBuild_.initBitWise();
		for(int i=0; i<bits.length; ++i) {
			int currentRow = bits[i];
			for(int j=columns-1; j>=0; --j) {
				if ((currentRow & 1) > 0)
					beingBuild_.board_[i][j] = Maze._1111;
				currentRow>>=1;
			}
		}
	}

	public void setFromTemplate(int i)
	{
		setParametersForBitWiseMode(MazeTemplates.templates[i], 16);
	}
	
	public void buildMaze()
	{		
		mapOfCellTypes_ = new TreeMap<Integer, ArrayList<CellPoint> >();
		cellTracker_ = new CellTracker(beingBuild_.getNumRows(), beingBuild_.getNumColumns());
		for(int j=0; j<beingBuild_.getNumColumns(); ++j)
			for(int i=0;i<beingBuild_.getNumRows(); ++i) {
				ArrayList<CellPoint> currentCellList = new ArrayList<CellPoint>();
				currentCellList.add(new CellPoint(i,j));
				mapOfCellTypes_.put(cellTracker_.getType(i, j), currentCellList);
		}
		makeSomePaths(beingBuild_.getNumRows(), beingBuild_.getNumColumns());
		completeMaze();
	}
		
	public Maze getMaze()
	{
		return beingBuild_;
	}
	
	// of paths times the length of them should be less than the number of cells.
	private void makeSomePaths(int _numPaths, int _pathLength)
	{
		for(int i=0; i<_numPaths; ++i)
		{
			CellPoint c = getRandomCell(beingBuild_.rows_, beingBuild_.columns_);
			while (!beingBuild_.doesCellExistAt(c.row_, c.column_))
				c = getRandomCell(beingBuild_.rows_, beingBuild_.columns_);
			makePathStartingFrom(c.row_,c.column_,_pathLength);
		}
	}
	
	private void makePathStartingFrom(int n, int m, int length)
	{
		int l=0;
		boolean deadEnd = false;
		while(l<length && !deadEnd) {
			ArrayList<Integer> directions = getAvailableDoorsForOpen(n,m);
			int dSize = directions.size();
			deadEnd = (dSize==0);
			if (dSize>0) {
				int nn = n;
				int mm = m;
				int dir = RandomProvider.getNextIntUpto(dSize);
				beingBuild_.openDoorIn(n,m, directions.get(dir));
				switch (directions.get(dir))
				{
				case(0): --nn; break;
				case(1): ++mm; break;
				case(2): ++nn; break;
				case(3): --mm; break;
				}
				l+=mapOfCellTypes_.get(cellTracker_.getType(nn, mm)).size();
				mergeCellClasses(n, m, nn, mm);
				n=nn; m=mm;
			}
		}
	}
	
	private void completeMaze()
	{
		for(int i=0; i<beingBuild_.getNumRows(); ++i)
			for(int j=0; j<beingBuild_.getNumColumns(); ++j) {
				if (beingBuild_.doesCellExistAt(i, j))
					makePathStartingFrom(i,j, beingBuild_.getNumColumns());
			}			
	}

	public CellPoint getRandomCell()
	{
		return getRandomCell(beingBuild_.rows_, beingBuild_.columns_);
	}
	
	private CellPoint getRandomCell(int rows, int columns)
	{
		int l = RandomProvider.getNextIntUpto(rows*columns);
		CellPoint c = new CellPoint(l/columns, l%columns);
		while (!beingBuild_.doesCellExistAt(c.row_, c.column_) ) {
			l = RandomProvider.getNextIntUpto(rows*columns);
			c = new CellPoint(l/columns, l%columns);
		}
		return c;
	}
		
	private void mergeCellClasses(int n, int m, int nn, int mm)
	{
		int aType = cellTracker_.getType(n,m);
		int bType = cellTracker_.getType(nn,mm);
		if (aType == bType)
			return ;
		ArrayList<CellPoint> bSiblings = mapOfCellTypes_.get(bType);
		ArrayList<CellPoint> aSiblings = mapOfCellTypes_.get(aType);
		for(Iterator<CellPoint> i=bSiblings.iterator(); i.hasNext(); ) {
			CellPoint c = i.next();
			cellTracker_.setType(c.row_, c.column_, aType);
			aSiblings.add(c);
		}
		mapOfCellTypes_.remove(bType);
	}
	
	// 0 The north. 1 The East, 2 the south, 3 the west.
	private ArrayList<Integer> getAvailableDoorsForOpen(int n, int m)
	{
		ArrayList<Integer> ans = new ArrayList<Integer>();
		if (canOpenTheNorth(n,m))
			ans.add(Maze.DIRECTION_NORTH);
		if (canOpenTheEast(n,m))
			ans.add(Maze.DIRECTION_EAST);
		if (canOpenTheSouth(n,m))
			ans.add(Maze.DIRECTION_SOUTH);
		if (canOpenTheWest(n,m)) {
			ans.add(Maze.DIRECTION_WEST);
		}
		return ans;
	}
	
	private boolean canOpenTheNorth(int row, int column)
	{
		if (!beingBuild_.doesCellExistAt(row-1, column))
			return false;
		return cellTracker_.getType(row-1, column) != cellTracker_.getType(row, column);
	}
	
	private boolean canOpenTheEast(int row, int column)
	{
		if (!beingBuild_.doesCellExistAt(row, column+1))
			return false;
		return cellTracker_.getType(row, column) != cellTracker_.getType(row, column+1);
	}
	
	private boolean canOpenTheSouth(int row, int column)
	{
		if (!beingBuild_.doesCellExistAt(row+1, column))
			return false;
		return cellTracker_.getType(row+1, column) != cellTracker_.getType(row, column);
	}	

	private boolean canOpenTheWest(int row, int column)
	{
		if (!beingBuild_.doesCellExistAt(row, column-1))
			return false;
		return cellTracker_.getType(row, column-1) != cellTracker_.getType(row, column);
	}	
	
	private class CellTracker
	{
		public CellTracker(int r, int c)
		{
			cellTracker_ = new int[r][c];
			// At the begining all cells are of different unvisited type:
			int type=1;
			for(int j=0; j<beingBuild_.getNumColumns(); ++j) {
				for(int i=0;i<beingBuild_.getNumRows(); ++i) {
					cellTracker_[i][j] = type;
					++type;
				}
			}
		}
		
		public int getType(int i, int j)
		{
			return (cellTracker_[i][j]);
		}
		public void setType(int i, int j, int t)
		{
			cellTracker_[i][j] = t;
		}
		
		private int cellTracker_[][];
	}
	
	private Maze beingBuild_;
	// We track with this board the status of the cells, the type for indicating the equivalent class of path and whether is has been visited or not
	private CellTracker cellTracker_;
	private TreeMap<Integer, ArrayList<CellPoint> > mapOfCellTypes_;
}

