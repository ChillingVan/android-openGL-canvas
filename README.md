# OpenGL canvas

Idea from: 
* Codes of Android source code under package com.android.gallery3d.glrenderer
* [GPUImage](https://github.com/CyberAgent/android-gpuimage)
* [grafika](https://github.com/google/grafika)

## Features
* This canvas provide similar API as android canvas. And there are GLViews that can be extended to custom your own view to draw things with OpenGL.
* Similar to the filters of GPUImage, you can apply the filter to the bitmap draw into the GLViews. 
* Provides GLViews that using GLSurfaceView and TextureView. 
* The GLContinuousView can provide high performance continuous rendering animation.

## Requirements
* Android API14 or higher (OpenGL ES 2.0)

## Usage

### Gradle dependency

```groovy
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.ChillingVan:android-openGL-canvas:v1.0.1'
}
```

### Sample Code

Custom your view.
```java
public class MyGLView extends GLView {

    public MyGLView(Context context) {
        super(context);
    }

    public MyGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onGLDraw(ICanvasGL canvas) {
        // draw things with canvas here
    }
}
```

![camera](https://github.com/ChillingVan/android-openGL-canvas/raw/master/screenshots/camera-example.jpg)


The Usage of GLContinuouslyView, GLTextureView, GLContinuousTextureView, GLSurfaceTextureProducerView and GLSharedContextView is similar.


Using canvas to draw
```java
        canvas.drawBitmap(textBitmap, left, top);
        
        // transform
        canvas.save();
        canvas.rotate(rotateDegree, x, y);
        canvas.drawBitmap(bitmap, left, top);
        canvas.restore();
        // or
        CanvasGL.BitmapMatrix matrix = new CanvasGL.BitmapMatrix();
        matrix.postScale(2.1f, 2.1f);
        matrix.postRotate(90);
        canvas.drawBitmap(bitmap, matrix);
        
        // apply filter to the bitmap
        textureFilter = new ContrastFilter(2.8f);
        canvas.drawBitmap(bitmap, left, top, textureFilter);
```

![filters](https://github.com/ChillingVan/android-openGL-canvas/raw/master/screenshots/filter_example.png)
![canvas](https://github.com/ChillingVan/android-openGL-canvas/raw/master/screenshots/canvas-example.png)


And can be used with camera, just run the camera sample code on a real device(not in emulator) to see what will happen.

## License
    Copyright 2012 CyberAgent, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
