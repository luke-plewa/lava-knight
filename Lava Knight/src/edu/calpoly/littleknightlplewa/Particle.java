package edu.calpoly.littleknightlplewa;

import edu.calpoly.littleknightlplewa.R;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class Particle extends Mesh{
	
	public Particle(){}

	public Particle(Context c, String mesh) {
        context = c;
        super.loadCube(mesh);
        
        final String vertexShaderS = getVertexShader();   		
 		final String fragmentShaderS = getFragmentShader();			
		
		final int vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderS);		
		final int fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderS);

        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);

        int[] linked = new int[1];
        GLES20.glGetProgramiv(shaderProgram, GLES20.GL_LINK_STATUS, linked, 0);
        if (linked[0] == 0){
            Log.d("DEBUG", "Shader code error.");
            Log.d("DEBUG", GLES20.glGetProgramInfoLog(shaderProgram));
            GLES20.glDeleteProgram(shaderProgram);
            return;
        }

        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);
	}
	
	@Override
	protected String getVertexShader()
	{
		return readTextFileFromRawResource(context, R.raw.regular_vertex_shader);
	}
	
	@Override
	protected String getFragmentShader()
	{
		return readTextFileFromRawResource(context, R.raw.regular_fragment_shader);
	}
	
	protected float color_x;
	protected float color_y;
	protected float color_z;
	
	protected void setColor(float x, float y, float z){
		color_x = x;
		color_y = y;
		color_z = z;
	}
	
	protected int uniform_color;
	
	@Override
	public void draw(float[] mvpMatrix, float[] normMatrix){
        GLES20.glUseProgram(shaderProgram);
        uniform_lightVec = GLES20.glGetUniformLocation(shaderProgram, "lightVec");
        GLES20.glUniform3f(uniform_lightVec, light_x, light_y, light_z);

        uniform_color = GLES20.glGetUniformLocation(shaderProgram, "uColor");
        GLES20.glUniform3f(uniform_color, color_x, color_y, color_z);
        
        attribute_Position = GLES20.glGetAttribLocation(shaderProgram, "a_position");
        GLES20.glVertexAttribPointer(attribute_Position, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(attribute_Position);

        attribute_Normal = GLES20.glGetAttribLocation(shaderProgram, "a_normal");
        GLES20.glVertexAttribPointer(attribute_Normal, 3, GLES20.GL_FLOAT, false, 3 * 4, normalBuffer);
        GLES20.glEnableVertexAttribArray(attribute_Normal);
        
        uniform_mvpMatrix = GLES20.glGetUniformLocation(shaderProgram, "mvpMatrix");
        GLES20.glUniformMatrix4fv(uniform_mvpMatrix, 1, false, mvpMatrix, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        
        uniform_mvMatrix = GLES20.glGetUniformLocation(shaderProgram, "mvMatrix");
        GLES20.glUniformMatrix4fv(uniform_mvMatrix, 1, false, normMatrix, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        
        GLES20.glDisableVertexAttribArray(attribute_Position);
        GLES20.glDisableVertexAttribArray(attribute_Normal);
    }
}