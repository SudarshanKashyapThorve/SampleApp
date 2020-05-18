package com.au10tix.au10sample.AsyncTasks;

import android.graphics.Bitmap;

import com.senticore.au10tix.sdk.image.jpeg.JpegImage;
import com.senticore.au10tix.sdk.utils.ImageUtil;

public class ResultObject {
    JpegImage jpegImage;
    int type = -1;
    String extra = "";

    public ResultObject(JpegImage jpegImage, int type) {
        this.jpegImage = jpegImage;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Bitmap getPhotoBitmap() {
        Bitmap photo = null;
        if (jpegImage != null) {
            photo = ImageUtil.jpegImageToBitmap(jpegImage);
        }
        jpegImage = null;
        return photo;
    }

}
