package ibqpd.maze;

import java.lang.StringBuffer;
import java.util.ArrayList;

public class Maze 
{
	public static final int DIRECTION_NORTH = 0;
	public static final int DIRECTION_EAST = 1;
	public static final int DIRECTION_SOUTH = 2;
	public static final int DIRECTION_WEST = 3;

	// From North to West clock wise cell possible states
	public static final int _NONE = 16;
	public static final int _0000 = 0;
	public static final int _0001 = 1;
	public static final int _0010 = 2;
	public static final int _0011 = 3;
	public static final int _0100 = 4;
	public static final int _0101 = 5;
	public static final int _0110 = 6;
	public static final int _0111 = 7;
	public static final int _1000 = 8;
	public static final int _1001 = 9;
	public static final int _1010 = 10;
	public static final int _1011 = 11;
	public static final int _1100 = 12;
	public static final int _1101 = 13;
	public static final int _1110 = 14;
	public static final int _1111 = 15;

	Maze(int _rows, int _columns)
	{
		rows_ = _rows;
		columns_ = _columns;
		board_ = new int[rows_][columns_];
		for(int i=0; i<rows_; ++i)
			for(int j=0; j<columns_; ++j)
				board_[i][j] = _NONE;

	}
	
	void initFullMazeGrid()
	{
		for(int i=0; i<rows_; ++i)
			for(int j=0; j<columns_; ++j)
				board_[i][j] = _1111;
	}
	
	void initBitWise()
	{
		for(int i=0; i<rows_; ++i)
			for(int j=0; j<columns_; ++j)
				board_[i][j] = _NONE;
	}
	
	void createCellAt(int i, int j)
	{
		board_[i][j] = _1111;
	}
	
	public boolean doesCellExistAt(int i, int j)
	{
		if (i<0 || i>rows_-1 || j<0 || j>columns_-1)
			return false;		
		return board_[i][j] != _NONE;
	}
	
	public int getNumRows()
	{
		return rows_;
	}
	// Rotating clockwise
	public void rotate(int row, int column)
	{
		// We cannot play with border walls
		if (!doesCellExistAt(row,column) ||
				!doesCellExistAt(row+1,column) ||
				!doesCellExistAt(row+1,column+1) ||
				!doesCellExistAt(row,column+1) )
			return;
		boolean toEastOpen = canMoveIn(row, column, Maze.DIRECTION_EAST);
		if(canMoveIn(row+1, column, Maze.DIRECTION_NORTH)) {
			openDoorIn(row, column, Maze.DIRECTION_EAST);
		} else {
			closeDoorIn(row, column, Maze.DIRECTION_EAST);
		}
		if(canMoveIn(row+1, column, Maze.DIRECTION_EAST)) {
			openDoorIn(row+1, column, Maze.DIRECTION_NORTH);
		} else {
			closeDoorIn(row+1, column, Maze.DIRECTION_NORTH);
		}
		if(canMoveIn(row+1, column+1, Maze.DIRECTION_NORTH)) {
			openDoorIn(row+1, column, Maze.DIRECTION_EAST);
		} else {
			closeDoorIn(row+1, column, Maze.DIRECTION_EAST);
		}
		if(toEastOpen) {
			openDoorIn(row+1, column+1, Maze.DIRECTION_NORTH);
		} else {
			closeDoorIn(row+1, column+1, Maze.DIRECTION_NORTH);
		}		
	}
	
	public int getNumColumns()
	{
		return columns_;
	}
	
	public boolean isDeadEnd(int i, int j)
	{
		if(!doesCellExistAt(i,j))
			return false;
		int numOpenDoors = 0;
		for(int d=0; d<4; ++d)
			if(canMoveIn(i,j,d))
				++numOpenDoors;
		return numOpenDoors==1;
	}
	
	public CellPoint getRandomDeadEnd()
	{
		ArrayList<CellPoint> deadEnds = new ArrayList<CellPoint>();
		for(int i=0; i<rows_; ++i) {
			for(int j=0; j<columns_; ++j) {
				if (isDeadEnd(i,j))
					deadEnds.add(new CellPoint(i,j));
			}
		}
		return deadEnds.get(RandomProvider.getNextIntUpto(deadEnds.size()));
	}
	
	public boolean canMoveIn(int row, int column, int direction)
	{
		return directions_[direction].to(row, column);
	}
	
	// Only friends are allowed to use this
	void openDoorIn(int row, int column, int direction)
	{
		doorOpeners_[direction].openDoorIn(row, column);
	}
	
	// Only friends are allowed to use this
	void closeDoorIn(int row, int column, int direction)
	{
		doorClosers_[direction].closeDoorIn(row, column);
	}
	public void dump()
	{
		for(int i=0; i<rows_; ++i) {
			for(int j=0; j<columns_; ++j) {
				System.out.print(board_[i][j]);
				System.out.print(",");
			}
			System.out.println();
		}
	}
	
	public String toString()
	{
		// Format is rows,columns:cellStates; rows are first
		String answer="";
		answer += rows_ + "," + columns_ + ":";
		StringBuffer sb = new StringBuffer(rows_*columns_);
		for(int i=0; i<rows_; ++i) {
			for(int j=0; j<columns_; ++j) {
				sb.append((char)board_[i][j]);
			}
		}		
		return answer + sb.toString();
	}
	
