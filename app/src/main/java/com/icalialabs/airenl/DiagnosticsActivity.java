package com.icalialabs.airenl;

import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.*;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
//import com.icalialabs.airenl.blurry.Blurry;
//import Blurry;
import jp.wasabeef.blurry.Blurry;

public class DiagnosticsActivity extends ActionBarActivity implements ViewTreeObserver.OnScrollChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        Bitmap image = decodeSampledBitmapFromResource(getResources(),R.drawable.fondodia, size.x/4, size.y/4);


        setContentView(R.layout.activity_diagnostics);



        ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(this);
//        ImageView mainViewBackground = (ImageView)findViewById(R.id.mainViewBackground);
//        mainViewBackground.setImageBitmap(image);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView background = (ImageView)findViewById(R.id.mainViewBackground);
        Blurry.with(DiagnosticsActivity.this)
                .radius(20)
                .sampling(1)
                .async()
                .capture(findViewById(R.id.mainViewBackground))
                .into((ImageView) findViewById(R.id.blurImageView));
    }

    @Override
    public void onScrollChanged() {


//        ImageView background = (ImageView)findViewById(R.id.mainViewBackground);
//        Blurry.with(DiagnosticsActivity.this)
//                .radius(20)
//                .sampling(1)
//                .async()
//                .capture(findViewById(R.id.mainViewBackground))
//                .into((ImageView) findViewById(R.id.blurImageView));

        final double maxAlphaDarkBackground = 0.6;
        final double minAlphaDarkBackground = 0.1;
        final double minAlphaBlur = 0;
        final double maxAlpahBlur = 1;
        final double adjustmentFactor = 1 / 2.0; // reach the max alpha in the total scroll position multiplied by this factor

        ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView);
        final int maxScroll = scrollView.getChildAt(0).getMeasuredHeight() - scrollView.getMeasuredHeight();
        FrameLayout darkBackground = (FrameLayout)findViewById(R.id.darkBackground);
        ImageView blurImageView = (ImageView)findViewById(R.id.blurImageView);

        final int currentScroll = scrollView.getScrollY();
        final int scrollAdjustmentForBlur = (currentScroll <= (int)(maxScroll * adjustmentFactor)) ? currentScroll : (int)(maxScroll * adjustmentFactor);

        final double maxAlphaDarkRange = maxAlphaDarkBackground - minAlphaDarkBackground;
        final double maxAlphaBlurRange = maxAlpahBlur - minAlphaBlur;
        final double currentDarkAlpha = (currentScroll / (maxScroll / maxAlphaDarkRange)) + minAlphaDarkBackground;
        final double currentBlurAlpha = (scrollAdjustmentForBlur / (maxScroll * adjustmentFactor / maxAlphaBlurRange)) + minAlphaBlur;

//        System.out.println("blur status: "+currentBlurAlpha + " scroll amount: " + currentScroll);

        darkBackground.setAlpha((float)currentDarkAlpha);
        blurImageView.setAlpha((float)currentBlurAlpha);
    }

    //    public static int calculateInSampleSize(
//            BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) > reqHeight
//                    && (halfWidth / inSampleSize) > reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//
//        return inSampleSize;
//    }
//
//    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
//                                                         int reqWidth, int reqHeight) {
//
//        // First decode with inJustDecodeBounds=true to check dimensions
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(res, resId, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeResource(res, resId, options);
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_diagnostics, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
