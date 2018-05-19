package com.chillingvan.canvasglsample.video;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Surface;
import android.widget.Toast;

import com.chillingvan.canvasgl.util.Loggers;

import java.io.IOException;
import java.util.Random;

/**
 * Created by Chilling on 2018/4/14.
 */
public class MediaPlayerHelper {

    public static final String TEST_VIDEO_MP4 = "test_video.mp4";
    public static final String TEST_VIDEO_MP4_2 = "test_video_2.mp4";
    private MediaPlayer mediaPlayer;
    private String videoName;

    public MediaPlayerHelper() {
        this(new Random().nextBoolean() ? TEST_VIDEO_MP4 : TEST_VIDEO_MP4_2);
    }

    public MediaPlayerHelper(String videoName) {
        this.videoName = videoName;
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    public boolean isLooping() {
        if (mediaPlayer != null) {
            return mediaPlayer.isLooping();
        }
        return false;
    }

    public void playMedia(final Context context, Surface mediaSurface) {
        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(videoName);
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
                Toast.makeText(context, "onPrepare --> Start", Toast.LENGTH_SHORT).show();
                mediaPlayer.start();
            }
        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer m) {
                Toast.makeText(context, "End Play", Toast.LENGTH_LONG).show();
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

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
