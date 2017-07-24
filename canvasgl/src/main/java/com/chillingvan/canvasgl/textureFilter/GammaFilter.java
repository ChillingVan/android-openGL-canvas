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
 * Created by Chilling on 2016/11/2.
 */


/**
 * gamma value ranges from 0.0 to 3.0, with 1.0 as the normal level
 */
public class GammaFilter extends BasicTextureFilter implements OneValueFilter {

    public static final String UNIFORM_GAMMA = "gamma";
    public static final String GAMMA_FRAGMENT_SHADER = "" +
            "precision mediump float; \n" +
            "varying vec2 " + VARYING_TEXTURE_COORD + ";\n" +
            " \n" +
            " uniform sampler2D " + TEXTURE_SAMPLER_UNIFORM + ";\n" +
            " uniform lowp float " + UNIFORM_GAMMA + ";\n" +
            " uniform float " + ALPHA_UNIFORM + ";\n" +
            " \n" +
            " void main() {\n" +
            " " +
            "     lowp vec4 textureColor = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + VARYING_TEXTURE_COORD + ");\n" +
            "     \n" +
            "     gl_FragColor = vec4(pow(textureColor.rgb, vec3(" + UNIFORM_GAMMA + ")), textureColor.w);\n" +
            "    gl_FragColor *= " + ALPHA_UNIFORM + ";\n" +
            " }";
    private float mGamma;
    private int mGammaLocation;

    public GammaFilter(@FloatRange(from = 0, to = 3) float mGamma) {
        this.mGamma = mGamma;
    }

    @Override
    public String getFragmentShader() {
        return GAMMA_FRAGMENT_SHADER;
    }

    @Override
    public void onPreDraw(int program, BasicTexture texture, ICanvasGL canvas) {
        super.onPreDraw(program, texture, canvas);
        mGammaLocation = GLES20.glGetUniformLocation(program, UNIFORM_GAMMA);
        OpenGLUtil.setFloat(mGammaLocation, mGamma);
    }

    @Override
    public void setValue(@FloatRange(from = 0, to = 3) float gamma) {
        mGamma = gamma;
    }
}
