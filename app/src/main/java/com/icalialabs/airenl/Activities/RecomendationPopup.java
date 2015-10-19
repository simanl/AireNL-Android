package com.icalialabs.airenl.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.icalialabs.airenl.R;

/**
 * Created by Compean on 19/10/15.
 */
public class RecomendationPopup extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.recomendations_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout((int) (width * .9), 600);
        getWindow().setDimAmount(0.1f);


        height = height - getStatusBarHeight();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.y = height/2 - 600/2 + getStatusBarHeight()/2;
        params.x = width/2 - (int) (width * 0.9)/2;
        getWindow().setAttributes(params);

        Bundle bundle = getIntent().getExtras();
        TextView descriptionLabel = (TextView)findViewById(R.id.description);
        String recomendationDescription = bundle.getString("description");
        descriptionLabel.setText(recomendationDescription);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void closePopup(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
