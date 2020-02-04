package com.example.manufacture.scratchcard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.manufacture.R;

public class ScratchCard extends View {

    private Paint mOutterPaint;
    private Path mPath;
    private Canvas mCanvas;
    private Bitmap mBitmap;
    private int mLastX;
    private int mLastY;

    private Bitmap bitmap;

    private String mText;
    private Paint mBackPaint;
    private Rect mTextBound;
    private int mTextSize;

    private OnScratchFinishedListener mListener;

    private volatile boolean mComplete = false;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            int width = getWidth();
            int height = getHeight();
            float wipeArea = 0;
            float totalArea = width*height;
            Bitmap bitmap = mBitmap;
            int[] mPixels = new int[width*height];
            //获得所有Bitmap上像素信息
            mBitmap.getPixels(mPixels,0,width,0,0,width,height);
            for(int i = 0;i < height;i++){
                for(int j = 0;j < width;j++){
                    int index = j + i * width;
                    if(mPixels[index] == 0){
                        wipeArea++;
                    }
                }
            }
            if(wipeArea>0 && totalArea>0){
                int precent = (int)(wipeArea*100/totalArea);
                if(precent>65){
                    //清除图层区域
                    mComplete = true;
                    postInvalidate();

                }
            }
        }
    };

    public ScratchCard(Context context) {
        this(context,null);
    }

    public ScratchCard(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScratchCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.parseColor("#c0c0c0"));
    }

    private void init() {
        mOutterPaint = new Paint();
        mPath = new Path();
        initOutterPaint();
      //  bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.bg);
        mText = "谢谢惠顾！";
        mTextSize = 30;
        mBackPaint = new Paint();
        mTextBound = new Rect();
        initBackPaint();
    }

    private void initOutterPaint() {
        mOutterPaint.setColor(Color.RED);
        mOutterPaint.setAntiAlias(true);
        mOutterPaint.setDither(true);
        mOutterPaint.setStrokeJoin(Paint.Join.ROUND);
        mOutterPaint.setStrokeCap(Paint.Cap.ROUND);
        mOutterPaint.setStyle(Paint.Style.STROKE);
        mOutterPaint.setStrokeWidth(20);
    }

    //初始化获奖信息画笔
    private void initBackPaint(){
        mBackPaint.setColor(Color.BLACK);
        mBackPaint.setTextSize(mTextSize);
        mBackPaint.setStyle(Paint.Style.FILL);
        mBackPaint.getTextBounds(mText,0,mText.length(),mTextBound);

    }

    @Override
    protected void onDraw(Canvas canvas) {
     //   canvas.drawBitmap(bitmap,0,0,null);
        canvas.drawText(mText,getWidth()/2-mTextBound.width()/2,getHeight()/2+mTextBound.height()/2,mBackPaint);
        if(!mComplete){
            drawPath();
            canvas.drawBitmap(mBitmap,0,0,null);
        }
        if(mComplete){
            if(mListener != null){
                mListener.finished();
            }
        }


    }

    private void drawPath() {
        mOutterPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mCanvas.drawPath(mPath,mOutterPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mPath.moveTo(mLastX,mLastY);
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = Math.abs(x-mLastX);
                int dy = Math.abs(y-mLastY);
                if(dx>3 || dy>3){
                    mPath.lineTo(x,y);
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                new Thread(mRunnable).start();
                break;
        }
        invalidate();
        return true;
    }

    public interface OnScratchFinishedListener{
        void finished();
    }

    public void setOnScratchFinishedListener(OnScratchFinishedListener listener){
        mListener = listener;
    }
}
