package com.chillingvan.canvasglsample.util.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Matthew on 2016/4/8.
 */
public abstract class BaseItemView<T extends RenderEntity> extends RecyclerView.ViewHolder implements CommonItemView<T>{

    protected View itemView;

    public BaseItemView(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    @Override
    public View getView() {
        return itemView;
    }
}
