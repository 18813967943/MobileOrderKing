/**
 * Represents a shader object
 * from Shayan Javed
 * http://blog.shayanjaved.com/2011/03/13/shaders-android/
 * <p>
 * modified!
 */

package com.lejia.mobile.orderking.hk3d.datas_3d;

import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RenderProgram {
    /************************
     * PROPERTIES
     **********************/
    private static final String TAG = "RenderProgram";

    // program/vertex/fragment handles
    private int mProgram, mVertexShader, mPixelShader;

    // The shaders
    private String mVertexS, mFragmentS;

    /************************
     * CONSTRUCTOR(S)
     *************************/
    // Takes in Strings directly
    public RenderProgram(String vertexS, String fragmentS) {
        setup(vertexS, fragmentS);
    }

    // Takes in ids for files to be read
    public RenderProgram(int vID, int fID, Context context) {
        StringBuffer vs = new StringBuffer();
        StringBuffer fs = new StringBuffer();

        // read the files
        try {
            // Read the file from the resource
            //Log.d("loadFile", "Trying to read vs");
            // Read VS first
            InputStream inputStream = context.getResources().openRawResource(vID);
            // setup Bufferedreader
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String read = in.readLine();
            while (read != null) {
                vs.append(read + "\n");
                read = in.readLine();
            }

            vs.deleteCharAt(vs.length() - 1);

            // Now read FS
            inputStream = context.getResources().openRawResource(fID);
            // setup Bufferedreader
            in = new BufferedReader(new InputStreamReader(inputStream));

            read = in.readLine();
            while (read != null) {
                fs.append(read + "\n");
                read = in.readLine();
            }

            fs.deleteCharAt(fs.length() - 1);
        } catch (Exception e) {
            Log.d(TAG, "Could not read shader: " + e.getLocalizedMessage());
        }


        // Setup everything
        setup(vs.toString(), fs.toString());
    }


    /**************************
     * OTHER METHODS
     *************************/

    /**
     * Sets up everything
     * @param vs the vertex shader
     * @param fs the fragment shader
     */
    private void setup(String vs, String fs) {
        this.mVertexS = vs;
        this.mFragmentS = fs;

        // create the program
        if (createProgram() != 1) {
            throw new RuntimeException("Error at creating shaders");
        }
    }

    /**
     * Creates a shader program.
     * @return returns 1 if creation successful, 0 if not
     */
    private int createProgram() {
        // Vertex shader
        mVertexShader = loadShader(GLES30.GL_VERTEX_SHADER, mVertexS);
        if (mVertexShader == 0) {
            return 0;
        }

        // pixel shader
        mPixelShader = loadShader(GLES30.GL_FRAGMENT_SHADER, mFragmentS);
        if (mPixelShader == 0) {
            return 0;
        }

        // Create the program
        mProgram = GLES30.glCreateProgram();
        if (mProgram != 0) {
            GLES30.glAttachShader(mProgram, mVertexShader);
            //checkGlError("glAttachShader VS " + this.toString());
            GLES30.glAttachShader(mProgram, mPixelShader);
            //checkGlError("glAttachShader PS");
            GLES30.glLinkProgram(mProgram);
            int[] linkStatus = new int[1];
            GLES30.glGetProgramiv(mProgram, GLES30.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES30.GL_TRUE) {
                Log.e(TAG, "Could not link _program: ");
                Log.e(TAG, GLES30.glGetProgramInfoLog(mProgram));
                GLES30.glDeleteProgram(mProgram);
                mProgram = 0;
                return 0;
            }
        } else
            Log.d("CreateProgram", "Could not create program");

        return 1;
    }

    /**
     * Loads a shader (either vertex or pixel) given the source
     * @param shaderType VERTEX or PIXEL
     * @param source The string data representing the shader code
     * @return handle for shader
     */
    private int loadShader(int shaderType, String source) {
        int shader = GLES30.glCreateShader(shaderType);
        if (shader != 0) {
            GLES30.glShaderSource(shader, source);
            GLES30.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES30.glGetShaderInfoLog(shader));
                GLES30.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    /***************************
     * GET/SET
     *************************/
    public int getProgram() {
        return mProgram;
    }
}