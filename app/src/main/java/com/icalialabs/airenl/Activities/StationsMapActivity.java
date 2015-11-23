package com.icalialabs.airenl.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.icalialabs.airenl.Models.AirQualityType;
import com.icalialabs.airenl.AireNL;
import com.icalialabs.airenl.Models.BackgroundProvider;
import com.icalialabs.airenl.Models.Forecast;
import com.icalialabs.airenl.Models.Station;
import com.icalialabs.airenl.R;
import com.icalialabs.airenl.RestApi.RestClient;
import com.squareup.leakcanary.RefWatcher;

import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;

public class StationsMapActivity extends AppCompatActivity implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private List<Station> stations;
    Marker selectedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_map);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Bitmap image = decodeSampledBitmapFromResource(getResources(), BackgroundProvider.providerWithDate(new Date()).getBackgroundResource(), size.x / 4, size.y / 4);


//
        ImageView mainViewBackground = (ImageView) findViewById(R.id.topBarImage);
        //mainViewBackground.destroyDrawingCache();
        mainViewBackground.setImageBitmap(image);
        loadStations();

    }

    public void loadStations() {
        final RelativeLayout errorLoadingView = (RelativeLayout)findViewById(R.id.errorLoadingView);
        RestClient client = new RestClient();
        client.getStationService().getAllStations().enqueue(new Callback<List<Station>>() {
            @Override
            public void onResponse(Response<List<Station>> response) {
                if (response != null) {
                    if (response.body() != null) {
                        stations = response.body();
                        resetMarkers();
                    } else {
                        System.out.println(response.errorBody());
                    }

                }
                errorLoadingView.animate().alpha(0f).setDuration(500);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println(t.getLocalizedMessage());
                final TextView serverStatusTextView = (TextView) findViewById(R.id.serverStatusBannerText);
                final FrameLayout reloadActionView = (FrameLayout) findViewById(R.id.reloadActionView);
                if (serverStatusTextView.getText().equals(getString(R.string.loading_station))) {
                    serverStatusTextView.animate().alpha(0f).setDuration(500).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (serverStatusTextView.getText().equals(getString(R.string.loading_station))) {
                                serverStatusTextView.setText(getString(R.string.error_loading_stations));
                                serverStatusTextView.animate().alpha(1f).setDuration(500);
                            }
                        }
                    });
                    ValueAnimator anim = ValueAnimator.ofInt(ContextCompat.getColor(getApplicationContext(), R.color.loading_banner_color), ContextCompat.getColor(getApplicationContext(), R.color.error_banner_color));
                    anim.setDuration(1000);
                    anim.setEvaluator(new ArgbEvaluator());
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            errorLoadingView.setBackgroundColor((Integer) animation.getAnimatedValue());
                        }
                    });
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            reloadActionView.setAlpha(1f);
                        }
                    });
                    anim.start();
                }

            }
        });

        setUpMapIfNeeded();
    }

    public void reloadStationsClicked(View view) {
        final RelativeLayout errorLoadingView = (RelativeLayout)findViewById(R.id.errorLoadingView);
        final TextView serverStatusTextView = (TextView)findViewById(R.id.serverStatusBannerText);
        final FrameLayout reloadActionView = (FrameLayout)findViewById(R.id.reloadActionView);
        errorLoadingView.setBackgroundResource(R.color.loading_banner_color);
        serverStatusTextView.setText(getString(R.string.loading_station));
        reloadActionView.setAlpha(0f);
        loadStations();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stations_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.theMapFragment))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void resetMarkers() {
        mMap.clear();
        if (stations != null) {
            for (int idx = 0; idx < stations.size(); idx++) {
                Station station = stations.get(idx);
                //Integer imecaPoints = (station.getLastMeasurement().getImecaPoints() != null)? station.getLastMeasurement().getImecaPoints() : 0;
                //AirQualityType qualityType = AirQualityType.qualityTypeWithImecaValue(imecaPoints);
                AirQualityType qualityType = AirQualityType.qualityTypeWithString(station.getLastMeasurement().getImecaCategory());
                mMap.addMarker(new MarkerOptions().position(station.getCoordinate().getLatLong()).title(station.getName()).snippet(String.valueOf(idx)).icon(qualityType.getIcon()));
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        resetMarkers();

//        mMap.addMarker(new MarkerOptions().position(new LatLng(25.684299, -100.316563)).title("Downton Obispado Station").snippet(String.valueOf(R.string.good)).icon(BitmapDescriptorFactory.fromResource(R.drawable.green_marker)));
//        mMap.addMarker(new MarkerOptions().position(new LatLng(25.743689, -100.286994)).title("San Nicolas Station").snippet(String.valueOf(R.string.regular)).icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_marker)));
//        mMap.addMarker(new MarkerOptions().position(new LatLng(25.776156, -100.316177)).title("Escobedo Station").snippet(String.valueOf(R.string.bad)).icon(BitmapDescriptorFactory.fromResource(R.drawable.orange_marker)));
//        mMap.addMarker(new MarkerOptions().position(new LatLng(25.673315, -100.457025)).title("Santa Catarina Station").snippet(String.valueOf(R.string.very_bad)).icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker)));
//        mMap.addMarker(new MarkerOptions().position(new LatLng(25.660008, -100.191293)).title("Guadalupe Station").snippet(String.valueOf(R.string.extremely_bad)).icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_marker)));
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(25.648795,-100.3030961));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
        mMap.moveCamera(center);
        mMap.moveCamera(zoom);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
    }

    @Override
    public View getInfoWindow(Marker marker) {
//        RelativeLayout contentView = new RelativeLayout(this);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
//        //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(200,100);
//        contentView.setLayoutParams(layoutParams);
//        contentView.setBackgroundColor(0xFFF);
//        return contentView;
        FrameLayout view = (FrameLayout)getLayoutInflater().inflate(R.layout.info_window, null);
        Station station = stations.get(Integer.parseInt(marker.getSnippet()));
        Integer imecaPoints = (station.getLastMeasurement().getImecaPoints() != null) ? station.getLastMeasurement().getImecaPoints() : 0;
        //AirQualityType type = AirQualityType.qualityTypeWithImecaValue(imecaPoints);
        AirQualityType type = AirQualityType.qualityTypeWithString(station.getLastMeasurement().getImecaCategory());



        View airStatusView = view.findViewById(R.id.airStatusView);
        GradientDrawable drawableAirStatus = new GradientDrawable();
        GradientDrawable drawableInfoWindow = new GradientDrawable();
        TextView locationText = (TextView)view.findViewById(R.id.locationText);
        TextView airQualityLabel = (TextView)view.findViewById(R.id.airQualityLabel);
        locationText.setText(marker.getTitle());
        airQualityLabel.setText(type.toString());
        drawableAirStatus.setCornerRadius(5000);
        drawableAirStatus.setColor(type.color());
        drawableAirStatus.setAlpha(60 * 255 / 100);
        drawableInfoWindow.setCornerRadius(20);
        drawableInfoWindow.setColor(Color.WHITE);
        drawableInfoWindow.setAlpha(90 * 255 / 100);
        airStatusView.setBackground(drawableAirStatus);
        view.getChildAt(0).setBackground(drawableInfoWindow);
        return view;

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        returnStationAndFinish(marker);
    }

    public void returnStationAndFinish(Marker marker) {
        Station station = stations.get(Integer.parseInt(marker.getSnippet()));
        Intent resultIntent = new Intent();
        resultIntent.putExtra("station",station);
        setResult(Activity.RESULT_OK,resultIntent);
        finish();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        selectedMarker = marker;
        FrameLayout imageView = (FrameLayout)findViewById(R.id.switchIconView);
        imageView.setAlpha(1f);
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        selectedMarker = null;
        FrameLayout imageView = (FrameLayout)findViewById(R.id.switchIconView);
        imageView.setAlpha(0f);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public void mapCrossCancelClicked(View view) {
        finish();
    }

    public void mapSwitchClicked(View view) {
        if (selectedMarker != null) {
            returnStationAndFinish(selectedMarker);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageView mainViewBackground = (ImageView) findViewById(R.id.topBarImage);
        mainViewBackground.setImageBitmap(null);
        //RefWatcher refWatcher = AireNL.getRefWatcher(this);
        //refWatcher.watch(this);
//        RefWatcher refWatcher;
//        refWatcher.watch(this);
    }
}
