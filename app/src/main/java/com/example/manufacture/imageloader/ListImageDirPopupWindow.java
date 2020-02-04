package com.example.manufacture.imageloader;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.manufacture.R;

import java.util.List;

/**
 * @program: Manufacture
 * @description: 图片类型选择
 * @author: YangRT
 * @create: 2019-12-27 23:11
 **/

public class ListImageDirPopupWindow extends PopupWindow {

    private int mWidth;
    private int mHeight;
    private View mConvertView;
    private ListView mListView;
    private List<FolderBean> mDatas;
    private OnDirSelectedListener mListener;

    public ListImageDirPopupWindow(Context context,List<FolderBean> list){
        calculateWidthAndHeight(context);
        mConvertView = LayoutInflater.from(context).inflate(R.layout.popup_main,null);
        mDatas = list;
        setContentView(mConvertView);
        setWidth(mWidth);
        setHeight(mHeight);

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable());

        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        initView(context);
        initEvent();
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mListener != null){
                    mListener.onSelected(mDatas.get(position));
                }
            }
        });

    }

    private void initView(Context context) {
        mListView = mConvertView.findViewById(R.id.popup_list);
        ListAdapter adapter = new ListAdapter(context,0,mDatas);
        mListView.setAdapter(adapter);
    }

    private void calculateWidthAndHeight(Context context) {

        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        mWidth = outMetrics.widthPixels;
        mHeight = (int)(outMetrics.heightPixels * 0.6);

    }

    public interface OnDirSelectedListener{
        void onSelected(FolderBean bean);
    }

    public void setOnDirSelectedListener(OnDirSelectedListener listener){
        mListener = listener;
    }

    private class ListAdapter extends ArrayAdapter<FolderBean>{

        private LayoutInflater mLayoutInflater;
        private List<FolderBean> mList;


        public ListAdapter(@NonNull Context context, int resource, @NonNull List<FolderBean> objects) {
            super(context, resource, objects);
            mLayoutInflater = LayoutInflater.from(context);
            mList = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.item_listview,parent,false);
                viewHolder.mImage = convertView.findViewById(R.id.dir_item_image);
                viewHolder.mCount = convertView.findViewById(R.id.dir_item_count);
                viewHolder.mDirName = convertView.findViewById(R.id.dir_item_name);
                viewHolder.mSelect = convertView.findViewById(R.id.dir_item_select);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            FolderBean bean = mList.get(position);
            viewHolder.mImage.setImageResource(R.drawable.first);
            ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(bean.getFirstImagePath(),viewHolder.mImage);
            viewHolder.mDirName.setText(bean.getDirName());
            viewHolder.mCount.setText(bean.getCount()+"");
            return convertView;
        }

        private class ViewHolder{
            ImageView mImage;
            TextView mDirName;
            TextView mCount;
            ImageView mSelect;
        }
    }
}
