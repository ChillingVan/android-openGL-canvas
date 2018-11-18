/*
 *
 *  *
 *  *  * Copyright (C) 2016 ChillingVan
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *
 *
 */
package com.chillingvan.canvasgl.glcanvas;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.chillingvan.canvasgl.shapeFilter.BasicDrawShapeFilter;
import com.chillingvan.canvasgl.shapeFilter.DrawShapeFilter;
import com.chillingvan.canvasgl.textureFilter.BasicTextureFilter;
import com.chillingvan.canvasgl.textureFilter.TextureFilter;
import com.chillingvan.canvasgl.util.Loggers;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * drawRect, drawLine, drawCircle --> prepareDraw --> {@link GLES20Canvas#draw}
 * drawTexture --> setupTextureFilter --> drawTextureRect --> prepareTexture
 * --> setPosition  --> draw --> setMatrix
 */
public class GLES20Canvas implements GLCanvas {
    // ************** Constants **********************
    private static final String TAG = GLES20Canvas.class.getSimpleName();
    private static final int FLOAT_SIZE = Float.SIZE / Byte.SIZE;
    private static final float OPAQUE_ALPHA = 0.95f;

    private static final int COORDS_PER_VERTEX = 2;
    private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * FLOAT_SIZE;

    private static final int COUNT_FILL_VERTEX = 4;
    private static final int COUNT_LINE_VERTEX = 2;
    private static final int COUNT_RECT_VERTEX = 4;
    private static final int OFFSET_FILL_RECT = 0;
    private static final int OFFSET_DRAW_LINE = OFFSET_FILL_RECT + COUNT_FILL_VERTEX;
    private static final int OFFSET_DRAW_RECT = OFFSET_DRAW_LINE + COUNT_LINE_VERTEX;

    private static final float[] BOX_COORDINATES = {
            0, 0, // Fill rectangle
            1, 0,
            0, 1,
            1, 1,
            0, 0, // Draw line
            1, 1,
            0, 0, // Draw rectangle outline
            0, 1,
            1, 1,
            1, 0,
    };

    private static final float[] BOUNDS_COORDINATES = {
            0, 0, 0, 1,
            1, 1, 0, 1,
    };

    public static final String POSITION_ATTRIBUTE = "aPosition";
    public static final String COLOR_UNIFORM = "uColor";
    public static final String MATRIX_UNIFORM = "uMatrix";
    public static final String TEXTURE_MATRIX_UNIFORM = "uTextureMatrix";
    public static final String TEXTURE_SAMPLER_UNIFORM = "uTextureSampler";
    public static final String ALPHA_UNIFORM = "uAlpha";
    public static final String TEXTURE_COORD_ATTRIBUTE = "aTextureCoordinate";

    public static final String MESH_VERTEX_SHADER = ""
            + "uniform mat4 " + MATRIX_UNIFORM + ";\n"
            + "attribute vec2 " + POSITION_ATTRIBUTE + ";\n"
            + "attribute vec2 " + TEXTURE_COORD_ATTRIBUTE + ";\n"
            + "varying vec2 vTextureCoord;\n"
            + "void main() {\n"
            + "  vec4 pos = vec4(" + POSITION_ATTRIBUTE + ", 0.0, 1.0);\n"
            + "  gl_Position = " + MATRIX_UNIFORM + " * pos;\n"
            + "  vTextureCoord = " + TEXTURE_COORD_ATTRIBUTE + ";\n"
            + "}\n";

    public static final int INITIAL_RESTORE_STATE_SIZE = 8;
    private static final int MATRIX_SIZE = 16;

    private Map<DrawShapeFilter, Integer> mDrawShapeFilterMapProgramId = new HashMap<>();
    private Map<TextureFilter, Integer> mTextureFilterMapProgramId = new HashMap<>();
    private Map<TextureFilter, Integer> mOESTextureFilterMapProgramId = new HashMap<>();

    // Keep track of restore state
    private float[] mMatrices = new float[INITIAL_RESTORE_STATE_SIZE * MATRIX_SIZE];
    private float[] mAlphas = new float[INITIAL_RESTORE_STATE_SIZE];
    private IntArray mSaveFlags = new IntArray();

    private int mCurrentAlphaIndex = 0;
    private int mCurrentMatrixIndex = 0;

    // Viewport size
    private int mWidth;
    private int mHeight;

    // Projection matrix
    private float[] mProjectionMatrix = new float[MATRIX_SIZE];

    // Screen size for when we aren't bound to a secondBitmap
    private int mScreenWidth;
    private int mScreenHeight;

    // GL programs
    private int mDrawProgram;
    private int mTextureProgram;
    private int mOesTextureProgram;
    private int mMeshProgram;

    // GL buffer containing BOX_COORDINATES
    private int mBoxCoordinates;

    // Handle indices -- common
    private static final int INDEX_POSITION = 0;
    private static final int INDEX_MATRIX = 1;

    // Handle indices -- draw
    private static final int INDEX_COLOR = 2;

    // Handle indices -- secondBitmap
    private static final int INDEX_TEXTURE_MATRIX = 2;
    private static final int INDEX_TEXTURE_SAMPLER = 3;
    private static final int INDEX_ALPHA = 4;

    // Handle indices -- mesh
    private static final int INDEX_TEXTURE_COORD = 2;


    private TextureFilter mTextureFilter;
    private DrawShapeFilter mDrawShapeFilter;

    private OnPreDrawTextureListener onPreDrawTextureListener;
    private OnPreDrawShapeListener onPreDrawShapeListener;

    private abstract static class ShaderParameter {
        public int handle;
        protected final String mName;

        public ShaderParameter(String name) {
            mName = name;
        }

        public abstract void loadHandle(int program);
    }

    private static class UniformShaderParameter extends ShaderParameter {
        public UniformShaderParameter(String name) {
            super(name);
        }

        @Override
        public void loadHandle(int program) {
            handle = GLES20.glGetUniformLocation(program, mName);
            checkError();
        }
    }

    private static class AttributeShaderParameter extends ShaderParameter {
        public AttributeShaderParameter(String name) {
            super(name);
        }

        @Override
        public void loadHandle(int program) {
            handle = GLES20.glGetAttribLocation(program, mName);
            checkError();
        }
    }

    ShaderParameter[] mDrawParameters = {
            new AttributeShaderParameter(POSITION_ATTRIBUTE), // INDEX_POSITION
            new UniformShaderParameter(MATRIX_UNIFORM), // INDEX_MATRIX
            new UniformShaderParameter(COLOR_UNIFORM), // INDEX_COLOR
    };
    ShaderParameter[] mTextureParameters = {
            new AttributeShaderParameter(POSITION_ATTRIBUTE), // INDEX_POSITION
            new UniformShaderParameter(MATRIX_UNIFORM), // INDEX_MATRIX
            new UniformShaderParameter(TEXTURE_MATRIX_UNIFORM), // INDEX_TEXTURE_MATRIX
            new UniformShaderParameter(TEXTURE_SAMPLER_UNIFORM), // INDEX_TEXTURE_SAMPLER
            new UniformShaderParameter(ALPHA_UNIFORM), // INDEX_ALPHA
    };
    ShaderParameter[] mOesTextureParameters = {
            new AttributeShaderParameter(POSITION_ATTRIBUTE), // INDEX_POSITION
            new UniformShaderParameter(MATRIX_UNIFORM), // INDEX_MATRIX
            new UniformShaderParameter(TEXTURE_MATRIX_UNIFORM), // INDEX_TEXTURE_MATRIX
            new UniformShaderParameter(TEXTURE_SAMPLER_UNIFORM), // INDEX_TEXTURE_SAMPLER
            new UniformShaderParameter(ALPHA_UNIFORM), // INDEX_ALPHA
    };
    ShaderParameter[] mMeshParameters = {
            new AttributeShaderParameter(POSITION_ATTRIBUTE), // INDEX_POSITION
            new UniformShaderParameter(MATRIX_UNIFORM), // INDEX_MATRIX
            new AttributeShaderParameter(TEXTURE_COORD_ATTRIBUTE), // INDEX_TEXTURE_COORD
            new UniformShaderParameter(TEXTURE_SAMPLER_UNIFORM), // INDEX_TEXTURE_SAMPLER
            new UniformShaderParameter(ALPHA_UNIFORM), // INDEX_ALPHA
    };

    private final IntArray mUnboundTextures = new IntArray();
    private final IntArray mDeleteBuffers = new IntArray();

    // Keep track of statistics for debugging
    private int mCountDrawMesh = 0;
    private int mCountTextureRect = 0;
    private int mCountFillRect = 0;
    private int mCountDrawLine = 0;

    // Buffer for framebuffer IDs -- we keep track so we can switch the attached
    // secondBitmap.
    private int[] mFrameBuffer = new int[1];

    // Bound textures.
    private ArrayList<RawTexture> mTargetTextures = new ArrayList<RawTexture>();

    // Temporary variables used within calculations
    private final float[] mTempMatrix = new float[32];
    private final float[] mTempColor = new float[4];
    private final RectF mTempSourceRect = new RectF();
    private final RectF mTempTargetRect = new RectF();
    private final float[] mTempTextureMatrix = new float[MATRIX_SIZE];
    private final int[] mTempIntArray = new int[1];

    private static final GLId mGLId = new GLES20IdImpl();

    public GLES20Canvas() {
        Matrix.setIdentityM(mTempTextureMatrix, 0);
        Matrix.setIdentityM(mMatrices, mCurrentMatrixIndex);
        mAlphas[mCurrentAlphaIndex] = 1f;
        mTargetTextures.add(null);

        FloatBuffer boxBuffer = createBuffer(BOX_COORDINATES);
        mBoxCoordinates = uploadBuffer(boxBuffer);


        mDrawProgram = assembleProgram(loadShader(GLES20.GL_VERTEX_SHADER, BasicDrawShapeFilter.DRAW_VERTEX_SHADER), loadShader(GLES20.GL_FRAGMENT_SHADER, BasicDrawShapeFilter.DRAW_FRAGMENT_SHADER), mDrawParameters, mTempIntArray);

        int textureFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, BasicTextureFilter.TEXTURE_FRAGMENT_SHADER);
        int meshVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, MESH_VERTEX_SHADER);
        setupMeshProgram(meshVertexShader, textureFragmentShader);

        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        checkError();
    }

    private void setupMeshProgram(int meshVertexShader, int textureFragmentShader) {
        mMeshProgram = assembleProgram(meshVertexShader, textureFragmentShader, mMeshParameters, mTempIntArray);
    }

    private static FloatBuffer createBuffer(float[] values) {
        // First create an nio buffer, then create a VBO from it.
        int size = values.length * FLOAT_SIZE;
        FloatBuffer buffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer.put(values, 0, values.length).position(0);
        return buffer;
    }


    private static int assembleProgram(int vertexShader, int fragmentShader, ShaderParameter[] params, int[] linkStatus) {
        int program = GLES20.glCreateProgram();
        checkError();
        if (program == 0) {
            throw new RuntimeException("Cannot create GL program: " + GLES20.glGetError());
        }
        GLES20.glAttachShader(program, vertexShader);
        checkError();
        GLES20.glAttachShader(program, fragmentShader);
        checkError();
        GLES20.glLinkProgram(program);
        checkError();
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Could not link program: ");
            Log.e(TAG, GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        loadHandles(params, program);
        return program;
    }

    private static void loadHandles(ShaderParameter[] params, int program) {
        for (int i = 0; i < params.length; i++) {
            params[i].loadHandle(program);
        }
    }

    private static int loadShader(int type, String shaderCode) {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        checkError();
        GLES20.glCompileShader(shader);
        checkError();

        return shader;
    }

    @Override
    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
        checkError();
        Matrix.setIdentityM(mMatrices, mCurrentMatrixIndex);
        Matrix.orthoM(mProjectionMatrix, 0, 0, width, 0, height, -1, 1);
        if (getTargetTexture() == null) {
            mScreenWidth = width;
            mScreenHeight = height;
            Matrix.translateM(mMatrices, mCurrentMatrixIndex, 0, height, 0);
            Matrix.scaleM(mMatrices, mCurrentMatrixIndex, 1, -1, 1);
        }
    }

    @Override
    public void clearBuffer() {
        GLES20.glClearColor(0f, 0f, 0f, 0f);
        checkError();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        checkError();
    }

    @Override
    public void clearBuffer(float[] argb) {
        GLES20.glClearColor(argb[1], argb[2], argb[3], argb[0]);
        checkError();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        checkError();
    }

    @Override
    public float getAlpha() {
        return mAlphas[mCurrentAlphaIndex];
    }

    @Override
    public void setAlpha(float alpha) {
        mAlphas[mCurrentAlphaIndex] = alpha;
    }

    @Override
    public void multiplyAlpha(float alpha) {
        setAlpha(getAlpha() * alpha);
    }

    @Override
    public void translate(float x, float y, float z) {
        Matrix.translateM(mMatrices, mCurrentMatrixIndex, x, y, z);
    }


    // This is a faster version of translate(x, y, z) because
    // (1) we knows z = 0, (2) we inline the Matrix.translateM call,
    // (3) we unroll the loop
    @Override
    public void translate(float x, float y) {
        int index = mCurrentMatrixIndex;
        float[] m = mMatrices;
        m[index + 12] += m[index + 0] * x + m[index + 4] * y;
        m[index + 13] += m[index + 1] * x + m[index + 5] * y;
        m[index + 14] += m[index + 2] * x + m[index + 6] * y;
        m[index + 15] += m[index + 3] * x + m[index + 7] * y;
    }

    @Override
    public void scale(float sx, float sy, float sz) {
        Matrix.scaleM(mMatrices, mCurrentMatrixIndex, sx, sy, sz);
    }

    @Override
    public void rotate(float angle, float x, float y, float z) {
        if (angle == 0f) {
            return;
        }
        float[] temp = mTempMatrix;
        Matrix.setRotateM(temp, 0, angle, x, y, z);
        float[] matrix = mMatrices;
        int index = mCurrentMatrixIndex;
        Matrix.multiplyMM(temp, MATRIX_SIZE, matrix, index, temp, 0);
        System.arraycopy(temp, MATRIX_SIZE, matrix, index, MATRIX_SIZE);
    }


    @Override
    public void multiplyMatrix(float[] matrix, int offset) {
        float[] temp = mTempMatrix;
        float[] currentMatrix = mMatrices;
        int index = mCurrentMatrixIndex;
        Matrix.multiplyMM(temp, 0, currentMatrix, index, matrix, offset);
        System.arraycopy(temp, 0, currentMatrix, index, 16);
    }

    @Override
    public void save() {
        save(SAVE_FLAG_ALL);
    }

    @Override
    public void save(int saveFlags) {
        boolean saveAlpha = (saveFlags & SAVE_FLAG_ALPHA) == SAVE_FLAG_ALPHA;
        if (saveAlpha) {
            float currentAlpha = getAlpha();
            mCurrentAlphaIndex++;
            if (mAlphas.length <= mCurrentAlphaIndex) {
                mAlphas = Arrays.copyOf(mAlphas, mAlphas.length * 2);
            }
            mAlphas[mCurrentAlphaIndex] = currentAlpha;
        }
        boolean saveMatrix = (saveFlags & SAVE_FLAG_MATRIX) == SAVE_FLAG_MATRIX;
        if (saveMatrix) {
            int currentIndex = mCurrentMatrixIndex;
            mCurrentMatrixIndex += MATRIX_SIZE;
            if (mMatrices.length <= mCurrentMatrixIndex) {
                mMatrices = Arrays.copyOf(mMatrices, mMatrices.length * 2);
            }
            System.arraycopy(mMatrices, currentIndex, mMatrices, mCurrentMatrixIndex, MATRIX_SIZE);
        }
        mSaveFlags.add(saveFlags);
    }

    @Override
    public void restore() {
        int restoreFlags = mSaveFlags.removeLast();
        boolean restoreAlpha = (restoreFlags & SAVE_FLAG_ALPHA) == SAVE_FLAG_ALPHA;
        if (restoreAlpha) {
            mCurrentAlphaIndex--;
        }
        boolean restoreMatrix = (restoreFlags & SAVE_FLAG_MATRIX) == SAVE_FLAG_MATRIX;
        if (restoreMatrix) {
            mCurrentMatrixIndex -= MATRIX_SIZE;
        }
    }

    @Override
    public void drawCircle(float x, float y, float radius, GLPaint paint, DrawShapeFilter drawShapeFilter) {
        setupDrawShapeFilter(drawShapeFilter);
        draw(GLES20.GL_TRIANGLE_STRIP, OFFSET_FILL_RECT, COUNT_FILL_VERTEX, x, y, 2*radius, 2*radius, paint.getColor(), 0f);
    }

    @Override
    public void drawLine(float x1, float y1, float x2, float y2, GLPaint paint, DrawShapeFilter drawShapeFilter) {
        setupDrawShapeFilter(drawShapeFilter);
        draw(GLES20.GL_LINE_STRIP, OFFSET_DRAW_LINE, COUNT_LINE_VERTEX, x1, y1, x2 - x1, y2 - y1,
                paint);
        mCountDrawLine++;
    }

    @Override
    public void drawRect(float x, float y, float width, float height, GLPaint paint, DrawShapeFilter drawShapeFilter) {
        setupDrawShapeFilter(drawShapeFilter);
        draw(GLES20.GL_LINE_LOOP, OFFSET_DRAW_RECT, COUNT_RECT_VERTEX, x, y, width, height, paint);
        mCountDrawLine++;
    }

    private void draw(int type, int offset, int count, float x, float y, float width, float height,
                      GLPaint paint) {
        draw(type, offset, count, x, y, width, height, paint.getColor(), paint.getLineWidth());
    }

    private void draw(int type, int offset, int count, float x, float y, float width, float height,
                      int color, float lineWidth) {
        prepareDraw(offset, color, lineWidth);
        if (onPreDrawShapeListener != null) {
            onPreDrawShapeListener.onPreDraw(mDrawProgram, mDrawShapeFilter);
        }
        draw(mDrawParameters, type, count, x, y, width, height, null);
    }

    private void prepareDraw(int offset, int color, float lineWidth) {
        GLES20.glUseProgram(mDrawProgram);
        checkError();
        if (lineWidth > 0) {
            GLES20.glLineWidth(lineWidth);
            checkError();
        }
        float[] colorArray = getColor(color);
        enableBlending(true);
        GLES20.glBlendColor(colorArray[0], colorArray[1], colorArray[2], colorArray[3]);
        checkError();

        GLES20.glUniform4fv(mDrawParameters[INDEX_COLOR].handle, 1, colorArray, 0);
        setPosition(mDrawParameters, offset);
        checkError();
    }

    private float[] getColor(int color) {
        float alpha = ((color >>> 24) & 0xFF) / 255f * getAlpha();
        float red = ((color >>> 16) & 0xFF) / 255f * alpha;
        float green = ((color >>> 8) & 0xFF) / 255f * alpha;
        float blue = (color & 0xFF) / 255f * alpha;
        mTempColor[0] = red;
        mTempColor[1] = green;
        mTempColor[2] = blue;
        mTempColor[3] = alpha;
        return mTempColor;
    }

    private static void enableBlending(boolean enableBlending) {
        if (enableBlending) {
            GLES20.glEnable(GLES20.GL_BLEND);
            checkError();
        } else {
            GLES20.glDisable(GLES20.GL_BLEND);
            checkError();
        }
    }

    private void setPosition(ShaderParameter[] params, int offset) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBoxCoordinates);
        checkError();
        GLES20.glVertexAttribPointer(params[INDEX_POSITION].handle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, VERTEX_STRIDE, offset * VERTEX_STRIDE);
        checkError();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        checkError();
    }

    private void draw(ShaderParameter[] params, int type, int count, float x, float y, float width,
                      float height, ICustomMVPMatrix customMVPMatrix) {
        setMatrix(params, x, y, width, height, customMVPMatrix);
        int positionHandle = params[INDEX_POSITION].handle;
        GLES20.glEnableVertexAttribArray(positionHandle);
        checkError();
        GLES20.glDrawArrays(type, 0, count);
        checkError();
        GLES20.glDisableVertexAttribArray(positionHandle);
        checkError();
    }

    private void setMatrix(ShaderParameter[] params, float x, float y, float width, float height, ICustomMVPMatrix customMVPMatrix) {
        if (customMVPMatrix != null) {
            GLES20.glUniformMatrix4fv(params[INDEX_MATRIX].handle, 1, false, customMVPMatrix.getMVPMatrix(mScreenWidth, mScreenHeight, x, y, width, height), 0);
            checkError();
            return;
        }
        GLES20.glViewport(0, 0, mScreenWidth, mScreenHeight);
        Matrix.translateM(mTempMatrix, 0, mMatrices, mCurrentMatrixIndex, x, y, 0f);
        Matrix.scaleM(mTempMatrix, 0, width, height, 1f);
        Matrix.multiplyMM(mTempMatrix, MATRIX_SIZE, mProjectionMatrix, 0, mTempMatrix, 0);
        printMatrix("translate matrix:", mTempMatrix, MATRIX_SIZE);
        GLES20.glUniformMatrix4fv(params[INDEX_MATRIX].handle, 1, false, mTempMatrix, MATRIX_SIZE);
        checkError();
    }

    @Override
    public void fillRect(float x, float y, float width, float height, int color, DrawShapeFilter drawShapeFilter) {
        setupDrawShapeFilter(drawShapeFilter);
        draw(GLES20.GL_TRIANGLE_STRIP, OFFSET_FILL_RECT, COUNT_FILL_VERTEX, x, y, width, height,
                color, 0f);
        mCountFillRect++;
    }

    @Override
    public void drawTexture(BasicTexture texture, int x, int y, int width, int height, TextureFilter textureFilter, ICustomMVPMatrix customMVPMatrix) {
        if (width <= 0 || height <= 0) {
            return;
        }
        setupTextureFilter(texture.getTarget(), textureFilter);
        TextureMatrixTransformer.copyTextureCoordinates(texture, mTempSourceRect);
        mTempTargetRect.set(x, y, x + width, y + height);
        TextureMatrixTransformer.convertCoordinate(mTempSourceRect, texture);
        changeTargetIfNeeded(mTempSourceRect, mTempTargetRect, texture);
        drawTextureRect(texture, mTempSourceRect, mTempTargetRect, customMVPMatrix);
    }

    @Override
    public void drawTexture(BasicTexture texture, RectF source, RectF target, TextureFilter textureFilter, ICustomMVPMatrix customMVPMatrix) {
        if (target.width() <= 0 || target.height() <= 0) {
            return;
        }
        setupTextureFilter(texture.getTarget(), textureFilter);
        mTempSourceRect.set(source);
        mTempTargetRect.set(target);

        TextureMatrixTransformer.convertCoordinate(mTempSourceRect, texture);
        changeTargetIfNeeded(mTempSourceRect, mTempTargetRect, texture);
        drawTextureRect(texture, mTempSourceRect, mTempTargetRect, customMVPMatrix);
    }

    @Override
    public void drawTexture(BasicTexture texture, float[] textureTransform, int x, int y, int w,
                            int h, TextureFilter textureFilter, ICustomMVPMatrix customMVPMatrix) {
        if (w <= 0 || h <= 0) {
            return;
        }
        setupTextureFilter(texture.getTarget(), textureFilter);
        mTempTargetRect.set(x, y, x + w, y + h);
        drawTextureRect(texture, textureTransform, mTempTargetRect, customMVPMatrix);
    }

    private void drawTextureRect(BasicTexture texture, RectF source, RectF target, ICustomMVPMatrix customMVPMatrix) {
        TextureMatrixTransformer.setTextureMatrix(source, mTempTextureMatrix);
        drawTextureRect(texture, mTempTextureMatrix, target, customMVPMatrix);
    }

    private static void changeTargetIfNeeded(RectF source, RectF target, BasicTexture texture) {
        float yBound = (float) texture.getHeight() / texture.getTextureHeight();
        float xBound = (float) texture.getWidth() / texture.getTextureWidth();
        if (source.right > xBound) {
            target.right = target.left + target.width() * (xBound - source.left) / source.width();
        }
        if (source.bottom > yBound) {
            target.bottom = target.top + target.height() * (yBound - source.top) / source.height();
        }
    }

    private void drawTextureRect(BasicTexture texture, float[] textureMatrix, RectF target, ICustomMVPMatrix customMVPMatrix) {
        ShaderParameter[] params = prepareTexture(texture);
        setPosition(params, OFFSET_FILL_RECT);
//        printMatrix("texture matrix", textureMatrix, 0);
        GLES20.glUniformMatrix4fv(params[INDEX_TEXTURE_MATRIX].handle, 1, false, textureMatrix, 0);
        if (onPreDrawTextureListener != null) {
            onPreDrawTextureListener.onPreDraw(texture.getTarget() == GLES20.GL_TEXTURE_2D ? mTextureProgram : mOesTextureProgram, texture, mTextureFilter);
        }
        checkError();
        if (texture.isFlippedVertically()) {
            save(SAVE_FLAG_MATRIX);
            translate(0, target.centerY());
            scale(1, -1, 1);
            translate(0, -target.centerY());
        }
        draw(params, GLES20.GL_TRIANGLE_STRIP, COUNT_FILL_VERTEX, target.left, target.top,
                target.width(), target.height(), customMVPMatrix);
        if (texture.isFlippedVertically()) {
            restore();
        }
        mCountTextureRect++;
    }

    private ShaderParameter[] prepareTexture(BasicTexture texture) {
        ShaderParameter[] params;
        int program;
        if (texture.getTarget() == GLES20.GL_TEXTURE_2D) {
            params = mTextureParameters;
            program = mTextureProgram;
        } else {
            params = mOesTextureParameters;
            program = mOesTextureProgram;
        }
        prepareTexture(texture, program, params);
        return params;
    }

    private void prepareTexture(BasicTexture texture, int program, ShaderParameter[] params) {
        GLES20.glUseProgram(program);
        checkError();
        enableBlending(!texture.isOpaque() || getAlpha() < OPAQUE_ALPHA);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        checkError();
        texture.onBind(this);
        GLES20.glBindTexture(texture.getTarget(), texture.getId());
        checkError();
        GLES20.glUniform1i(params[INDEX_TEXTURE_SAMPLER].handle, 0);
        checkError();
        GLES20.glUniform1f(params[INDEX_ALPHA].handle, getAlpha());
        checkError();
    }

    @Override
    public void drawMesh(BasicTexture texture, int x, int y, int xyBuffer, int uvBuffer,
                         int indexBuffer, int indexCount, int mode) {
        prepareTexture(texture, mMeshProgram, mMeshParameters);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        checkError();

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, xyBuffer);
        checkError();
        int positionHandle = mMeshParameters[INDEX_POSITION].handle;
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                VERTEX_STRIDE, 0);
        checkError();

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, uvBuffer);
        checkError();
        int texCoordHandle = mMeshParameters[INDEX_TEXTURE_COORD].handle;
        GLES20.glVertexAttribPointer(texCoordHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, VERTEX_STRIDE, 0);
        checkError();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        checkError();

        GLES20.glEnableVertexAttribArray(positionHandle);
        checkError();
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        checkError();

        setMatrix(mMeshParameters, x, y, 1, 1, null);
        GLES20.glDrawElements(mode, indexCount, GLES20.GL_UNSIGNED_BYTE, 0);
        checkError();

        GLES20.glDisableVertexAttribArray(positionHandle);
        checkError();
        GLES20.glDisableVertexAttribArray(texCoordHandle);
        checkError();
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        checkError();
        mCountDrawMesh++;
    }

    @Override
    public void drawMixed(BasicTexture texture, int toColor, float ratio, int x, int y, int w, int h, DrawShapeFilter drawShapeFilter) {
        TextureMatrixTransformer.copyTextureCoordinates(texture, mTempSourceRect);
        mTempTargetRect.set(x, y, x + w, y + h);
        drawMixed(texture, toColor, ratio, mTempSourceRect, mTempTargetRect, drawShapeFilter);
    }

    @Override
    public void drawMixed(BasicTexture texture, int toColor, float ratio, RectF source, RectF target, DrawShapeFilter drawShapeFilter) {
        if (target.width() <= 0 || target.height() <= 0) {
            return;
        }
        save(SAVE_FLAG_ALPHA);

        float currentAlpha = getAlpha();
        float cappedRatio = Math.min(1f, Math.max(0f, ratio));

        float textureAlpha = (1f - cappedRatio) * currentAlpha;
        setAlpha(textureAlpha);
        drawTexture(texture, source, target, new BasicTextureFilter(), null);

        float colorAlpha = cappedRatio * currentAlpha;
        setAlpha(colorAlpha);
        fillRect(target.left, target.top, target.width(), target.height(), toColor, drawShapeFilter);

        restore();
    }

    @Override
    public boolean unloadTexture(BasicTexture texture) {
        boolean unload = texture.isLoaded();
        if (unload) {
            synchronized (mUnboundTextures) {
                mUnboundTextures.add(texture.getId());
            }
        }
        return unload;
    }

    @Override
    public void deleteBuffer(int bufferId) {
        synchronized (mUnboundTextures) {
            mDeleteBuffers.add(bufferId);
        }
    }

    @Override
    public void deleteRecycledResources() {
        synchronized (mUnboundTextures) {
            IntArray ids = mUnboundTextures;
            if (mUnboundTextures.size() > 0) {
                mGLId.glDeleteTextures(ids.size(), ids.getInternalArray(), 0);
                ids.clear();
            }

            ids = mDeleteBuffers;
            if (ids.size() > 0) {
                mGLId.glDeleteBuffers(ids.size(), ids.getInternalArray(), 0);
                ids.clear();
            }
        }
    }

    @Override
    public void dumpStatisticsAndClear() {
        String line = String.format("MESH:%d, TEX_RECT:%d, FILL_RECT:%d, LINE:%d", mCountDrawMesh,
                mCountTextureRect, mCountFillRect, mCountDrawLine);
        mCountDrawMesh = 0;
        mCountTextureRect = 0;
        mCountFillRect = 0;
        mCountDrawLine = 0;
        Log.d(TAG, line);
    }

    @Override
    public void endRenderTarget() {
        RawTexture oldTexture = mTargetTextures.remove(mTargetTextures.size() - 1);
        RawTexture texture = getTargetTexture();
        setRenderTarget(oldTexture, texture);
        restore(); // restore matrix and alpha
    }

    @Override
    public void beginRenderTarget(RawTexture texture) {
        save(); // save matrix and alpha and blending
        RawTexture oldTexture = getTargetTexture();
        mTargetTextures.add(texture);
        setRenderTarget(oldTexture, texture);
    }

    private RawTexture getTargetTexture() {
        return mTargetTextures.get(mTargetTextures.size() - 1);
    }

    private void setRenderTarget(BasicTexture oldTexture, RawTexture texture) {
        // FIXME: 2016/11/18 If client version is 2, then bufferOES will crash...
        if (oldTexture == null && texture != null) {
            if (texture.getTarget() == GLES20.GL_TEXTURE_2D) {
                GLES20.glGenFramebuffers(1, mFrameBuffer, 0);
                checkError();
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);
                checkError();
            } else {
                GLES11Ext.glGenFramebuffersOES(1, mFrameBuffer, 0);
                checkError();
                GLES11Ext.glBindFramebufferOES(GLES11Ext.GL_FRAMEBUFFER_OES, mFrameBuffer[0]);
                checkError();
            }
        } else if (oldTexture != null && texture == null) {
            if (oldTexture.getTarget() == GLES20.GL_TEXTURE_2D) {
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                checkError();
                GLES20.glDeleteFramebuffers(1, mFrameBuffer, 0);
                checkError();
            } else {
                GLES11Ext.glBindFramebufferOES(GLES11Ext.GL_FRAMEBUFFER_OES, 0);
                checkError();
                GLES11Ext.glDeleteFramebuffersOES(1, mFrameBuffer, 0);
                checkError();
            }
        }

        if (texture == null) {
            setSize(mScreenWidth, mScreenHeight);
        } else {
            setSize(texture.getWidth(), texture.getHeight());

            if (!texture.isLoaded()) {
                texture.prepare(this);
            }

            if (texture.getTarget() == GLES20.GL_TEXTURE_2D) {
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                        texture.getTarget(), texture.getId(), 0);
                checkError();
                checkFramebufferStatus();
            } else {
                GLES11Ext.glFramebufferTexture2DOES(GLES11Ext.GL_FRAMEBUFFER_OES, GLES11Ext.GL_COLOR_ATTACHMENT0_OES,
                        texture.getTarget(), texture.getId(), 0);
                checkError();
                checkFramebufferStatusOes();
            }

        }
    }

    private static void checkFramebufferStatus() {
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            String msg = "";
            switch (status) {
                case GLES20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                    msg = "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT";
                    break;
                case GLES20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
                    msg = "GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS";
                    break;
                case GLES20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                    msg = "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT";
                    break;
                case GLES20.GL_FRAMEBUFFER_UNSUPPORTED:
                    msg = "GL_FRAMEBUFFER_UNSUPPORTED";
                    break;
            }
            throw new RuntimeException(msg + ":" + Integer.toHexString(status));
        }
    }


    private static void checkFramebufferStatusOes() {
        int status = GLES11Ext.glCheckFramebufferStatusOES(GLES11Ext.GL_FRAMEBUFFER_OES);
        if (status != GLES11Ext.GL_FRAMEBUFFER_COMPLETE_OES) {
            String msg = "";
            switch (status) {
                case GLES11Ext.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_OES:
                    msg = "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT";
                    break;
                case GLES11Ext.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_OES:
                    msg = "GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS";
                    break;
                case GLES11Ext.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_OES:
                    msg = "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT";
                    break;
                case GLES11Ext.GL_FRAMEBUFFER_UNSUPPORTED_OES:
                    msg = "GL_FRAMEBUFFER_UNSUPPORTED";
                    break;
            }
            throw new RuntimeException(msg + ":" + Integer.toHexString(status));
        }
    }

    @Override
    public void setTextureParameters(BasicTexture texture) {
        int target = texture.getTarget();
        GLES20.glBindTexture(target, texture.getId());
        checkError();
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
    }

    @Override
    public void initializeTextureSize(BasicTexture texture, int format, int type) {
        int target = texture.getTarget();
        GLES20.glBindTexture(target, texture.getId());
        checkError();
        int width = texture.getTextureWidth();
        int height = texture.getTextureHeight();
        GLES20.glTexImage2D(target, 0, format, width, height, 0, format, type, null);
    }

    @Override
    public void initializeTexture(BasicTexture texture, Bitmap bitmap) {
        int target = texture.getTarget();
        GLES20.glBindTexture(target, texture.getId());
        checkError();
        GLUtils.texImage2D(target, 0, bitmap, 0);
    }

    @Override
    public void texSubImage2D(BasicTexture texture, int xOffset, int yOffset, Bitmap bitmap,
                              int format, int type) {
        int target = texture.getTarget();
        GLES20.glBindTexture(target, texture.getId());
        checkError();
        GLUtils.texSubImage2D(target, 0, xOffset, yOffset, bitmap, format, type);
    }

    @Override
    public int uploadBuffer(FloatBuffer buf) {
        return uploadBuffer(buf, FLOAT_SIZE);
    }

    @Override
    public int uploadBuffer(ByteBuffer buf) {
        return uploadBuffer(buf, 1);
    }

    private int uploadBuffer(Buffer buffer, int elementSize) {
        mGLId.glGenBuffers(1, mTempIntArray, 0);
        checkError();
        int bufferId = mTempIntArray[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferId);
        checkError();
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, buffer.capacity() * elementSize, buffer,
                GLES20.GL_STATIC_DRAW);
        checkError();
        return bufferId;
    }

    public static void checkError() {
        int error = GLES20.glGetError();
        if (error != 0) {
            Throwable t = new Throwable();
            Log.e(TAG, "GL error: " + error, t);
        }
    }

    @SuppressWarnings("unused")
    public static void printMatrix(String message, float[] m, int offset) {
        if (!Loggers.DEBUG) {
            return;
        }
        StringBuilder b = new StringBuilder(message);
        b.append('\n');
        int size = 4;
        for (int i = 0; i < size; i++) {
            String format = "%.6f";
            b.append(String.format(format, m[offset + i]));
            b.append(", ");
            b.append(String.format(format, m[offset + 4+i]));
            b.append(", ");
            b.append(String.format(format, m[offset + 8+i]));
            b.append(", ");
            b.append(String.format(format, m[offset + 12+i]));
            if (i<size-1) {
                b.append(", ");
            }
            b.append('\n');
        }
        Loggers.v(TAG, b.toString());
    }

    @Override
    public void recoverFromLightCycle() {
        // FIXME: 2017/8/20 
//        GLES20.glViewport(0, 0, mWidth, mHeight);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        checkError();
    }

    @Override
    public void getBounds(Rect bounds, int x, int y, int width, int height) {
        Matrix.translateM(mTempMatrix, 0, mMatrices, mCurrentMatrixIndex, x, y, 0f);
        Matrix.scaleM(mTempMatrix, 0, width, height, 1f);
        Matrix.multiplyMV(mTempMatrix, MATRIX_SIZE, mTempMatrix, 0, BOUNDS_COORDINATES, 0);
        Matrix.multiplyMV(mTempMatrix, MATRIX_SIZE + 4, mTempMatrix, 0, BOUNDS_COORDINATES, 4);
        bounds.left = Math.round(mTempMatrix[MATRIX_SIZE]);
        bounds.right = Math.round(mTempMatrix[MATRIX_SIZE + 4]);
        bounds.top = Math.round(mTempMatrix[MATRIX_SIZE + 1]);
        bounds.bottom = Math.round(mTempMatrix[MATRIX_SIZE + 5]);
        bounds.sort();
    }

    @Override
    public GLId getGLId() {
        return mGLId;
    }

    private void setupDrawShapeFilter(DrawShapeFilter drawShapeFilter) {
        if (drawShapeFilter == null) {
            throw new NullPointerException("draw shape filter is null.");
        }
        mDrawShapeFilter = drawShapeFilter;
        if (mDrawShapeFilterMapProgramId.containsKey(drawShapeFilter)) {
            mDrawProgram = mDrawShapeFilterMapProgramId.get(drawShapeFilter);
            loadHandles(mDrawParameters, mDrawProgram);
            return;
        }
        mDrawProgram = loadAndAssemble(mDrawParameters, drawShapeFilter.getVertexShader(), drawShapeFilter.getFragmentShader());
        mDrawShapeFilterMapProgramId.put(drawShapeFilter, mDrawProgram);
    }

    private void setupTextureFilter(int target, TextureFilter textureFilter) {
        if (textureFilter == null) {
            throw new NullPointerException("Texture filter is null.");
        }

        this.mTextureFilter = textureFilter;
        if (target == GLES20.GL_TEXTURE_2D) {
            if (mTextureFilterMapProgramId.containsKey(textureFilter)) {
                mTextureProgram = mTextureFilterMapProgramId.get(textureFilter);
                loadHandles(mTextureParameters, mTextureProgram);
                return;
            }
            mTextureProgram = loadAndAssemble(mTextureParameters, textureFilter.getVertexShader(), textureFilter.getFragmentShader());
            mTextureFilterMapProgramId.put(textureFilter, mTextureProgram);
        } else {
            if (mOESTextureFilterMapProgramId.containsKey(textureFilter)) {
                mOesTextureProgram = mOESTextureFilterMapProgramId.get(textureFilter);
                loadHandles(mOesTextureParameters, mOesTextureProgram);
                return;
            }
            mOesTextureProgram = loadAndAssemble(mOesTextureParameters, textureFilter.getVertexShader(), textureFilter.getOesFragmentProgram());
            mOESTextureFilterMapProgramId.put(textureFilter, mOesTextureProgram);
        }

    }

    private int loadAndAssemble(ShaderParameter[] shaderParameters, String vertexProgram, String fragmentProgram) {
        int vertexShaderHandle = loadShader(GLES20.GL_VERTEX_SHADER, vertexProgram);
        int fragmentShaderHandle = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentProgram);
        return assembleProgram(vertexShaderHandle, fragmentShaderHandle, shaderParameters, mTempIntArray);
    }


    @Override
    public void setOnPreDrawTextureListener(OnPreDrawTextureListener l) {
        this.onPreDrawTextureListener = l;
    }


    @Override
    public void setOnPreDrawShapeListener(OnPreDrawShapeListener l) {
        this.onPreDrawShapeListener = l;
    }

}
