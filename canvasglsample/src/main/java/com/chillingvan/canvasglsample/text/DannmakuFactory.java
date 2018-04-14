package com.chillingvan.canvasglsample.text;

import android.graphics.Color;
import android.graphics.PointF;

import java.util.Random;

import static com.chillingvan.canvasglsample.animation.AnimActivity.VY_MULTIPLIER;

/**
 * Created by Chilling on 2018/4/14.
 */
public class DannmakuFactory extends ObjectFactory<Dannmaku> {
    private final Random random = new Random();
    private static final float VX_MULTIPLIER = 0.001f;
    private final int width;
    private final int height;
    private static final String[] WORDS = new String[]{
            "23333", "66666666", "哈哈哈哈哈哈哈哈哈", "Awesome", "凄い"
    };

    private static final int[] COLORS = new int[]{
            Color.WHITE, Color.GREEN, Color.LTGRAY
    };

    public DannmakuFactory(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    protected Dannmaku produce(Dannmaku dannmaku) {
        float vx = -(20 + random.nextInt(30)) * VY_MULTIPLIER;
        float y = 30 + random.nextInt(height/2);
        if (dannmaku == null) {
            dannmaku = new Dannmaku(new PointF(width, y), vx);
        } else {
            dannmaku.reset(width, y, vx);
        }
        dannmaku.setText(WORDS[random.nextInt(WORDS.length)]);
        dannmaku.setColor(COLORS[random.nextInt(COLORS.length)]);
        return dannmaku;
    }
}
