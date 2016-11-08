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
 * Created by Chilling on 2016/11/1.
 */

public class PixelationFilter extends BasicTextureFilter implements OneValueFilter {

    public static final String UNIFORM_IMAGE_WIDTH_FACTOR = "imageWidthFactor";
    public static final String UNIFORM_IMAGE_HEIGHT_FACTOR = "imageHeightFactor";
    public static final String UNIFORM_PIXEL = "pixel";
    public static final String PIXELATION_FRAGMENT_SHADER = "" +
            "precision highp float;\n" +

            " varying vec2 " + VARYING_TEXTURE_COORD + ";\n" +
            "uniform float " + UNIFORM_IMAGE_WIDTH_FACTOR + ";\n" +
            "uniform float " + UNIFORM_IMAGE_HEIGHT_FACTOR + ";\n" +
            " uniform float " + ALPHA_UNIFORM + ";\n" +
            "uniform sampler2D " + TEXTURE_SAMPLER_UNIFORM + ";\n" +
            "uniform float " + UNIFORM_PIXEL + ";\n" +
            "void main() {\n" +
            "" +
            "  vec2 uv  = " + VARYING_TEXTURE_COORD + ".xy;\n" +
            "  float dx = " + UNIFORM_PIXEL + " * " + UNIFORM_IMAGE_WIDTH_FACTOR + ";\n" +
            "  float dy = " + UNIFORM_PIXEL + " * " + UNIFORM_IMAGE_HEIGHT_FACTOR + ";\n" +
            "  vec2 coord = vec2(dx * floor(uv.x / dx), dy * floor(uv.y / dy));\n" +
            "  vec4 tc = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", coord);\n" +
            "  gl_FragColor = vec4(tc);\n" +
            "    gl_FragColor *= " + ALPHA_UNIFORM + ";\n" +
            "}";

    private int mImageWidthFactorLocation;
    private int mImageHeightFactorLocation;
    private int mPixelLocation;
    private float mPixel;

    public PixelationFilter(@FloatRange(from = 1, to = 100) float pixel) {
        this.mPixel = pixel;
    }

    @Override
    public String getFragmentShader() {
        return PIXELATION_FRAGMENT_SHADER;
    }

    @Override
    public void onPreDraw(int program, BasicTexture texture, ICanvasGL canvas) {
        super.onPreDraw(program, texture, canvas);
        mImageWidthFactorLocation = GLES20.glGetUniformLocation(program, UNIFORM_IMAGE_WIDTH_FACTOR);
        mImageHeightFactorLocation = GLES20.glGetUniformLocation(program, UNIFORM_IMAGE_HEIGHT_FACTOR);
        mPixelLocation = GLES20.glGetUniformLocation(program, UNIFORM_PIXEL);

        OpenGLUtil.setFloat(mImageWidthFactorLocation, 1.0f / texture.getWidth());
        OpenGLUtil.setFloat(mImageHeightFactorLocation, 1.0f / texture.getHeight());
        OpenGLUtil.setFloat(mPixelLocation, mPixel);
    }

    @Override
    public void setValue(@FloatRange(from = 1, to = 100) final float pixel) {
        mPixel = pixel;
    }
}
