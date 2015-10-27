package com.icalialabs.airenl.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.icalialabs.airenl.Models.BackgroundProvider;
import com.icalialabs.airenl.Models.Forecast;
import com.icalialabs.airenl.Models.Recomendation;
import com.icalialabs.airenl.Models.Screenshot;
import com.icalialabs.airenl.Models.Station;
import com.icalialabs.airenl.R;
import com.icalialabs.airenl.RestApi.RestClient;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.SpacingItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

    private BackgroundProvider provider;

    private boolean isShowingPopup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostics);
        restoreActivityData(savedInstanceState);

//        Fabric.with(this, new Crashlytics());

        setupPullToReload();
        //loadBackgroundImage();
        setupNonBoxedDiagnosticsLayout();


        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(this);

        //setupBlurredBackground();
        //setupBlurredScreenshot();

        findViewById(R.id.locationIcon).setAlpha(0.5f);
        if (checkPlayServices()) {

            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }

        setupRecommendationsView();

        Station currentStation = Station.getPersistedCurrentStation();
        if (currentStation != null) {
            reloadDataWithStation(currentStation);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (provider == null || provider.getDayFraction() != BackgroundProvider.providerWithDate(new Date()).getDayFraction()) {
            provider = BackgroundProvider.providerWithDate(new Date());
            loadBackgroundImage();
            setupBlurredBackground();
            setupBlurredScreenshot();
        }
        reloadStationInfo();
    }

    void setupPullToReload() {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange_redish, R.color.orange, R.color.yellow, R.color.sky_blue, R.color.dull_blue, R.color.dark_blue, R.color.dull_blue, R.color.sky_blue, R.color.yellow, R.color.orange);
        swipeRefreshLayout.setProgressViewEndTarget(true, 260);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadStationInfo();
            }
        });
    }

    void restoreActivityData(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.get("is_showing_popup") != null) {
            isShowingPopup = savedInstanceState.getBoolean("is_showing_popup");
        }

        if (getApplicationContext().getSharedPreferences("current_station",MODE_PRIVATE).getBoolean("using_location",true)) {
            findViewById(R.id.locationIcon).setAlpha(1f);
        } else {
            findViewById(R.id.locationIcon).setAlpha(0.5f);
        }
    }

    void setupBlurredScreenshot() {
        ImageView blurScreenshot = (ImageView)findViewById(R.id.blurScreenshot);
        blurScreenshot.post(new Runnable() {
            @Override
            public void run() {
                if (isShowingPopup) {
                    blurScreenshotShow();
                }
            }
        });
    }


    void setupRecommendationsView() {
        final List<Recomendation> imageIds = new ArrayList<Recomendation>();
        imageIds.add(Recomendation.outdoors());
        imageIds.add(Recomendation.window());
        imageIds.add(Recomendation.run());
        imageIds.add(Recomendation.allergy());
        imageIds.add(Recomendation.smoke());
        imageIds.add(Recomendation.heartCondition());
        imageIds.add(Recomendation.gas());
        imageIds.add(Recomendation.car());

        TwoWayView twoWayView = (TwoWayView)findViewById(R.id.recomendedActivitesListView);
        twoWayView.setHasFixedSize(true);
        twoWayView.setAdapter(new RecomendedActivityAdapter(imageIds));
        twoWayView.addItemDecoration(new SpacingItemDecoration(0,20));

        final ItemClickSupport itemClick = ItemClickSupport.addTo(twoWayView);

        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View child, int position, long id) {
                if (!isShowingPopup) {
                    isShowingPopup = true;
                    blurScreenshotShow();
                    Intent intent = new Intent(DiagnosticsActivity.this, RecomendationPopup.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("description", imageIds.get(position).getDescription());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, POPUP_RESULT_CODE);
                }
            }
        });
    }

    void setupBlurredBackground() {
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
    }

    void loadBackgroundImage() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Bitmap image = decodeSampledBitmapFromResource(getResources(), provider.getBackgroundResource(), size.x / 4, size.y / 4);

        ImageView mainViewBackground = (ImageView) findViewById(R.id.mainViewBackground);
