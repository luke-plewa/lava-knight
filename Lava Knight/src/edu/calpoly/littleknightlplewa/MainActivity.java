/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.calpoly.littleknightlplewa;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

public class MainActivity extends Activity {

    private GLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
	
    	mGLView = new MyGLSurfaceView(this);
    	((MyGLSurfaceView) mGLView).setActivity(this);

    	setContentView(mGLView);
    }

    @Override
    protected void onPause() {
		/*SharedPreferences sp = this.getSharedPreferences("myPref", MODE_PRIVATE);
		Editor e = sp.edit();
		String s = "123 245 32342 45432 55432  ";
		e.putString("KEY", s);
		e.commit();*/
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.@Override
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }
}

class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(getContext());
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
    
    public void setActivity(Activity a){
        mRenderer.setActivity(a);
    }

    /*private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    private float cur_x, cur_y;
    private static final float TOUCH_FACTOR = 1.0f;*/

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        //float x = e.getX();
        //float y = e.getY();
        
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //float dx = x - mPreviousX;
                //float dy = y - mPreviousY;
                //cur_x += dx;
                //cur_y += dy;
                //mRenderer.drawParticles(cur_x, cur_y, 1, 1, 1);
                //if(mRenderer.getMesh() != null)
                //	mRenderer.getMesh().setLight(dx, 0, 1);
                //mRenderer.moveX += dx * TOUCH_FACTOR;
                //mRenderer.moveY += dy * -TOUCH_FACTOR;
                requestRender();
                break;
            case MotionEvent.ACTION_DOWN:
                mRenderer.mDown = true;
            	requestRender();
                break;
            default:
                mRenderer.mDown = false;
            	requestRender();
            	break;
        }
         
        //mPreviousX = x;
        //mPreviousY = y;
        return true;
    }
}
