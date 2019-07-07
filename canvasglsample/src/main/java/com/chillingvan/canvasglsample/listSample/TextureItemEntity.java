package com.chillingvan.canvasglsample.listSample;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.chillingvan.canvasgl.glview.texture.GLMultiTexConsumerView;
import com.chillingvan.canvasglsample.R;
import com.chillingvan.canvasglsample.util.adapter.BaseItemView;
import com.chillingvan.canvasglsample.util.adapter.RenderEntity;

/**
 * Created by Chilling on 2019/6/30.
 */
public class TextureItemEntity implements RenderEntity {
    private OnClickShowCallback onClickShowCallback;

    public TextureItemEntity(OnClickShowCallback onClickShowCallback) {
        this.onClickShowCallback = onClickShowCallback;
    }

    @Override
    public BaseItemView createView(ViewGroup parent) {
        return new TextureItemView(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_texture_list, parent, false));
    }

    public OnClickShowCallback getOnClickShowCallback() {
        return onClickShowCallback;
    }

    public interface OnClickShowCallback {
        void onShow(GLMultiTexConsumerView textureView);
    }
}
