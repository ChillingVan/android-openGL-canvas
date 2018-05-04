package com.chillingvan.canvasgl.textureFilter;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.OpenGLUtil;
import com.chillingvan.canvasgl.glcanvas.BasicTexture;

/**
 * Created by Chilling on 2018/5/4.
 */
public class LookupFilter extends TwoTextureFilter implements OneValueFilter {
    private float mIntensity = 1.0f;

    private static final String LOOKUP_FRAGMENT_SHADER =
            "varying highp vec2 " + VARYING_TEXTURE_COORD + ";\n" +
                    " varying highp vec2 " + VARYING_TEXTURE_COORD2 + "; // TODO: This is not used\n" +
                    " \n" +
                    " uniform sampler2D " + TEXTURE_SAMPLER_UNIFORM + ";\n" +
                    " uniform sampler2D " + UNIFORM_TEXTURE_SAMPLER2 + "; // lookup texture\n" +
                    " \n" +
                    " uniform lowp float intensity;\n" +
                    " \n" +
                    " void main()\n" +
                    " {\n" +
                    "     highp vec4 textureColor = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", " + VARYING_TEXTURE_COORD + ");\n" +
                    "     \n" +
                    "     highp float blueColor = textureColor.b * 63.0;\n" +
                    "     \n" +
                    "     highp vec2 quad1;\n" +
                    "     quad1.y = floor(floor(blueColor) / 8.0);\n" +
                    "     quad1.x = floor(blueColor) - (quad1.y * 8.0);\n" +
                    "     \n" +
                    "     highp vec2 quad2;\n" +
                    "     quad2.y = floor(ceil(blueColor) / 8.0);\n" +
                    "     quad2.x = ceil(blueColor) - (quad2.y * 8.0);\n" +                    "     \n" +
                    "     highp vec2 texPos1;\n" +
                    "     texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);\n" +
                    "     texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);\n" +
                    "     \n" +
                    "     highp vec2 texPos2;\n" +
                    "     texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);\n" +
                    "     texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);\n" +
                    "     \n" +
                    "     lowp vec4 newColor1 = texture2D(" + UNIFORM_TEXTURE_SAMPLER2 + ", texPos1);\n" +
                    "     lowp vec4 newColor2 = texture2D(" + UNIFORM_TEXTURE_SAMPLER2 + ", texPos2);\n" +
                    "     \n" +
                    "     lowp vec4 newColor = mix(newColor1, newColor2, fract(blueColor));\n" +
                    "     gl_FragColor = mix(textureColor, vec4(newColor.rgb, textureColor.w), intensity);\n" +
                    " }";

    public LookupFilter(@NonNull Bitmap secondBitmap) {
        super(secondBitmap);
    }

    @Override
    public String getFragmentShader() {
        return LOOKUP_FRAGMENT_SHADER;
    }

    @Override
    public void onPreDraw(int program, BasicTexture texture, ICanvasGL canvas) {
        super.onPreDraw(program, texture, canvas);
        int mIntensityLocation = GLES20.glGetUniformLocation(program, "intensity");
        OpenGLUtil.setFloat(mIntensityLocation, mIntensity);
    }

    @Override
    public void setValue(@FloatRange(from = 0.0, to = 1.0f) final float intensity) {
        mIntensity = intensity;
    }
}
