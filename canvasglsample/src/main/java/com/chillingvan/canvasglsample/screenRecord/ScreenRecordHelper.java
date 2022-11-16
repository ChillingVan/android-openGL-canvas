package com.chillingvan.canvasglsample.screenRecord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Surface;

import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.GLTexture;

import androidx.annotation.RequiresApi;

/**
 * Created by Chilling on 2020/3/7.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScreenRecordHelper {

    public static final int REQUEST_MEDIA_PROJECTION = 1;

    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private RawTexture mRawTexture;
    private Surface mSurface;
    private Activity mActivity;
    private int mScreenDensity;
    private int mActivityResultCode;
    private Intent mActivityResultData;

    public void init(Activity activity, GLTexture glTexture) {
        mActivity = activity;
        mSurface = new Surface(glTexture.getSurfaceTexture());
        mRawTexture = glTexture.getRawTexture();
        mMediaProjectionManager = (MediaProjectionManager) activity.
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
    }

    public void start() {
        if (mRawTexture == null) {
            return;
        }
        if (mMediaProjection != null) {
            setupVirtualDisplay();
        } else if (mActivityResultCode != 0 && mActivityResultData != null) {
            setUpMediaProjection();
            setupVirtualDisplay();
        } else {
            startRequestPermission();
        }
    }

    private void startRequestPermission() {
        mActivity.startActivityForResult(
                mMediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION);
    }

    public void fetchPermissionResultCode(int resultCode, Intent data) {
        mActivityResultCode = resultCode;
        mActivityResultData = data;
    }

    private void setUpMediaProjection() {
        mMediaProjection = mMediaProjectionManager.getMediaProjection(mActivityResultCode, mActivityResultData);
    }

    private void setupVirtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                mRawTexture.getWidth(), mRawTexture.getHeight(), mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mSurface, null, null);
    }

    public void stopScreenCapture() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
    }

    public void destroy() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    public boolean isRecording() {
        return mVirtualDisplay != null;
    }
}
