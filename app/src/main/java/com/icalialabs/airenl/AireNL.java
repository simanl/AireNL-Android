package com.icalialabs.airenl;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by Compean on 08/09/15.
 */
public class AireNL extends Application {

    private RefWatcher refWatcher;
    private static Context mContext;

    public static RefWatcher getRefWatcher(Context context) {
        AireNL application = (AireNL) context.getApplicationContext();
        return application.refWatcher;
    }

    public static Context getContext(){
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
        mContext = this;
    }
}