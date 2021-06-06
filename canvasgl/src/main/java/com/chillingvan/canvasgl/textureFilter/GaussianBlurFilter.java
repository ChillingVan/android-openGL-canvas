package com.chillingvan.canvasgl.textureFilter;

import android.opengl.GLES20;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.OpenGLUtil;
import com.chillingvan.canvasgl.glcanvas.BasicTexture;

import java.util.ArrayList;

public class GaussianBlurFilter extends FilterGroup {
    public static class GaussianFilter extends BasicTextureFilter {
        private float blurSize;
        private int dir;
        public static final String TEXEL_WIDTH_OFFSET = "texelWidthOffset";
        public static final String TEXEL_HEIGHT_OFFSET = "texelHeightOffset";
        public static final String BLUR_COORDINATES = "blurCoordinates";


        public GaussianFilter(int dir, float blurSize) {
            this.dir = dir;
            this.blurSize = blurSize;


        }

        public GaussianFilter(int dir) {
            this(dir, 1f);
        }

        public static final String VERTEX_SHADER =
                "precision mediump float;\n" +
                        "uniform mat4 " + MATRIX_UNIFORM + ";\n" +
                        "uniform mat4 " + TEXTURE_MATRIX_UNIFORM + ";\n" +
                        "attribute vec2 " + POSITION_ATTRIBUTE + ";\n" +
                        "const int GAUSSIAN_SAMPLES = 9;\n" +
                        "\n" +
                        "uniform float " + TEXEL_WIDTH_OFFSET + ";\n" +
                        "uniform float " + TEXEL_HEIGHT_OFFSET + ";\n" +
                        "\n" +
                        "varying vec2 " + VARYING_TEXTURE_COORD + ";\n" +
                        "varying vec2 " + BLUR_COORDINATES + "[GAUSSIAN_SAMPLES];\n" +
                        "\n" +
                        "void main()\n" +
                        "{\n" +
                        "  vec4 pos = vec4(" + POSITION_ATTRIBUTE + ", 0.0, 1.0);\n" +
                        "	gl_Position = " + MATRIX_UNIFORM + "* pos;\n" +
                        VARYING_TEXTURE_COORD + " = (" + TEXTURE_MATRIX_UNIFORM + " * pos).xy;\n" +
                        "	\n" +
                        "	int multiplier = 0;\n" +
                        "	vec2 blurStep;\n" +
                        "   vec2 singleStepOffset = vec2(" + TEXEL_HEIGHT_OFFSET + ", " + TEXEL_WIDTH_OFFSET + ");\n" +
                        "    \n" +
                        "	for (int i = 0; i < GAUSSIAN_SAMPLES; i++)\n" +
                        "   {\n" +
                        "		multiplier = (i - ((GAUSSIAN_SAMPLES - 1) / 2));\n" +
                        "       // Blur in x (horizontal)\n" +
                        "       blurStep = float(multiplier) * singleStepOffset;\n" +
                        "		" + BLUR_COORDINATES + "[i] = " + VARYING_TEXTURE_COORD + ".xy + blurStep;\n" +
                        "	}\n" +
                        "}\n";

        public static final String FRAGMENT_SHADER =
                "uniform sampler2D " + TEXTURE_SAMPLER_UNIFORM + ";\n" +
                        "\n" +
                        "const lowp int GAUSSIAN_SAMPLES = 9;\n" +
                        "\n" +
                        "varying highp vec2 " + VARYING_TEXTURE_COORD + ";\n" +
                        "varying highp vec2 " + BLUR_COORDINATES + "[GAUSSIAN_SAMPLES];\n" +
                        "\n" +
                        "void main()\n" +
                        "{\n" +
                        "	lowp vec3 sum = vec3(0.0);\n" +
                        "   lowp vec4 fragColor=texture2D(" + TEXTURE_SAMPLER_UNIFORM + "," + VARYING_TEXTURE_COORD + ");\n" +
                        "	\n" +
                        "    sum += texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + BLUR_COORDINATES + "[0]).rgb * 0.05;\n" +
                        "    sum += texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + BLUR_COORDINATES + "[1]).rgb * 0.09;\n" +
                        "    sum += texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + BLUR_COORDINATES + "[2]).rgb * 0.12;\n" +
                        "    sum += texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + BLUR_COORDINATES + "[3]).rgb * 0.15;\n" +
                        "    sum += texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + BLUR_COORDINATES + "[4]).rgb * 0.18;\n" +
                        "    sum += texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + BLUR_COORDINATES + "[5]).rgb * 0.15;\n" +
                        "    sum += texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + BLUR_COORDINATES + "[6]).rgb * 0.12;\n" +
                        "    sum += texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + BLUR_COORDINATES + "[7]).rgb * 0.09;\n" +
                        "    sum += texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + BLUR_COORDINATES + "[8]).rgb * 0.05;\n" +
                        "\n" +
                        "	gl_FragColor = vec4(sum,fragColor.a);\n" +
                        "}";


        @Override
        public String getVertexShader() {
            return VERTEX_SHADER;
        }

        @Override
        public String getFragmentShader() {
            return FRAGMENT_SHADER;
        }


        @Override
        public void onPreDraw(int program, BasicTexture texture, ICanvasGL canvas) {
            super.onPreDraw(program, texture, canvas);
            int widthOffsetLocation = GLES20.glGetUniformLocation(program, TEXEL_WIDTH_OFFSET);
            int heightOffsetLocation = GLES20.glGetUniformLocation(program, TEXEL_HEIGHT_OFFSET);
            if (dir == 0) {
                OpenGLUtil.setFloat(widthOffsetLocation, blurSize / texture.getWidth());
                OpenGLUtil.setFloat(heightOffsetLocation, 0);

            } else {
                OpenGLUtil.setFloat(widthOffsetLocation, 0);
                OpenGLUtil.setFloat(heightOffsetLocation, blurSize / texture.getHeight());
            }


        }

        public void setBlurSize(float blurSize) {
            this.blurSize = blurSize;
        }


    }


    public GaussianBlurFilter(float blurSize) {
        super(new ArrayList<TextureFilter>());
        mFilters.add(new GaussianFilter(0, blurSize));
        mFilters.add(new GaussianFilter(1, blurSize));
        updateMergedFilters();
    }

    public void setBlurSize(float blurSize) {
        for (TextureFilter textureFilter : mFilters) {
            if (textureFilter instanceof GaussianFilter) {
                GaussianFilter gaussianFilter = (GaussianFilter) textureFilter;
                gaussianFilter.setBlurSize(blurSize);
            }

        }
    }
}
