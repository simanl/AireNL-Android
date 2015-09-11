package com.icalialabs.airenl;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.internal.IUiSettingsDelegate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.leakcanary.RefWatcher;

public class StationsMapActivity extends AppCompatActivity implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_map);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Bitmap image = decodeSampledBitmapFromResource(getResources(), R.drawable.fondodia, size.x / 4, size.y / 4);

        ImageView mainViewBackground = (ImageView) findViewById(R.id.topBarImage);
        mainViewBackground.destroyDrawingCache();
        mainViewBackground.setImageBitmap(image);
        setUpMapIfNeeded();
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

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(25.684299, -100.316563)).title("Downton Obispado Station").snippet(String.valueOf(R.string.good)).icon(BitmapDescriptorFactory.fromResource(R.drawable.green_marker)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(25.743689, -100.286994)).title("San Nicolas Station").snippet(String.valueOf(R.string.regular)).icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_marker)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(25.776156, -100.316177)).title("Escobedo Station").snippet(String.valueOf(R.string.bad)).icon(BitmapDescriptorFactory.fromResource(R.drawable.orange_marker)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(25.673315, -100.457025)).title("Santa Catarina Station").snippet(String.valueOf(R.string.very_bad)).icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(25.660008, -100.191293)).title("Guadalupe Station").snippet(String.valueOf(R.string.extremely_bad)).icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_marker)));
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(25.648795,-100.3030961));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
        mMap.moveCamera(center);
        mMap.moveCamera(zoom);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);
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
        AirQualityType type = AirQualityType.qualityType(Integer.parseInt(marker.getSnippet()));

        View airStatusView = view.findViewById(R.id.airStatusView);
        GradientDrawable drawableAirStatus = new GradientDrawable();
        GradientDrawable drawableInfoWindow = new GradientDrawable();
        TextView locationText = (TextView)view.findViewById(R.id.locationText);
        TextView airQualityLabel = (TextView)view.findViewById(R.id.airQualityLabel);
        locationText.setText(marker.getTitle());
        airQualityLabel.setText(type.toString());
        drawableAirStatus.setCornerRadius(5000);
        drawableAirStatus.setColor(type.color());
        drawableAirStatus.setAlpha(60*255/100);
        drawableInfoWindow.setCornerRadius(20);
        drawableInfoWindow.setColor(Color.WHITE);
        drawableInfoWindow.setAlpha(90*255/100);
        airStatusView.setBackground(drawableAirStatus);
        view.getChildAt(0).setBackground(drawableInfoWindow);
        return view;

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        finish();
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AireNL.getRefWatcher(this);
        refWatcher.watch(this);
//        RefWatcher refWatcher;
//        refWatcher.watch(this);
    }
}
