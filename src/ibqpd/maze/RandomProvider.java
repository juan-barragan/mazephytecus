package ibqpd.maze;

import java.util.Random;

public final class RandomProvider {
	public static RandomProvider instance() 
	{ 
		if (instance_ == null)
			instance_ = new RandomProvider();
		return instance_; 
	}
	public static int getNextIntUpto(int max)
	{
		return r_.nextInt(max);
	}
	private RandomProvider() {}	
	private static RandomProvider instance_ = null;
	static Random r_ = new Random(System.currentTimeMillis());
}
