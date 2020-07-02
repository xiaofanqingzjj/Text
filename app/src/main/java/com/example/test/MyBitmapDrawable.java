package com.example.test;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

import java.io.InputStream;

public class MyBitmapDrawable extends BitmapDrawable {

    public MyBitmapDrawable() {
    }

    public MyBitmapDrawable(Resources res) {
        super(res);
    }

    public MyBitmapDrawable(Bitmap bitmap) {
        super(bitmap);
    }

    public MyBitmapDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
    }

    public MyBitmapDrawable(String filepath) {
        super(filepath);
    }

    public MyBitmapDrawable(Resources res, String filepath) {
        super(res, filepath);
    }

    public MyBitmapDrawable(InputStream is) {
        super(is);
    }

    public MyBitmapDrawable(Resources res, InputStream is) {
        super(res, is);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
}
