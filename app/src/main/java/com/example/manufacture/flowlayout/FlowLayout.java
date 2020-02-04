package com.example.manufacture.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {


    private List<List<View>> mAllView = new ArrayList<>();
    private List<Integer> mLineHeight = new ArrayList<>();

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mAllView.clear();
        mLineHeight.clear();

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int lineWidth = 0;
        int height = 0;
        int lineHeight = 0;
        List<View> lineView = new ArrayList<>();
        int childCount = getChildCount();
        for(int i = 0;i < childCount;i++){
            View childView = getChildAt(i);
            if(childView.getVisibility() == View.GONE){
                continue;
            }
            measureChild(childView,widthMeasureSpec,heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams)childView.getLayoutParams();
            int cWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int cHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            if(cWidth+lineWidth > widthSize - (getPaddingLeft()+getPaddingRight())){
                mLineHeight.add(lineHeight);
                //换行
                height += lineHeight;
                lineWidth = cWidth;
                lineHeight = cHeight;
                mAllView.add(lineView);
                lineView = new ArrayList<>();
                lineView.add(childView);
            }else {
                lineWidth += cWidth;
                lineHeight = Math.max(lineHeight,cHeight);
                lineView.add(childView);
            }
            if(i == childCount-1){
                height += lineHeight;
                mLineHeight.add(lineHeight);
                mAllView.add(lineView);
            }
        }
        if(heightMode == MeasureSpec.EXACTLY){
            height = heightSize;
        }else if(heightMode == MeasureSpec.AT_MOST){
            height = Math.min(heightSize,height);
            height = height + getPaddingTop() + getBottom();
        }else {
            height = height + getPaddingTop() + getBottom();
        }
        setMeasuredDimension(widthSize,height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int lineNums = mAllView.size();
        for(int i = 0;i < lineNums;i++){
            List<View> views = mAllView.get(i);
            int lineHeight = mLineHeight.get(i);
            for(int j = 0;j < views.size();j++){
                View child = views.get(j);
                MarginLayoutParams lp = (MarginLayoutParams)child.getLayoutParams();
                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();
                child.layout(lc,tc,rc,bc);
                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
            left = getPaddingLeft();
            top += lineHeight;
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }
}
