package com.example.manufacture.imageloader;

/**
 * @program: Manufacture
 * @description: 图片分类相关信息
 * @author: YangRT
 * @create: 2019-12-27 21:02
 **/

public class FolderBean {

    //目录名
    private String dir;

    //分类名字
    private String dirName;

    //第一张图片路径
    private String firstImagePath;

    //包含图片数目
    private int count;

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        int lastIndexOf = this.dir.lastIndexOf("/");
        this.dirName = this.dir.substring(lastIndexOf+1);
    }

    public String getDirName() {
        return dirName;
    }



    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
