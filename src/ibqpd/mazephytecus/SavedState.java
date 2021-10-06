package ibqpd.mazephytecus;

import ibqpd.maze.CellPoint;
import android.content.SharedPreferences;

public class SavedState {
	public static final int DIF_EASY = 0;
	public static final int DIF_MEDIUM = 1;
	public static final int DIF_HARD = 2;
	public static final int DIF_TEMPLATE = 3;
	public static final int DIF_SAVED = 4;
	
	public SavedState(int cellSize, int xOffset, int yOffset, CellPoint playerPosition, CellPoint exitPosition, 
			int pathLength, int playerNumMoves, long timeStamp, String maze)
	{
		cellSize_ = cellSize;
		xOffset_ = xOffset;
		yOffset_ = yOffset;
		rowPlayer_ = playerPosition.row_;
		colPlayer_ = playerPosition.column_;
		rowExit_ = exitPosition.row_;
		colExit_ = exitPosition.column_;
		pathLength_ = pathLength;
		playerNumMoves_ = playerNumMoves;
		timeStamp_ = timeStamp;
		maze_ = maze;	
	}
	
	public void commit(SharedPreferences p)
	{
		p.edit().putInt(cellSizeKey_,cellSize_).commit();
		p.edit().putInt(xOffsetKey_,xOffset_).commit();
		p.edit().putInt(yOffsetKey_,yOffset_).commit();
		p.edit().putInt(rowPlayerKey_,rowPlayer_).commit();
		p.edit().putInt(colPlayerKey_,colPlayer_).commit();
		p.edit().putInt(rowExitKey_,rowExit_).commit();
		p.edit().putInt(colExitKey_,colExit_).commit();
		p.edit().putInt(pathLengthKey,pathLength_).commit();
		p.edit().putInt(playerMovesKey,playerNumMoves_).commit();
		p.edit().putLong(timeStampKey,timeStamp_).commit();	
		p.edit().putString(mazeKey_, maze_).commit();
	}
	
	public static SavedState getFromPreferences(SharedPreferences p)
	{
		return new SavedState( p.getInt(cellSizeKey_, 0),
				p.getInt(xOffsetKey_, 0),
				p.getInt(yOffsetKey_, 0),
				new CellPoint(p.getInt(rowPlayerKey_, 0), p.getInt(colPlayerKey_, 0)),
				new CellPoint(p.getInt(rowExitKey_, 0),	p.getInt(colExitKey_, 0)),
				p.getInt(pathLengthKey, 0), p.getInt(playerMovesKey, 0), p.getLong(timeStampKey, 0), p.getString(mazeKey_, "") );		
	}
	
	static public void saveHighScore(SharedPreferences p, int s)
	{
		p.edit().putInt(highScoreKey,s).commit();
	}
	
	static public int getHighScore(SharedPreferences p)
	{
		return p.getInt(highScoreKey,0);
	}
	
	public static boolean somethingSaved(SharedPreferences p)
	{
		String m = p.getString(mazeKey_, "");
		return (m!="");
	}
	
	int cellSize_;
	int xOffset_;
	int yOffset_;
	int rowPlayer_;
	int colPlayer_;
	int rowExit_;
	int colExit_;
	int pathLength_;
	int mazeArea_;
	int playerNumMoves_;
	long timeStamp_;
	String maze_;
	
	static final String cellSizeKey_= "SaveState.cellSize";
	static final String xOffsetKey_= "SaveState.xOffset";
	static final String yOffsetKey_= "SaveState.yOffset";
	static final String rowPlayerKey_= "SaveState.rowPlayer";
	static final String colPlayerKey_= "SaveState.colPlayer";
	static final String rowExitKey_= "SaveState.rowExit";
	static final String colExitKey_= "SaveState.colExit";
	static final String pathLengthKey = "SaveState.pathLength";
	static final String playerMovesKey = "SaveState.playerMoves";
	static final String mazeAreaKey = "SaveState.mazeArea";
	static final String mazeKey_= "SaveState.maze";
	static final String preferencesKey = "SaveState.Preferences";
	static final String highScoreKey = "SaveState.highScore";
	static final String timeStampKey = "SaveState.timeStamp";
	static final String gameDifficultyKey = "SaveState.gameDifficulty";
	static final String templateIDKey = "SaveState.templateID";
}
