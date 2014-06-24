package ibqpd.mazephytecus;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class StartActivity extends Activity implements OnClickListener {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        buttonMapping_ = new HashMap<Integer, Integer>();
        for(int i=templatesStart; i<buttonsID.length; ++i) {
        	buttonMapping_.put(buttonsID[i], i-templatesStart);
        }

    	t_ = new Intent(this, PlayActivity.class);
    }
    
    public void onResume()
    {
    	super.onResume();
        preferences_ = getSharedPreferences(SavedState.preferencesKey, MODE_PRIVATE);
        //listeners for all buttons  
        for(int i = 0; i<buttonsID.length; ++i) {
        	ImageButton button  = (ImageButton) findViewById(buttonsID[i]);
        	button.setOnClickListener(this);
        }
        if (SavedState.somethingSaved(preferences_)) {
        	((ImageButton) findViewById(R.id.resume_button)).setEnabled(true);
        }
        else {
        	((ImageButton) findViewById(R.id.resume_button)).setEnabled(false);
        }
        templates_ = findViewById(R.id.templates);
        templates_.setVisibility(View.GONE);
    }
    public void onClick(View v)
    {
    	switch(v.getId())
    	{	
    	case R.id.resume_button:
    		startGame(SavedState.DIF_SAVED);
    		break;
    	case R.id.exit_button:
    		finish();
    		break;
    	case R.id.maze_button:
    		templates_.setVisibility(View.VISIBLE);
    		break;
    	case R.id.easy:
    		startGame(SavedState.DIF_EASY);
    		break;
    	case R.id.medium:
    		startGame(SavedState.DIF_MEDIUM);
    		break;
    	case R.id.difficult:
    		startGame(SavedState.DIF_HARD);
    		break;    		
    	default:
    		startGame(v.getId());
    	}
    }
    
    private void startGame(int i)
    {
    	t_.putExtra(SavedState.gameDifficultyKey, i);
    	if (buttonMapping_.containsKey(i)) {
    		t_.putExtra(SavedState.gameDifficultyKey, SavedState.DIF_TEMPLATE);
    		t_.putExtra(SavedState.templateIDKey, buttonMapping_.get(i));
    	}
    	startActivity(t_);
    }
    
    private Intent t_;
    private SharedPreferences preferences_;
    private final int templatesStart = 6;
    private final int buttonsID[] = 
    	{	R.id.resume_button, R.id.exit_button, R.id.maze_button,
    		R.id.easy, R.id.medium, R.id.difficult,
    		R.id.t_o, R.id.t_oo, R.id.t_blade,R.id.t_bug,
    		R.id.t_clubs,R.id.t_comb,R.id.t_communist,R.id.t_doric,
    		R.id.t_globe,R.id.t_heart,R.id.t_italy,R.id.t_maple,
    		R.id.t_mexico,R.id.t_mosaic,R.id.t_spiral,R.id.t_tree,
    		R.id.t_window
    	}; 
    private HashMap<Integer, Integer> buttonMapping_;
    View templates_;
}