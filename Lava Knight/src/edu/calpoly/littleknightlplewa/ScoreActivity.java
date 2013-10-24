package edu.calpoly.littleknightlplewa;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scores);
    	
    	SharedPreferences sp = this.getSharedPreferences("myPref", MODE_PRIVATE);
		String s = "";
		if(sp != null && sp.contains("KEY"))
			s = sp.getString("KEY", "");
		TextView t1 = (TextView) findViewById(R.id.textView1);
		TextView t2 = (TextView) findViewById(R.id.textView2);
		TextView t3 = (TextView) findViewById(R.id.textView3);
		TextView t4 = (TextView) findViewById(R.id.textView4);
		TextView t5 = (TextView) findViewById(R.id.textView5);
		ArrayList<TextView> t_arr = new ArrayList<TextView>();
		t_arr.add(t1);
		t_arr.add(t2);
		t_arr.add(t3);
		t_arr.add(t4);
		t_arr.add(t5);
		for(int i = 0; i< 5 && s.length() > 0; i++){
			int index = s.indexOf(" ");
			if(index < s.length() && index > 0){
				String text = s.substring(0, index);
				s = s.substring(index+1);
				t_arr.get(i).setText((i+1) + ". " + text);
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		Intent myIntent = new Intent(this.getBaseContext(), FirstScreen.class);
		myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
		return true;
    }
}
