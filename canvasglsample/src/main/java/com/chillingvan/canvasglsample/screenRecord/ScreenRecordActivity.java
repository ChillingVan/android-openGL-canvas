package com.chillingvan.canvasglsample.screenRecord;

import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chillingvan.canvasgl.glcanvas.RawTexture;
import com.chillingvan.canvasgl.glview.texture.GLMultiTexProducerView;
import com.chillingvan.canvasgl.glview.texture.GLTexture;
import com.chillingvan.canvasglsample.R;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static com.chillingvan.canvasglsample.screenRecord.ScreenRecordHelper.REQUEST_MEDIA_PROJECTION;

/**
 * Created by Chilling on 2020/3/7.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScreenRecordActivity extends AppCompatActivity {


    private ScreenRecordTextureView mScreenRecordTextureView;
    private TextView mToggleBtn;
    private ScreenRecordHelper mScreenRecordHelper = new ScreenRecordHelper();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_record);
        mScreenRecordTextureView = findViewById(R.id.texture_screen_record);
        mToggleBtn = findViewById(R.id.btn_start_screen_record);

        initTextureView();
        mToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScreenRecordHelper.isRecording()) {
                    mToggleBtn.setText("Start");
                    mScreenRecordHelper.stopScreenCapture();
                } else {
                    mToggleBtn.setText("Stop");
                    mScreenRecordHelper.start();
                }
            }
        });
    }

    private void initTextureView() {
        mScreenRecordTextureView.setSurfaceTextureCreatedListener(new GLMultiTexProducerView.SurfaceTextureCreatedListener() {
            @Override
            public void onCreated(List<GLTexture> producedTextureList) {
                GLTexture texture = producedTextureList.get(0);
                SurfaceTexture surfaceTexture = texture.getSurfaceTexture();
                RawTexture rawTexture = texture.getRawTexture();

                // SurfaceTexture need to call this for screen record
                surfaceTexture.setDefaultBufferSize(rawTexture.getWidth(), rawTexture.getHeight());
                surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        mScreenRecordTextureView.requestRender();
                    }
                });
                mScreenRecordHelper.init(ScreenRecordActivity.this, texture);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "User Canceled", Toast.LENGTH_SHORT).show();
                return;
            }
            mScreenRecordHelper.fetchPermissionResultCode(resultCode, data);
            mScreenRecordHelper.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScreenRecordHelper.destroy();
    }
}
