package ibqpd.scenario;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import ibqpd.maze.CellPoint;
import ibqpd.maze.Maze;

public class Avatar {
	public static final int DIR_NORTH_1 = 0;
	public static final int DIR_NORTH_2 = 1;
	public static final int DIR_EAST_1 = 2;
	public static final int DIR_EAST_2 = 3;		
	public static final int DIR_SOUTH_1 = 4;
	public static final int DIR_SOUTH_2 = 5;
	public static final int DIR_WEST_1 = 6;
	public static final int DIR_WEST_2 = 7;

	
	public Avatar(CellPoint p, Bitmap[] caracters)
	{
		position_ = p.clone();
		caracter_ = caracters;
		currentCaracter_ = DIR_NORTH_1;
		currentBitmap_ = caracter_[currentCaracter_];
		rBounds_ = new Rect();

		xCorner_ = CellMetrics.instance().getXOffset() + position_.column_*CellMetrics.instance().getCellSize() + CellMetrics.instance().getTileSize();
		yCorner_ = CellMetrics.instance().getYOffset() + position_.row_ * CellMetrics.instance().getCellSize() + CellMetrics.instance().getTileSize();
		rBounds_.set(xCorner_, yCorner_, xCorner_+ CellMetrics.instance().getCellInternalSize(), yCorner_ + CellMetrics.instance().getCellInternalSize());
		numMoves_ = 0;
		moves_ = new Move[4];
		moves_[Maze.DIRECTION_NORTH] = new North();
		moves_[Maze.DIRECTION_EAST] = new East();
		moves_[Maze.DIRECTION_SOUTH] = new South();
		moves_[Maze.DIRECTION_WEST] = new West();
	}
	
	public void moveTo(Maze m, int direction)
	{
		if (m.canMoveIn(position_.row_, position_.column_, direction)) {
			moves_[direction].go();
			++numMoves_;
		}
	}
	
	public CellPoint getPosition()
	{
		return position_;
	}
	
	public Rect getBounds() {
		return rBounds_;
	}
	
	public void draw(Canvas c)
	{
		c.drawBitmap(currentBitmap_, null, rBounds_, null);
	}
	

	
	private abstract class Move
	{
		public abstract void go();
	}
	
	private class North extends Move
	{
		public North() {}
		public void go() 
		{  
			--position_.row_;
			// Toggle characters
			currentCaracter_ = (currentCaracter_==DIR_NORTH_1)?DIR_NORTH_2:DIR_NORTH_1;
			currentBitmap_ = caracter_[currentCaracter_];		
			yCorner_ -= CellMetrics.instance().getCellSize();
			rBounds_.set(xCorner_, yCorner_, xCorner_+ CellMetrics.instance().getCellInternalSize(), yCorner_ + CellMetrics.instance().getCellInternalSize());
		}
	}
	
	private class South extends Move
	{
		public South() {}
		public void go() 
		{  
			++position_.row_;
			// Toggle characters			
			currentCaracter_ = (currentCaracter_==DIR_SOUTH_1)?DIR_SOUTH_2:DIR_SOUTH_1;
			currentBitmap_ = caracter_[currentCaracter_];				
			yCorner_ += CellMetrics.instance().getCellSize();
			rBounds_.set(xCorner_, yCorner_, xCorner_+ CellMetrics.instance().getCellInternalSize(), yCorner_ + CellMetrics.instance().getCellInternalSize());
		}
	}
	
	private class East extends Move
	{
		public East() {}
		public void go() 
		{  
			++position_.column_;
			currentCaracter_ = (currentCaracter_==DIR_EAST_1)?DIR_EAST_2:DIR_EAST_1;
			currentBitmap_ = caracter_[currentCaracter_];								
			xCorner_ += CellMetrics.instance().getCellSize();
			rBounds_.set(xCorner_, yCorner_, xCorner_+ CellMetrics.instance().getCellInternalSize(), yCorner_ + CellMetrics.instance().getCellInternalSize());
		}
	}
	
	private class West extends Move
	{
		public West() {}
		public void go() 
		{  
			--position_.column_;
			currentCaracter_ = (currentCaracter_==DIR_WEST_1)?DIR_WEST_2:DIR_WEST_1;			
			currentBitmap_ = caracter_[currentCaracter_];										
			xCorner_ -= CellMetrics.instance().getCellSize();
			rBounds_.set(xCorner_, yCorner_, xCorner_+ CellMetrics.instance().getCellInternalSize(), yCorner_ + CellMetrics.instance().getCellInternalSize());
		}
	}

	private Rect rBounds_;			// Avatar enclosing rectangle
	private CellPoint position_; 	// In the maze
	private Bitmap[] caracter_;	
	private Bitmap currentBitmap_;
	private int currentCaracter_;
	private int xCorner_; // In pixels for drawing
	private int yCorner_;
	int numMoves_;	
	private final Move moves_[];	
}

