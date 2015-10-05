package com.icalialabs.airenl.Models;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.icalialabs.airenl.AireNL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Compean on 30/09/15.
 */
public class Screenshot {

    public static Bitmap takeScreenshot(View view) {
        View rootView = view.getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    private Bitmap drawViewToBitmap(Bitmap dest, View view, int downSampling) {
        float scale = 1f / downSampling;
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        int bmpWidth = Math.round(viewWidth * scale);
        int bmpHeight = Math.round(viewHeight * scale);

        if (dest == null || dest.getWidth() != bmpWidth || dest.getHeight() != bmpHeight) {
            dest = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888);
        }

        Canvas c = new Canvas(dest);
        if (downSampling > 1) {
            c.scale(scale, scale);
        }

        view.draw(c);
        return dest;
    }

    public static void saveBitmap(Bitmap bitmap, String outputFileName) {

        try {
            File file = new File(AireNL.getContext().getFilesDir(), outputFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos;
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    public static Bitmap getBitmapFromFile(String fileName) {
        File file = new File(AireNL.getContext().getFilesDir(), fileName);
        return BitmapFactory.decodeFile(file.getPath());
    }

    public static boolean deleteBitmapFile(String fileName) {
        File file = new File(AireNL.getContext().getFilesDir(), fileName);
        if (file.exists()) {
            return new File(AireNL.getContext().getFilesDir(), fileName).delete();
        }
        return false;
    }
}