//        mainViewBackground.setImageBitmap(null);
//        mainViewBackground.setImageDrawable(null);

        mainViewBackground.setImageBitmap(image);
    }

    void setupNonBoxedDiagnosticsLayout() {
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

                android.view.ViewGroup.LayoutParams params = nonBoxedDiagnostics.getLayoutParams();
                params.height = newHeight;
                nonBoxedDiagnostics.setLayoutParams(params);
            }
        });
    }



    public void reloadStationInfo() {
        final FrameLayout errorLoadingView = (FrameLayout)findViewById(R.id.errorLoadingView);
        errorLoadingView.setAlpha(0);
        if (getApplicationContext().getSharedPreferences("current_station",MODE_PRIVATE).getBoolean("using_location",true)) {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.reconnect();
            }

        } else {
            final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
            RestClient apiClient = new RestClient();
            apiClient.getStationService().getStation(Station.getPersistedCurrentStation().getId()).enqueue(new Callback<Station>() {
                @Override
                public void onResponse(Response<Station> response) {
                    if (response != null) {
                        if (response.body() != null) {
                            TextView lastUpdatedDate = (TextView)findViewById(R.id.lastUpdatedDate);
                            Date date = new Date();
                            lastUpdatedDate.setText(date.toString());
                            reloadDataWithStation(response.body());
                        } else {
                            System.out.println(response.errorBody());
                        }
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(Throwable t) {
                    System.out.println(t.getLocalizedMessage());
                    swipeRefreshLayout.setRefreshing(false);
                    errorLoadingView.animate().alpha(1f).setDuration(500).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (errorLoadingView.getAlpha() == 1) {
                                errorLoadingView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        errorLoadingView.animate().alpha(0f).setDuration(500);
                                    }
                                }, 2000);
                            }
                        }
                    });
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
        if (!isShowingPopup) {
            isShowingPopup = true;
            blurScreenshotShow();
            startActivityForResult(new Intent(DiagnosticsActivity.this, Popup.class), POPUP_RESULT_CODE);
        }
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
        AirQualityType type = AirQualityType.qualityTypeWithImecaValue(0);
        String imecaValue = "--";
        if (station.getLastMeasurement().getImecaPoints() != null) {
            type = AirQualityType.qualityTypeWithImecaValue(station.getLastMeasurement().getImecaPoints());
            imecaValue = station.getLastMeasurement().getImecaPoints().toString();
        }


        TextView stationNameTextView = (TextView)findViewById(R.id.stationTextView);
        TextView imecaValueTextView = (TextView)findViewById(R.id.imecaQuantity);
        RelativeLayout airStatusView = (RelativeLayout)findViewById(R.id.airQualityView);
        TextView airQualityStatusTextView = (TextView)findViewById(R.id.airQualityStatusText);
        TextView temperatureTextView = (TextView)findViewById(R.id.temperatureValueText);
        TextView windSpeedTextView = (TextView)findViewById(R.id.windSpeedValueText);
        TextView pm10TextView = (TextView)findViewById(R.id.pm10ValueText);
        TextView pm2_5TextView = (TextView)findViewById(R.id.pm2_5ValueText);
        TextView o3TextView = (TextView)findViewById(R.id.o3ValueText);
        TextView backgroundLocationTextView = (TextView)findViewById(R.id.backgroundLocationText);

        DecimalFormat temperatureFormat = new DecimalFormat("0.##º");
        DecimalFormat numberFormat = new DecimalFormat("0.##");
        DateFormat timeFormat = new SimpleDateFormat("kk:mm");

        String pm10Text = (station.getLastMeasurement().getToracicParticles() != null) ? numberFormat.format(station.getLastMeasurement().getToracicParticles()) : "--";
        String pm2_5Text = (station.getLastMeasurement().getRespirableParticles() != null) ? numberFormat.format(station.getLastMeasurement().getRespirableParticles()) : "--";
        String o3Text = (station.getLastMeasurement().getOzone() != null) ? numberFormat.format(station.getLastMeasurement().getOzone()) : "--";
        TableLayout table = (TableLayout)findViewById(R.id.forecastsTable);

        if (station.getForecasts() != null) {
            Collections.sort(station.getForecasts(), Forecast.StartDateAscendingComparator);
            List<Forecast> forecasts = station.getForecasts();

            for (int index = 0; index < station.getForecasts().size(); index++) {
                TableRow row = (TableRow)table.getChildAt(index + 1);
                if (row != null) {
                    TextView timeLabel = (TextView)row.getChildAt(0);
                    TextView pm10Label = (TextView)row.getChildAt(1);
                    TextView pm2_5Label = (TextView)row.getChildAt(2);
                    TextView o3Label = (TextView)row.getChildAt(3);

                    String pm10 = (station.getForecasts().get(index).getToracicParticles() != null)? AirQualityType.qualityTypeWithString(station.getForecasts().get(index).getToracicParticles().toString()).lowerCaseString() : "--";
                    String pm2_5 = (station.getForecasts().get(index).getRespirableParticles() != null)? AirQualityType.qualityTypeWithString(station.getForecasts().get(index).getRespirableParticles().toString()).lowerCaseString() : "--";
                    String o3 = (station.getForecasts().get(index).getOzone() != null)? AirQualityType.qualityTypeWithString(station.getForecasts().get(index).getOzone().toString()).lowerCaseString() : "--";

                    timeLabel.setText(timeFormat.format(station.getForecasts().get(index).getStartsAt())+"-"+timeFormat.format(station.getForecasts().get(index).getEndsAt()));
                    pm10Label.setText(pm10);
                    pm2_5Label.setText(pm2_5);
                    o3Label.setText(o3);
                }
            }
        }


        stationNameTextView.setText(station.getName());
        imecaValueTextView.setText(imecaValue);
        airQualityStatusTextView.setText(type.toString());
        temperatureTextView.setText(temperatureFormat.format(station.getLastMeasurement().getTemperature()));
        windSpeedTextView.setText(numberFormat.format(station.getLastMeasurement().getWindSpeed()));
        pm10TextView.setText(pm10Text);
        pm2_5TextView.setText(pm2_5Text);
        o3TextView.setText(o3Text);
        backgroundLocationTextView.setText(BackgroundProvider.providerWithDate(new Date()).getBackgroundLocation());

        GradientDrawable drawableAirStatusView = new GradientDrawable();
        drawableAirStatusView.setCornerRadius(5000);
        drawableAirStatusView.setColor(type.color());
        drawableAirStatusView.setAlpha(60*255/100);
        airStatusView.setBackground(drawableAirStatusView);
    }

    public void userSelectedLocation(View view) {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        final FrameLayout errorLoadingView = (FrameLayout)findViewById(R.id.errorLoadingView);
        //final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        if (!getApplicationContext().getSharedPreferences("current_station",MODE_PRIVATE).getBoolean("using_location",true)) {
            swipeRefreshLayout.setRefreshing(true);
        }

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
                                Date date = new Date();
                                TextView lastUpdatedDate = (TextView)findViewById(R.id.lastUpdatedDate);
                                lastUpdatedDate.setText(date.toString());
                                findViewById(R.id.locationIcon).setAlpha(1f);
                                reloadDataWithStation(response.body());
                                persistStation(response.body(), true);
                            } else {
                                System.out.println(response.errorBody());
                            }
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        System.out.println(t.getLocalizedMessage());
                        swipeRefreshLayout.setRefreshing(false);
                        errorLoadingView.animate().alpha(1f).setDuration(500).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (errorLoadingView.getAlpha() == 1) {
                                    errorLoadingView.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            errorLoadingView.animate().alpha(0f).setDuration(500);
                                        }
                                    }, 2000);
                                }
                            }
                        });
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
        if (getApplicationContext().getSharedPreferences("current_station",MODE_PRIVATE).getBoolean("using_location",true)) {
            userSelectedLocation(null);
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

        darkBackground.setAlpha((float)currentDarkAlpha);
        blurImageView.setAlpha((float) currentBlurAlpha);

        RelativeLayout topItemsGroup = (RelativeLayout)findViewById(R.id.topItemsGroup);
        TextView imecaQuantity = (TextView)findViewById(R.id.imecaQuantity);
        int imecaOriginY = imecaQuantity.getTop() - currentScroll;
        if (imecaOriginY - topItemsGroup.getBottom() <= 20) {
            topItemsGroup.setTranslationY(-(topItemsGroup.getTop() - (imecaOriginY - (20 + topItemsGroup.getHeight()))));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageView mainViewBackground = (ImageView) findViewById(R.id.mainViewBackground);
        ImageView blurredBackground = (ImageView) findViewById(R.id.blurImageView);
        ImageView blurredScreenshot = (ImageView) findViewById(R.id.blurScreenshot);
        mainViewBackground.setImageBitmap(null);
        blurredBackground.setImageBitmap(null);
        blurredScreenshot.setImageBitmap(null);
    }
}
