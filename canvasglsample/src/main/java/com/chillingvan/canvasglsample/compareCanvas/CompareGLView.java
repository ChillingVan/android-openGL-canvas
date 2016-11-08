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

package com.chillingvan.canvasglsample.compareCanvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.CanvasGL;
import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.GLPaint;
import com.chillingvan.canvasgl.glview.GLView;
import com.chillingvan.canvasglsample.R;

/**
 * Created by Matthew on 2016/10/5.
 */

public class CompareGLView extends GLView {

    private Bitmap baboon;

    public CompareGLView(Context context) {
        super(context);
    }

    public CompareGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        super.init();

        baboon = BitmapFactory.decodeResource(getResources(), R.drawable.baboon);
    }

    @Override
    protected void onGLDraw(ICanvasGL canvas) {


        CanvasGL.BitmapMatrix matrix = new CanvasGL.BitmapMatrix();
        matrix.postScale(2.1f, 2.1f);
        matrix.postRotate(90);
        matrix.postTranslate(90, 120);
        matrix.postScale(0.4f, 0.4f, 140, 150);
        matrix.postRotate(10, 128 , 128);
        matrix.postTranslate(90, -120);
        canvas.drawBitmap(baboon, matrix);


        GLPaint paint = new GLPaint();
        paint.setColor(Color.parseColor("#88FF0000"));
        paint.setLineWidth(4);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(360, 0, 380, 20, paint);
        GLPaint paint2 = new GLPaint();
        paint2.setColor(Color.parseColor("#8800FF00"));
        paint2.setLineWidth(4);
        paint2.setStyle(Paint.Style.STROKE);
        canvas.drawRect(360, 20, 380, 40, paint2);

        canvas.drawLine(360, 40, 360, 60, paint);

        Bitmap textBitmap = Bitmap.createBitmap(180, 100, Bitmap.Config.ARGB_8888);
        Canvas normalCanvas = new Canvas(textBitmap);
        String text = "text";
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLUE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(40);
        normalCanvas.drawColor(Color.WHITE);
        normalCanvas.drawText(text, 20, 30, textPaint);

        canvas.drawBitmap(textBitmap, 400, 80);
    }
}
