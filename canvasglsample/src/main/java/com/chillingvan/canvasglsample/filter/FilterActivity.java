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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SeekBar;

import com.chillingvan.canvasgl.textureFilter.BasicTextureFilter;
import com.chillingvan.canvasgl.textureFilter.ColorMatrixFilter;
import com.chillingvan.canvasgl.textureFilter.ContrastFilter;
import com.chillingvan.canvasgl.textureFilter.CropFilter;
import com.chillingvan.canvasgl.textureFilter.DarkenBlendFilter;
import com.chillingvan.canvasgl.textureFilter.DirectionalSobelEdgeDetectionFilter;
import com.chillingvan.canvasgl.textureFilter.FilterGroup;
import com.chillingvan.canvasgl.textureFilter.GammaFilter;
import com.chillingvan.canvasgl.textureFilter.HueFilter;
import com.chillingvan.canvasgl.textureFilter.LightenBlendFilter;
import com.chillingvan.canvasgl.textureFilter.LookupFilter;
import com.chillingvan.canvasgl.textureFilter.OneValueFilter;
import com.chillingvan.canvasgl.textureFilter.PixelationFilter;
import com.chillingvan.canvasgl.textureFilter.RGBFilter;
import com.chillingvan.canvasgl.textureFilter.SaturationFilter;
import com.chillingvan.canvasgl.textureFilter.TextureFilter;
import com.chillingvan.canvasglsample.R;
import com.chillingvan.canvasglsample.filter.adapter.CommonBaseAdapter;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.cyberagent.android.gpuimage.GPUImageColorMatrixFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDarkenBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDirectionalSobelEdgeDetectionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLightenBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLookupFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePixelationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageRGBFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;

public class FilterActivity extends AppCompatActivity {

    public static final int REQUEST_PICK_IMAGE_FIRST = 12;
    public static final int REQUEST_PICK_IMAGE_SEC = 34;
    private ListView listView;
    private CommonBaseAdapter<CaseEntity> adapter;
    private List<CaseEntity> renderEntityList = new ArrayList<>();
    private final static Map<Class, Range> FILTER_RANGE_MAP = new HashMap<Class, Range>() {
        {
            put(ContrastFilter.class, new Range(0, 4));
            put(SaturationFilter.class, new Range(0, 2));
            put(PixelationFilter.class, new Range(1, 100));
            put(HueFilter.class, new Range(0, 360));
            put(GammaFilter.class, new Range(0, 3));
            put(RGBFilter.class, new Range(0, 1));
            put(ColorMatrixFilter.class, new Range(0, 1));
            put(DirectionalSobelEdgeDetectionFilter.class, new Range(0, 5));
            put(LookupFilter.class, new Range(0, 1));
        }
    };

    private List<FilterAdjuster> filterAdjusters = new ArrayList<>();
    private Bitmap secondBitmap;
    private Bitmap firstBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        initData();
        listView = (ListView) findViewById(R.id.filter_list);
        initSeekBar();



