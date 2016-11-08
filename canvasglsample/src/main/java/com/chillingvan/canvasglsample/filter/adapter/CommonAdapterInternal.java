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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew on 2016/5/31.
 */
public class CommonAdapterInternal {
    private Map<Class, Integer> classMapViewType = new HashMap<>(10);

    public CommonAdapterInternal(Class<? extends RenderEntity>[] classArr) {
        for (int i = 0; classArr != null && i < classArr.length; i++) {
            classMapViewType.put(classArr[i], i);
        }
    }

    public int getItemViewType(Class entityCls) {
        Integer viewType = classMapViewType.get(entityCls);
        return viewType == null ? 0 : viewType;
    }

    public int getViewTypeCount() {
        return classMapViewType.size() == 0 ? 1 : classMapViewType.size();
    }
}
