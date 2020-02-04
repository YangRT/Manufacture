package com.example.manufacture.imageloader;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


/**
 * @program: Manufacture
 * @description: 图片加载类
 * @author: YangRT
 * @create: 2019-12-26 17:22
 **/

public class ImageLoader {
    
    private final static String TAG = "ImageLoader";
    
    private static ImageLoader mInstance;

    //图片缓存核心对象
    private LruCache<String, Bitmap> mLruCache;

    //线程池
    private ExecutorService mThreadPool;
    private static final int DEFAULT_THREAD_COUNT = 1;

    //图片加载策略
    private Type mType = Type.LIFO;

    public enum Type{
        FIFO,LIFO
    }

    //任务队列
    private LinkedList<Runnable> mTaskQueue;

    //后台轮询线程
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;

    //UI 线程Handler
    private Handler mUIHandler;

    private Semaphore mSemaphore = new Semaphore(0);

    private Semaphore mSemaphoreThreadPool;

    private ImageLoader(int threadCount,Type type){
        init(threadCount,type);
    }



    /** 
    * @Description:
    * @Param: [] 
    * @return: com.example.manufacture.imageloader.ImageLoader 
    * @Author: YangRT 
    * @Date: 2019/12/26 
    */ 
    public static ImageLoader getInstance(int threadCount, Type type){
        if(mInstance == null){
            synchronized (ImageLoader.class){
                if(mInstance == null){
                    mInstance = new ImageLoader(threadCount,type);
                }
            }
        }
        return mInstance;
    }


    //初始化变量
    private void init(int threadCount, Type type) {
        //后台轮询线程
        mPoolThread = new Thread(){
            @SuppressLint("HandlerLeak")
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        //从线程池中取出任务
                        mThreadPool.execute(getTask());
                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                mSemaphore.release();
                Looper.loop();
            }
        };

        mPoolThread.start();

        int maxMemory = (int)Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };

        //创建线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mType = type;
        mTaskQueue = new LinkedList<>();

        mSemaphoreThreadPool = new Semaphore(threadCount);
    }

    /**
    * @Description: 为imageView 设置图片
    * @Param: [path, imageView]
    * @return: void
    * @Author: YangRT
    * @Date: 2019/12/27
    */
    @SuppressLint("HandlerLeak")
    public void loadImage(final String path, final ImageView imageView){
        imageView.setTag(path);
        if(mUIHandler == null) {
            Log.e(TAG, "loadImage ;init ui handler");
            mUIHandler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    //异步获取图片，给imageView 设置图片
                    ImageBeanHolder holder = (ImageBeanHolder) msg.obj;
                    ImageView iv = holder.getImageView();
                    String path = holder.getPath();
                    Bitmap bitmap = holder.getBitmap();
                    Log.e(TAG, "loadImage :set image");
                    if (iv.getTag().toString().equals(path)) {
                        iv.setImageBitmap(bitmap);
                        System.out.println("loading success");
                    } else {
                        System.err.println("loading error");
                    }
                }
            };
        }
            //根据path 在LruCache 中获取缓存
            Bitmap bitmap = getBitmapFromLruCache(path);
            if(bitmap != null){
                refreshBitmap(path,imageView,bitmap);
            }else {
                addTaskToQueue(new Runnable(){
                    @Override
                    public void run() {
                        //加载图片 图片压缩
                        Log.e(TAG,"loadImage :add to queue");
                        //1.获取图片显示宽高
                        ImageSize imageSize = getImageSize(imageView);
                        //2.压缩图片
                        Bitmap bm = decodeSampledBitmapFromPath(path,imageSize.getWidth(),imageSize.getHeight());
                        //3.将图片加入缓存
                        addBitmapToLruCache(path,bm);
                        //4.将图片显示
                        refreshBitmap(path,imageView,bm);
                        mSemaphoreThreadPool.release();
                    }
                });
            }

    }

    private void refreshBitmap(String path, ImageView imageView, Bitmap bm) {
        Message message = Message.obtain();
        message.obj = new ImageBeanHolder(bm,imageView,path);
        Log.e(TAG,"loadImage :run to  ui handler");
        mUIHandler.sendMessage(message);
    }

    private void addBitmapToLruCache(String path, Bitmap bm) {
        if(getBitmapFromLruCache(path) == null){
            if(bm != null){
                mLruCache.put(path,bm);
            }
        }
    }

    private Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
        //获取图片真实宽高
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        options.inSampleSize = calculateInSampleSize(options,width,height);

        //根据获得的inSampleSize 解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap =  BitmapFactory.decodeFile(path,options);
        if(bitmap == null){
            Log.e(TAG,"the path is wrong");
        }
        return bitmap;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reWidth, int reHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSmapleSize = 1;
        if(width > reWidth || height > reHeight){
            int widthRadio = Math.round(width*1.0f/reWidth);
            int heightRadio = Math.round(height*1.0f/reHeight);
            inSmapleSize = Math.max(widthRadio,heightRadio);
        }
        return inSmapleSize;
    }

    /**
    * @Description: 获取图片显示宽高
    * @Param: [imageView] 
    * @return: com.example.manufacture.imageloader.ImageLoader.ImageSize 
    * @Author: YangRT 
    * @Date: 2019/12/27 
    */ 
    private ImageSize getImageSize(ImageView imageView){
        ImageSize imageSize = new ImageSize();
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        int width = imageView.getWidth(); //获取实际宽度
        if(width <= 0){
            width = lp.width; //获取imageView 在Layout声明宽度
        }
        if(width <= 0){
            width = imageView.getMaxWidth(); // 获取 最大值
        }
        if(width <= 0){
            width = displayMetrics.widthPixels;
        }
        int height = imageView.getHeight(); //获取实际宽度
        if(height <= 0){
            height = lp.height; //获取imageView 在Layout声明宽度
        }
        if(height <= 0){
            height = imageView.getMaxHeight(); // 获取 最大值
        }
        if(height <= 0){
            height = displayMetrics.heightPixels;
        }
        imageSize.setWidth(width);
        imageSize.setHeight(height);
        return imageSize;
    }

    private synchronized void addTaskToQueue(Runnable runnable) {
        mTaskQueue.add(runnable);
        try {
            if(mPoolThreadHandler == null)
                mSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);

    }

    private Runnable getTask(){
        if(mType == Type.FIFO){
            return mTaskQueue.removeFirst();
        }else {
            return mTaskQueue.removeLast();
        }
    }

    /**
    * @Description:  根据key 在LruCache 中获取缓存
    * @Param: [key]
    * @return: android.graphics.Bitmap
    * @Author: YangRT
    * @Date: 2019/12/27
    */
    private Bitmap getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }

    //图片相关信息
    private class ImageBeanHolder{

        private Bitmap bitmap;
        private ImageView imageView;
        private String path;

         ImageBeanHolder(Bitmap bitmap, ImageView imageView, String path) {
            this.bitmap = bitmap;
            this.imageView = imageView;
            this.path = path;
        }

         Bitmap getBitmap() {
            return bitmap;
        }

        ImageView getImageView() {
            return imageView;
        }

        String getPath() {
            return path;
        }
    }

    private class ImageSize{

        private int width;
        private int height;

        int getWidth() {
            return width;
        }

        void setWidth(int width) {
            this.width = width;
        }

        int getHeight() {
            return height;
        }

        void setHeight(int height) {
            this.height = height;
        }
    }
}


