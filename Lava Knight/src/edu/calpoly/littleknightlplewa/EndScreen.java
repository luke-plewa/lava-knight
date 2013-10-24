package edu.calpoly.littleknightlplewa;

import edu.calpoly.littleknightlplewa.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class EndScreen extends Activity{
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.end_game);
    }
    public boolean onTouchEvent(MotionEvent e) {
		Intent myIntent = new Intent(this.getBaseContext(), FirstScreen.class);
		myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
		return true;
    }
}