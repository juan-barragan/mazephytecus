package ibqpd.mazephytecus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import ibqpd.maze.Maze;
import ibqpd.scenario.CellMetrics;
import ibqpd.scenario.Scenario;

public class PlayView extends View  {

	public PlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
		gameOver_ = false;
        dx_ = dy_ = 0;
        setFocusable(true);
        setFocusableInTouchMode(true);
		gd_ = new GestureDetector(gestureHandler_);
	}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	return gd_.onTouchEvent(event);
    }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{	
		int direction = 0;
		switch(keyCode)
		{
		case KeyEvent.KEYCODE_DPAD_UP:
			direction = Maze.DIRECTION_NORTH;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			direction = Maze.DIRECTION_EAST;
			break;			
		case KeyEvent.KEYCODE_DPAD_DOWN:
			direction = Maze.DIRECTION_SOUTH;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			direction = Maze.DIRECTION_WEST;
			break;			
		default:
			return super.onKeyDown(keyCode, event);
		}
		movePlayer(direction, 1);
		return true;
	}
	
	void movePlayer(int direction, int numMoves)
	{
		for(int i=0; i<numMoves; ++i)
		{
			invalidate(sc_.getPlayerBounds());
			if (sc_.movePlayerTo(direction))
				if (!gameOver_)
					endThePlay();
			invalidate(sc_.getPlayerBounds());
		}
	}
	void redrawArea(int xUpper, int yUpper, int xLower, int yLower)
	{
		invalidate(xUpper, yUpper, xLower, yLower);
	}
	
	public void onDraw(Canvas canvas)
	{	
		sc_.draw(canvas);
	}	
	
	protected int numMoves(double delta)
	{
		return (int)Math.rint(Math.abs(delta)/CellMetrics.instance().getCellSize());
	}	
	
	protected void endThePlay()
	{
		gameOver_ = true;
		Intent i = new Intent(getContext(), EndingActivity.class);
		i.putExtra(SavedState.pathLengthKey, sc_.getPathLength());
		i.putExtra(SavedState.playerMovesKey, sc_.getNumMoves());
		i.putExtra(SavedState.mazeAreaKey, sc_.getMazeAire());
		i.putExtra(SavedState.timeStampKey, timeStamp_); 
		((PlayActivity)getContext()).startActivity(i);
		((PlayActivity)getContext()).finish();
	}
	
	Scenario sc_;
	boolean gameOver_;
	float xCurr;
	float yCurr;
	float dx_;
	float dy_;
	long timeStamp_;
	GestureDetector gd_;
	private GestureDetector.SimpleOnGestureListener gestureHandler_ = new GestureDetector.SimpleOnGestureListener() {	 
    	@Override
    	public boolean onDown(MotionEvent arg0) {
            // Don't forget to return true here to get the following touch events
            return true;
        }
 
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    		int direction = 0;
    		dx_ += distanceX;
    		dy_ += distanceY;
    		int numMoves=0;
    		int mx = (int)Math.abs(dx_)/CellMetrics.instance().getCellSize();
    		int my = (int)Math.abs(dy_)/CellMetrics.instance().getCellSize();
    		if ( (mx> 0) || (my> 0)) {
    			if (-Math.abs(dx_) > dy_)  {
    				numMoves = my;								
    				direction = Maze.DIRECTION_SOUTH;
    			}
    			if (-Math.abs(dy_) > dx_) {
    				numMoves = mx;											
    				direction = Maze.DIRECTION_EAST;			
    			}
    			if (Math.abs(dx_) < dy_) {
    				numMoves = my;						
    				direction = Maze.DIRECTION_NORTH;
    			}
    			if (Math.abs(dy_) < dx_) {
    				numMoves = mx;					
    				direction = Maze.DIRECTION_WEST;	
    			}
    			movePlayer(direction, numMoves);
    			dx_ = 0;
    			dy_ = 0;
    		}        	
            return true;
        }
        
        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
        	return false;
        }
    };

}
