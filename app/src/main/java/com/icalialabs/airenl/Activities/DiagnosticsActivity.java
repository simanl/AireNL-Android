package com.icalialabs.airenl.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.*;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
//import com.icalialabs.airenl.blurry.Blurry;
//import Blurry;
import com.crashlytics.android.Crashlytics;
import com.icalialabs.airenl.Activities.StationsMapActivity;
import com.icalialabs.airenl.Models.Station;
import com.icalialabs.airenl.R;
import com.icalialabs.airenl.RestApi.StationRestClient;
import com.squareup.leakcanary.LeakCanary;

import java.util.List;

import io.fabric.sdk.android.Fabric;
import jp.wasabeef.blurry.Blurry;
import retrofit.Callback;
import retrofit.Response;

public class DiagnosticsActivity extends AppCompatActivity implements ViewTreeObserver.OnScrollChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Bitmap image = decodeSampledBitmapFromResource(getResources(), R.drawable.fondodia, size.x / 4, size.y / 4);


        setContentView(R.layout.activity_diagnostics);

        final RelativeLayout nonBoxedDiagnostics = (RelativeLayout) findViewById(R.id.nonBoxedDiagnostics);
        nonBoxedDiagnostics.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup rootView = (ViewGroup) findViewById(R.id.rootDiagnosticsLayout);
                RelativeLayout firstSectionView = (RelativeLayout) findViewById(R.id.firstSection);
                LinearLayout.LayoutParams firstSectionParams = (LinearLayout.LayoutParams) firstSectionView.getLayoutParams();

                ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
                scrollView.setScrollY(0);

                int newHeight = 0;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    newHeight = rootView.getMeasuredHeight() - firstSectionParams.topMargin / 2;
                } else {
                    newHeight = rootView.getMeasuredHeight() - (firstSectionParams.height * 2 + firstSectionParams.topMargin + 3 * firstSectionParams.bottomMargin / 2);
                }

//                int newHeight = rootView.getMeasuredHeight() - (firstSectionParams.height + firstSectionParams.topMargin + firstSectionParams.bottomMargin / 2);

                android.view.ViewGroup.LayoutParams params = nonBoxedDiagnostics.getLayoutParams();
                params.height = newHeight;
                nonBoxedDiagnostics.setLayoutParams(params);
            }
        });


        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(this);
        ImageView mainViewBackground = (ImageView) findViewById(R.id.mainViewBackground);
        mainViewBackground.setImageBitmap(image);
        findViewById(R.id.mapIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DiagnosticsActivity.this, StationsMapActivity.class));
            }
        });

        ImageView blurredBackground = (ImageView) findViewById(R.id.blurImageView);
        blurredBackground.setAlpha(0f);
        blurredBackground.post(new Runnable() {
            @Override
            public void run() {
                ImageView background = (ImageView) findViewById(R.id.mainViewBackground);
                ImageView blurredBackground = (ImageView) findViewById(R.id.blurImageView);
                Blurry.with(DiagnosticsActivity.this)
                        .radius(20)
                        .sampling(1)
                        .capture(background)
                        .into(blurredBackground);
            }
        });

        StationRestClient client = new StationRestClient();
        client.getStationService().getAll().enqueue(new Callback<List<Station>>() {
            @Override
            public void onResponse(Response<List<Station>> response) {
                if (response != null) {

                }

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
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
        final double adjustmentFactor = 1 / 3.0; // reach the max alpha for blur in the total scroll position multiplied by this factor

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

        RelativeLayout topItemsGroup = (RelativeLayout)findViewById(R.id.topItemsGroup);
        TextView imecaQuantity = (TextView)findViewById(R.id.imecaQuantity);
        int imecaOriginY = imecaQuantity.getTop() - currentScroll;
        if (imecaOriginY - topItemsGroup.getBottom() <= 20) {
            topItemsGroup.setTranslationY(-(topItemsGroup.getTop() - (imecaOriginY - (20 + topItemsGroup.getHeight()))));
//            topItemsGroup.setTop(imecaOriginY - (20 + topItemsGroup.getHeight()));
        } else {
            topItemsGroup.setTranslationY(0);
        }
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
