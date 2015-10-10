package com.icalialabs.airenl.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.icalialabs.airenl.Models.AirQualityType;
import com.icalialabs.airenl.Models.Screenshot;
import com.icalialabs.airenl.R;

import jp.wasabeef.blurry.Blurry;

/**
 * Created by Compean on 30/09/15.
 */
public class Popup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.popup);

        roundDots();

//        findViewById(R.id.cross_cancel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        Bitmap image = decodeSampledBitmapFromResource(getResources(), R.drawable.fondodia, size.x / 4, size.y / 4);
//
//
//
//        ImageView popupBackground = (ImageView)findViewById(R.id.popupBackground);
//        popupBackground.setImageBitmap(image);
//
//        popupBackground.setImageBitmap(Screenshot.getBitmapFromFile("main_screen"));
//        Screenshot.deleteBitmapFile("main_screen");
//        View background =  findViewById(R.id.popup_layout);
//        //ImageView blurredBackground = (ImageView) findViewById(R.id.blurImageView);
//        Blurry.with(getApplicationContext())
//                .radius(20)
//                .sampling(1)
//                .capture(background)
//                .into(popupBackground);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout((int) (width * .9), (int) (height * 0.7));
        getWindow().setDimAmount(0.1f);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        height = height - getStatusBarHeight();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.y = height/2 - (int) (height * 0.7)/2 + getStatusBarHeight()/2;
        params.x = width/2 - (int) (width * 0.9)/2;
//        getWindowManager().updateViewLayout(getWindow().getDecorView(), params);
        getWindow().setAttributes(params);
    }


    private void roundDots() {
        FrameLayout goodDot = (FrameLayout)findViewById(R.id.goodDot);
        FrameLayout regularDot = (FrameLayout)findViewById(R.id.regularDot);
        FrameLayout badDot = (FrameLayout)findViewById(R.id.badDot);
        FrameLayout veryBadDot = (FrameLayout)findViewById(R.id.veryBadDot);
        FrameLayout extremelyBadDot = (FrameLayout)findViewById(R.id.extremelyBadDot);

        GradientDrawable drawableGoodDot = new GradientDrawable();
        GradientDrawable drawableRegularDot = new GradientDrawable();
        GradientDrawable drawableBadDot = new GradientDrawable();
        GradientDrawable drawableVeryBadDot = new GradientDrawable();
        GradientDrawable drawableExtremelyBadDot = new GradientDrawable();

        drawableGoodDot.setCornerRadius(5000);
        drawableRegularDot.setCornerRadius(5000);
        drawableBadDot.setCornerRadius(5000);
        drawableVeryBadDot.setCornerRadius(5000);
        drawableExtremelyBadDot.setCornerRadius(5000);
        drawableGoodDot.setColor(AirQualityType.GOOD.color());
        drawableRegularDot.setColor(AirQualityType.REGULAR.color());
        drawableBadDot.setColor(AirQualityType.BAD.color());
        drawableVeryBadDot.setColor(AirQualityType.VERY_BAD.color());
        drawableExtremelyBadDot.setColor(AirQualityType.EXTREMELY_BAD.color());

        goodDot.setBackground(drawableGoodDot);
        regularDot.setBackground(drawableRegularDot);
        badDot.setBackground(drawableBadDot);
        veryBadDot.setBackground(drawableVeryBadDot);
        extremelyBadDot.setBackground(drawableExtremelyBadDot);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public void closePopup(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Intent resultIntent = new Intent();
//        setResult(Activity.RESULT_OK, resultIntent);
    }
}
