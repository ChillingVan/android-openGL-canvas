package com.chillingvan.canvasglsample.util.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Matthew on 2016/4/7.
 */
public class CommonRecyclerAdapter<T extends RenderEntity> extends RecyclerView.Adapter<BaseItemView>{


    protected final List<T> entityList;
    private SparseArray<RenderEntity> viewTypeMapViewCreator = new SparseArray<>(10);
    private CommonAdapterInternal commonAdapterInternal;

    public CommonRecyclerAdapter(List<T> entityList) {
        this(entityList, null);
    }

    public CommonRecyclerAdapter(List<T> entityList, Class<? extends RenderEntity>[] classArr) {
        this.entityList = entityList;
        commonAdapterInternal = new CommonAdapterInternal(classArr);
    }


    @Override
    public BaseItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewTypeMapViewCreator.get(viewType).createView(parent);
    }

    @Override
    public void onBindViewHolder(BaseItemView holder, int position) {
        holder.render(entityList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        RenderEntity renderEntity = entityList.get(position);
        Integer viewType = commonAdapterInternal.getItemViewType(entityList.get(position).getClass());
        viewTypeMapViewCreator.put(viewType, renderEntity);
        return viewType;
    }

    @Override
    public int getItemCount() {
        return entityList.size();
    }
}
