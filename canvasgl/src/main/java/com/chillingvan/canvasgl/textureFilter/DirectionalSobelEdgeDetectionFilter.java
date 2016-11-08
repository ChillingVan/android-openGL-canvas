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

import android.support.annotation.FloatRange;

/**
 * Created by Chilling on 2016/11/6.
 */

public class DirectionalSobelEdgeDetectionFilter extends TextureSampling3mul3Filter {
    public static final String DIRECTIONAL_SOBEL_EDGE_DETECTION_FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
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
            "uniform sampler2D " + TEXTURE_SAMPLER_UNIFORM + ";\n" +
            " uniform float " + ALPHA_UNIFORM + ";\n" +
            "\n" +
            "void main() {\n" +
            "" +
            "    float bottomLeftIntensity = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + VARYING_BOTTOM_LEFT_TEXTURE_COORDINATE + ").r;\n" +
            "    float topRightIntensity = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + VARYING_TOP_RIGHT_TEXTURE_COORDINATE + ").r;\n" +
            "    float topLeftIntensity = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + VARYING_TOP_LEFT_TEXTURE_COORDINATE + ").r;\n" +
            "    float bottomRightIntensity = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + VARYING_BOTTOM_RIGHT_TEXTURE_COORDINATE + ").r;\n" +
            "    float leftIntensity = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + VARYING_LEFT_TEXTURE_COORDINATE + ").r;\n" +
            "    float rightIntensity = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + VARYING_RIGHT_TEXTURE_COORDINATE + ").r;\n" +
            "    float bottomIntensity = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + VARYING_BOTTOM_TEXTURE_COORDINATE + ").r;\n" +
            "    float topIntensity = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + VARYING_TOP_TEXTURE_COORDINATE + ").r;\n" +
            "\n" +
            "    vec2 gradientDirection;\n" +
            "    gradientDirection.x = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;\n" +
            "    gradientDirection.y = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;\n" +
            "\n" +
            "    float gradientMagnitude = length(gradientDirection);\n" +
            "    vec2 normalizedDirection = normalize(gradientDirection);\n" +
            "    normalizedDirection = sign(normalizedDirection) * floor(abs(normalizedDirection) + 0.617316); // Offset by 1-sin(pi/8) to set to 0 if near axis, 1 if away\n" +
            "    normalizedDirection = (normalizedDirection + 1.0) * 0.5; // Place -1.0 - 1.0 within 0 - 1.0\n" +
            "\n" +
            "    gl_FragColor = vec4(gradientMagnitude, normalizedDirection.x, normalizedDirection.y, 1.0);\n" +
            "    gl_FragColor *= " + ALPHA_UNIFORM + ";\n" +
            "}";

    public DirectionalSobelEdgeDetectionFilter(@FloatRange(from = 0, to = 5) float lineSize) {
        super(lineSize);
    }

    @Override
    public String getFragmentShader() {
        return DIRECTIONAL_SOBEL_EDGE_DETECTION_FRAGMENT_SHADER;
    }
}
