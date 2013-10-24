package edu.calpoly.littleknightlplewa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.calpoly.littleknightlplewa.R;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.os.SystemClock;
import android.util.Log;

public class Mesh {
    protected Context context;
    public FloatBuffer vertexBuffer;
    public ShortBuffer normalIBuffer;
    public FloatBuffer normalBuffer;
    public ShortBuffer indexBuffer;
    public FloatBuffer vertexBuffer2;
    public FloatBuffer normalBuffer2;
    protected float light_x, light_y, light_z;

    protected int shaderProgram;
    
    protected float time = 0;
    
    public Mesh(){
    }

    public Mesh(Context c) {
        context = c;
        loadCube("obj/zero3.obj");
        loadSecond("obj/zero2.obj");
        
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

    protected void loadSecond(String filename) {
    	vertexBuffer2 = vertexBuffer;
    	normalBuffer2 = normalBuffer;
    	loadCube(filename);
    }
    
    protected void loadCube(String filename) {

        ArrayList<Float> tempVertices = new ArrayList<Float>();
        ArrayList<Float> tempNormals = new ArrayList<Float>();
        ArrayList<Short> vertexIndices = new ArrayList<Short>();
        ArrayList<Short> normalIndices = new ArrayList<Short>();

        try {
            AssetManager manager = context.getAssets();
            BufferedReader reader = new BufferedReader(new InputStreamReader(manager.open(filename)));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("v ")) {
                	StringTokenizer tok = new StringTokenizer(line);
                	tok.nextToken();
                	tempVertices.add(Float.parseFloat(tok.nextToken()));
                	tempVertices.add(Float.parseFloat(tok.nextToken()));
                	tempVertices.add(Float.parseFloat(tok.nextToken()));
                }
                else if (line.startsWith("vn ")) {
					StringTokenizer tok = new StringTokenizer(line);
					tok.nextToken();
					tempNormals.add(Float.parseFloat(tok.nextToken()));
					tempNormals.add(Float.parseFloat(tok.nextToken()));
					tempNormals.add(Float.parseFloat(tok.nextToken()));
				}
                else if (line.startsWith("f ")){
					int[] face = new int[3];
					int[] face_n_ix = new int[3];
					int[] val;

					StringTokenizer tok = new StringTokenizer(line);
					tok.nextToken();
					val = parseIntTriple(tok.nextToken());
					face[0] = val[0];
					if (val.length > 2 && val[2] > -1)
						face_n_ix[0] = val[2];

					val = parseIntTriple(tok.nextToken());
					face[1] = val[0];
					if (val.length > 2 && val[2] > -1)
						face_n_ix[1] = val[2];

					val = parseIntTriple(tok.nextToken());
					face[2] = val[0];
					if (val.length > 2 && val[2] > -1) {
						face_n_ix[2] = val[2];
						//m.addFaceNormals(face_n_ix);
						normalIndices.add((short)face_n_ix[0]);
						normalIndices.add((short)face_n_ix[1]);
						normalIndices.add((short)face_n_ix[2]);
					}
					//m.addFace(face);
					vertexIndices.add((short)face[0]);
					vertexIndices.add((short)face[1]);
					vertexIndices.add((short)face[2]);
					if (tok.hasMoreTokens()) {
						val = parseIntTriple(tok.nextToken());
						face[1] = face[2];
						face[2] = val[0];
						if (val.length > 2 && val[2] > -1) {
							face_n_ix[1] = face_n_ix[2];
							face_n_ix[2] = val[2];
							//m.addFaceNormals(face_n_ix);
							normalIndices.add((short)face_n_ix[0]);
							normalIndices.add((short)face_n_ix[1]);
							normalIndices.add((short)face_n_ix[2]);
						}
						//m.addFace(face);
						vertexIndices.add((short)face[0]);
						vertexIndices.add((short)face[1]);
						vertexIndices.add((short)face[2]);
					}
                }
            }

            float[] vertices = new float[tempVertices.size()];
            for (int i = 0; i < tempVertices.size(); i++) {
                Float f = tempVertices.get(i);
                vertices[i] = (f != null ? f : Float.NaN);
            }
            
            float[] normals = new float[tempNormals.size()];
            for (int i = 0; i < tempNormals.size(); i++) {
                Float f = tempNormals.get(i);
                normals[i] = (f != null ? f : Float.NaN);
            }

            short[] indices = new short[vertexIndices.size()];
            for (int i = 0; i < vertexIndices.size(); i++) {
                Short s = vertexIndices.get(i);
                indices[i] = (s != null ? s : 1);
            }
            
            short[] norm_indices = new short[normalIndices.size()];
            for (int i = 0; i < normalIndices.size(); i++) {
                Short s = normalIndices.get(i);
                norm_indices[i] = (s != null ? s : 1);
            }

            vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            vertexBuffer.put(vertices).position(0);
            
            normalBuffer = ByteBuffer.allocateDirect(normals.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            normalBuffer.put(vertices).position(0);

            indexBuffer = ByteBuffer.allocateDirect(indices.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
            indexBuffer.put(indices).position(0);
            
            normalIBuffer = ByteBuffer.allocateDirect(norm_indices.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
            normalIBuffer.put(norm_indices).position(0);
        }
        catch (Exception e) {
            Log.d("DEBUG", "Error.", e);
        }
    }
    
    protected static int parseInt(String val) {
		if (val.length() == 0) {
			return -1;
		}
		return Integer.parseInt(val);
	}
    
    protected static int[] parseIntTriple(String face) {
		int ix = face.indexOf("/");
		if (ix == -1)
			return new int[] {Integer.parseInt(face)-1};
		else {
			int ix2 = face.indexOf("/", ix+1);
			if (ix2 == -1) {
				return new int[] 
				               {Integer.parseInt(face.substring(0,ix))-1,
						Integer.parseInt(face.substring(ix+1))-1};
			}
			else {
				return new int[] 
				               {parseInt(face.substring(0,ix))-1,
						parseInt(face.substring(ix+1,ix2))-1,
						parseInt(face.substring(ix2+1))-1
				               };
			}
		}
	}

    protected int attribute_Position;
    protected int attribute_Position2;
    protected int uniform_Time;
    protected int attribute_Normal;
    protected int attribute_Normal2;
    protected int uniform_mvpMatrix;
    protected int uniform_mvMatrix;
    protected int uniform_lightVec;
    protected boolean reverse;
    
    public void setLight(float x, float y, float z){
    	light_x = x;
    	light_y = y;
    	light_z = z;
    }

    private float curr_time = 0;
    private float prev_time = 0;
    private static final float CHAR_STEP = 50;
    public void draw(float[] mvpMatrix, float[] normMatrix){
        GLES20.glUseProgram(shaderProgram);
        uniform_Time = GLES20.glGetUniformLocation(shaderProgram, "u_time");
        if(time >= 1) reverse = true;
        else if (time <= 0) reverse = false;
        curr_time = SystemClock.uptimeMillis();
        if(prev_time+CHAR_STEP < curr_time){
        	if(reverse) time -= 0.2f;
        	else time += 0.2f;
        	prev_time = curr_time;
        }
        GLES20.glUniform1f(uniform_Time, time);
        uniform_lightVec = GLES20.glGetUniformLocation(shaderProgram, "lightVec");
        GLES20.glUniform3f(uniform_lightVec, light_x, light_y, light_z);
        
        attribute_Position = GLES20.glGetAttribLocation(shaderProgram, "a_position");
        GLES20.glVertexAttribPointer(attribute_Position, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(attribute_Position);
        attribute_Position2 = GLES20.glGetAttribLocation(shaderProgram, "a_position2");
        GLES20.glVertexAttribPointer(attribute_Position2, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer2);
        GLES20.glEnableVertexAttribArray(attribute_Position2);

        attribute_Normal = GLES20.glGetAttribLocation(shaderProgram, "a_normal");
        GLES20.glVertexAttribPointer(attribute_Normal, 3, GLES20.GL_FLOAT, false, 3 * 4, normalBuffer);
        GLES20.glEnableVertexAttribArray(attribute_Normal);
        attribute_Normal = GLES20.glGetAttribLocation(shaderProgram, "a_normal2");
        GLES20.glVertexAttribPointer(attribute_Normal2, 3, GLES20.GL_FLOAT, false, 3 * 4, normalBuffer2);
        GLES20.glEnableVertexAttribArray(attribute_Normal2);
        
        uniform_mvpMatrix = GLES20.glGetUniformLocation(shaderProgram, "mvpMatrix");
        GLES20.glUniformMatrix4fv(uniform_mvpMatrix, 1, false, mvpMatrix, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        
        uniform_mvMatrix = GLES20.glGetUniformLocation(shaderProgram, "mvMatrix");
        GLES20.glUniformMatrix4fv(uniform_mvMatrix, 1, false, normMatrix, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        
        GLES20.glDisableVertexAttribArray(attribute_Position);
        GLES20.glDisableVertexAttribArray(attribute_Normal);
    }
    
    protected String getVertexShader()
	{
		return readTextFileFromRawResource(context, R.raw.my_vertex_shader);
	}
	
	protected String getFragmentShader()
	{
		return readTextFileFromRawResource(context, R.raw.my_fragment_shader);
	}
	
	public static int compileShader(final int shaderType, final String shaderSource) 
	{
		int shaderHandle = GLES20.glCreateShader(shaderType);

		if (shaderHandle != 0) 
		{
			// Pass in the shader source.
			GLES20.glShaderSource(shaderHandle, shaderSource);

			// Compile the shader.
			GLES20.glCompileShader(shaderHandle);

			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0) 
			{
				Log.e("Shader", "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0)
		{			
			throw new RuntimeException("Error creating shader.");
		}
		
		return shaderHandle;
	}
	
	public static String readTextFileFromRawResource(final Context context,
			final int resourceId)
	{
		final InputStream inputStream = context.getResources().openRawResource(
				resourceId);
		final InputStreamReader inputStreamReader = new InputStreamReader(
				inputStream);
		final BufferedReader bufferedReader = new BufferedReader(
				inputStreamReader);

		String nextLine;
		final StringBuilder body = new StringBuilder();

		try
		{
			while ((nextLine = bufferedReader.readLine()) != null)
			{
				body.append(nextLine);
				body.append('\n');
			}
		}
		catch (IOException e)
		{
			return null;
		}

		return body.toString();
	}
}