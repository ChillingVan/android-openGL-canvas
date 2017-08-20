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

package com.chillingvan.canvasgl.textureFilter;


import com.chillingvan.canvasgl.glcanvas.BasicTexture;
import com.chillingvan.canvasgl.glcanvas.GLCanvas;
import com.chillingvan.canvasgl.glcanvas.RawTexture;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chilling on 2016/10/27.
 */

public class FilterGroup extends BasicTextureFilter {


    protected List<TextureFilter> mFilters;
    protected List<TextureFilter> mMergedFilters;
    private final List<RawTexture> rawTextureList = new ArrayList<>();
    private BasicTexture outputTexture;
    private BasicTexture initialTexture;

    public FilterGroup(List<TextureFilter> mFilters) {
        this.mFilters = mFilters;
        updateMergedFilters();

    }

    private void createTextures(BasicTexture initialTexture) {
        recycleTextures();

        rawTextureList.clear();
        for (int i = 0; i < mMergedFilters.size(); i++) {
            rawTextureList.add(new RawTexture(initialTexture.getWidth(), initialTexture.getHeight(), false));
        }
    }

    private void recycleTextures() {
        for (RawTexture rawTexture : rawTextureList) {
            rawTexture.recycle();
        }
    }


    public BasicTexture draw(BasicTexture initialTexture, GLCanvas glCanvas) {
        if (this.initialTexture == initialTexture && outputTexture != null) {
            return outputTexture;
        }
        this.initialTexture = initialTexture;

        createTextures(initialTexture);
        BasicTexture drawTexture = initialTexture;
        for (int i = 0, size = rawTextureList.size(); i < size; i++) {
            RawTexture rawTexture = rawTextureList.get(i);
            TextureFilter textureFilter = mMergedFilters.get(i);
            glCanvas.beginRenderTarget(rawTexture);
            glCanvas.drawTexture(drawTexture, 0, 0, drawTexture.getWidth(), drawTexture.getHeight(), textureFilter, null);
            glCanvas.endRenderTarget();
            drawTexture = rawTexture;
        }
        outputTexture = drawTexture;

        return drawTexture;
    }

    @Override
    public void destroy() {
        super.destroy();
        recycleTextures();
    }

    public List<TextureFilter> getMergedFilters() {
        return mMergedFilters;
    }


    public void updateMergedFilters() {
        if (mFilters == null) {
            return;
        }

        if (mMergedFilters == null) {
            mMergedFilters = new ArrayList<>();
        } else {
            mMergedFilters.clear();
        }

        List<TextureFilter> filters;
        for (TextureFilter filter : mFilters) {
            if (filter instanceof FilterGroup) {
                ((FilterGroup) filter).updateMergedFilters();
                filters = ((FilterGroup) filter).getMergedFilters();
                if (filters == null || filters.isEmpty())
                    continue;
                mMergedFilters.addAll(filters);
                continue;
            }
            mMergedFilters.add(filter);
        }
    }
}
