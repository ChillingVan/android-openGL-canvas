/*
 *
 *  *
 *  *  * Copyright (C) 2016 ChillingVan
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *
 *
 */

package com.chillingvan.canvasglsample.filter;

import android.view.View;
import android.widget.TextView;

import com.chillingvan.canvasglsample.R;
import com.chillingvan.canvasglsample.filter.adapter.CommonItemView;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by Chilling on 2016/10/24.
 */

public class CaseItemView implements CommonItemView<CaseEntity> {

    private View view;
    private final FilterGLView glView;
    private final TextView filterNameTxt;
    private final GPUImageView gpuImageView;

    public CaseItemView(View view) {
        this.view = view;
        glView = (FilterGLView) view.findViewById(R.id.gl_view);
        filterNameTxt = (TextView) view.findViewById(R.id.filter_name_txt);
        gpuImageView = (GPUImageView) view.findViewById(R.id.gpu_image_view);

    }

    @Override
    public void render(CaseEntity caseEntity) {
        filterNameTxt.setText(caseEntity.getFilterName());

        glView.setTextureFilter(caseEntity.getTextureFilter());
        glView.setBitmap(caseEntity.getFirstBitmap());
        glView.requestRender();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                glView.requestRender();
                gpuImageView.requestRender();
            }
        });

        gpuImageView.getGPUImage().deleteImage();
        gpuImageView.setImage(caseEntity.getFirstBitmap());
        gpuImageView.setFilter(caseEntity.getGpuImageFilter());
        gpuImageView.requestRender();
    }

    @Override
    public View getView() {
        return view;
    }
}
