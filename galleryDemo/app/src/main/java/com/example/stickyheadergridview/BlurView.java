package com.example.stickyheadergridview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;


import android.graphics.Color;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;


class BlurView extends AppCompatImageView {
    private static final float BITMAP_SCALE = 0.125f;
    private int tag = 0;

    BlurView(Context context) {
        super(context);
    }

    void show() {
        if (tag++ <= 0) {
            animate().alpha(1f).setDuration(100).start();
        }
    }

    void hide() {
        if (--tag <= 0) {
            animate().alpha(0f).setDuration(100).start();
        }
    }

    void blur() {
        Activity activity = (Activity) getContext();
        if (tag <= 0) {
            View decorView1 = activity.getWindow().getDecorView();
            Bitmap bitmap = Bitmap.createBitmap(decorView1.getWidth(), decorView1.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            decorView1.draw(canvas);
            setBackground(new BitmapDrawable(getResources(), blurBitmap(activity, bitmap, 4)));
            tag = 0;
        }
    }

    public static Bitmap blurBitmap(Context context, Bitmap image, float blurRadius) {
        if (null == image) {
            return null;
        }
        boolean isRgb8888 = false;
        if (image.getConfig() != Bitmap.Config.ARGB_8888) {
            image = RGB565toARGB888(image);
            isRgb8888 = true;
        }
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        if (isRgb8888) {
            image.recycle();
            isRgb8888 = false;
        }
        RenderScript rs = null;
        try {
            rs = RenderScript.create(context);
        } catch (Exception e) {
            e.printStackTrace();

            inputBitmap.recycle();
            outputBitmap.recycle();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.parseColor("#D9454545"));
            return bitmap;
        }
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        blurScript.setRadius(blurRadius);
        blurScript.setInput(tmpIn);
        blurScript.forEach(tmpOut);

        tmpOut.copyTo(outputBitmap);

        blurScript.destroy();
        tmpIn.destroy();
        tmpOut.destroy();
        rs.destroy();
        inputBitmap.recycle();

        return outputBitmap;
    }


    private static Bitmap RGB565toARGB888(Bitmap img) {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());

        return result;
    }


}
