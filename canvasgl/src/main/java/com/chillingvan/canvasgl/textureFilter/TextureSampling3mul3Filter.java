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

import android.opengl.GLES20;
import android.support.annotation.FloatRange;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.OpenGLUtil;
import com.chillingvan.canvasgl.glcanvas.BasicTexture;

/**
 * Created by Chilling on 2016/11/6.
 */

public abstract class TextureSampling3mul3Filter extends BasicTextureFilter implements OneValueFilter {
    public static final String UNIFORM_TEXEL_WIDTH = "texelWidth";
    public static final String UNIFORM_TEXEL_HEIGHT = "texelHeight";
    public static final String VARYING_LEFT_TEXTURE_COORDINATE = "leftTextureCoordinate";
    public static final String VARYING_RIGHT_TEXTURE_COORDINATE = "rightTextureCoordinate";
    public static final String VARYING_TOP_TEXTURE_COORDINATE = "topTextureCoordinate";
    public static final String VARYING_TOP_LEFT_TEXTURE_COORDINATE = "topLeftTextureCoordinate";
    public static final String VARYING_TOP_RIGHT_TEXTURE_COORDINATE = "topRightTextureCoordinate";
    public static final String VARYING_BOTTOM_TEXTURE_COORDINATE = "bottomTextureCoordinate";
    public static final String VARYING_BOTTOM_LEFT_TEXTURE_COORDINATE = "bottomLeftTextureCoordinate";
    public static final String VARYING_BOTTOM_RIGHT_TEXTURE_COORDINATE = "bottomRightTextureCoordinate";
    public static final String THREE_X_THREE_TEXTURE_SAMPLING_VERTEX_SHADER = "" +
            "attribute vec2 " + POSITION_ATTRIBUTE + ";\n" +
            "\n" +
            "\n" +
            "uniform highp float " + UNIFORM_TEXEL_WIDTH + "; \n" +
            "uniform highp float " + UNIFORM_TEXEL_HEIGHT + "; \n" +
            "\n" +
            "varying vec2 " + VARYING_TEXTURE_COORD + ";\n" +
            "varying vec2 " + VARYING_LEFT_TEXTURE_COORDINATE + ";\n" +
            "varying vec2 " + VARYING_RIGHT_TEXTURE_COORDINATE + ";\n" +
            "\n" +
            "varying vec2 " + VARYING_TOP_TEXTURE_COORDINATE + ";\n" +
            "varying vec2 " + VARYING_TOP_LEFT_TEXTURE_COORDINATE + ";\n" +
            "varying vec2 " + VARYING_TOP_RIGHT_TEXTURE_COORDINATE + ";\n" +
            "\n" +
            "varying vec2 " + VARYING_BOTTOM_TEXTURE_COORDINATE + ";\n" +
            "varying vec2 " + VARYING_BOTTOM_LEFT_TEXTURE_COORDINATE + ";\n" +
            "varying vec2 " + VARYING_BOTTOM_RIGHT_TEXTURE_COORDINATE + ";\n" +
            "\n" +
            "uniform mat4 " + MATRIX_UNIFORM + ";\n" + "uniform mat4 " + TEXTURE_MATRIX_UNIFORM + ";\n" +             "void main() {\n" +
            "  vec4 pos = vec4(" + POSITION_ATTRIBUTE + ", 0.0, 1.0);\n" +             "" +
            "    gl_Position = " + MATRIX_UNIFORM + " * pos;\n" +
            "\n" +
            "    vec2 widthStep = vec2(" + UNIFORM_TEXEL_WIDTH + ", 0.0);\n" +
            "    vec2 heightStep = vec2(0.0, " + UNIFORM_TEXEL_HEIGHT + ");\n" +
            "    vec2 widthHeightStep = vec2(" + UNIFORM_TEXEL_WIDTH + ", " + UNIFORM_TEXEL_HEIGHT + ");\n" +
            "    vec2 widthNegativeHeightStep = vec2(" + UNIFORM_TEXEL_WIDTH + ", -" + UNIFORM_TEXEL_HEIGHT + ");\n" +
            "\n" +
            "    " + VARYING_TEXTURE_COORD + " = (" + TEXTURE_MATRIX_UNIFORM + " * pos).xy;\n" +
            "    " + VARYING_LEFT_TEXTURE_COORDINATE + " = " + VARYING_TEXTURE_COORD + ".xy - widthStep;\n" +
            "    " + VARYING_RIGHT_TEXTURE_COORDINATE + " = " + VARYING_TEXTURE_COORD + ".xy + widthStep;\n" +
            "\n" +
            "    " + VARYING_TOP_TEXTURE_COORDINATE + " = " + VARYING_TEXTURE_COORD + ".xy - heightStep;\n" +
            "    " + VARYING_TOP_LEFT_TEXTURE_COORDINATE + " = " + VARYING_TEXTURE_COORD + ".xy - widthHeightStep;\n" +
            "    " + VARYING_TOP_RIGHT_TEXTURE_COORDINATE + " = " + VARYING_TEXTURE_COORD + ".xy + widthNegativeHeightStep;\n" +
            "\n" +
            "    " + VARYING_BOTTOM_TEXTURE_COORDINATE + " = " + VARYING_TEXTURE_COORD + ".xy + heightStep;\n" +
            "    " + VARYING_BOTTOM_LEFT_TEXTURE_COORDINATE + " = " + VARYING_TEXTURE_COORD + ".xy - widthNegativeHeightStep;\n" +
            "    " + VARYING_BOTTOM_RIGHT_TEXTURE_COORDINATE + " = " + VARYING_TEXTURE_COORD + ".xy + widthHeightStep;\n" +
            "}";

