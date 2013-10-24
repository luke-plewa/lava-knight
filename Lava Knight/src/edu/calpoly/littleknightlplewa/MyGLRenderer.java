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


import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer 
{	
	
	private final Context mActivityContext;

	private float[] mModelMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] MVPMatrix = new float[16];
	
	public volatile boolean mDown = false;
	public volatile float moveX = 0;
	public volatile float moveY = 0;   
    private Mesh m; // the player character
    private Particle b; // the missiles
    private Particle box; // particle effects
    private Particle coin; // coins to increase points
    private Texture t; // a textured unit lava
    private Texture score; // timer score board
    private float particle_y;
    private Texture background; // bg image
    
    private static final float FRAME_RIGHT = 10;
    private static final float FRAME_LEFT = -10;
    private static final float FRAME_TOP = 3;
    private static final float FRAME_BOT = -4;
    private float PLAYER_X = 2.5f;
	
	/**
	 * Initialize the model data.
	 */
	public MyGLRenderer(final Context activityContext)
	{	
		mActivityContext = activityContext;
	}
	
	public Mesh getMesh(){
		return m;
	}
	
	private Activity mAct;
	public void setActivity(Activity a){
		mAct = a;
	}
	
	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) 
	{
		// Set the background clear color to black.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		m = new Mesh(mActivityContext);
		b = new Particle(mActivityContext, "obj/missile.obj");
		box = new Particle(mActivityContext, "obj/box.obj");
		box.setLight(0.0f, 0.0f, -3.0f);
		t = new Texture(mActivityContext, "obj/box.obj");
		coin = new Particle(mActivityContext, "obj/coin.obj");
		coin.setColor(0.5f, 0.5f, 0.1f);
		t.loadTexture(mActivityContext, R.drawable.lava);
		score = new Texture(mActivityContext, "obj/box.obj");
		score.loadTexture(mActivityContext, R.drawable.tex_0);
		background = new Texture(mActivityContext, "obj/box.obj");
		background.loadTexture(mActivityContext, R.drawable.lava_scene);
		createLava();
		setScores();
		
		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		
		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			
		// Position the eye in front of the origin.
		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = 0.0f;

		// We are looking toward the distance
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -5.0f;

		// Set our up vector. This is where our head would be pointing were we holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;

		// Set the view matrix. This matrix can be said to represent the camera position.
		// NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
		// view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
		Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);		
		
	}	
		
	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) 
	{
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);

		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 10.0f;
		
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}	
	
	private ArrayList<Texture> text;
	public void setScores(){
		text = new ArrayList<Texture>();
		for(int i=0; i<10; i++){
			text.add(new Texture(mActivityContext, "obj/box.obj"));
			text.get(i).setLight(0.0f, 0.0f, -3.0f);
			switch(i){
				case 0: text.get(i).loadTexture(mActivityContext, R.drawable.tex_0); break;
				case 1: text.get(i).loadTexture(mActivityContext, R.drawable.tex_1); break;
				case 2: text.get(i).loadTexture(mActivityContext, R.drawable.tex_2); break;
				case 3: text.get(i).loadTexture(mActivityContext, R.drawable.tex_3); break;
				case 4: text.get(i).loadTexture(mActivityContext, R.drawable.tex_4); break;
				case 5: text.get(i).loadTexture(mActivityContext, R.drawable.tex_5); break;
				case 6: text.get(i).loadTexture(mActivityContext, R.drawable.tex_6); break;
				case 7: text.get(i).loadTexture(mActivityContext, R.drawable.tex_7); break;
				case 8: text.get(i).loadTexture(mActivityContext, R.drawable.tex_8); break;
				case 9: text.get(i).loadTexture(mActivityContext, R.drawable.tex_9); break;
			}
		}
	}
	
	private float background_x = -240.f;
	private static final float BG_START_X = -25.f;
	private static final float BG_FINAL_X = -255.f;
	
	public void moveBackground(){
		background_x -= 0.6f;
		if(background_x < BG_FINAL_X) background_x = BG_START_X;
	}
	
	public void drawBackground(){
        background.setLight(0.0f, (FRAME_TOP+FRAME_BOT)/2.0f, 0.0f);
        if(PLAYER_X > .75*FRAME_LEFT+ .25*FRAME_RIGHT)
        	background.setColor(0.7f, 0.7f, 0.3f);
        else
        	background.setColor(0.7f, 0.3f, 0.3f);
        
		Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, background_x, -4.0f*UNIT, -6.5f);
        Matrix.scaleM(mModelMatrix, 0, 40.0f, 1.3f, 0.001f);
        Matrix.rotateM(mModelMatrix, 0, 180, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, 90, 0.0f, 0.0f, 1.0f);
        
        float[] tempMatrix = new float[16];
        Matrix.multiplyMM(tempMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, mProjectionMatrix, 0, tempMatrix, 0);
        
        float[] normMatrix = new float[16];
        Matrix.multiplyMM(normMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        background.draw(MVPMatrix, normMatrix);
	}
	
	private static final int SEEK_AI = 0;
	private static final int ZAG_AI = 1;
	private int AI_id = 0;
	private static final float MAX_SEEK = 0.15f;
	private static final float SEEK_STEP = 0.0005f;
	private float zag_scale = 0;
	private float enemy_seeking = 0.01f;
	private float missile_step = 0.6f;
	
	public void checkMissile(){
		missile_step += 0.0005f;
		drawParticles(moveX+0.5f, particle_y+(float)Math.random(), 0.8f, 0.1f, 0.3f);
		drawParticles(moveX+0.7f, particle_y+(float)Math.random(), 0.8f, 0.1f, 0.3f);
		drawParticles(moveX+0.9f, particle_y+(float)Math.random(), 0.8f, 0.1f, 0.3f);
		if(moveX > FRAME_LEFT) // move missile
        	moveX-= missile_step;
        else{ // check reset
        	moveX = FRAME_RIGHT;
        	particle_y = moveY;
        	AI_id = (int)(Math.random()*4.0);
        }
		if(particle_y > FRAME_TOP) particle_y = FRAME_TOP;
		else if(particle_y < FRAME_BOT) particle_y = FRAME_BOT;
		if(AI_id == SEEK_AI){
			if(enemy_seeking < MAX_SEEK)
				enemy_seeking += SEEK_STEP;
			particle_y = particle_y*(1.0f-enemy_seeking) + moveY*enemy_seeking;
	        b.setColor(0.5f, 0.1f, 0.9f);
		}
		else if(AI_id == ZAG_AI){
			zag_scale+= 0.1f;
			if(zag_scale > 1) zag_scale = -1;
			particle_y += zag_scale;
	        b.setColor(0.1f, 0.35f, 0.8f);
		}
		else{
			moveX-=missile_step;
	        b.setColor(0.9f, 0.5f, 0.1f);
		}
	}
	
	private static final float HIT_DISTANCE = 1.5f;
	private static final long GAME_TICK = 50;
	private long curr_time = 0;
	private long prev_time = 0;
	private float player_vel = 0;
	private float gravity = -0.07f;
	private float coin_x = 0.0f;
	private float coin_y = 0.0f;

	@Override
	public void onDrawFrame(GL10 glUnused) 
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		drawBackground();
		
		curr_time = SystemClock.uptimeMillis();
	if(prev_time+GAME_TICK < curr_time){
        prev_time = curr_time;
		scoreNum++;
		checkLoss();
		moveBackground();
		
		PLAYER_X += 0.03f; // check right bound
		if(PLAYER_X > 0.8f*FRAME_RIGHT+0.2f*FRAME_LEFT){
			PLAYER_X =  0.8f*FRAME_RIGHT+0.2f*FRAME_LEFT;
		}
		
		if(coin_x > FRAME_LEFT){ // move missile
			coin_x -= missile_step;
			coin_y = -1.0f*moveY*(1.0f-enemy_seeking) + coin_y*enemy_seeking;
		}
        else{ // check reset
        	coin_x = FRAME_RIGHT;
        	coin_y = (float)Math.random()*FRAME_TOP + (float)Math.random()*FRAME_BOT;
        }
		
        if(moveY > FRAME_BOT && !mDown){ // check fall
        	player_vel += gravity;
        }
        else if(moveY < FRAME_TOP && mDown){ // check rise
        	drawParticles(PLAYER_X-0.3f*(float)Math.random(), moveY-0.2f*(float)Math.random(), 0.7f, 0.9f, 0.8f);
        	drawParticles(PLAYER_X-0.5f*(float)Math.random(), moveY-0.4f*(float)Math.random(), 0.7f, 0.9f, 0.8f);
        	drawParticles(PLAYER_X-0.7f*(float)Math.random(), moveY-0.2f*(float)Math.random(), 0.7f, 0.9f, 0.8f);
        	drawParticles(PLAYER_X-0.9f*(float)Math.random(), moveY-0.4f*(float)Math.random(), 0.7f, 0.9f, 0.8f);

        	player_vel -= gravity;
        }
    	moveY += player_vel;
        checkMissile();
        
        //collision detection
        float x_diff = PLAYER_X - moveX;
        x_diff *= x_diff;
        float y_diff = moveY - particle_y;
        y_diff *= y_diff;
        if(Math.sqrt(x_diff+y_diff) < HIT_DISTANCE){
        	if(moveY > particle_y && moveY < FRAME_TOP){
        		moveY += 0.15f; // bounce amount
        	}
        	else{
        		moveY -= 0.15f;
        	}
        	PLAYER_X -= 0.1f*(FRAME_RIGHT+Math.abs(FRAME_LEFT));
        	drawParticles(moveX, particle_y, 0.9f, 0.7f, 0.3f);
        	drawParticles(moveX-0.3f, particle_y-0.3f, 0.9f, 0.7f, 0.3f);
        	drawParticles(moveX, particle_y+0.3f, 0.9f, 0.7f, 0.3f);
        	drawParticles(moveX-0.3f, particle_y, 0.9f, 0.7f, 0.3f);
        	moveX = FRAME_RIGHT;
        }
        x_diff = PLAYER_X - coin_x;
        x_diff *= x_diff;
        y_diff = moveY - coin_y;
        y_diff *= y_diff;
        if(Math.sqrt(x_diff+y_diff) < HIT_DISTANCE){
        	scoreNum += 100; // coin worth
        	drawParticles(coin_x, coin_y, 0.9f, 0.7f, 0.3f);
        	coin_x = FRAME_RIGHT;
        	coin_y = particle_y;
        }
        
        if(moveY < FRAME_BOT){
        	moveY = FRAME_BOT;
        	player_vel = 0;
        }
        if(moveY > FRAME_TOP){
        	moveY = FRAME_TOP;
        	player_vel *= -player_vel;
        }
        
        m.setLight(PLAYER_X, moveY+1.0f, -3.0f);
        b.setLight(moveX, 1.0f+(FRAME_TOP+FRAME_BOT)/2.0f, -2.0f);
	} // end game loop
     
        // Draw some cubes.        
        
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, PLAYER_X, moveY, -5.0f);
        Matrix.rotateM(mModelMatrix, 0, 90, 0.0f, 1.0f, 0.0f);
        Matrix.scaleM(mModelMatrix, 0, 0.025f, 0.025f, 0.025f);
        
        float[] tempMatrix = new float[16];
        Matrix.multiplyMM(tempMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, mProjectionMatrix, 0, tempMatrix, 0);
        
        float[] normMatrix = new float[16];
        Matrix.multiplyMM(normMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        m.draw(MVPMatrix, normMatrix);  
        
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, moveX, particle_y, -5.0f);
        Matrix.scaleM(mModelMatrix, 0, 0.07f, 0.07f, 0.035f);
        Matrix.rotateM(mModelMatrix, 0, -90, 0.0f, 1.0f, 0.0f);
        
        tempMatrix = new float[16];
        Matrix.multiplyMM(tempMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, mProjectionMatrix, 0, tempMatrix, 0);
        
        normMatrix = new float[16];
        Matrix.multiplyMM(normMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        b.draw(MVPMatrix, normMatrix);
        
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, coin_x, coin_y, -5.0f);
        Matrix.scaleM(mModelMatrix, 0, 0.07f, 0.07f, 0.2f);
        Matrix.rotateM(mModelMatrix, 0, 90, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, 90, 0.0f, 0.0f, 1.0f);
        
        tempMatrix = new float[16];
        Matrix.multiplyMM(tempMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, mProjectionMatrix, 0, tempMatrix, 0);
        
        normMatrix = new float[16];
        Matrix.multiplyMM(normMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

		coin.setLight(coin_x, coin_y, -3.0f);
        coin.draw(MVPMatrix, normMatrix);
        
        drawLava();
        drawScore();
	}
	
	public void checkLoss(){
		if(PLAYER_X <  0.1f*FRAME_RIGHT+0.9f*FRAME_LEFT){
        	SharedPreferences sp = mAct.getSharedPreferences("myPref", Context.MODE_PRIVATE);
    		Editor e = sp.edit();
    		String s = "312 112 096 087 030  ";
    		if(sp != null && sp.contains("KEY"))
    			s = sp.getString("KEY", "");
    		
    		String new_scores = s;
    		int[] arr = new int[5];
    		for(int i = 0; i< 5 && s.length() > 0; i++){
    			int index = s.indexOf(" ");
    			if(index < s.length() && index > 0){
    				String text = s.substring(0, index);
    				s = s.substring(index+1);
    				arr[i] = Integer.parseInt(text);
    			}
    		}
    		boolean changed = false;
    		int temp = 0;
    		for(int i = 0; i< 5; i++){
    			if(changed){
    				int new_temp = arr[i];
    				arr[i] = temp;
    				temp = new_temp;
    			}
    			else if(arr[i] < scoreNum){
    				changed =true;
    				temp = arr[i];
    				arr[i] = scoreNum;
    			}
    		}
    		new_scores = arr[0] + " " + arr[1] + " " + arr[2] + " " + arr[3] + " " + arr[4] + "  ";
    		
    		e.putString("KEY", new_scores);
    		e.commit();
    		Intent myIntent = new Intent(mActivityContext, EndScreen.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mAct.startActivity(myIntent);
        }
	}
	
	private int scoreNum;
	private static final float score_x = -1.0f*FRAME_LEFT;
	private static final float score_y = -3.0f;
	private static final float score_scale = 0.1f;
	public void drawScore(){
		if(scoreNum >= 99000) scoreNum = 0;
		int count = scoreNum;
		
		score = text.get(scoreNum/10000);
		count -= (int)(scoreNum/10000)*10000;
		score.setColor(1.0f, 1.0f, 1.0f);
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, FRAME_RIGHT-(score_x+3.6f)*UNIT, FRAME_TOP-score_y*UNIT, -6.0f);
		//Matrix.rotateM(mModelMatrix, 0, p_arr[i], p_x[i], p_x[i], p_y[i]);
        Matrix.rotateM(mModelMatrix, 0, 180, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, 90, 0.0f, 0.0f, 1.0f);
		Matrix.scaleM(mModelMatrix, 0, score_scale, score_scale, 0.0001f);
		float[] tempMatrix = new float[16];
		Matrix.multiplyMM(tempMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		Matrix.multiplyMM(MVPMatrix, 0, mProjectionMatrix, 0, tempMatrix, 0);
		float[] normMatrix = new float[16];
		Matrix.multiplyMM(normMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		score.draw(MVPMatrix, normMatrix);
		
		score = text.get(count/1000);
		count -= (int)(scoreNum/1000)*1000;
		score.setColor(1.0f, 1.0f, 1.0f);
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, FRAME_RIGHT-(score_x+1.8f)*UNIT, FRAME_TOP-score_y*UNIT, -6.0f);
		//Matrix.rotateM(mModelMatrix, 0, p_arr[i], p_x[i], p_x[i], p_y[i]);
        Matrix.rotateM(mModelMatrix, 0, 180, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, 90, 0.0f, 0.0f, 1.0f);
		Matrix.scaleM(mModelMatrix, 0, score_scale, score_scale, 0.0001f);
		tempMatrix = new float[16];
		Matrix.multiplyMM(tempMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		Matrix.multiplyMM(MVPMatrix, 0, mProjectionMatrix, 0, tempMatrix, 0);
		normMatrix = new float[16];
		Matrix.multiplyMM(normMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		score.draw(MVPMatrix, normMatrix);
		
			score = text.get(count/100);
			count -= (int)(count/100)*100;
			score.setColor(1.0f, 1.0f, 1.0f);
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, FRAME_RIGHT-score_x*UNIT, FRAME_TOP-score_y*UNIT, -6.0f);
			//Matrix.rotateM(mModelMatrix, 0, p_arr[i], p_x[i], p_x[i], p_y[i]);
	        Matrix.rotateM(mModelMatrix, 0, 180, 0.0f, 1.0f, 0.0f);
	        Matrix.rotateM(mModelMatrix, 0, 90, 0.0f, 0.0f, 1.0f);
			Matrix.scaleM(mModelMatrix, 0, score_scale, score_scale, 0.00001f);
			tempMatrix = new float[16];
			Matrix.multiplyMM(tempMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
			Matrix.multiplyMM(MVPMatrix, 0, mProjectionMatrix, 0, tempMatrix, 0);
			normMatrix = new float[16];
			Matrix.multiplyMM(normMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
			score.draw(MVPMatrix, normMatrix);
			
			score = text.get(count/10);
			count -= (int)(count/10)*10;
			score.setColor(1.0f, 1.0f, 1.0f);
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, FRAME_RIGHT-(score_x-1.8f)*UNIT, FRAME_TOP-score_y*UNIT, -6.0f);
			//Matrix.rotateM(mModelMatrix, 0, p_arr[i], p_x[i], p_x[i], p_y[i]);
	        Matrix.rotateM(mModelMatrix, 0, 180, 0.0f, 1.0f, 0.0f);
	        Matrix.rotateM(mModelMatrix, 0, 90, 0.0f, 0.0f, 1.0f);
			Matrix.scaleM(mModelMatrix, 0, score_scale, score_scale, 0.00001f);
			tempMatrix = new float[16];
			Matrix.multiplyMM(tempMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
			Matrix.multiplyMM(MVPMatrix, 0, mProjectionMatrix, 0, tempMatrix, 0);
			normMatrix = new float[16];
			Matrix.multiplyMM(normMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
			score.draw(MVPMatrix, normMatrix);
			
			score = text.get(count);
			score.setColor(1.0f, 1.0f, 1.0f);
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, FRAME_RIGHT-(score_x-3.6f)*UNIT, FRAME_TOP-score_y*UNIT, -6.0f);
			//Matrix.rotateM(mModelMatrix, 0, p_arr[i], p_x[i], p_x[i], p_y[i]);
	        Matrix.rotateM(mModelMatrix, 0, 180, 0.0f, 1.0f, 0.0f);
	        Matrix.rotateM(mModelMatrix, 0, 90, 0.0f, 0.0f, 1.0f);
			Matrix.scaleM(mModelMatrix, 0, score_scale, score_scale, 0.00001f);
			tempMatrix = new float[16];
			Matrix.multiplyMM(tempMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
			Matrix.multiplyMM(MVPMatrix, 0, mProjectionMatrix, 0, tempMatrix, 0);
			normMatrix = new float[16];
			Matrix.multiplyMM(normMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
			score.draw(MVPMatrix, normMatrix);
	}
	
	private float[] p_arr, p_x, p_y;
	private static final int P_NUM = 5;
	private static final float UNIT = 0.5f;
	
	public void createLava(){
		p_arr = new float[P_NUM];
		p_x = new float[P_NUM];
		p_y = new float[P_NUM];
		for(int i =0; i < P_NUM; i++){
			p_arr[i] = 25*i;
			p_x[i] = FRAME_LEFT;
			p_y[i] = FRAME_TOP - 2*i*UNIT;
			p_x[i] += (i)*UNIT;
		}
	}
	
	public void drawLava(){
		for(int i = 0; i < P_NUM; i++){
			p_arr[i] += 1.0f;
			p_y[i] += 0.5f-Math.random();
			if(p_y[i] > FRAME_TOP) p_y[i] = FRAME_TOP;
			else if(p_y[i] < FRAME_BOT) p_y[i] = FRAME_BOT;
			if(p_arr[i] > 360.0f) p_arr[i] = 0.0f;
			t.setColor(0.8f, 0.2f, 0.2f);
	        t.setLight(p_x[i]+1.0f, p_y[i]+1.0f, -2.0f);
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, p_x[i], p_y[i], -5.0f);
			Matrix.scaleM(mModelMatrix, 0, 0.1f, 0.1f, 0.1f);
			Matrix.rotateM(mModelMatrix, 0, p_arr[i], p_x[i], p_x[i], p_y[i]);
        
			float[] tempMatrix = new float[16];
			Matrix.multiplyMM(tempMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
			Matrix.multiplyMM(MVPMatrix, 0, mProjectionMatrix, 0, tempMatrix, 0);
        
			float[] normMatrix = new float[16];
			Matrix.multiplyMM(normMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

			t.draw(MVPMatrix, normMatrix);
		}
	}
	
	public void drawParticles(float x, float y, float r, float g, float bl){
		box.setColor(r, g, bl);
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, x, y, -5.0f);
		Matrix.rotateM(mModelMatrix, 0, p_arr[0], 0, 1.0f, 0);
		Matrix.scaleM(mModelMatrix, 0, 0.02f, 0.03f, 0.01f);
		float[] tempMatrix = new float[16];
		Matrix.multiplyMM(tempMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		Matrix.multiplyMM(MVPMatrix, 0, mProjectionMatrix, 0, tempMatrix, 0);
		float[] normMatrix = new float[16];
		Matrix.multiplyMM(normMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		box.draw(MVPMatrix, normMatrix);
	}
}

