package edu.calpoly.littleknightlplewa;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import edu.calpoly.littleknightlplewa.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Texture extends Particle{
	
	/** Store our model data in a float buffer. */
	private final FloatBuffer mCubeTextureCoordinates;
	 
	/** This will be used to pass in the texture. */
	private int mTextureUniformHandle;
	 
	/** This will be used to pass in model texture coordinate information. */
	private int mTextureCoordinateHandle;
	 
	/** Size of the texture coordinate data in elements. */
	private final int mTextureCoordinateDataSize = 2;
	 
	/** This is a handle to our texture data. */
	private int mTextureDataHandle;

	private final int mBytesPerFloat = 4;
	
	public Texture(Context c, String mesh){
		super(c, mesh);
		
		final float[] cubeTextureCoordinateData =
			{												
					// Front face
					0.0f, 0.0f, 				
					0.0f, 1.0f,
					1.0f, 0.0f,
					0.0f, 1.0f,
					1.0f, 1.0f,
					1.0f, 0.0f,				
					
					// Right face 				
					0.0f, 1.0f,
					0.0f, 0.0f, 
					1.0f, 0.0f,
					0.0f, 1.0f,
					1.0f, 1.0f,
					1.0f, 0.0f,	
					
					// Back face 
					0.0f, 0.0f, 				
					0.0f, 1.0f,
					1.0f, 0.0f,
					0.0f, 1.0f,
					1.0f, 1.0f,
					1.0f, 0.0f,	
					
					// Left face 
					0.0f, 0.0f, 				
					0.0f, 1.0f,
					1.0f, 0.0f,
					0.0f, 1.0f,
					1.0f, 1.0f,
					1.0f, 0.0f,	
					
					// Top face 
					0.0f, 0.0f, 				
					0.0f, 1.0f,
					1.0f, 0.0f,
					0.0f, 1.0f,
					1.0f, 1.0f,
					1.0f, 0.0f,	
					
					// Bottom face 
					0.0f, 0.0f, 				
					0.0f, 1.0f,
					1.0f, 0.0f,
					0.0f, 1.0f,
					1.0f, 1.0f,
					1.0f, 0.0f
			};
		
		mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * mBytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
				mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);
	}

	@Override
	protected String getVertexShader()
	{
		return readTextFileFromRawResource(context, R.raw.texture_vertex_shader);
	}
	
	@Override
	protected String getFragmentShader()
	{
		return readTextFileFromRawResource(context, R.raw.texture_fragment_shader);
	}
	
	public int loadTexture(final Context context, final int resourceId)
	{
		final int[] textureHandle = new int[1];
 
		GLES20.glGenTextures(1, textureHandle, 0);
 
		if (textureHandle[0] != 0)
		{
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false;   // No pre-scaling
 
			// Read in the resource
			final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
 
			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
 
			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
 
			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
 
			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();
		}
 
		if (textureHandle[0] == 0)
		{
			throw new RuntimeException("Error loading texture.");
		}
		
		mTextureDataHandle = textureHandle[0];
 
		return textureHandle[0];
	}
	
	@Override
	public void draw(float[] mvpMatrix, float[] normMatrix){
        GLES20.glUseProgram(shaderProgram);
        uniform_lightVec = GLES20.glGetUniformLocation(shaderProgram, "lightVec");
        GLES20.glUniform3f(uniform_lightVec, light_x, light_y, light_z);

        uniform_color = GLES20.glGetUniformLocation(shaderProgram, "uColor");
        GLES20.glUniform3f(uniform_color, color_x, color_y, color_z);
        
        mTextureUniformHandle = GLES20.glGetUniformLocation(shaderProgram, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram, "a_TexCoordinate");
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
     
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        
        mCubeTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 
        		0, mCubeTextureCoordinates);
        
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        
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