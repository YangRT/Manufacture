package com.example.manufacture.flowlayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class TagAdapter {

    abstract int getItemCount();

    abstract View createView(LayoutInflater inflater, ViewGroup parent,int position);

    abstract void bindView(View view,int position);

    void tipForMaxSelectedMax(int maxSelectCount){}

    void onItemViewClick(View v,int position){}

    void onItemSelected(View view,int position){}

    void onItemUnselected(View view,int position){}

    void notifyDataSetChanged(){
        if(mListener != null){
            mListener.onDataChanged();
        }
    }

    static interface OnDataChangedListener{
        void onDataChanged();
    }

    private OnDataChangedListener mListener;

    void setOnDataChangedListener(OnDataChangedListener listener){
        mListener = listener;
    }
}
