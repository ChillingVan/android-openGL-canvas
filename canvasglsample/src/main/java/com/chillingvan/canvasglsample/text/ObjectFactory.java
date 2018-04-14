package com.chillingvan.canvasglsample.text;

import android.support.annotation.Nullable;
import android.support.v4.util.Pools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Chilling on 2018/4/14.
 */
public abstract class ObjectFactory<T> {

    private static final int OVER_STOCK_MAX_SIZE = 300;
    private ObjectPool<T> pool = new ObjectPool<>(600);
    private final AvailableItems<T> availableBalloons = new AvailableItems<>(600);
    private List<T> overStockItems = Collections.synchronizedList(new ArrayList<T>(100));
    private final AvailableItems.OnRemoveCallback onRemoveCallback;

    public ObjectFactory() {
        onRemoveCallback = new AvailableItems.OnRemoveCallback() {
            @Override
            public void onRemove() {
                availableBalloons.putNewProductions(overStockItems);
            }
        };
    }

    public List<T> getAvailableItems() {
        return availableBalloons.getAvailableItems();
    }

    public void release(T obj) {
        pool.release(obj);
        availableBalloons.remove(obj, onRemoveCallback);
    }


    public void book(int bookingCnt) {
        for (int i = 0; i < bookingCnt && overStockItems.size() < OVER_STOCK_MAX_SIZE; i++) {
            T obj = produce(pool.acquire());
            if (!availableBalloons.putNewProductions(obj)) {
                overStockItems.add(obj);
            }
        }
    }

    protected abstract T produce(@Nullable T objFromPool);


    private static class AvailableItems<T> {

        private Queue<T> queue = new LinkedBlockingQueue<>();

        int maxCnt;

        public AvailableItems(int maxCnt) {
            this.maxCnt = maxCnt;
        }

        public List<T> getAvailableItems() {
            return new ArrayList<>(queue);
        }

        List<T> putNewProductions(List<T> items) {
            if (queue.size() < maxCnt) {
                int canAddSize = maxCnt - queue.size();
                queue.addAll(items.subList(0, canAddSize > items.size() ? items.size() : canAddSize));
                items.removeAll(queue);
            }
            return items;
        }

        boolean putNewProductions(T item) {
            if (queue.size() < maxCnt) {
                queue.add(item);
                return true;
            }
            return false;
        }

        public void remove(T item, OnRemoveCallback callback) {
            queue.remove(item);
            if (callback != null) {
                callback.onRemove();
            }
        }

        public interface OnRemoveCallback {
            void onRemove();
        }
    }

    private static class ObjectPool<T> implements Pools.Pool<T> {

        private final Object[] mPool;

        private int mPoolSize;

        /**
         * Creates a new instance.
         *
         * @param maxPoolSize The max pool size.
         * @throws IllegalArgumentException If the max pool size is less than zero.
         */
        public ObjectPool(int maxPoolSize) {
            if (maxPoolSize <= 0) {
                throw new IllegalArgumentException("The max pool size must be > 0");
            }
            mPool = new Object[maxPoolSize];
        }

        @Override
        @SuppressWarnings("unchecked")
        public T acquire() {
            if (mPoolSize > 0) {
                final int lastPooledIndex = mPoolSize - 1;
                T instance = (T) mPool[lastPooledIndex];
                mPool[lastPooledIndex] = null;
                mPoolSize--;
                return instance;
            }
            return null;
        }

        @Override
        public boolean release(T instance) {
            if (isInPool(instance)) {
                return false;
            }
            if (mPoolSize < mPool.length) {
                mPool[mPoolSize] = instance;
                mPoolSize++;
                return true;
            }
            return false;
        }

        private boolean isInPool(T instance) {
            for (int i = 0; i < mPoolSize; i++) {
                if (mPool[i] == instance) {
                    return true;
                }
            }
            return false;
        }
    }
}
