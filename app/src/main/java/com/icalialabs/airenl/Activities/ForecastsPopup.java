package com.icalialabs.airenl.Activities;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.icalialabs.airenl.R;

/**
 * Created by Compean on 05/10/15.
 */
public class ForecastsPopup extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.forecasts_popup);


        findViewById(R.id.cross_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