	public int getAire() {
		int aire = 0;
		for(int i=0; i<rows_; ++i) 
			for(int j=0; j<columns_; ++j)
				if (board_[i][j] != _NONE)
					++aire;
		return aire;
	}	
	
	public CellPoint getClosestPivot(int row, int column)
	{
		int modRow = row%2;
		int modColumn = column%2;
		if (modRow == 0 && modColumn == 0)
			return new CellPoint(row, column);
		if (modRow == 0 && modColumn == 1)
			return new CellPoint(row, column-1); 
		if (modRow == 1 && modColumn == 0)
			return new CellPoint(row-1, column);
		return new CellPoint(row-1, column-1);
	}
	
	private abstract class CanGo
	{
		abstract boolean to(int row, int col); 
	}
	
	private class ToNorth extends CanGo
	{
		boolean to(int row, int column)
		{
			if(!doesCellExistAt(row -1, column))
				return false;
			return (board_[row][column] & _0001 ) == 0;
		}
	}
	
	private class ToEast extends CanGo
	{
		boolean to(int row, int column)
		{
			if(!doesCellExistAt(row, column +1))
				return false;
			return (board_[row][column] & _0010 ) == 0;
		}
	}
	
	private class ToSouth extends CanGo
	{
		boolean to(int row, int column)
		{
			if(!doesCellExistAt(row+1, column))
				return false;
			return (board_[row][column] & _0100) == 0;
		}
	}
	private class ToWest extends CanGo
	{
		boolean to(int row, int column)
		{
			if(!doesCellExistAt(row, column-1))
				return false;
			return (board_[row][column] & _1000 ) == 0;
		}
	}
	
	private abstract class OpenDoorIn
	{
		public abstract void openDoorIn(int row, int column);
	}
	
	private class OpenNorthDoor extends OpenDoorIn
	{
		public void openDoorIn(int row, int column)
		{
			// Border cells cannot be opened
			if(!doesCellExistAt(row-1, column))
				return ;
			// Open ours
			board_[row][column] &= _1110;
			// As well as the one from our North neighbourhod
			board_[row-1][column] &= _1011;
		}
	}
	private class OpenEastDoor extends OpenDoorIn
	{
		public void openDoorIn(int row, int column)
		{
			// Border cells cannot be opened
			if(!doesCellExistAt(row, column+1))
				return ;
			board_[row][column] &= _1101;
			board_[row][column+1] &= _0111;
		}
	}
	private class OpenSouthDoor extends OpenDoorIn
	{
		public void openDoorIn(int row, int column)
		{
			// Border cells cannot be opened
			if(!doesCellExistAt(row+1, column))
				return ;
			board_[row][column] &= _1011;
			board_[row+1][column] &= _1110;
		}
	}
	private class OpenWestDoor extends OpenDoorIn
	{
		public void openDoorIn(int row, int column)
		{
			// Border cells cannot be opened
			if(!doesCellExistAt(row, column-1))
				return ;
			board_[row][column] &= _0111;
			board_[row][column-1] &= _1101;
		}
	}	

	private abstract class CloseDoorIn
	{
		public abstract void closeDoorIn(int row, int column);
	}
	
	private class CloseNorthDoor extends CloseDoorIn
	{
		public void closeDoorIn(int row, int column)
		{
			// Border cells cannot be closed
			if(!doesCellExistAt(row-1, column))
				return ;
			// Open ours
			board_[row][column] |= _0001;
			// As well as the one from our North neighbourhod
			board_[row-1][column] |= _0100;
		}
	}
	private class CloseEastDoor extends CloseDoorIn
	{
		public void closeDoorIn(int row, int column)
		{
			// Border cells cannot be opened
			if(!doesCellExistAt(row, column+1))
				return ;
			board_[row][column] |= _0010;
			board_[row][column+1] |= _1000;
		}
	}
	private class CloseSouthDoor extends CloseDoorIn
	{
		public void closeDoorIn(int row, int column)
		{
			// Border cells cannot be opened
			if(!doesCellExistAt(row+1, column))
				return ;
			board_[row][column] |= _0100;
			board_[row+1][column] |= _0001;
		}
	}
	private class CloseWestDoor extends CloseDoorIn
	{
		public void closeDoorIn(int row, int column)
		{
			// Border cells cannot be opened
			if(!doesCellExistAt(row, column-1))
				return ;
			board_[row][column] |= _1000;
			board_[row][column-1] |= _0010;
		}
	}	
	
	int rows_;
	int columns_;
	int[][] board_;

	private final CanGo directions_[] = { new ToNorth(), new ToEast(), new ToSouth(), new ToWest() };
	private final OpenDoorIn doorOpeners_[] = { new OpenNorthDoor(), new OpenEastDoor(), new OpenSouthDoor(), new OpenWestDoor() };
	private final CloseDoorIn doorClosers_[] = { new CloseNorthDoor(), new CloseEastDoor(), new CloseSouthDoor(), new CloseWestDoor() };

}
