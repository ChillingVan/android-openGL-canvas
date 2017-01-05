package com.chillingvan.canvasglsample.offscreen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.OffScreenCanvas;
import com.chillingvan.canvasgl.glcanvas.BasicTexture;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.GLView;
import com.chillingvan.canvasglsample.R;

public class SimpleOffScreenActivity extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap bitmap;
    private OffScreenCanvas offScreenCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_off_screen);

        imageView = (ImageView) findViewById(R.id.off_screen_img_v);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);

        offScreenCanvas = new OffScreenCanvas(400, 400) {
            @Override
            protected void onGLDraw(ICanvasGL canvas, SurfaceTexture producedSurfaceTexture, RawTexture producedRawTexture,
                                    @Nullable SurfaceTexture outsideSharedSurfaceTexture, @Nullable BasicTexture outsideSharedTexture) {
                canvas.drawBitmap(bitmap, 0, 0, 256, 256);
            }
        };

        offScreenCanvas.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        offScreenCanvas.onResume();
        offScreenCanvas.getDrawingBitmap(new Rect(0, 0, 300, 300), new GLView.GetDrawingCacheCallback() {
            @Override
            public void onFetch(final Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        offScreenCanvas.onPause();
    }
}
