package ibqpd.scenario;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import ibqpd.maze.Maze;

public class TiledMaze {
	@SuppressWarnings("unchecked")
	public TiledMaze(Bitmap[] patterns, Maze l)
	{
		l_ = l;
		tilesInCell_ = CellMetrics.instance().getCellSize()/CellMetrics.instance().getTileSize();
		t_ = (ArrayList<MazeTile>[][])new ArrayList[l.getNumRows()][l.getNumColumns()];
		patterns_ = patterns;
		createTiles(l_, patterns_);
	}

	public void draw(Canvas c)
	{
		for(int i=0; i<l_.getNumRows(); ++i)
			for(int j=0; j<l_.getNumColumns(); ++j) { 
				if ( t_[i][j] != null )
					for(Iterator<MazeTile> lIterator = t_[i][j].iterator(); lIterator.hasNext(); ) {
						MazeTile t = lIterator.next(); 
						t.draw(c);
					}
			}
	}
	
	private void createTiles(Maze l, Bitmap[] patterns)
	{
		for(int i=0; i<l_.getNumRows(); ++i)
			for(int j=0; j<l_.getNumColumns(); ++j) {
				if (!l_.doesCellExistAt(i, j))
					continue ;
				t_[i][j] = getTilesAt(i,j);
			}
	}
	
	private ArrayList<MazeTile> getTilesAt(int i, int j)
	{
		// Cell does exist and is responsible always for the right & down walls
		ArrayList<MazeTile> tmp = new ArrayList<MazeTile>();
		// Put upper right corner
		tmp.add(getUpperRightIntersection(i,j));
		// Put as many right walls as needed
		if (!l_.canMoveIn(i,j, Maze.DIRECTION_EAST)) {
			tmp.addAll(getRightWallTiles(i,j));
		}
		// Right Down corner is also under this cell responsibility
		tmp.add(getDownRightIntersection(i,j));
		if (!l_.canMoveIn(i,j, Maze.DIRECTION_SOUTH)) {
			tmp.addAll(getDownWallTiles(i,j));
		}
		// Unless no neighbours these walls are not the responsibility of this cell, check for neighbours
		if(!l_.doesCellExistAt(i-1, j)) {
			// Nobody upper stairs, add walls
			tmp.add(getUpperLeftIntersection(i,j));
			tmp.addAll(getUpperWallTiles(i,j));
		}				
		if(!l_.doesCellExistAt(i, j-1)) {
			// nobody to the left, add walls
			tmp.add(getDownLeftIntersection(i,j));
			tmp.addAll(getLeftWallTiles(i,j));
		}
		
		return tmp;
	}
	
	ArrayList<MazeTile> getRightWallTiles(int i, int j)
	{
		ArrayList<MazeTile> tmp = new ArrayList<MazeTile>();
		int xCorner = CellMetrics.instance().getXOffset() + (j+1)*CellMetrics.instance().getCellSize(); 
		int yCorner = CellMetrics.instance().getYOffset() + i*CellMetrics.instance().getCellSize() + CellMetrics.instance().getTileSize();
		for(int k=0; k<tilesInCell_-1; ++k) {
			tmp.add(new MazeTile(xCorner, yCorner, patterns_[Maze._0101]));
			yCorner += CellMetrics.instance().getTileSize();
		}
		
		return tmp;
	}
	ArrayList<MazeTile> getDownWallTiles(int i, int j)
	{
		ArrayList<MazeTile> tmp = new ArrayList<MazeTile>();
		int xCorner = CellMetrics.instance().getXOffset() + j*CellMetrics.instance().getCellSize() + CellMetrics.instance().getTileSize(); 
		int yCorner = CellMetrics.instance().getYOffset() + (i+1)*CellMetrics.instance().getCellSize();
		for(int k=0; k<tilesInCell_-1; ++k) {
			tmp.add(new MazeTile(xCorner, yCorner, patterns_[Maze._1010]));
			xCorner += CellMetrics.instance().getTileSize();
		}
		
		return tmp;
	}	
	ArrayList<MazeTile> getUpperWallTiles(int i, int j)
	{
		return getDownWallTiles(i-1, j);
	}
	
