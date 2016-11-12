package com.chillingvan.canvasgl.shapeFilter;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.GLES20Canvas;

/**
 * Created by Chilling on 2016/11/11.
 */

public class BasicDrawShapeFilter implements DrawShapeFilter {
    public static final String MATRIX_UNIFORM = GLES20Canvas.MATRIX_UNIFORM;
    public static final String POSITION_ATTRIBUTE = GLES20Canvas.POSITION_ATTRIBUTE;
    public static final String COLOR_UNIFORM = GLES20Canvas.COLOR_UNIFORM;

    public static final String DRAW_VERTEX_SHADER = ""
            + "uniform mat4 " + MATRIX_UNIFORM + ";\n"
            + "attribute vec2 " + POSITION_ATTRIBUTE + ";\n"
            + "void main() {\n"
            + "  vec4 pos = vec4(" + POSITION_ATTRIBUTE + ", 0.0, 1.0);\n"
            + "  gl_Position = " + MATRIX_UNIFORM + " * pos;\n"
            + "}\n";

    public static final String DRAW_FRAGMENT_SHADER = ""
            + "precision mediump float;\n"
            + "uniform vec4 " + COLOR_UNIFORM + ";\n"
            + "void main() {\n"
            + "  gl_FragColor = " + COLOR_UNIFORM + ";\n"
            + "}\n";

    @Override
    public String getVertexShader() {
        return DRAW_VERTEX_SHADER;
    }

    @Override
    public String getFragmentShader() {
        return DRAW_FRAGMENT_SHADER;
    }

    @Override
    public void onPreDraw(int program, ICanvasGL canvas) {

    }

    @Override
    public void destroy() {

    }
}
