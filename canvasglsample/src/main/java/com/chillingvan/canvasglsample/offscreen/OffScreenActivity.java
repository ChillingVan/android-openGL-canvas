package com.chillingvan.canvasglsample.offscreen;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.ImageView;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.OffScreenCanvas;
import com.chillingvan.canvasgl.glview.GLView;
import com.chillingvan.canvasglsample.R;

public class OffScreenActivity extends Activity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_off_screen);

        imageView = (ImageView) findViewById(R.id.off_screen_img_v);

        OffScreenCanvas offScreenCanvas = new OffScreenCanvas(400, 400) {
            @Override
            protected void onGLDraw(ICanvasGL canvas) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);
                canvas.drawBitmap(bitmap, 0, 0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDrawingBitmap(new Rect(0, 0, 300, 300), new GLView.GetDrawingCacheCallback() {
                            @Override
                            public void onFetch(final Bitmap bitmap) {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                });
            }
        };
        offScreenCanvas.start();
    }
}
