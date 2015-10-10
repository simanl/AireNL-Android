package com.icalialabs.airenl.Activities;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Display;
import android.graphics.*;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
//import com.icalialabs.airenl.blurry.Blurry;
//import Blurry;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.icalialabs.airenl.Adapters.RecomendedActivityAdapter;
import com.icalialabs.airenl.Models.AirQualityType;
import com.icalialabs.airenl.Models.Screenshot;
import com.icalialabs.airenl.Models.Station;
import com.icalialabs.airenl.R;
import com.icalialabs.airenl.RestApi.RestClient;

import org.lucasr.twowayview.widget.SpacingItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import jp.wasabeef.blurry.Blurry;
import retrofit.Callback;
import retrofit.Response;

public class DiagnosticsActivity extends AppCompatActivity implements ViewTreeObserver.OnScrollChangedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private final static int STATIONS_RESULT_CODE = 1;
    private final static int POPUP_RESULT_CODE = 2;
    private final static int FORECASTS_POPUP_RESULT_CODE = 2;

    private boolean isShowingPopup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.get("is_showing_popup") != null) {
            isShowingPopup = savedInstanceState.getBoolean("is_showing_popup");
        }

//        Fabric.with(this, new Crashlytics());
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
                LinearLayout secondSectionView = (LinearLayout) findViewById(R.id.secondSection);
                LinearLayout.LayoutParams firstSectionParams = (LinearLayout.LayoutParams) firstSectionView.getLayoutParams();
                LinearLayout.LayoutParams secondSectionParams = (LinearLayout.LayoutParams) secondSectionView.getLayoutParams();

                ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
                scrollView.setScrollY(0);

                int newHeight = 0;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    newHeight = rootView.getMeasuredHeight() - firstSectionParams.topMargin / 2;
                } else {
                    newHeight = rootView.getMeasuredHeight() - (firstSectionParams.height + secondSectionParams.height + firstSectionParams.topMargin + firstSectionParams.bottomMargin + secondSectionParams.bottomMargin / 2);
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
//        findViewById(R.id.mapIcon).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mapButtonClicked();
//                //startActivity(new Intent(DiagnosticsActivity.this, StationsMapActivity.class));
//            }
//        });

//        findViewById(R.id.locationIcon).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                userSelectedLocation();
//            }
//        });

//        findViewById(R.id.airStatusInfoButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                statusInfoButtonClicked();
//            }
//        });

//        findViewById(R.id.forecastsInfoButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                forecastsInfoButtonClicked();
//            }
//        });

        ImageView blurredBackground = (ImageView) findViewById(R.id.blurImageView);
        blurredBackground.setAlpha(0f);
        blurredBackground.post(new Runnable() {
            @Override
            public void run() {
                ImageView background = (ImageView) findViewById(R.id.mainViewBackground);
                ImageView blurredBackground = (ImageView) findViewById(R.id.blurImageView);
                Blurry.with(getApplicationContext())
                        .radius(20)
                        .sampling(1)
                        .capture(background)
                        .into(blurredBackground);
            }
        });

        ImageView blurScreenshot = (ImageView)findViewById(R.id.blurScreenshot);
        blurScreenshot.post(new Runnable() {
            @Override
            public void run() {
                if (isShowingPopup) {
                    blurScreenshotShow();
                }
            }
        });

        findViewById(R.id.locationIcon).setAlpha(0.5f);
        if (checkPlayServices()) {

            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }

        if (getApplicationContext().getSharedPreferences("current_station",MODE_PRIVATE).getBoolean("using_location",true)) {
            findViewById(R.id.locationIcon).setAlpha(1f);
        } else {
            findViewById(R.id.locationIcon).setAlpha(0.5f);
        }

        List<Integer> imageIds = new ArrayList<Integer>();
        imageIds.add(R.drawable.outdoors);
        imageIds.add(R.drawable.window);
        imageIds.add(R.drawable.run);
        imageIds.add(R.drawable.allergy);
        imageIds.add(R.drawable.smoke);
        imageIds.add(R.drawable.heart_condition);
        imageIds.add(R.drawable.gas);
        imageIds.add(R.drawable.car);

        TwoWayView twoWayView = (TwoWayView)findViewById(R.id.recomendedActivitesListView);
        twoWayView.setHasFixedSize(true);
        //twoWayView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        twoWayView.setAdapter(new RecomendedActivityAdapter(imageIds));
        twoWayView.addItemDecoration(new SpacingItemDecoration(0,20));

        Station currentStation = Station.getPersistedCurrentStation();
        if (currentStation != null) {
            reloadDataWithStation(currentStation);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getApplicationContext().getSharedPreferences("current_station",MODE_PRIVATE).getBoolean("using_location",true)) {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.reconnect();
            }

        } else {
            RestClient apiClient = new RestClient();
            apiClient.getStationService().getStation(Station.getPersistedCurrentStation().getId()).enqueue(new Callback<Station>() {
                @Override
                public void onResponse(Response<Station> response) {
                    if (response != null) {
                        if (response.body() != null) {
                            reloadDataWithStation(response.body());
                        } else {
                            System.out.println(response.errorBody());
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    System.out.println(t.getLocalizedMessage());
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STATIONS_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                findViewById(R.id.locationIcon).setAlpha(0.5f);
                Station station = (Station)data.getExtras().getSerializable("station");
                reloadDataWithStation(station);
                persistStation(station, false);
            }
        } else if (requestCode == POPUP_RESULT_CODE) {
            ImageView screenShot = (ImageView)findViewById(R.id.blurScreenshot);
            screenShot.animate().alpha(0f).setDuration(500);
            isShowingPopup = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_showing_popup", isShowingPopup);
    }

    private void blurScreenshotShow() {
        View screen = (View) findViewById(R.id.rootDiagnosticsLayout);
        ImageView blurredBackground = (ImageView) findViewById(R.id.blurScreenshot);
//        if (blurredBackground.getDrawable() != null) {
//            blurredBackground.animate().alpha(1f).setDuration(300);
//        } else {
        Blurry.with(getApplicationContext())
                .radius(25)
                .sampling(5)
                .animate(300)
                .capture(screen)
                .into(blurredBackground);
        blurredBackground.setAlpha(1f);
    }

    public void mapButtonClicked(View view) {
        startActivityForResult(new Intent(DiagnosticsActivity.this, StationsMapActivity.class), STATIONS_RESULT_CODE);
    }

    public void forecastsInfoButtonClicked(View view) {
        if (!isShowingPopup) {
            isShowingPopup = true;
            blurScreenshotShow();
            startActivityForResult(new Intent(DiagnosticsActivity.this, ForecastsPopup.class), FORECASTS_POPUP_RESULT_CODE);
        }

    }

    public void statusInfoButtonClicked(View view) {
//        View view = findViewById(R.id.rootDiagnosticsLayout);
//        Bitmap bitmap = Screenshot.takeScreenshot(view);
//        Screenshot.saveBitmap(bitmap,"main_screen");
        if (!isShowingPopup) {
            isShowingPopup = true;
            blurScreenshotShow();
            startActivityForResult(new Intent(DiagnosticsActivity.this, Popup.class), POPUP_RESULT_CODE);
        }

//        }


    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void reloadDataWithStation(Station station) {
        AirQualityType type = AirQualityType.qualityTypeWithImecaValue(station.getLastMeasurement().getImecaPoints());

        TextView stationNameTextView = (TextView)findViewById(R.id.stationTextView);
        TextView imecaValueTextView = (TextView)findViewById(R.id.imecaQuantity);
        RelativeLayout airStatusView = (RelativeLayout)findViewById(R.id.airQualityView);
        TextView airQualityStatusTextView = (TextView)findViewById(R.id.airQualityStatusText);
        TextView temperatureTextView = (TextView)findViewById(R.id.temperatureValueText);
        TextView windSpeedTextView = (TextView)findViewById(R.id.windSpeedValueText);
        TextView pm10TextView = (TextView)findViewById(R.id.pm10ValueText);
        TextView pm2_5TextView = (TextView)findViewById(R.id.pm2_5ValueText);
        TextView o3TextView = (TextView)findViewById(R.id.o3ValueText);

        DecimalFormat temperatureFormat = new DecimalFormat("0.##º");
        DecimalFormat numberFormat = new DecimalFormat("0.##");

        String pm10Text = (station.getLastMeasurement().getRespirableSuspendedParticles() != null) ? numberFormat.format(station.getLastMeasurement().getRespirableSuspendedParticles()) : "0";
        String pm2_5Text = (station.getLastMeasurement().getFineParticles() != null) ? numberFormat.format(station.getLastMeasurement().getFineParticles()) : "0";
        String o3Text = (station.getLastMeasurement().getOzone() != null) ? numberFormat.format(station.getLastMeasurement().getOzone()) : "0";

        stationNameTextView.setText(station.getName());
        imecaValueTextView.setText(station.getLastMeasurement().getImecaPoints().toString());
        airQualityStatusTextView.setText(type.toString());
        temperatureTextView.setText(temperatureFormat.format(station.getLastMeasurement().getTemperature()));
        windSpeedTextView.setText(numberFormat.format(station.getLastMeasurement().getWindSpeed()));
        pm10TextView.setText(pm10Text);
        pm2_5TextView.setText(pm2_5Text);
        o3TextView.setText(o3Text);

        GradientDrawable drawableAirStatusView = new GradientDrawable();
        drawableAirStatusView.setCornerRadius(5000);
        drawableAirStatusView.setColor(type.color());
        drawableAirStatusView.setAlpha(60*255/100);
        airStatusView.setBackground(drawableAirStatusView);
    }

    public void userSelectedLocation(View view) {
        if (mGoogleApiClient != null) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                RestClient client = new RestClient();
                client.getStationService().getNearestStationFrom(mLastLocation.getLatitude()+","+mLastLocation.getLongitude()).enqueue(new Callback<Station>() {
                    @Override
                    public void onResponse(Response<Station> response) {
                        if (response != null) {
                            if (response.body() != null) {
                                findViewById(R.id.locationIcon).setAlpha(1f);
                                reloadDataWithStation(response.body());
                                persistStation(response.body(), true);
                            } else {
                                System.out.println(response.errorBody());
                            }
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        System.out.println(t.getLocalizedMessage());
                    }
                });
            }
        }


    }

    private void persistStation(Station station, boolean usingLocation) {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("current_station",MODE_PRIVATE);
        SharedPreferences.Editor settingEditor = settings.edit();
        settingEditor.putBoolean("using_location",usingLocation);
        settingEditor.commit();
        station.persistAsCurrentStation();
    }

    @Override
    public void onConnected(Bundle bundle) {
//        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (getApplicationContext().getSharedPreferences("current_station",MODE_PRIVATE).getBoolean("using_location",true)) {
            userSelectedLocation(null);
            //findViewById(R.id.locationIcon).setAlpha(1f);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        findViewById(R.id.locationIcon).setAlpha(0.5f);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        findViewById(R.id.locationIcon).setAlpha(0.5f);
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
        blurImageView.setAlpha((float) currentBlurAlpha);

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
