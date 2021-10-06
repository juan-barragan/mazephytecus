package ibqpd.mazephytecus;

import java.util.ArrayList;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;

import ibqpd.maze.CellPoint;
import ibqpd.maze.FromAlgorithmSquared;
import ibqpd.maze.FromText;
import ibqpd.maze.MazeTemplates;
import ibqpd.maze.PathSolver;
import ibqpd.scenario.CellMetrics;
import ibqpd.scenario.Scenario;

public class PlayActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		int diff = getIntent().getIntExtra(SavedState.gameDifficultyKey, SavedState.DIF_EASY);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		setContentView(R.layout.play_activity);
        pv_ = (PlayView)findViewById(R.id.play_view);
        pv_.sc_ = (diff == SavedState.DIF_SAVED)?getScenarioFromSavedData():getScenarioFromMetrics(diff);
        pv_.timeStamp_ = (diff == SavedState.DIF_SAVED)
        		?getSharedPreferences(SavedState.preferencesKey, MODE_PRIVATE).getLong(SavedState.timeStampKey, SystemClock.uptimeMillis())
        				: SystemClock.uptimeMillis();
        getIntent().putExtra(SavedState.gameDifficultyKey, SavedState.DIF_SAVED);
	}

	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	}
	
	private Scenario getScenarioFromMetrics(int diff)
	{
		if ( diff == SavedState.DIF_TEMPLATE )
			return getScenarioFromTemplate(getIntent().getIntExtra(SavedState.templateIDKey, 0));
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);		
        CellMetrics.setCellSize(cellSizes_[diff]);
        int rows = metrics.heightPixels/CellMetrics.instance().getCellSize()-1;
        int cols = metrics.widthPixels/CellMetrics.instance().getCellSize()-1;
        CellMetrics.setXOffxet((metrics.widthPixels-(cellSizes_[diff]*cols+CellMetrics.instance().getTileSize()))/2);
        CellMetrics.setYOffxet((metrics.heightPixels-(cellSizes_[diff]*rows+CellMetrics.instance().getTileSize()))/2);
        FromAlgorithmSquared lb = new FromAlgorithmSquared();
        lb.initSquared(rows,cols);
        lb.buildMaze();
        ArrayList<CellPoint> path = PathSolver.getLonguestPath(lb.getMaze().getRandomDeadEnd(), lb.getMaze());
        return new Scenario(getResources(), lb.getMaze(), path.get(path.size()-1), path.get(0), path.size());
	}
	
	private Scenario getScenarioFromTemplate(int i)
	{
		FromAlgorithmSquared lb = new FromAlgorithmSquared();
		lb.setFromTemplate(i);
		lb.buildMaze();		
		int rows = lb.getMaze().getNumRows();
        int cols = lb.getMaze().getNumColumns();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int cellSize = getCellSize(metrics.widthPixels, metrics.heightPixels);
		CellMetrics.setCellSize(cellSize);
        CellMetrics.setXOffxet((metrics.widthPixels-(cellSize*cols+CellMetrics.instance().getTileSize()))/2);
        CellMetrics.setYOffxet((metrics.heightPixels-(cellSize*rows+CellMetrics.instance().getTileSize()))/2);
        ArrayList<CellPoint> path = PathSolver.getLonguestPath(lb.getMaze().getRandomDeadEnd(), lb.getMaze());
        return new Scenario(getResources(), lb.getMaze(), path.get(path.size()-1), path.get(0), path.size());		
	}
	
	int getCellSize(int width, int height)
	{
		// we need at least MazeTemplates.rows ans MazeTemplates.columns otherwise send an error and get back
		int factor = 2;
		// minimum
		int columns = width/(factor*CellMetrics.instance().getTileSize()) - 1;
		int rows = height/(factor*CellMetrics.instance().getTileSize()) - 1;
		while (columns >= MazeTemplates.columns && rows >= MazeTemplates.rows ) {
			++factor;
			columns = width/(factor*CellMetrics.instance().getTileSize()) - 1;
			rows = height/(factor*CellMetrics.instance().getTileSize()) - 1;		
		}
		return (factor-1)*CellMetrics.instance().getTileSize();
	}
	
	private Scenario getScenarioFromSavedData()
	{
		SavedState state = SavedState.getFromPreferences(getSharedPreferences(SavedState.preferencesKey, MODE_PRIVATE));
		if (state.maze_ == "")
			return getScenarioFromMetrics(SavedState.DIF_EASY);
        CellMetrics.setCellSize(state.cellSize_);
        CellMetrics.setXOffxet(state.xOffset_);
        CellMetrics.setYOffxet(state.yOffset_);
        FromText lb = new FromText();
        lb.setString(state.maze_);
        lb.buildMaze();
		return new Scenario(getResources(), lb.getMaze(), 
				new CellPoint(state.rowPlayer_, state.colPlayer_), 
				new CellPoint(state.rowExit_, state.colExit_), state.pathLength_);
	}
	
	protected void onPause()
	{
		super.onPause();
		saveState();
	}
	@Override
	public void onBackPressed() {
	// do something on back.
		saveState();
		super.onBackPressed();
	}
	
	private void saveState()
	{
		new SavedState(CellMetrics.instance().getCellSize(),
				CellMetrics.instance().getXOffset(),
				CellMetrics.instance().getYOffset(),
				pv_.sc_.getPlayerPosition(), pv_.sc_.getExit(),
				pv_.sc_.getPathLength(), pv_.sc_.getNumMoves(), pv_.timeStamp_,
				pv_.sc_.getMaze().toString() ).commit(getSharedPreferences(SavedState.preferencesKey, MODE_PRIVATE));	
	}
	
	private PlayView pv_;
	private static final int cellSizes_[] = {45, 36, 27};
}
