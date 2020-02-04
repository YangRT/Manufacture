package com.example.manufacture.zoomimageview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatImageView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;


import com.example.manufacture.R;

public class ZoomActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private int[] images = new int[]{R.drawable.zoom_1,R.drawable.zoom_2,R.drawable.zoom_3};
    private AppCompatImageView[] imageViews = new AppCompatImageView[images.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);
        mViewPager = findViewById(R.id.zoom_view_pager);
        mViewPager.setAdapter(new PagerAdapter() {

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                ZoomImageView zoomImageView = new ZoomImageView(container.getContext());
                zoomImageView.setImageResource(images[position]);
                container.addView(zoomImageView);
                imageViews[position] = zoomImageView;
                return zoomImageView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
               container.removeView(imageViews[position]);
            }

            @Override
            public int getCount() {
                return imageViews.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }
        });
    }
}
