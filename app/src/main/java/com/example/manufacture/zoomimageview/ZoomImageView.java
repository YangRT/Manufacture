package com.example.manufacture.zoomimageview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class ZoomImageView extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

    private boolean mFirst = false;

    //初始化时缩放比例
    private float mInitScale;

    //双击时缩放比例
    private float mMidScale;

    //最大缩放比例
    private float mMaxScale;

    private Matrix mScaleMatrix;

    //捕获多点触碰时缩放比例
    private ScaleGestureDetector mScaleGestureDetector;

    //上次多点触控数量
    private int mLastPointerCount;

    private int mTouchSlop;
    private boolean isCanDrag;

    private boolean isAutoScale;

    private float mLastX;
    private float mLastY;

    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;

    private GestureDetector mGestureDetector;

    public ZoomImageView(Context context) {
        this(context,null);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleMatrix = new Matrix();
        super.setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(context,this);
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mGestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if(isAutoScale){
                    return true;
                }
                float x = e.getX();
                float y = e.getY();
                if(getScale() < mMidScale){
//                    mScaleMatrix.setScale(mMidScale/getScale(),mMidScale/getScale(),x,y);
//                    setImageMatrix(mScaleMatrix);
                    postDelayed(new AutoScaleRunnable(mMidScale,x,y),16);
                    isAutoScale = true;
                }else {
//                    mScaleMatrix.setScale(mInitScale/getScale(),mInitScale/getScale(),x,y);
//                    setImageMatrix(mScaleMatrix);
                    postDelayed(new AutoScaleRunnable(mInitScale,x,y),16);
                    isAutoScale = true;
                }
                return true;
            }
        });
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    /** 
    * @Description: 获取加载完成后图片(此方法在布局完成后执行，通过继承接口实现）
    * @Param: [] 
    * @return: void 
    * @Author: YangRT 
    * @Date: 2019/12/28 
    */ 
    @Override
    public void onGlobalLayout() {
         if(!mFirst){
             mFirst = true;
             //控件宽高
             int width = getWidth();
             int height = getHeight();

             Drawable drawable = getDrawable();
             if(drawable == null) return;
             //图片宽高
             int dw = drawable.getIntrinsicWidth();
             int dh = drawable.getIntrinsicHeight();

             float scale = 1.0f;
             if(dw > width && dh <height){
                 scale = width * 1.0f / dw;
             }
             if(dh > height && dw < width){
                 scale = height * 1.0f / dh;
             }
             if((dw > width && dh > height) || (dw < width && dh < height)){
                 scale = Math.min(width*1.0f/dw,height*1.0f/dh);
             }
             mInitScale = scale;
             mMaxScale = mInitScale * 4;
             mMidScale = mInitScale * 2;

             //将图片移至控件中心
             int dx = getWidth()/2 - dw/2;
             int dy = getHeight()/2 - dh/2;
             mScaleMatrix.postTranslate(dx,dy);
             mScaleMatrix.postScale(mInitScale,mInitScale,width/2,height/2);
             setImageMatrix(mScaleMatrix);
         }

    }

    /**
    * @Description: 获取当前图片缩放值
    * @Param: []
    * @return: float
    * @Author: YangRT
    * @Date: 2019/12/28
    */
    public float   getScale(){
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }


    /**
    * @Description: 处理缩放（范围：initScale - maxScale)
    * @Param: [detector]
    * @return: boolean
    * @Author: YangRT
    * @Date: 2019/12/28
    */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        float scale = getScale();
        if(getDrawable() == null) return true;

        //缩放范围控制
        if((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mInitScale && scaleFactor < 1.0f)){
            if(scale * scaleFactor < mInitScale){
                scaleFactor = mInitScale/scale;
            }
            if(scale * scaleFactor > mMaxScale){
                scaleFactor = mMaxScale/scale;
            }
            mScaleMatrix.postScale(scaleFactor,scaleFactor,detector.getFocusX(),detector.getFocusY());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }


    /** 
    * @Description: 缩放时进行边界控制及位置控制
    * @Param: [] 
    * @return: void 
    * @Author: YangRT 
    * @Date: 2019/12/28 
    */ 
    private void checkBorderAndCenterWhenScale() {
        RectF rectF = getMatrixRecF();
        float deltaX = 0;
        float deltaY = 0;
        int width = getWidth();
        int height = getHeight();
        if(rectF.width() >= width){
            if(rectF.left > 0){
                deltaX = -rectF.left;
            }
            if(rectF.right < width){
                deltaX = width - rectF.right;
            }
        }
        if(rectF.height() >= height){
            if(rectF.top > 0){
                deltaY = -rectF.top;
            }
            if(rectF.bottom < height){
                deltaY = height - rectF.bottom;
            }
        }
        //如果图片宽或高小于屏幕宽或高，居中
        if(rectF.width()<width){
            deltaX = width*1.0f/2 - rectF.right + rectF.width()/2;
        }
        if(rectF.height()<height){
            deltaY = height*1.0f/2 - rectF.bottom + rectF.height()/2;
        }
        mScaleMatrix.postTranslate(deltaX,deltaY);
    }

    /**
    * @Description:  获得放大缩小后的宽高，以及l,t,r,b
    * @Param: []
    * @return: android.graphics.RectF
    * @Author: YangRT
    * @Date: 2019/12/28
    */
    private RectF getMatrixRecF(){
        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();

        Drawable drawable = getDrawable();
        if(drawable != null){
            rectF.set(0,0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(mGestureDetector.onTouchEvent(event))
            return true;
        mScaleGestureDetector.onTouchEvent(event);
        float x = 0;
        float y = 0;
        //拿到多点触控数量
        int pointerCount = event.getPointerCount();
        for(int i = 0;i < pointerCount;i++){
            x += event.getX(i);
            y += event.getY(i);
        }
        x /= pointerCount;
        y /= pointerCount;
        if(mLastPointerCount != pointerCount){
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        mLastPointerCount = pointerCount;
        RectF rectF = getMatrixRecF();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(rectF.width() > getWidth() + 0.01||rectF.height() > getHeight()+ 0.01){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(rectF.width() > getWidth() + 0.01||rectF.height() > getHeight()+ 0.01){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                float dx = x - mLastX;
                float dy = y - mLastY;
                if(!isCanDrag){
                    isCanDrag = isMoveAction(dx,dy);
                }
                if(isCanDrag){
                    if(getDrawable() != null){
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        if(rectF.width() < getWidth()){
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }
                        if(rectF.height() < getHeight()){
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }
                        mScaleMatrix.postTranslate(dx,dy);
                        checkBorderAndCenterWhenTranslate();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;
        }
        return true;
    }


    /**
    * @Description: 当移动时进行边界检查
    * @Param: []
    * @return: void
    * @Author: YangRT
    * @Date: 2019/12/28
    */
    private void checkBorderAndCenterWhenTranslate() {
        RectF rectF = getMatrixRecF();
        float deltaX = 0;
        float deltaY = 0;
        int width = getWidth();
        int height = getHeight();
        if(rectF.top > 0 && isCheckTopAndBottom){
            deltaY = -rectF.top;
        }
        if(rectF.bottom < height && isCheckTopAndBottom){
            deltaY = height - rectF.bottom;
        }
        if(rectF.left > 0 && isCheckLeftAndRight){
            deltaX = - rectF.left;
        }
        if(rectF.right < width && isCheckLeftAndRight){
            deltaX = width - rectF.right;
        }
        mScaleMatrix.postTranslate(deltaX,deltaY);

    }

    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt((dx*dx+dy*dy))>mTouchSlop;
    }

    private class AutoScaleRunnable implements Runnable{

        private float mTargetScale;
        private float x;
        private float y;

        private float tmpScale;

        AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if(getScale() < mTargetScale){
                tmpScale = 1.07f;
            }
            if(getScale() > mTargetScale){
                tmpScale = 0.93f;
            }
        }

        @Override
        public void run() {
            mScaleMatrix.postScale(tmpScale,tmpScale,x,y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
            float currentScale = getScale();
            if((tmpScale>1.0f && currentScale < mTargetScale)||(tmpScale < 1.0f && currentScale>mTargetScale)){
                postDelayed(this,16);
            }else {
                float scale = mTargetScale / currentScale;
                mScaleMatrix.postScale(scale,scale,x,y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }
    }
}
