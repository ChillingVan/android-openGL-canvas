package com.chillingvan.canvasgl.shapeFilter;

import android.opengl.GLES20;
import android.support.annotation.FloatRange;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.OpenGLUtil;

/**
 * Created by Chilling on 2016/11/11.
 */

public class DrawCircleFilter extends BasicDrawShapeFilter {

    public static final String VARYING_DRAW_REGION_COORD = "vDrawRegionCoord";

    public static final String CIRCLE_VERTEX_SHADER = ""
            + "uniform mat4 " + MATRIX_UNIFORM + ";\n"
            + "attribute vec2 " + POSITION_ATTRIBUTE + ";\n"
            + "varying vec2 " + VARYING_DRAW_REGION_COORD + ";\n"
            + "void main() {\n"
            + "  vec4 pos = vec4(" + POSITION_ATTRIBUTE + ", 0.0, 1.0);\n"
            + "  gl_Position = " + MATRIX_UNIFORM + " * pos;\n"
            + "  " + VARYING_DRAW_REGION_COORD + " = pos.xy;\n"
            + "}\n";

    public static final String UNIFORM_LINE_WIDTH = "lineWidth";
    public static final String CIRCLE_FRAGMENT_SHADER = ""
            + "precision mediump float;\n"
            + "varying vec2 " + VARYING_DRAW_REGION_COORD + ";\n"
            + "uniform vec4 " + COLOR_UNIFORM + ";\n"
            + "uniform float " + UNIFORM_LINE_WIDTH + ";\n"
            + "void main() {\n"
            + "  float dx = " + VARYING_DRAW_REGION_COORD + ".x - 0.5;\n"
            + "  float dy = " + VARYING_DRAW_REGION_COORD + ".y - 0.5;\n"
            + "  float powVal = dx*dx + dy*dy; \n"
            + "  float subRadius = 0.5 - " + UNIFORM_LINE_WIDTH + "; \n"
            + "  if(powVal >= subRadius * subRadius && powVal <= 0.5 * 0.5) {\n"
            + "    gl_FragColor = " + COLOR_UNIFORM + ";\n"
            + "  } else {\n"
            + "    gl_FragColor = vec4(0, 0, 0, 0);\n"
            + "  }\n"
            + " \n"
            + "}\n";

    private float lineWidth;

    public void setLineWidth(@FloatRange(from = 0, to = 0.5) float lineWidth) {
        this.lineWidth = lineWidth;
    }

    @Override
    public String getVertexShader() {
        return CIRCLE_VERTEX_SHADER;
    }

    @Override
    public String getFragmentShader() {
        return CIRCLE_FRAGMENT_SHADER;
    }

    @Override
    public void onPreDraw(int program, ICanvasGL canvas) {
        super.onPreDraw(program, canvas);
        int lineWidthLocation = GLES20.glGetUniformLocation(program, UNIFORM_LINE_WIDTH);
        OpenGLUtil.setFloat(lineWidthLocation, lineWidth);
    }
}
