package com.example.manufacture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.manufacture.imageloader.PictureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        EventBus.getDefault().post("777");
//        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//        }else {
//            //queryAlbum();
//            Intent t = new Intent(MainActivity.this, PictureActivity.class);
//            startActivity(t);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().post("777");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealWithString(String msg){
        Log.e("Test","dealWithString-"+msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void solveString(String msg){
        Log.e("Test","solveString-"+msg);
    }


    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    //    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode){
//            case 1:
//                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    //queryAlbum();
//                    Intent t = new Intent(MainActivity.this,PictureActivity.class);
//                    startActivity(t);
//                }else {
//                    Toast.makeText(this,"你拒绝了访问",Toast.LENGTH_LONG).show();
//                }
//                break;
//            default:
//                break;
//        }
//    }
}
