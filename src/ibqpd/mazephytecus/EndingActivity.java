package ibqpd.mazephytecus;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class EndingActivity extends Activity implements OnClickListener {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ending_layout);
        
        int hScore = SavedState.getHighScore(getSharedPreferences(SavedState.preferencesKey, MODE_PRIVATE));
        TextView hScoreView = (TextView) findViewById(R.id.high_score_value);
        hScoreView.setText(String.valueOf(hScore));        
        
        TextView timeView = (TextView) findViewById(R.id.elapsed_time_value);
        long elapsedTime = getIntent().getLongExtra(SavedState.timeStampKey, SystemClock.uptimeMillis());
        elapsedTime = (SystemClock.uptimeMillis() - elapsedTime)/1000;
        timeView.setText(String.valueOf(elapsedTime));
        
        TextView lengthView = (TextView) findViewById(R.id.path_length_value);
        int pathLength = getIntent().getIntExtra(SavedState.pathLengthKey, 0) - 1;
        lengthView.setText(String.valueOf(pathLength));
        
        TextView movesView = (TextView)  findViewById(R.id.player_moves_value);
        int numMoves = getIntent().getIntExtra(SavedState.playerMovesKey, 0);
        movesView.setText(String.valueOf(numMoves));
        if ( numMoves == pathLength ) {
        	// No errors double bonus in length
        	Toast toast = Toast.makeText(this, R.string.no_errors_text, Toast.LENGTH_LONG);
        	toast.setGravity(Gravity.CENTER, 0, 0);
        	toast.show();
        	pathLength *= 2;
        }       
        TextView areaView = (TextView)  findViewById(R.id.maze_area_value);
        int mazeArea = getIntent().getIntExtra(SavedState.mazeAreaKey, 0);
        areaView.setText(String.valueOf(mazeArea));
        
        TextView scoreView = (TextView)  findViewById(R.id.score_value);
        int grandTotal = (int) (mazeArea + pathLength -  numMoves)  - (int)elapsedTime;
        if (grandTotal > hScore) {
            Toast toast = Toast.makeText(this, R.string.new_high_score_text, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        	SavedState.saveHighScore(getSharedPreferences(SavedState.preferencesKey, MODE_PRIVATE), grandTotal);
        }
        scoreView.setText(String.valueOf(grandTotal));
        
        View finishButton = findViewById(R.id.finish_button);
        finishButton.setOnClickListener(this);   
    }
    
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.finish_button :
			getSharedPreferences(SavedState.preferencesKey, MODE_PRIVATE).edit().putString(SavedState.mazeKey_, "").commit();
			finish();
			break;
		}
	}
}
