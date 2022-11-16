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
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.chillingvan.canvasglsample.R

/**
 * Created by Matthew on 2016/10/8.
 */
class CompareNormalView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var baboon: Bitmap? = null
    private val mMatrix: Matrix = Matrix()

    init {
        init()
    }

    private fun init() {
        baboon = BitmapFactory.decodeResource(resources, R.drawable.baboon)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mMatrix.reset()
        mMatrix.postScale(2.1f, 2.1f)
        mMatrix.postRotate(90f)
        mMatrix.postTranslate(90f, 120f)
        mMatrix.postScale(0.4f, 0.4f, 139f, 149f)
        mMatrix.postRotate(10f, 128f, 128f)
        mMatrix.postTranslate(90f, -120f)
        canvas.drawBitmap(baboon!!, mMatrix!!, Paint())
        val paint = Paint()
        paint.color = Color.parseColor("#88FF0000")
        paint.strokeWidth = 4f
        paint.style = Paint.Style.FILL
        canvas.drawRect(360f, 0f, 380f, 40f, paint)
        val paint2 = Paint()
        paint2.color = Color.parseColor("#8800FF00")
        paint2.strokeWidth = 4f
        paint2.style = Paint.Style.STROKE
        canvas.drawRect(360f, 40f, 380f, 80f, paint2)
        canvas.drawLine(360f, 80f, 360f, 120f, paint)
        val text = "text"
        val textPaint = Paint()
        textPaint.color = Color.BLUE
        textPaint.textSize = 30f
        canvas.drawText(text, 0, text.length, 500f, 80f, textPaint)


        //circle
        val circlePaint = Paint()
        circlePaint.color = Color.parseColor("#88FF0000")
        circlePaint.style = Paint.Style.FILL
        canvas.drawCircle(430f, 30f, 30f, circlePaint)
        val strokeCirclePaint = Paint()
        strokeCirclePaint.color = Color.parseColor("#88FF0000")
        strokeCirclePaint.strokeWidth = 4f
        strokeCirclePaint.style = Paint.Style.STROKE
        canvas.drawCircle(490f, 30f, 30f, strokeCirclePaint)
    }
}