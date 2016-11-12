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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.chillingvan.canvasglsample.R;

/**
 * Created by Matthew on 2016/10/8.
 */

public class CompareNormalView extends View {

    private Bitmap baboon;
    private Matrix matrix;

    public CompareNormalView(Context context) {
        super(context);
        init();
    }

    public CompareNormalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CompareNormalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        baboon = BitmapFactory.decodeResource(getResources(), R.drawable.baboon);
        matrix = new Matrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        matrix.reset();
        matrix.postScale(2.1f, 2.1f);
        matrix.postRotate(90);
        matrix.postTranslate(90, 120);
        matrix.postScale(0.4f, 0.4f, 139, 149);
        matrix.postRotate(10, 128 , 128);
        matrix.postTranslate(90, -120);
        canvas.drawBitmap(baboon, matrix, new Paint());


        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#88FF0000"));
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(360, 0, 380, 40, paint);

        Paint paint2 = new Paint();
        paint2.setColor(Color.parseColor("#8800FF00"));
        paint2.setStrokeWidth(4);
        paint2.setStyle(Paint.Style.STROKE);
        canvas.drawRect(360, 40, 380, 80, paint2);

        canvas.drawLine(360, 80, 360, 120, paint);


        String text = "text";
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLUE);
        textPaint.setTextSize(30);
        canvas.drawText(text,0, text.length(), 500, 80, textPaint);


        //circle
        Paint circlePaint = new Paint();
        circlePaint.setColor(Color.parseColor("#88FF0000"));
        circlePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(430, 30, 30, circlePaint);

        Paint strokeCirclePaint = new Paint();
        strokeCirclePaint.setColor(Color.parseColor("#88FF0000"));
        strokeCirclePaint.setStrokeWidth(4);
        strokeCirclePaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(490, 30, 30, strokeCirclePaint);
    }


}
