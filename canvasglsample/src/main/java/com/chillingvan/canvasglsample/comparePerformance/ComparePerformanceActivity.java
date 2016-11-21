package com.chillingvan.canvasglsample.comparePerformance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.chillingvan.canvasglsample.R;

import java.util.Locale;

public class ComparePerformanceActivity extends AppCompatActivity {

    private NormalBubblesView hwBubblesView;
    private SurfaceBubblesView swBubblesView;
    private GLBubblesView glBubblesView;
    private Bitmap bitmap;
    private TextView oneText;
    private TextView twoText;
    private TextView threeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_performace);
        hwBubblesView = (NormalBubblesView) findViewById(R.id.hw_bubble_view);
        swBubblesView = (SurfaceBubblesView) findViewById(R.id.sw_bubble_view);
        glBubblesView = (GLBubblesView) findViewById(R.id.gl_bubble_view);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_robot);
        hwBubblesView.setBitmap(bitmap);
        swBubblesView.setBitmap(bitmap);
        glBubblesView.setBitmap(bitmap);

        oneText = (TextView) findViewById(R.id.count_one);
        twoText = (TextView) findViewById(R.id.count_two);
        threeText = (TextView) findViewById(R.id.count_three);
        threeText.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                oneText.setText(String.format(Locale.CHINA, "hw canvas:%d", hwBubblesView.getCnt()));
                twoText.setText(String.format(Locale.CHINA, "sw canvas:%d", swBubblesView.getCnt()));
                threeText.setText(String.format(Locale.CHINA, "gl canvas:%d", glBubblesView.getCnt()));
                threeText.postDelayed(this, 500);
            }
        }, 500);
        new Thread(new Runnable() {
            @Override
            public void run() {
            }
        }).start();
    }


    public void onFire(View view) {
        hwBubblesView.setAdd(true);
        swBubblesView.setAdd(true);
        glBubblesView.setAdd(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hwBubblesView.onResume();
        swBubblesView.onResume();
        glBubblesView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hwBubblesView.onPause();
        swBubblesView.onPause();
        glBubblesView.onPause();
    }
}
