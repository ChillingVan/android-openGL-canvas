package com.chillingvan.canvasglsample.video;

import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.chillingvan.canvasgl.util.Loggers;
import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.GLSurfaceTextureProducerView;
import com.chillingvan.canvasglsample.R;

import java.io.IOException;

public class MediaPlayerActivity extends AppCompatActivity {

    public static final String TEST_VIDEO_MP4 = "test_video.mp4";
    private MediaPlayer mediaPlayer;
    private Surface mediaSurface;
    private MediaPlayerTextureView mediaPlayerTextureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        initTextureView();
    }

    private void initTextureView() {
        mediaPlayerTextureView = findViewById(R.id.media_player_texture_view);

        mediaPlayerTextureView.setOnSurfaceTextureSet(new GLSurfaceTextureProducerView.OnSurfaceTextureSet() {
            @Override
            public void onSet(SurfaceTexture surfaceTexture, RawTexture surfaceTextureRelatedTexture) {
                surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        Loggers.i("MediaPlayerActivity", "onFrameAvailable: ");
                        mediaPlayerTextureView.requestRenderAndWait();
                    }
                });

                mediaSurface = new Surface(surfaceTexture);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayerTextureView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayerTextureView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.release();
        }
    }

    public void onClickStart(View view) {
        if (mediaPlayer != null && (mediaPlayer.isPlaying() || mediaPlayer.isLooping())) {
            return;
        }

        playMedia();

    }

    private void playMedia() {
        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = getAssets().openFd(TEST_VIDEO_MP4);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setSurface(mediaSurface);
        mediaPlayer.setLooping(true);

        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
                Loggers.i("onSeekComplete","onSeekComplete----"+mediaPlayer.getCurrentPosition());
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Toast.makeText(MediaPlayerActivity.this, "onPrepare --> Start", Toast.LENGTH_SHORT).show();
                mediaPlayer.start();
            }
        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer m) {
                Toast.makeText(MediaPlayerActivity.this, "End Play", Toast.LENGTH_LONG).show();
                m.stop();
                m.release();
            }
        });

        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
