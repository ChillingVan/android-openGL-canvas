package com.chillingvan.canvasglsample.text;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;

import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.GLSurfaceTextureProducerView;
import com.chillingvan.canvasglsample.R;
import com.chillingvan.canvasglsample.video.MediaPlayerHelper;

public class DrawTextActivity extends AppCompatActivity {

    private MediaPlayerHelper mediaPlayer = new MediaPlayerHelper();
    private Surface mediaSurface;
    private DrawTextTextureView drawTextTextureView;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_text);
        initTextureView();
    }

    private void initTextureView() {
        drawTextTextureView = findViewById(R.id.media_player_texture_view);
        final TextView frameRateTxt = findViewById(R.id.frame_rate_txt);

        drawTextTextureView.setOnSurfaceTextureSet(new GLSurfaceTextureProducerView.OnSurfaceTextureSet() {
            @Override
            public void onSet(SurfaceTexture surfaceTexture, RawTexture surfaceTextureRelatedTexture) {
                // No need to request draw because it is continues GL View.

                mediaSurface = new Surface(surfaceTexture);
            }
        });
        countDownTimer = new CountDownTimer(1000 * 3600, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                frameRateTxt.setText(String.valueOf(drawTextTextureView.getFrameRate()));
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawTextTextureView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        drawTextTextureView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.release();
        }
    }

    public void onClickStart(View view) {
        if ((mediaPlayer.isPlaying() || mediaPlayer.isLooping())) {
            return;
        }

        playMedia();

    }

    private void playMedia() {
        mediaPlayer.playMedia(this, mediaSurface);
        drawTextTextureView.start();

    }
}
