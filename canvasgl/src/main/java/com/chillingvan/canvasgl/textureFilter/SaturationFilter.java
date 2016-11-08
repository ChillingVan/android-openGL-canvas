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
 * Created by Chilling on 2016/10/23.
 */

/**
 * saturation: The degree of saturation or desaturation to apply to the image (0.0 - 2.0, with 1.0 as the default)
 */
public class SaturationFilter extends BasicTextureFilter implements OneValueFilter{

    public static final String UNIFORM_SATURATION = "saturation";
    public static final String SATURATION_FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
            " varying vec2 " + VARYING_TEXTURE_COORD + ";\n" +
            " \n" +
            " uniform sampler2D " + TEXTURE_SAMPLER_UNIFORM + ";\n" +
            " uniform float " + ALPHA_UNIFORM + ";\n" +
            " uniform float " + UNIFORM_SATURATION + ";\n" +
            " \n" +
            " // Values from \"Graphics Shaders: Theory and Practice\" by Bailey and Cunningham\n" +
            " const vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
            " \n" +
            " void main() {\n" +
            " " +
            "    vec4 textureColor = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + VARYING_TEXTURE_COORD + ");\n" +
            "    float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
            "    vec3 greyScaleColor = vec3(luminance);\n" +
            "    \n" +
            "    gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, " + UNIFORM_SATURATION + "), textureColor.w);\n" +
            "    gl_FragColor *= " + ALPHA_UNIFORM + ";\n" +
            " }";


    private float mSaturation;
    private int mSaturationLocation;

    public SaturationFilter(@FloatRange(from = 0.0, to = 2.0) float mSaturation) {
        this.mSaturation = mSaturation;
    }

    @Override
    public String getFragmentShader() {
        return SATURATION_FRAGMENT_SHADER;
    }

    @Override
    public void onPreDraw(int program, BasicTexture texture, ICanvasGL canvas) {
        super.onPreDraw(program, texture, canvas);
        mSaturationLocation = GLES20.glGetUniformLocation(program, UNIFORM_SATURATION);
        OpenGLUtil.setFloat(mSaturationLocation, mSaturation);
    }

    @Override
    public void setValue(@FloatRange(from = 0.0, to = 2.0) final float saturation) {
        mSaturation = saturation;
    }
}