	ArrayList<MazeTile> getLeftWallTiles(int i, int j)
	{
		return getRightWallTiles(i, j-1);
	}	

	
	MazeTile getUpperRightIntersection(int i, int j)
	{
		// Empty one default.
		int tileIndex = Maze._0000;
		if (l_.doesCellExistAt(i, j)) {
			if (!l_.canMoveIn(i,j, Maze.DIRECTION_NORTH))
				tileIndex |= Maze._1000;
			if (!l_.canMoveIn(i,j, Maze.DIRECTION_EAST))
				tileIndex |= Maze._0100;
		}
		if (l_.doesCellExistAt(i-1, j)) {
			// If there is a wall put bit likewise
			if (!l_.canMoveIn(i-1,j, Maze.DIRECTION_SOUTH))
				tileIndex |= Maze._1000;
			if (!l_.canMoveIn(i-1,j, Maze.DIRECTION_EAST))
				tileIndex |= Maze._0001;
		}
		if (l_.doesCellExistAt(i-1, j+1)) {
			if (!l_.canMoveIn(i-1,j+1, Maze.DIRECTION_SOUTH))
				tileIndex |= Maze._0010;
			if (!l_.canMoveIn(i-1,j+1, Maze.DIRECTION_WEST))
				tileIndex |= Maze._0001;
		}		
		if (l_.doesCellExistAt(i, j+1)) {
			if (!l_.canMoveIn(i,j+1, Maze.DIRECTION_NORTH))
				tileIndex |= Maze._0010;
			if (!l_.canMoveIn(i,j+1, Maze.DIRECTION_WEST)) {				
				tileIndex |= Maze._0100;
			}
				
		}

		return new MazeTile(CellMetrics.instance().getXOffset() + (j+1)*CellMetrics.instance().getCellSize(), 
				CellMetrics.instance().getYOffset() + i*CellMetrics.instance().getCellSize(), 
				patterns_[tileIndex]);
	}
	
	MazeTile getUpperLeftIntersection(int i, int j)
	{
		return getDownRightIntersection(i-1,j-1);
	}
	
	MazeTile getDownRightIntersection(int i, int j)
	{
		// Empty one default.
		int tileIndex = Maze._0000;
		// Look at the ways we can move around the cell. If not move is possible in one direction set the appropriate bit.
		if (l_.doesCellExistAt(i, j)) {
			// If there is a wall put bit likewise
			if (!l_.canMoveIn(i,j, Maze.DIRECTION_SOUTH))
				tileIndex |= Maze._1000;
			if (!l_.canMoveIn(i,j, Maze.DIRECTION_EAST))
				tileIndex |= Maze._0001;				
		}
		if (l_.doesCellExistAt(i+1, j)) {
			// If there is a wall put bit likewise
			if (!l_.canMoveIn(i+1,j, Maze.DIRECTION_NORTH))
				tileIndex |= Maze._1000;
			if (!l_.canMoveIn(i+1,j, Maze.DIRECTION_EAST))
				tileIndex |= Maze._0100;
		}
		if (l_.doesCellExistAt(i+1, j+1)) {
			if (!l_.canMoveIn(i+1,j+1, Maze.DIRECTION_NORTH))
				tileIndex |= Maze._0010;
			if (!l_.canMoveIn(i+1,j+1, Maze.DIRECTION_WEST))
				tileIndex |= Maze._0100;
		}		
		if (l_.doesCellExistAt(i, j+1)) {
			if (!l_.canMoveIn(i,j+1, Maze.DIRECTION_SOUTH))
				tileIndex |= Maze._0010;
			if (!l_.canMoveIn(i,j+1, Maze.DIRECTION_WEST))
				tileIndex |= Maze._0001;
		}

		return new MazeTile(CellMetrics.instance().getXOffset() + (j+1)*CellMetrics.instance().getCellSize(), 
				CellMetrics.instance().getYOffset() + (i+1)*CellMetrics.instance().getCellSize(), 
				patterns_[tileIndex]);
	}	

	MazeTile getDownLeftIntersection(int i, int j)
	{
		return getDownRightIntersection(i, j-1);
	}
	
	public void rotate(int row, int column)
	{
		if( !l_.doesCellExistAt(row+1, column) ||
				!l_.doesCellExistAt(row+1, column+1) ||
				!l_.doesCellExistAt(row, column+1) )
			return;
		l_.rotate(row, column);
		// Refresh tiles here
		for(int i=row-1; i<=row+2; ++i) 
			for(int j=column-1; j<column+2; ++j) {
				if(l_.doesCellExistAt(i, j))
					t_[i][j] = getTilesAt(i, j);	
			}
	}
	
	public Maze getMaze()
	{
		return l_;
	}
	
	Maze l_;	
	int tilesInCell_;
	private ArrayList<MazeTile>[][] t_;
	Bitmap patterns_[];
}
