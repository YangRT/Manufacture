package com.example.manufacture.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class TagFlowLayout extends FlowLayout implements TagAdapter.OnDataChangedListener{

    private TagAdapter mAdapter;
    private int mMaxSelectCount;

    public void setmSelectCount(int selectCount) {
        this.mMaxSelectCount = selectCount;
    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(TagAdapter adapter){
        mAdapter = adapter;
        mAdapter.setOnDataChangedListener(this);
        onDataChanged();
    }


    @Override
    public void onDataChanged() {
        removeAllViews();
        TagAdapter adapter = mAdapter;
        for(int i = 0;i < adapter.getItemCount();i++){
            View view = adapter.createView(LayoutInflater.from(getContext()),this,i);
            adapter.bindView(view,i);
            addView(view);
            if(view.isSelected()){
                mAdapter.onItemSelected(view,i);
            }else {
                mAdapter.onItemUnselected(view,i);
            }
            bindViewMethod(view,i);
        }
    }

    private void bindViewMethod(View view, final int position) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.onItemViewClick(v,position);
                if(mMaxSelectCount < 0){
                    return;
                }
                // view 状态
                if(!v.isSelected()){
                    if(getSelectedCount() >= mMaxSelectCount){
                        //单选
                        if(getSelectedCount() == 1){
                            View selectedView = getSelectedView();
                            if(selectedView != null){
                                selectedView.setSelected(false);
                                mAdapter.onItemUnselected(selectedView,getPositionForChild(selectedView));
                            }
                        }else {
                            mAdapter.tipForMaxSelectedMax(mMaxSelectCount);
                            return;
                        }
                    }
                }
                if(v.isSelected()){
                    v.setSelected(false);
                    mAdapter.onItemUnselected(v,position);
                }else {
                    v.setSelected(true);
                    mAdapter.onItemSelected(v,position);
                }
            }
        });
    }

    private int getPositionForChild(View selectedView) {
        for(int i = 0;i < getChildCount();i++){
            View view = getChildAt(i);
            if(view == selectedView){
                return i;
            }
        }
        return -1;
    }

    public List<Integer> getSelectedPosition(){
        List<Integer> result = new ArrayList<>();
        for(int i = 0;i < getChildCount();i++){
            View view = getChildAt(i);
            if(view.isSelected()){
                result.add(i);
            }
        }
        return result;
    }

    private View getSelectedView() {
        for(int i = 0;i < getChildCount();i++){
            View view = getChildAt(i);
            if(view.isSelected()){
                return view;
            }
        }
        return null;
    }

    private int getSelectedCount() {
        int result = 0;
        for(int i = 0;i < getChildCount();i++){
            View view = getChildAt(i);
            if(view.isSelected()){
                result++;
            }
        }
        return result;
    }

}
