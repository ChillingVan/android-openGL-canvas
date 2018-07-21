package com.chillingvan.canvasgl.textureFilter;

import android.opengl.GLES20;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.OpenGLUtil;
import com.chillingvan.canvasgl.glcanvas.BasicTexture;

/**
 * Created by Chilling on 2018/7/5.
 */
public class CropFilter extends BasicTextureFilter {

    private static final String UNIFORM_LEFT = "left";
    private static final String UNIFORM_TOP = "top";
    private static final String UNIFORM_RIGHT = "right";
    private static final String UNIFORM_BOTTOM = "bottom";

    private static final String CROP_FRAGMENT_SHADER = ""
            + "precision mediump float;\n"
            + "varying vec2 " + VARYING_TEXTURE_COORD + ";\n"
            + "uniform float " + ALPHA_UNIFORM + ";\n"
            + "uniform " + SAMPLER_2D + " " + TEXTURE_SAMPLER_UNIFORM + ";\n"
            + "  uniform highp float " + UNIFORM_LEFT + ";\n"
            + "  uniform highp float " + UNIFORM_TOP + ";\n"
            + "  uniform highp float " + UNIFORM_RIGHT + ";\n"
            + "  uniform highp float " + UNIFORM_BOTTOM + ";\n"
            + "void main() {\n"
            + "if( " + VARYING_TEXTURE_COORD + ".x > " + UNIFORM_LEFT + " &&  " + VARYING_TEXTURE_COORD + ".x < " + UNIFORM_RIGHT +
            " &&  " + VARYING_TEXTURE_COORD + ".y > " + UNIFORM_TOP + " &&  " + VARYING_TEXTURE_COORD + ".y < " + UNIFORM_BOTTOM + ") {"
            + " gl_FragColor = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + VARYING_TEXTURE_COORD + ");\n"
            + "} else {"
            + " gl_FragColor = " + "vec4(0, 0, 0, 0)" + ";\n"
            + "}"
            + "}\n";

    private float left;
    private float right;
    private float top;
    private float bottom;


    public CropFilter(float left, float top, float right, float bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public String getFragmentShader() {
        return CROP_FRAGMENT_SHADER;
    }

    @Override
    public void onPreDraw(int program, BasicTexture texture, ICanvasGL canvas) {
        super.onPreDraw(program, texture, canvas);
        OpenGLUtil.setFloat(GLES20.glGetUniformLocation(program, UNIFORM_LEFT), left);
        OpenGLUtil.setFloat(GLES20.glGetUniformLocation(program, UNIFORM_TOP), top);
        OpenGLUtil.setFloat(GLES20.glGetUniformLocation(program, UNIFORM_RIGHT), right);
        OpenGLUtil.setFloat(GLES20.glGetUniformLocation(program, UNIFORM_BOTTOM), bottom);
    }


    public void setLeft(float left) {

        this.left = left;
    }

    public void setRight(float right) {

        this.right = right;
    }

    public void setTop(float top) {

        this.top = top;
    }

    public void setBottom(float bottom) {

        this.bottom = bottom;
    }

    public static float calc(int wantCoord, int size) {
        return (float)wantCoord / size;
    }
}
