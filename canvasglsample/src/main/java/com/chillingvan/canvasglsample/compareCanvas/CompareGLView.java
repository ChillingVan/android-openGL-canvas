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
import com.chillingvan.canvasgl.textureFilter.CropFilter;
import com.chillingvan.canvasglsample.R;

/**
 * Created by Matthew on 2016/10/5.
 */

public class CompareGLView extends GLView {

    private Bitmap baboon;
    private CropFilter cropFilter;

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

        cropFilter = new CropFilter(0.5f, 0.5f, 1, 1);
    }

    @Override
    protected void onGLDraw(ICanvasGL canvas) {
        drawBitmapWithMatrix(canvas);
        drawRectAndLine(canvas);
        drawText(canvas);
        drawCircle(canvas);
//        drawBitmapWithOrthoMatrix(canvas);

    }

    private void drawBitmapWithOrthoMatrix(ICanvasGL canvas) {
        CanvasGL.OrthoBitmapMatrix matrix = new CanvasGL.OrthoBitmapMatrix();
        canvas.drawBitmap(baboon, matrix);


        matrix.reset();
        matrix.translate(500, 150);
        matrix.scale(10f, 10f);
        canvas.drawBitmap(baboon, matrix);


        matrix.reset();
        matrix.translate(0, 100);
        matrix.rotateZ(45);
        canvas.drawBitmap(baboon, matrix);
    }


    private void drawCircle(ICanvasGL canvas) {
        //circle
        GLPaint circlePaint = new GLPaint();
        circlePaint.setColor(Color.parseColor("#88FF0000"));
        circlePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(430, 30, 30, circlePaint);

        GLPaint strokeCirclePaint = new GLPaint();
        strokeCirclePaint.setColor(Color.parseColor("#88FF0000"));
        strokeCirclePaint.setLineWidth(4);
        strokeCirclePaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(490, 30, 30, strokeCirclePaint);
    }

    private void drawRectAndLine(ICanvasGL canvas) {
        GLPaint paint = new GLPaint();
        paint.setColor(Color.parseColor("#88FF0000"));
        paint.setLineWidth(4);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(360, 0, 380, 40, paint);

        GLPaint paint2 = new GLPaint();
        paint2.setColor(Color.parseColor("#8800FF00"));
        paint2.setLineWidth(4);
        paint2.setStyle(Paint.Style.STROKE);
        canvas.drawRect(560, 40, 760, 180, paint2);

        canvas.drawLine(360, 80, 360, 120, paint);
    }

    private static void drawText(ICanvasGL canvas) {
        // text
        Bitmap textBitmap = Bitmap.createBitmap(180, 100, Bitmap.Config.ARGB_8888);
        Canvas normalCanvas = new Canvas(textBitmap);
        String text = "text";
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLUE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(40);
        normalCanvas.drawColor(Color.WHITE);
        normalCanvas.drawText(text, 20, 30, textPaint);
        canvas.drawBitmap(textBitmap, 500, 80);
    }

    private void drawBitmapWithMatrix(ICanvasGL canvas) {
        CanvasGL.BitmapMatrix matrix = new CanvasGL.BitmapMatrix();
        matrix.scale(1.3f, 1.6f);
        matrix.rotateX(34);
        matrix.rotateY(64);
        matrix.rotateZ(30);
        matrix.translate(390, 0);
        canvas.drawBitmap(baboon, matrix);

        matrix.reset();
        matrix.translate(28, 19);
        matrix.rotateZ(30);
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.lenna), matrix);
    }
}
