package ibqpd.scenario;


import android.graphics.Bitmap;
import android.graphics.Canvas;

public class MazeTile {
	public MazeTile(int x, int y, Bitmap b) { 
		x_ = x;
		y_ = y;
		h_ = 10000*x_ + y_;
		b_ = b;
	}
	
	public void draw(Canvas c)
	{
		c.drawBitmap(b_, x_, y_, null);
	}
	
	@Override 
	public boolean equals(Object o)
	{
		if(o == this)
			return true;
		if (!(o instanceof MazeTile))
			return false;
		MazeTile t = (MazeTile)o;
		return (x_ == t.x_ && y_ == t.y_);
	}
	
	@Override 
	public int hashCode()
	{
		return h_;
	}
	
	protected int x_;
	protected int y_;
	protected int h_;
	protected Bitmap b_;
}

