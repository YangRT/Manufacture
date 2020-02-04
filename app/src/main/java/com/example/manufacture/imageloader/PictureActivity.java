package com.example.manufacture.imageloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manufacture.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @program: Manufacture
 * @description: 图片显示activity
 * @author: YangRT
 * @create: 2019-12-27 21:00
 **/

public class PictureActivity extends AppCompatActivity {

    private GridView mGridView;
    private RelativeLayout mBottomLayout;
    private TextView mDirName;
    private TextView mPicCount;
    private ListImageDirPopupWindow mPopupWindow;

    private List<String> mList;

    private File mCurrentDir;
    private int mMaxCount;
    private ImageAdapter mImageAdapter;

    private List<FolderBean> mFolderBeans = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            data2View();
            initPopupWindow();
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        initView();
        initData();
        initEvent();
    }

    private void initPopupWindow() {
        mPopupWindow = new ListImageDirPopupWindow(PictureActivity.this,mFolderBeans);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
            }
        });
        mPopupWindow.setOnDirSelectedListener(new ListImageDirPopupWindow.OnDirSelectedListener() {
            @Override
            public void onSelected(FolderBean bean) {
                mCurrentDir = new File(bean.getDir());
                mList = Arrays.asList(mCurrentDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if(name.endsWith(".gif") || name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")){
                            return true;
                        }
                        return false;
                    }
                }));
                mImageAdapter = new ImageAdapter(PictureActivity.this,mList,mCurrentDir.getAbsolutePath());
                mGridView.setAdapter(mImageAdapter);
                mDirName.setText(bean.getDirName());
                mPicCount.setText(mList.size()+"");
                mPopupWindow.dismiss();
            }
        });
    }

    private void lightOn(){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }

    private void lightOff(){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.3f;
        getWindow().setAttributes(lp);
    }

    private void data2View() {
        if(mCurrentDir == null){
            Toast.makeText(PictureActivity.this,"没扫描到图片！！！",Toast.LENGTH_SHORT).show();
            return;
        }
        mList = Arrays.asList(mCurrentDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name.endsWith(".gif") || name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")){
                    return true;
                }
                return false;
            }}));
        mImageAdapter = new ImageAdapter(PictureActivity.this,mList,mCurrentDir.getAbsolutePath());
        mGridView.setAdapter(mImageAdapter);
        mPicCount.setText(mMaxCount+"");
        mDirName.setText(mCurrentDir.getName());


    }

    private void initView() {
        mGridView = findViewById(R.id.pic_gridview);
        mBottomLayout = findViewById(R.id.pic_bottom_layout);
        mDirName = findViewById(R.id.pic_bottom_type);
        mPicCount = findViewById(R.id.pic_bottom_count);

    }

    //利用ContentProvider 扫描图片
    private void initData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = PictureActivity.this.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DATE_MODIFIED );
                Set<String> mDirPaths = new HashSet<>();
                while (cursor.moveToNext()){
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    System.out.println(path);
                    File parentFile = new File(path).getParentFile();
                    if(parentFile == null){
                        continue;
                    }
                    String dirPath = parentFile.getAbsolutePath();
                    System.out.println(dirPath);
                    FolderBean folderBean = null;
                    if(mDirPaths.contains(dirPath)){
                        continue;
                    }else {
                        mDirPaths.add(dirPath);
                        folderBean = new FolderBean();
                        folderBean.setDir(dirPath);
                        folderBean.setFirstImagePath(path);
                    }
                    if(parentFile.list() == null) continue;
                    int picCount= parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            if(name.endsWith(".gif") || name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")){
                                return true;
                            }
                            return false;
                        }
                    }).length;
                    folderBean.setCount(picCount);
                    mFolderBeans.add(folderBean);

                    if(picCount > mMaxCount){
                        mMaxCount = picCount;
                        mCurrentDir = parentFile;
                    }
                }
                cursor.close();
                mHandler.sendEmptyMessage(0x110);
            }
        }).start();

    }

    private void initEvent() {
        mBottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.showAsDropDown(mBottomLayout,0,0);
                lightOff();
            }
        });
    }



}