    private int mUniformTexelWidthLocation;
    private int mUniformTexelHeightLocation;

    private boolean mHasOverriddenImageWidthFactor = false;
    private boolean mHasOverriddenImageHeightFactor = false;
    private float mTexelWidth;
    private float mTexelHeight;
    private float mLineSize = 1.0f;


    public TextureSampling3mul3Filter(@FloatRange(from = 0, to = 5) float lineSize) {
        this.mLineSize = lineSize;
    }

    @Override
    public String getVertexShader() {
        return THREE_X_THREE_TEXTURE_SAMPLING_VERTEX_SHADER;
    }

    public void setTexelWidth(final float texelWidth) {
        mHasOverriddenImageWidthFactor = true;
        mTexelWidth = texelWidth;
        setLineSize(mLineSize);
    }

    public void setTexelHeight(final float texelHeight) {
        mHasOverriddenImageHeightFactor = true;
        mTexelHeight = texelHeight;
        setLineSize(mLineSize);
    }

    public void setLineSize(@FloatRange(from = 0, to = 5) final float size) {
        mLineSize = size;
    }

    @Override
    public void onPreDraw(int program, BasicTexture texture, ICanvasGL canvas) {
        super.onPreDraw(program, texture, canvas);

        mTexelWidth = !mHasOverriddenImageWidthFactor ? mLineSize / texture.getWidth() : mTexelWidth;
        mTexelHeight = !mHasOverriddenImageHeightFactor ? mLineSize / texture.getHeight() : mTexelHeight;

        mUniformTexelWidthLocation = GLES20.glGetUniformLocation(program, UNIFORM_TEXEL_WIDTH);
        mUniformTexelHeightLocation = GLES20.glGetUniformLocation(program, UNIFORM_TEXEL_HEIGHT);
        if (mTexelWidth != 0) {
            OpenGLUtil.setFloat(mUniformTexelWidthLocation, mTexelWidth);
            OpenGLUtil.setFloat(mUniformTexelHeightLocation, mTexelHeight);
        }
    }

    @Override
    public void setValue(@FloatRange(from = 0, to = 5) float lineSize) {
        setLineSize(lineSize);
    }
}
