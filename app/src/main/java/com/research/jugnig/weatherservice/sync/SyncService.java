package com.research.jugnig.weatherservice.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by JugniG on 16-11-2016.
 */


public class SyncService extends Service {
    private static final String TAG = "SyncService";

    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter sSyncAdapter = null;

    /**
     * Thread-safe constructor, creates static {@link SyncAdapter} instance.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    /**
     * Logging-only destructor.
     */
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
    }

//     * <p>New sync requests will be sent directly to the SyncAdapter using this channel.
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
