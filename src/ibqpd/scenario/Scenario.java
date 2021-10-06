package ibqpd.scenario;

import java.util.ArrayList;

import ibqpd.maze.CellPoint;
import ibqpd.maze.Maze;
import ibqpd.mazephytecus.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Scenario {
	public Scenario(Resources r, Maze m, CellPoint playerPosition, CellPoint exitPosition, int pathLength)
	{
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inDither = false;
		opt.inDensity = 0;
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		initTiledMaze(r, opt, m);
		initPlayerSprite(r, opt, playerPosition);
		initExit(exitPosition);
		pathLength_ = pathLength;
	}
	
	private void initTiledMaze(Resources r, BitmapFactory.Options opt, Maze m)
	{
		mazePatterns_ = new Bitmap[16];
		for(int i=0; i<16; ++i) {
			mazePatterns_[i] = BitmapFactory.decodeResource(r, mazeTilesID[i], opt);
		}
		m_ = new TiledMaze(mazePatterns_, m);
	}
	
	private void initPlayerSprite(Resources r, BitmapFactory.Options opt, CellPoint initialPosition)
	{
		Bitmap[] sprite = new Bitmap[8];
		sprite[0] = BitmapFactory.decodeResource(r, R.drawable.pdn4_bk1, opt);
		sprite[1] = BitmapFactory.decodeResource(r, R.drawable.pdn4_bk2, opt);
		sprite[2] = BitmapFactory.decodeResource(r, R.drawable.pdn4_rt1, opt);
		sprite[3] = BitmapFactory.decodeResource(r, R.drawable.pdn4_rt2, opt);		
		sprite[4] = BitmapFactory.decodeResource(r, R.drawable.pdn4_fr1, opt);
		sprite[5] = BitmapFactory.decodeResource(r, R.drawable.pdn4_fr2, opt);
		sprite[6] = BitmapFactory.decodeResource(r, R.drawable.pdn4_lf1, opt);
		sprite[7] = BitmapFactory.decodeResource(r, R.drawable.pdn4_lf2, opt);	
		playerAvatar_ = new Avatar(initialPosition, sprite);
	}
	
	private void initExit(CellPoint exitPosition)
	{
		exitPoint_ = exitPosition;
		exitPaint_ = new Paint();
		exitPaint_.setColor(Color.GREEN);
		int xCorner_ = CellMetrics.instance().getXOffset() + exitPoint_.column_*CellMetrics.instance().getCellSize() + CellMetrics.instance().getTileSize();
		int yCorner_ = CellMetrics.instance().getYOffset() + exitPoint_.row_ * CellMetrics.instance().getCellSize() + CellMetrics.instance().getTileSize();
		exitRectangle_ = new Rect(xCorner_, yCorner_, xCorner_+ CellMetrics.instance().getCellInternalSize(), yCorner_ + CellMetrics.instance().getCellInternalSize());		
	}
	
	public void draw(Canvas c)
	{
		m_.draw(c);
		playerAvatar_.draw(c);
		c.drawRect(exitRectangle_, exitPaint_);
	}
	
	public CellPoint getPlayerPosition()
	{
		return playerAvatar_.getPosition();
	}
	public CellPoint getExit()
	{
		return exitPoint_;
	}
	
	public int getNumMoves()
	{
		return playerAvatar_.numMoves_;
	}

	public Maze getMaze()
	{
		return m_.getMaze();
	}
	
	public int getMazeAire()
	{
		return m_.getMaze().getAire();
	}
	public int getPathLength()
	{
		return pathLength_;
	}
	
	public Rect getPlayerBounds()
	{
		return playerAvatar_.getBounds();
	}
	public boolean movePlayerTo(int direction)
	{
		playerAvatar_.moveTo(m_.l_,direction);
		return playerAvatar_.getPosition().equals(exitPoint_);
	}			
	
	TiledMaze m_;
	Avatar playerAvatar_; 
	CellPoint exitPoint_; 
	Bitmap[] mazePatterns_;
	Rect exitRectangle_;
	Paint exitPaint_;
	int pathLength_;
	ArrayList<CellPoint> pathToPlayer_;
	private final int mazeTilesID[] = 
		{
			R.drawable.oooo, R.drawable.oool, R.drawable.oolo, R.drawable.ooll,
			R.drawable.oloo, R.drawable.olol, R.drawable.ollo, R.drawable.olll,
			R.drawable.looo, R.drawable.lool, R.drawable.lolo, R.drawable.loll, 
			R.drawable.lloo, R.drawable.llol, R.drawable.lllo, R.drawable.llll	
		};
}
