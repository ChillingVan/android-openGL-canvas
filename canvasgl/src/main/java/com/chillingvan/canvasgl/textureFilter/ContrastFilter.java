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
 * Created by Matthew on 2016/10/14.
 */


/**
 * Changes the contrast of the image.<br>
 * <br>
 * contrast value ranges from 0.0 to 4.0, with 1.0 as the normal level
 */
public class ContrastFilter extends BasicTextureFilter implements OneValueFilter{

    public static final String UNIFORM_CONTRAST = "contrast";
    public static final String CONTRAST_FRAGMENT_SHADER = ""
            + "precision mediump float;\n"
            + "varying vec2 " + VARYING_TEXTURE_COORD + ";\n"
            + "uniform float " + ALPHA_UNIFORM + ";\n"
            + "uniform float " + UNIFORM_CONTRAST + ";\n"
            + "uniform sampler2D " + TEXTURE_SAMPLER_UNIFORM + ";\n"
            + "void main() {\n"
            + "  vec4 textureColor = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + VARYING_TEXTURE_COORD + ");\n"
            + "  gl_FragColor = vec4(((textureColor.rgb - vec3(0.5)) * " + UNIFORM_CONTRAST + "+ vec3(0.5)), textureColor.w);\n"
            + "  gl_FragColor *= " + ALPHA_UNIFORM + ";\n"
            + "}\n";

    private float mContrast;


    public ContrastFilter(@FloatRange(from = 0.0, to = 4.0f) float contrast) {
        super();
        mContrast = contrast;
    }


    @Override
    public String getFragmentShader() {
        return CONTRAST_FRAGMENT_SHADER;
    }


    @Override
    public void onPreDraw(int program, BasicTexture texture, ICanvasGL canvas) {
        super.onPreDraw(program, texture, canvas);
        int contrastLocation = GLES20.glGetUniformLocation(program, UNIFORM_CONTRAST);
        OpenGLUtil.setFloat(contrastLocation, mContrast);
    }

    @Override
    public void setValue(@FloatRange(from = 0.0, to = 4.0f) final float contrast) {
        mContrast = contrast;
    }
}
