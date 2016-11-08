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

package com.chillingvan.canvasglsample.filter.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class CommonBaseAdapter<T extends RenderEntity> extends BaseAdapter {

    protected final List<T> entityList;
    private CommonAdapterInternal commonAdapterInternal;

    public CommonBaseAdapter(List<T> entityList) {
        this(entityList, null);
    }

    public CommonBaseAdapter(List<T> entityList, Class<? extends RenderEntity>[] classArr) {
        this.entityList = entityList;
        commonAdapterInternal = new CommonAdapterInternal(classArr);
    }

    @Override
    public int getCount() {
        return entityList.size();
    }

    @Override
    public Object getItem(int position) {
        return entityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return commonAdapterInternal.getItemViewType(entityList.get(position).getClass());
    }

    @Override
    public int getViewTypeCount() {
        return commonAdapterInternal.getViewTypeCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonItemView itemView;
        RenderEntity renderEntity = entityList.get(position);
        if (convertView == null) {
            itemView = renderEntity.createView(parent);
            convertView = itemView.getView();
            convertView.setTag(itemView);
        } else {
            itemView = (CommonItemView) convertView.getTag();
        }
        itemView.render(renderEntity);
        return convertView;
    }
}