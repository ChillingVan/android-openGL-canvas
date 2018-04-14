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
 * Created by Matthew on 2016/8/13.
 */
public class MovableObj {


    public PointF point;
    public float vx;
    public float vy;
    public float collisionRadius;

    public float vRotate;
    public float rotateDegree;


    public MovableObj(PointF point, float vx, float vy, float vRotate, float collisionRadius) {
        this.point = point;
        this.vx = vx;
        this.vy = vy;
        this.vRotate = vRotate;
        this.collisionRadius = collisionRadius;
    }

    public void reset(PointF point, float vx, float vy, float vRotate, float collisionRadius) {
        this.point = point;
        this.vx = vx;
        this.vy = vy;
        this.vRotate = vRotate;
        this.collisionRadius = collisionRadius;
    }

    public void updatePosition(int timeMs) {
        point.x += vx * timeMs;
        point.y += vy * timeMs;
        rotateDegree += vRotate * timeMs;
    }

    public interface CollisionListener {
        int DIRECTION_HORIZONTAL = 0;
        int DIRECTION_VERTICAL = 1;
        void onCollision(int direction);
    }

    @Override
    public String toString() {
        return "MovableObj{" +
                "point=" + point +
                ", vx=" + vx +
                ", vy=" + vy +
                ", collisionRadius=" + collisionRadius +
                ", vRotate=" + vRotate +
                ", rotateDegree=" + rotateDegree +
                '}';
    }
}
