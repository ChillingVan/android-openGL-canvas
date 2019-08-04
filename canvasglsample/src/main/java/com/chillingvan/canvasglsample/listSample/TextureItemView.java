package com.chillingvan.canvasglsample.listSample;

import android.view.View;
import android.widget.Button;

import com.chillingvan.canvasgl.glview.texture.GLMultiTexConsumerView;
import com.chillingvan.canvasglsample.R;
import com.chillingvan.canvasglsample.util.adapter.BaseItemView;

/**
 * Created by Chilling on 2019/6/30.
 */
public class TextureItemView extends BaseItemView<TextureItemEntity> {

    private final GLMultiTexConsumerView textureView;
    private final Button showTextureBtn;

    public TextureItemView(View itemView) {
        super(itemView);
        textureView = itemView.findViewById(R.id.texture_item);
        showTextureBtn = itemView.findViewById(R.id.btn_show_texture);
    }

    @Override
    public void render(final TextureItemEntity renderEntity) {
        showTextureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renderEntity.getOnClickShowCallback().onShow(textureView);
            }
        });
    }
}
