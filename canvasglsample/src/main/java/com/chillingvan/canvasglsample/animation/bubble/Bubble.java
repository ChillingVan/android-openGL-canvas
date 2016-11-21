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

package com.chillingvan.canvasglsample.animation.bubble;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.textureFilter.BasicTextureFilter;
import com.chillingvan.canvasgl.textureFilter.TextureFilter;

/**
 * Created by Chilling on 2016/10/24.
 */

public class Bubble extends MovableObj implements MovableObj.CollisionListener {
    private Bitmap bitmap;
    private TextureFilter textureFilter;
    private Paint paint = new Paint();

    public Bubble(PointF point, float vx, float vy, float vRotate, Bitmap bitmap, TextureFilter textureFilter) {
        super(point, vx, vy, vRotate, bitmap.getWidth()/(float)2);
        this.bitmap = bitmap;
        if (textureFilter == null) {
            this.textureFilter = new BasicTextureFilter();
        } else {
            this.textureFilter = textureFilter;
        }
    }


    public void glDraw(ICanvasGL canvas) {
        canvas.save();
        int left = (int) (point.x - bitmap.getWidth() / (float)2);
        int top = (int) (point.y - bitmap.getHeight() / (float)2);

        canvas.rotate(rotateDegree, point.x, point.y);
        canvas.drawBitmap(bitmap, left, top, textureFilter);

        canvas.restore();
    }

    public void normalDraw(Canvas canvas) {
        canvas.save();
        int left = (int) (point.x - bitmap.getWidth() / (float)2);
        int top = (int) (point.y - bitmap.getHeight() / (float)2);

        canvas.rotate(rotateDegree, point.x, point.y);
        canvas.drawBitmap(bitmap, left, top, paint);

        canvas.restore();
    }

    @Override
    public void updatePosition(int timeMs) {
        super.updatePosition(timeMs);
    }

    @Override
    public void onCollision(int direction) {
        if (direction == DIRECTION_HORIZONTAL) {
            vx = -vx;
        } else if (direction == DIRECTION_VERTICAL) {
            vy = -vy;
        }
    }
}
