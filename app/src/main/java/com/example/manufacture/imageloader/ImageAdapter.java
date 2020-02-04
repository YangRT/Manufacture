package com.example.manufacture.imageloader;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.manufacture.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @program: Manufacture
 * @description: 适配器
 * @author: YangRT
 * @create: 2019-12-27 22:32
 **/

public class ImageAdapter extends BaseAdapter{

        private static Set<String> mSelectImages = new HashSet<>();

        private Context context;
        private List<String> mImagePath;
        private LayoutInflater mLayoutInflater;
        private String dirName;


        ImageAdapter(Context context,List<String> datas,String dirName){
            this.context = context;
            this.dirName = dirName;
            mImagePath = datas;
            mLayoutInflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return mImagePath.size();
        }

        @Override
        public Object getItem(int position) {
            return mImagePath.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final AdapterViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new AdapterViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.item_gridview,parent,false);
                viewHolder.mImage = convertView.findViewById(R.id.item_image);
                viewHolder.mSelect = convertView.findViewById(R.id.item_select);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (AdapterViewHolder) convertView.getTag();
            }
            viewHolder.mImage.setImageResource(R.drawable.first);
            viewHolder.mSelect.setImageResource(R.drawable.unselected);
            viewHolder.mImage.setColorFilter(null);
            final String path = dirName+"/"+mImagePath.get(position);
            ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(path,viewHolder.mImage);
            viewHolder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //已被选择
                    if(mSelectImages.contains(path)){
                        mSelectImages.remove(path);
                        viewHolder.mSelect.setImageResource(R.drawable.unselected);
                        viewHolder.mImage.setColorFilter(null);
                    }else {
                        mSelectImages.add(path);
                        viewHolder.mSelect.setImageResource(R.drawable.selected);
                        viewHolder.mImage.setColorFilter(Color.parseColor("#77000000"));
                    }

                }
            });
            if(mSelectImages.contains(path)){
                viewHolder.mSelect.setImageResource(R.drawable.selected);
                viewHolder.mImage.setColorFilter(Color.parseColor("#77000000"));
            }

            return convertView;
        }

        private class AdapterViewHolder{

            ImageView mImage;
            ImageButton mSelect;
        }
    }

