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

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.chillingvan.canvasgl.textureFilter.TextureFilter;
import com.chillingvan.canvasglsample.R;
import com.chillingvan.canvasglsample.filter.adapter.CommonItemView;
import com.chillingvan.canvasglsample.filter.adapter.RenderEntity;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by Chilling on 2016/10/24.
 */

public class CaseEntity implements RenderEntity {


    private String filterName;
    private TextureFilter textureFilter;
    private GPUImageFilter gpuImageFilter;
    private Bitmap firstBitmap;

    public CaseEntity(TextureFilter textureFilter, GPUImageFilter gpuImageFilter, Bitmap firstBitmap) {
        this.filterName = textureFilter.getClass().getSimpleName();
        this.textureFilter = textureFilter;
        this.gpuImageFilter = gpuImageFilter;
        this.firstBitmap = firstBitmap;
    }

    public Bitmap getFirstBitmap() {
        return firstBitmap;
    }

    public GPUImageFilter getGpuImageFilter() {
        return gpuImageFilter;
    }

    public String getFilterName() {
        return filterName;
    }

    public TextureFilter getTextureFilter() {
        return textureFilter;
    }

    @Override
    public CommonItemView createView(ViewGroup parent) {
        return new CaseItemView(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_case_filter, parent, false));
    }
}
