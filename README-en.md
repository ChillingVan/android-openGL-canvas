# OpenGL Canvas


Idea from: 
* Codes of Android source code under package com.android.gallery3d.glrenderer
* [GPUImage](https://github.com/CyberAgent/android-gpuimage)
* [grafika](https://github.com/google/grafika)
Thanks to them!

Used by:
* [AndroidInstantVideo](https://github.com/ChillingVan/AndroidInstantVideo)

## Features
* The canvasGL provides similar API as android canvas. And there are GLViews that can be extended to custom your own view to draw things with OpenGL.
* Similar to the filters of GPUImage, you can apply the filter to the bitmap draw into the GLViews. 
* Provides GLViews that using GLSurfaceView and TextureView. 

* The GLContinuousView can provide high performance continuous rendering animation because it uses openGL to draw in its own thread.
![anim](https://github.com/ChillingVan/android-openGL-canvas/raw/master/screenshots/anim-activity-example.png)

Compare to GPUImage:
* Provide the continuous rendering Thread in GLContinuousView and GLContinuousTextureView.
* Using TextureView has this benefit:
    TextureView does not create a separate window but behaves as a regular View. This key difference allows a TextureView to be moved, transformed, animated, etc. For instance, you can make a TextureView semi-translucent by calling myView.setAlpha(0.5f)
* Canvas is provided. Not only the image processing but drawing thing what you want.

## Requirements
* Android API 14 or higher (OpenGL ES 2.0)

## Usage

### Gradle dependency

sample:
```groovy
// in root build.gradle
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}

// module build.gradle
dependencies {
    implementation 'com.github.ChillingVan:android-openGL-canvas:v1.4.1.2'
}
```

### Sample Code

* Custom your view.
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


![canvas](https://github.com/ChillingVan/android-openGL-canvas/raw/master/screenshots/canvas-example-v1.png)

* The Usage of GLContinuouslyView, GLTextureView, GLContinuousTextureView, GLMultiTexProducerView and GLMultiTexConsumerView is similar.


* Using canvas to draw
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

![filters](https://github.com/ChillingVan/android-openGL-canvas/raw/master/screenshots/filter_example-v1.png)


* And can be used with camera, just run the camera sample code on a real device(not in emulator) to see what will happen.

![camera](https://github.com/ChillingVan/android-openGL-canvas/raw/master/screenshots/camera-example-v1.jpg)


* If you do not want to use GLView, you can use MultiTexOffScreenCanvas to draw things and fetch it by getDrawingBitmap.

* MediaPlayer

You can use MediaPlayer to decode video and draw it on the TextureView. 
If you use GLSurfaceTextureProducerView, then you can process the video frames and provide the texture to MediaCodec to create a new Video. 
Use this sample and the stream publisher sample of [AndroidInstantVideo](https://github.com/ChillingVan/AndroidInstantVideo). You can implement this.

* AndroidCanvasHelper

This GLCanvas cannot draw text. 
You can use AndroidCanvasHelper to draw what you want and turn it to bitmap for GLCanvas. 
It has sync and async modes.

* See the wiki page for more use case.[here](https://github.com/ChillingVan/android-openGL-canvas/wiki)

## Note & FAQ
* The onGLDraw method in GLView runs in its own thread but not the main thread. 
* I haven't implemented all the filters in GPUImage. I will add more later. If you need, you can take my code as example to implement your filter. It is simple.
* Remember to call onResume and onPause in the Activity lifecycle when using GLContinuousView and GLContinuousTextureView.
* Why the bitmap drawn is not updated even the bitmap is changed?

  You can use canvasGL.invalidateTextureContent(bitmap) to rebind the bitmap to texture. 
  This is kind of heavy so I do not update call this for every drawn.

## Latest Update
* Add OrthoBitmapMatrix as One BitmapMatrix. Default BitmapMatrix uses perspective matrix.
* Fix BitmapMatrix cut by small viewport issue when Bitmap out of screen.
* Support cut bitmap with CropFilter
* Add MultiTexOffScreenCanvas, GLMultiTexProducerView, GLMultiTexConsumerView to support producing multiple textures and consume multiple textures
* Add AndroidCanvasHelper and its example for drawing text.

## License
    Copyright 2016 ChillingVan.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
