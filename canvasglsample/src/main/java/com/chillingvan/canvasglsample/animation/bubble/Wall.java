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

import android.graphics.PointF;

/**
 * Created by Chilling on 2016/10/24.
 */

public abstract class Wall {

    protected float value;

    public Wall(float value) {
        this.value = value;
    }

    public abstract boolean isTouch(PointF point, float objRadius);

    public static class WallX extends Wall {

        public WallX(float value) {
            super(value);
        }

        @Override
        public boolean isTouch(PointF point, float objRadius) {
            return Math.abs(point.x - this.value) <= objRadius;
        }
    }

    public static class WallY extends Wall {

        public WallY(float value) {
            super(value);
        }

        @Override
        public boolean isTouch(PointF point, float objRadius) {
            return Math.abs(point.y - this.value) <= objRadius;
        }
    }
}
