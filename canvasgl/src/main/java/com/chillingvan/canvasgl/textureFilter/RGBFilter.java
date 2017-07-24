package com.chillingvan.canvasgl.textureFilter;

import android.opengl.GLES20;
import android.support.annotation.FloatRange;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.OpenGLUtil;
import com.chillingvan.canvasgl.glcanvas.BasicTexture;

/**
 * Created by Chilling on 2017/4/15.
 */

public class RGBFilter extends BasicTextureFilter implements OneValueFilter {

    public static final String UNIFORM_RED = "red";
    public static final String UNIFORM_GREEN = "green";
    public static final String UNIFORM_BLUE = "blue";
    public static final String RGB_FRAGMENT_SHADER = "" +
            "precision mediump float; \n"+
            "  varying highp vec2 " + VARYING_TEXTURE_COORD + ";\n" +
            "  \n" +
            "  uniform sampler2D " + TEXTURE_SAMPLER_UNIFORM + ";\n" +
            "  uniform highp float " + UNIFORM_RED + ";\n" +
            "  uniform highp float " + UNIFORM_GREEN + ";\n" +
            "  uniform highp float " + UNIFORM_BLUE + ";\n" +
            " uniform float " + ALPHA_UNIFORM + ";\n" +
            "  \n" +
            "  void main() {\n" +
            "  " +
            "      highp vec4 textureColor = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + VARYING_TEXTURE_COORD + ");\n" +
            "      \n" +
            "      gl_FragColor = vec4(textureColor.r * " + UNIFORM_RED + ", textureColor.g * " + UNIFORM_GREEN + ", textureColor.b * " + UNIFORM_BLUE + ", textureColor.w);\n" +
            "    gl_FragColor *= " + ALPHA_UNIFORM + ";\n" +
            "  }\n";
    private float red;
    private float green;
    private float blue;


    public RGBFilter(@FloatRange(from = 0, to = 1) float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }


    public void setRed(@FloatRange(from = 0, to = 1) final float value) {
        red = value;
    }

    public void setGreen(final float value) {
        green = value;
    }

    public void setBlue(final float value) {
        blue = value;
    }

    @Override
    public String getFragmentShader() {
        return RGB_FRAGMENT_SHADER;
    }

    @Override
    public void onPreDraw(int program, BasicTexture texture, ICanvasGL canvas) {
        super.onPreDraw(program, texture, canvas);
        OpenGLUtil.setFloat(GLES20.glGetUniformLocation(program, UNIFORM_RED), red);
        OpenGLUtil.setFloat(GLES20.glGetUniformLocation(program, UNIFORM_GREEN), green);
        OpenGLUtil.setFloat(GLES20.glGetUniformLocation(program, UNIFORM_BLUE), blue);
    }

    @Override
    public void setValue(float value) {
        setRed(value);
        setGreen(value);
        setBlue(value);
    }
}
