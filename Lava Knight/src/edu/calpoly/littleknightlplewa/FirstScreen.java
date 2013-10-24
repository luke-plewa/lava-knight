package edu.calpoly.littleknightlplewa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class FirstScreen extends Activity{
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.new_game);
    	Button next = (Button) findViewById(R.id.button1);
    	next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(), MainActivity.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
            }
    	});
    	

    	Button score = (Button) findViewById(R.id.button2);
    	score.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(), ScoreActivity.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
            }
    	});
    	}
}