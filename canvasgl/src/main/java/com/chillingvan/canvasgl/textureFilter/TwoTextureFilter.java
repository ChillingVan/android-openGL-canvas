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

package com.chillingvan.canvasgl.textureFilter;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.BasicTexture;
import com.chillingvan.canvasgl.glcanvas.GLES20Canvas;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glcanvas.TextureMatrixTransformer;
import com.chillingvan.canvasgl.glview.texture.GLTexture;

import java.util.Arrays;

import androidx.annotation.NonNull;

import static com.chillingvan.canvasgl.ICanvasGL.BitmapMatrix.MATRIX_SIZE;

/**
 * A filter that supports mix two textures.
 * This accept the second texture as a RawTexture or a Bitmap
 */
public abstract class TwoTextureFilter extends BasicTextureFilter {


    public static final String VARYING_TEXTURE_COORD2 = "vTextureCoord2";
    public static final String UNIFORM_TEXTURE_SAMPLER2 = "uTextureSampler2";
    private static final String TEXTURE_MATRIX_UNIFORM2 = "uTextureMatrix2";

    private static final String VERTEX_SHADER =
            " \n" +
                    "attribute vec2 " + POSITION_ATTRIBUTE + ";\n" +
                    "varying vec2 " + VARYING_TEXTURE_COORD + ";\n" +
                    "varying vec2 " + VARYING_TEXTURE_COORD2 + ";\n" +
                    "uniform mat4 " + MATRIX_UNIFORM + ";\n" +
                    "uniform mat4 " + TEXTURE_MATRIX_UNIFORM + ";\n" +
                    "uniform mat4 " + TEXTURE_MATRIX_UNIFORM2 + ";\n" +
                    " \n" +
                    "void main() {\n" +
                    "  vec4 pos = vec4(" + POSITION_ATTRIBUTE + ", 0.0, 1.0);\n" +
                    "    gl_Position = " + MATRIX_UNIFORM + " * pos;\n" +
                    "    " + VARYING_TEXTURE_COORD + " = (" + TEXTURE_MATRIX_UNIFORM + " * pos).xy;\n" +
                    "    " + VARYING_TEXTURE_COORD2 + " = (" + TEXTURE_MATRIX_UNIFORM2 + " * pos).xy;\n" +
                    "}";

    protected final float[] mTempTextureMatrix = new float[MATRIX_SIZE];
    protected Bitmap secondBitmap;
    private RectF mTempSrcRectF = new RectF();
    protected GLTexture secondRawTexture;

    /**
     * Set the second bitmap. If this exists, the secondRawTexture will be ignored
     * @param secondBitmap The second bitmap that will be mixed
     */
    public TwoTextureFilter(@NonNull Bitmap secondBitmap) {
        this.secondBitmap = secondBitmap;
    }

    public TwoTextureFilter() {
    }

    /**
     * Set the second texture. If secondBitmap exists, this will be ignored.
     * @param secondRawTexture The second texture. If secondBitmap exists, this will be ignored
     */
    public void setSecondRawTexture(GLTexture secondRawTexture) {
        this.secondRawTexture = secondRawTexture;
    }

    public void setBitmap(@NonNull Bitmap secondBitmap) {
        this.secondBitmap = secondBitmap;
    }

    @Override
    public String getVertexShader() {
        return VERTEX_SHADER;
    }

    private void resetMatrix() {
        Arrays.fill(mTempTextureMatrix, 0);
    }

    @Override
    public void onPreDraw(int program, BasicTexture texture, ICanvasGL canvas) {
        super.onPreDraw(program, texture, canvas);
        if (useSecondBitmap()) {
            handleSecondBitmapTexture(program, canvas);
            return;
        }

        if (secondRawTexture != null) {
            handleSecondRawTexture(program, canvas);
        }
    }

    private void handleSecondBitmapTexture(int program, ICanvasGL canvas) {
        BasicTexture secondTexture = canvas.bindBitmapToTexture(GLES20.GL_TEXTURE3, secondBitmap);

        resetMatrix();
        Matrix.setIdentityM(mTempTextureMatrix, 0);
        TextureMatrixTransformer.copyTextureCoordinates(secondTexture, mTempSrcRectF);
        TextureMatrixTransformer.convertCoordinate(mTempSrcRectF, secondTexture);
        TextureMatrixTransformer.setTextureMatrix(mTempSrcRectF, mTempTextureMatrix);

        GLES20Canvas.printMatrix("two tex matrix", mTempTextureMatrix, 0);
        int textureMatrixPosition = GLES20.glGetUniformLocation(program, TEXTURE_MATRIX_UNIFORM2);
        GLES20.glUniformMatrix4fv(textureMatrixPosition, 1, false, mTempTextureMatrix, 0);

        int sampler2 = GLES20.glGetUniformLocation(program, UNIFORM_TEXTURE_SAMPLER2);
        GLES20Canvas.checkError();
        GLES20.glUniform1i(sampler2, 3); // match GL_TEXTURE3
        GLES20Canvas.checkError();
    }

    private void handleSecondRawTexture(int program, ICanvasGL canvas) {
        RawTexture rawTexture = secondRawTexture.getRawTexture();
        canvas.bindRawTexture(GLES20.GL_TEXTURE3, rawTexture);

        resetMatrix();
        secondRawTexture.getSurfaceTexture().getTransformMatrix(mTempTextureMatrix);
        int textureMatrixPosition = GLES20.glGetUniformLocation(program, TEXTURE_MATRIX_UNIFORM2);
        GLES20.glUniformMatrix4fv(textureMatrixPosition, 1, false, mTempTextureMatrix, 0);

        int sampler2 = GLES20.glGetUniformLocation(program, UNIFORM_TEXTURE_SAMPLER2);
        GLES20Canvas.checkError();
        GLES20.glUniform1i(sampler2, 3);
        GLES20Canvas.checkError();
    }

    @Override
    public String getOesFragmentProgram() {
        if (useSecondBitmap()) {
            return "#extension GL_OES_EGL_image_external : require\n" + getFragmentShader().replaceFirst(SAMPLER_2D, SAMPLER_EXTERNAL_OES);
        }
        return "#extension GL_OES_EGL_image_external : require\n" + getFragmentShader().replaceAll(SAMPLER_2D, SAMPLER_EXTERNAL_OES);
    }

    private boolean useSecondBitmap() {
        return secondBitmap != null;
    }
}
