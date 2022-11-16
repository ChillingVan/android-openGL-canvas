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
package com.chillingvan.canvasglsample.compareCanvas

import android.content.Context
import com.chillingvan.canvasgl.glview.GLView
import android.graphics.Bitmap
import com.chillingvan.canvasgl.textureFilter.CropFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.chillingvan.canvasglsample.R
import com.chillingvan.canvasgl.ICanvasGL
import com.chillingvan.canvasgl.ICanvasGL.OrthoBitmapMatrix
import com.chillingvan.canvasgl.glcanvas.GLPaint
import com.chillingvan.canvasgl.ICanvasGL.BitmapMatrix
import com.chillingvan.canvasgl.androidCanvas.IAndroidCanvasHelper

/**
 * Created by Matthew on 2016/10/5.
 */
class CompareGLView : GLView {
    private var baboon: Bitmap? = null
    private var cropFilter: CropFilter? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun init() {
        super.init()
        baboon = BitmapFactory.decodeResource(resources, R.drawable.baboon)
        cropFilter = CropFilter(0.5f, 0.5f, 1f, 1f)
    }

    override fun onGLDraw(canvas: ICanvasGL) {
        drawBitmapWithMatrix(canvas)
        drawRectAndLine(canvas)
        drawText(canvas)
        drawCircle(canvas)
        //        drawBitmapWithOrthoMatrix(canvas);
    }

    private fun drawBitmapWithOrthoMatrix(canvas: ICanvasGL) {
        val matrix = OrthoBitmapMatrix()
        canvas.drawBitmap(baboon, matrix)
        matrix.reset()
        matrix.translate(500f, 150f)
        matrix.scale(10f, 10f)
        canvas.drawBitmap(baboon, matrix)
        matrix.reset()
        matrix.translate(0f, 100f)
        matrix.rotateZ(45f)
        canvas.drawBitmap(baboon, matrix)
    }

    private fun drawCircle(canvas: ICanvasGL) {
        //circle
        val circlePaint = GLPaint()
        circlePaint.color = Color.parseColor("#88FF0000")
        circlePaint.style = Paint.Style.FILL
        canvas.drawCircle(430f, 30f, 30f, circlePaint)
        val strokeCirclePaint = GLPaint()
        strokeCirclePaint.color = Color.parseColor("#88FF0000")
        strokeCirclePaint.lineWidth = 4f
        strokeCirclePaint.style = Paint.Style.STROKE
        canvas.drawCircle(490f, 30f, 30f, strokeCirclePaint)
    }

    private fun drawRectAndLine(canvas: ICanvasGL) {
        val paint = GLPaint()
        paint.color = Color.parseColor("#88FF0000")
        paint.lineWidth = 4f
        paint.style = Paint.Style.FILL
        canvas.drawRect(360f, 0f, 380f, 40f, paint)
        val paint2 = GLPaint()
        paint2.color = Color.parseColor("#8800FF00")
        paint2.lineWidth = 4f
        paint2.style = Paint.Style.STROKE
        canvas.drawRect(560f, 40f, 760f, 180f, paint2)
        canvas.drawLine(360f, 80f, 360f, 120f, paint)
    }

    private fun drawBitmapWithMatrix(canvas: ICanvasGL) {
        val matrix = BitmapMatrix()
        matrix.scale(1.3f, 1f)
        matrix.rotateX(34f)
        matrix.rotateY(64f)
        matrix.rotateZ(30f)
        matrix.translate(390f, 0f)
        canvas.drawBitmap(baboon, matrix)
        matrix.reset()
        matrix.translate(28f, 19f)
        matrix.rotateZ(30f)
        canvas.drawBitmap(BitmapFactory.decodeResource(resources, R.drawable.lenna), matrix)
    }

    companion object {
        private fun drawText(canvas: ICanvasGL) {
            // text
            val androidCanvasHelper =
                IAndroidCanvasHelper.Factory.createAndroidCanvasHelper(IAndroidCanvasHelper.MODE.MODE_SYNC)
            androidCanvasHelper.init(canvas.width, canvas.height)
            androidCanvasHelper.draw { androidCanvas, drawBitmap ->
                val text = "text"
                val textPaint = Paint()
                textPaint.color = Color.BLUE
                textPaint.style = Paint.Style.FILL
                textPaint.textSize = 40f
                androidCanvas.drawText(text, 20f, 30f, textPaint)
            }
            canvas.drawBitmap(androidCanvasHelper.outputBitmap, 500, 80)
        }
    }
}