package com.example.manufacture.puzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.manufacture.R;

public class PuzzleActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{

    private ImageView[][] array = new ImageView[3][5];
    private GridLayout gridLayout;
    private ImageView nullImageView;
    private GestureDetector gestureDetector;
    private boolean isAniamtioning = false;
    private boolean isStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        gestureDetector = new GestureDetector(this,this);
        //获取图片
        Bitmap bitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.pintu,null)).getBitmap();
        //item 宽高
        DisplayMetrics dm2 = getResources().getDisplayMetrics();
        int length = dm2.widthPixels/5;
        //初始化方块
        for(int i = 0 ;i < array.length;i++){
            for(int j = 0; j < array[0].length;j++){
                //切图片 设置图片
                Bitmap item = Bitmap.createBitmap(bitmap,j*length,i*length,length,length);
                array[i][j] = new ImageView(this);
                array[i][j].setImageBitmap(item);
                array[i][j].setTag(new GameItem(j,i,item));
                //设置 item 间距
                array[i][j].setPadding(2,2,2,2);
                array[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean by = isByNullImageView((ImageView) v);
                        Toast.makeText(PuzzleActivity.this,"状态:"+by,Toast.LENGTH_SHORT).show();
                        if(by){
                            changeItemData((ImageView)v);
                        }
                    }
                });
            }
        }
        //初始化游戏主界面
        gridLayout = findViewById(R.id.puzzle_ui);
        for(int i = 0 ;i < array.length;i++){
            for(int j = 0; j < array[0].length;j++){
                gridLayout.addView(array[i][j]);
            }
        }
        // 设置空白 item
        setNullImageView(array[2][4]);
        while (isGameOver()){
            randomMove();
        }
        isStart = true;
    }


    public void setNullImageView(ImageView imageView){
        imageView.setImageBitmap(null);
        nullImageView = imageView;
    }

    //判断当前 item 是否与 空 item 相邻
    public boolean isByNullImageView(ImageView imageView){
        GameItem nullItem = (GameItem) nullImageView.getTag();
        GameItem myItem = (GameItem)imageView.getTag();
        if(nullItem.getX() == myItem.getX() && nullItem.getY()-1 == myItem.getY()){//在空item 上方
            return true;
        }else if(nullItem.getX() == myItem.getX() && nullItem.getY()+1 == myItem.getY()){//在空item 下方
            return true;
        }else if(nullItem.getY() == myItem.getY() && nullItem.getX()-1 == myItem.getX()){//在空item 左方
            return true;
        }else if(nullItem.getY() == myItem.getY() && nullItem.getX()+1 == myItem.getX()){//在空item 右方
            return true;
        }
        return false;
    }

    public void changeItemDataNotAnimation(ImageView imageView){
        GameItem item = (GameItem) imageView.getTag();
        GameItem nullItem = (GameItem)nullImageView.getTag();
        nullImageView.setImageBitmap(item.bitmap);
        nullItem.setBitmap(item.getBitmap());
        nullItem.setPx(item.getPx());
        nullItem.setPy(item.getPy());
        setNullImageView(imageView);
    }

    public void changeItemData(final ImageView imageView){
        if(isAniamtioning){
            return;
        }
        //创建动画 设置移动方向 距离 时长
        TranslateAnimation translateAnimation = null;
        if(imageView.getY() < nullImageView.getY()){//在空 item 上方 往下移
            translateAnimation = new TranslateAnimation(0.1f,0.1f,0.1f,nullImageView.getWidth());
        }else if(imageView.getY() > nullImageView.getY()){//在空 item 下方 往上移
            translateAnimation = new TranslateAnimation(0.1f,0.1f,0.1f,-nullImageView.getWidth());
        }else if(imageView.getX() > nullImageView.getX()){//在空 item 右方 往左移
            translateAnimation = new TranslateAnimation(0.1f,-nullImageView.getWidth(),0.1f,0.1f);
        }else {
            translateAnimation = new TranslateAnimation(0.1f,nullImageView.getWidth(),0.1f,0.1f);
        }
        translateAnimation.setDuration(80);
        translateAnimation.setFillAfter(true);
        //动画结束后交换
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                    isAniamtioning = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAniamtioning = false;
                imageView.clearAnimation();
                GameItem item = (GameItem) imageView.getTag();
                GameItem nullItem = (GameItem)nullImageView.getTag();
                nullImageView.setImageBitmap(item.bitmap);
                nullItem.setBitmap(item.getBitmap());
                nullItem.setPx(item.getPx());
                nullItem.setPy(item.getPy());

                setNullImageView(imageView);
                isGameOver();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }
        });
        imageView.startAnimation(translateAnimation);
    }

    public void changeByDir(int type){
        changeByDir(type,true);
    }

    //根据手势方向移动 左 1 右 2 上 3 下 4
    public void changeByDir(int type,boolean isAnimation){
        GameItem item = (GameItem) nullImageView.getTag();
        int newX = item.getX();
        int newY = item.getY();
        if(type == 1){
            newX++;
        }else if(type == 2){
            newX--;
        }else if(type == 3){
            newY++;
        }else {
            newY--;
        }
        if(newX >= 0 && newX <= 4 && newY >= 0 && newY <= 2){
            if(isAnimation) {
                changeItemData(array[newY][newX]);
            }else {
                changeItemDataNotAnimation(array[newY][newX]);
            }
        }
    }

    //判断手势滑动方向  左 1 右 2 上 3 下 4
    public int getDirByGes(float startX,float startY,float endX,float endY){
        boolean isLeftOrRight = Math.abs(startX-endX) > Math.abs(startY-endY);
        if(isLeftOrRight){//左右
            boolean isLeft = startX > endX;
            if(isLeft){
                return 1;
            }else {
                return 2;
            }
        }else {//上下
            boolean isUp = startY > endY;
            if(isUp){
                return 3;
            }else {
                return 4;
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int type = getDirByGes(e1.getX(),e1.getY(),e2.getX(),e2.getY());
        changeByDir(type);
        return false;
    }

    //随机移动
    public void randomMove(){
        for(int i = 0; i < 40;i++){
            int type = (int)(Math.random()*4);
            changeByDir(type,false);
        }
    }

    //判断游戏是否结束
    public boolean isGameOver(){
        boolean isGameOver = true;
        for(int i = 0 ;i < array.length;i++){
            for(int j = 0; j < array[0].length;j++){
               if(array[i][j] != nullImageView){
                    GameItem item = (GameItem)array[i][j].getTag();
                    if(!item.isTrue()){
                        isGameOver = false;
                        break;
                    }
               }
            }
        }
        if(isGameOver && isStart){
            Toast.makeText(PuzzleActivity.this,"游戏结束！",Toast.LENGTH_SHORT).show();
        }
        return isGameOver;
    }

    class GameItem{

        private int x;
        private int y;
        private Bitmap bitmap;
        private int px;
        private int py;

        public GameItem(int x, int y, Bitmap bitmap) {
            this.x = x;
            this.y = y;
            this.bitmap = bitmap;
            px = x;
            py = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public int getPx() {
            return px;
        }

        public void setPx(int px) {
            this.px = px;
        }

        public int getPy() {
            return py;
        }

        public void setPy(int py) {
            this.py = py;
        }

        public boolean isTrue(){
            if(x == px && y == py){
                return true;
            }
            return false;
        }
    }
}
