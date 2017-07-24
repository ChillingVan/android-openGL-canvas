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
import android.support.annotation.NonNull;

/**
 * Created by Chilling on 2016/10/17.
 */

public class DarkenBlendFilter extends TwoTextureFilter {

    public static final String DARKEN_BLEND_FRAGMENT_SHADER =
            "precision mediump float; \n" +
            "varying vec2 " + VARYING_TEXTURE_COORD + ";\n" +
            " varying vec2 " + VARYING_TEXTURE_COORD2 + ";\n" +
            "\n" +
            " uniform sampler2D " + TEXTURE_SAMPLER_UNIFORM + ";\n" +
            " uniform sampler2D " + UNIFORM_TEXTURE_SAMPLER2 + ";\n" +
            " \n" +
            " void main() {\n" +
            " " +
            "    lowp vec4 base = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + VARYING_TEXTURE_COORD + ");\n" +
            "    lowp vec4 overlayer = texture2D(" + UNIFORM_TEXTURE_SAMPLER2 + ", " + VARYING_TEXTURE_COORD2 + ");\n" +
            "    \n" +
            "    gl_FragColor = vec4(min(overlayer.rgb * base.a, base.rgb * overlayer.a) + overlayer.rgb * (1.0 - base.a) + base.rgb * (1.0 - overlayer.a), 1.0);\n" +
            " }";

    public DarkenBlendFilter(@NonNull Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public String getFragmentShader() {
        return DARKEN_BLEND_FRAGMENT_SHADER;
    }
}