        adapter = new CommonBaseAdapter<>(renderEntityList);
        listView.setAdapter(adapter);
        refreshDataList();
    }

    private void refreshDataList() {
        renderEntityList.clear();
        filterAdjusters.clear();
        renderEntityList.addAll(createRenderEntities());
        for (CaseEntity caseEntity : renderEntityList) {
            filterAdjusters.add(new FilterAdjuster(caseEntity.getGpuImageFilter()));
        }
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        secondBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baboon);
        firstBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);
    }

    private void initSeekBar() {
        SeekBar seekBar = (SeekBar) findViewById(R.id.filter_seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                for (CaseEntity renderEntity : renderEntityList) {
                    TextureFilter textureFilter = renderEntity.getTextureFilter();
                    if (textureFilter instanceof OneValueFilter) {
                        ((OneValueFilter) textureFilter).setValue(FILTER_RANGE_MAP.get(textureFilter.getClass()).value(progress));
                    }

                }

                for (FilterAdjuster filterAdjuster : filterAdjusters) {
                    filterAdjuster.adjust(progress);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    @NonNull
    private List<CaseEntity> createRenderEntities() {
        List<CaseEntity> renderEntityList = new ArrayList<>(100);

        BasicTextureFilter basicTextureFilter = new BasicTextureFilter();
        renderEntityList.add(new CaseEntity(basicTextureFilter, new GPUImageFilter(), firstBitmap));


        int width = firstBitmap.getWidth();
        int height = firstBitmap.getHeight();
        CropFilter cropFilter = new CropFilter(CropFilter.calc(width/2, width), 0, 1, CropFilter.calc(height/2, height));
        renderEntityList.add(new CaseEntity(cropFilter, new GPUImageFilter(), firstBitmap));

        Bitmap lookupAmatorka = BitmapFactory.decodeResource(getResources(), R.drawable.lookup_amatorka);
        LookupFilter lookupFilter = new LookupFilter(lookupAmatorka);
        lookupFilter.setValue(0.5f);
        GPUImageLookupFilter gpuImageLookupFilter = new GPUImageLookupFilter();
        gpuImageLookupFilter.setBitmap(lookupAmatorka);
        gpuImageLookupFilter.setIntensity(0.5f);
        renderEntityList.add(new CaseEntity(lookupFilter, gpuImageLookupFilter, firstBitmap));

        ContrastFilter contrastFilter = new ContrastFilter(3.0f);
        GPUImageContrastFilter gpuImageContrastFilter = new GPUImageContrastFilter(3.0f);
        renderEntityList.add(new CaseEntity(contrastFilter, gpuImageContrastFilter, firstBitmap));

        SaturationFilter saturationFilter = new SaturationFilter(0.5f);
        GPUImageSaturationFilter gpuImageSaturationFilter = new GPUImageSaturationFilter(0.5f);
        renderEntityList.add(new CaseEntity(saturationFilter, gpuImageSaturationFilter, firstBitmap));

        List<TextureFilter> filters = new ArrayList<>();
        filters.add(new ContrastFilter(3.0f));
        filters.add(new SaturationFilter(0.5f));
        FilterGroup filterGroup = new FilterGroup(filters);
        GPUImageFilterGroup gpuImageFilterGroup = new GPUImageFilterGroup();
        gpuImageFilterGroup.addFilter(new GPUImageContrastFilter(3.0f));
        gpuImageFilterGroup.addFilter(new GPUImageSaturationFilter(0.5f));
        renderEntityList.add(new CaseEntity(filterGroup, gpuImageFilterGroup, firstBitmap));

        HueFilter hueFilter = new HueFilter(190);
        GPUImageHueFilter gpuImageHueFilter = new GPUImageHueFilter(190);
        renderEntityList.add(new CaseEntity(hueFilter, gpuImageHueFilter, firstBitmap));

        PixelationFilter pixelationFilter = new PixelationFilter(36);
        GPUImagePixelationFilter gpuImagePixelationFilter = new GPUImagePixelationFilter();
        gpuImagePixelationFilter.setPixel(36);
        renderEntityList.add(new CaseEntity(pixelationFilter, gpuImagePixelationFilter, firstBitmap));

        LightenBlendFilter lightenBlendFilter = new LightenBlendFilter(secondBitmap);
        GPUImageLightenBlendFilter gpuImageLightenBlendFilter = new GPUImageLightenBlendFilter();
        gpuImageLightenBlendFilter.setBitmap(secondBitmap);
        renderEntityList.add(new CaseEntity(lightenBlendFilter, gpuImageLightenBlendFilter, firstBitmap));

        DarkenBlendFilter darkenBlendFilter = new DarkenBlendFilter(secondBitmap);
        GPUImageDarkenBlendFilter gpuImageDarkenBlendFilter = new GPUImageDarkenBlendFilter();
        gpuImageDarkenBlendFilter.setBitmap(secondBitmap);
        renderEntityList.add(new CaseEntity(darkenBlendFilter, gpuImageDarkenBlendFilter, firstBitmap));


        GammaFilter gammaFilter = new GammaFilter(2.0f);
        GPUImageGammaFilter gpuImageGammaFilter = new GPUImageGammaFilter(2.0f);
        renderEntityList.add(new CaseEntity(gammaFilter, gpuImageGammaFilter, firstBitmap));

        DirectionalSobelEdgeDetectionFilter directionalSobelEdgeDetectionFilter = new DirectionalSobelEdgeDetectionFilter(4.0f);
        GPUImageDirectionalSobelEdgeDetectionFilter gpuImageDirectionalSobelEdgeDetectionFilter = new GPUImageDirectionalSobelEdgeDetectionFilter();
        gpuImageDirectionalSobelEdgeDetectionFilter.setLineSize(4.0f);
        renderEntityList.add(new CaseEntity(directionalSobelEdgeDetectionFilter, gpuImageDirectionalSobelEdgeDetectionFilter, firstBitmap));

        RGBFilter rgbFilter = new RGBFilter(1.0f, 0.3f, 0.5f);
        GPUImageRGBFilter gpuImageRGBFilter = new GPUImageRGBFilter(1.0f, 0.3f, 0.5f);
        renderEntityList.add(new CaseEntity(rgbFilter, gpuImageRGBFilter, firstBitmap));

        float[] matrix4 = {
                1.0f, 0.0f, 0.0f, 0.3f,
                0.0f, 1.0f, 0.0f, 0.4f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        };
        ColorMatrixFilter colorMatrixFilter = new ColorMatrixFilter(0.3f, matrix4);
        GPUImageColorMatrixFilter gpuImageColorMatrixFilter = new GPUImageColorMatrixFilter(0.3f, matrix4);
        renderEntityList.add(new CaseEntity(colorMatrixFilter, gpuImageColorMatrixFilter, firstBitmap));

        return renderEntityList;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE_FIRST:
                if (resultCode == RESULT_OK) {
                    try {
                        firstBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    refreshDataList();
                } else {
                    finish();
                }
                break;

            case REQUEST_PICK_IMAGE_SEC:
                if (resultCode == RESULT_OK) {
                    try {
                        secondBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    refreshDataList();
                } else {
                    finish();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public void selectFirstImage(View view) {
        startPicActivity(REQUEST_PICK_IMAGE_FIRST);
    }

    public void selectSecondImage(View view) {
        startPicActivity(REQUEST_PICK_IMAGE_SEC);
    }

    private void startPicActivity(int requestPickImage) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, requestPickImage);
    }

    public static class Range {
        float min ;
        float max;

        public Range(float min, float max) {
            this.min = min;
            this.max = max;
        }

        public float value(float percentage) {
            return min + (max - min) * (percentage /100);
        }
    }
}
