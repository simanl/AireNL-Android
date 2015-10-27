package com.icalialabs.airenl;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.fabric.sdk.android.Fabric;
import java.lang.ref.WeakReference;

/**
 * Created by Compean on 08/09/15.
 */
public class AireNL extends Application {

    private RefWatcher refWatcher;
    private static WeakReference<Context> mContext;

    public static RefWatcher getRefWatcher(Context context) {
        AireNL application = (AireNL) context.getApplicationContext();
        return application.refWatcher;
    }

    public static Context getContext(){
        return mContext.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        //refWatcher = LeakCanary.install(this);
        mContext = new WeakReference<Context>(this);
    }
}
