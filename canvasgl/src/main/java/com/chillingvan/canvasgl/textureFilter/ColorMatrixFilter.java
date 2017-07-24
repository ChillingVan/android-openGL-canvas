package com.chillingvan.canvasgl.textureFilter;

import android.opengl.GLES20;
import android.support.annotation.FloatRange;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.OpenGLUtil;
import com.chillingvan.canvasgl.glcanvas.BasicTexture;

/**
 * Created by Chilling on 2017/4/15.
 */

public class ColorMatrixFilter extends BasicTextureFilter implements OneValueFilter{

    public static final String UNIFORM_COLOR_MATRIX = "colorMatrix";
    public static final String UNIFORM_INTENSITY = "intensity";
    public static final String COLOR_MATRIX_FRAGMENT_SHADER = "" +
            "precision mediump float; \n"+
            "varying highp vec2 " + VARYING_TEXTURE_COORD + ";\n" +
            "\n" +
            "uniform sampler2D " + TEXTURE_SAMPLER_UNIFORM + ";\n" +
            "\n" +
            "uniform lowp mat4 " + UNIFORM_COLOR_MATRIX + ";\n" +
            "uniform lowp float " + UNIFORM_INTENSITY + ";\n" +
            " uniform float " + ALPHA_UNIFORM + ";\n" +
            "\n" +
            "void main() {\n" +
            "" +
            "    lowp vec4 textureColor = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + VARYING_TEXTURE_COORD + ");\n" +
            "    lowp vec4 outputColor = textureColor * " + UNIFORM_COLOR_MATRIX + ";\n" +
            "    \n" +
            "    gl_FragColor = (" + UNIFORM_INTENSITY + " * outputColor) + ((1.0 - intensity) * textureColor);\n" +
            "    gl_FragColor *= " + ALPHA_UNIFORM + ";\n" +
            "}";

    private float mIntensity;
    private float[] mColorMatrix;

    public ColorMatrixFilter(@FloatRange(from = 0, to = 1) final float intensity, final float[] colorMatrix) {
        mIntensity = intensity;
        mColorMatrix = colorMatrix;
    }

    public void setIntensity(@FloatRange(from = 0, to = 1) final float intensity) {
        mIntensity = intensity;
    }

    public void setColorMatrix(final float[] colorMatrix) {
        mColorMatrix = colorMatrix;
    }

    @Override
    public String getFragmentShader() {
        return COLOR_MATRIX_FRAGMENT_SHADER;
    }

    @Override
    public void onPreDraw(int program, BasicTexture texture, ICanvasGL canvas) {
        super.onPreDraw(program, texture, canvas);
        OpenGLUtil.setFloat(GLES20.glGetUniformLocation(program, UNIFORM_INTENSITY), mIntensity);
        OpenGLUtil.setUniformMatrix4f(GLES20.glGetUniformLocation(program, UNIFORM_COLOR_MATRIX), mColorMatrix);
    }

    @Override
    public void setValue(float value) {
        setIntensity(value);
    }
}
