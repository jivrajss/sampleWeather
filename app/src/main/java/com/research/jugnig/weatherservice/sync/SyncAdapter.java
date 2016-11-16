package com.research.jugnig.weatherservice.sync;

import android.accounts.Account;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.research.jugnig.weatherservice.MainActivity;
import com.research.jugnig.weatherservice.R;
import com.research.jugnig.weatherservice.WeatherApplication;
import com.research.jugnig.weatherservice.api.ApiService;
import com.research.jugnig.weatherservice.data.HistoryObject;
import com.research.jugnig.weatherservice.data.WeatherResponse;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.ACTIVITY_SERVICE;


/**
 * Created by JugniG on 16-11-2016.
 */


public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";
    private Date date;


    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Log.d(TAG, "Construc IKey-----");
    }

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        Log.d(TAG, "Construc IIKey-----");
    }

    OkHttpClient mOkHttpClient;

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "Beginning Api Synchronization");
        date = new Date();
        Realm mRealm = Realm.getDefaultInstance();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        mOkHttpClient = new OkHttpClient.Builder().
                addInterceptor(logging)
                .build();

        updateWeather(mRealm);
        mRealm.close();
//            MyFirebaseMessagingService.getIntent(getContext(), "Sync!!", "Sync completed, open app to see updated data!");
    }


    private void updateWeather(Realm mRealm) {
        String temp = "";
        try {
            Log.d(TAG, "Hitting api/weather");
            WeatherResponse response = new Retrofit.Builder().client(mOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create()).
                            baseUrl(ApiService.BASE_URL).
                            build().create(ApiService.class)
                    .getCurrentWeather(ApiService.location, ApiService.APP_ID).execute().body();
            if (response != null && response.getName() != null) {
                mRealm.beginTransaction();
                temp = Float.toString(response.getMain().getTemp());
                HistoryObject historyObject = new HistoryObject();
                historyObject.setTemp(response.getMain().getTemp());
                historyObject.setTimeStamp(Calendar.getInstance().getTimeInMillis());
                mRealm.copyToRealmOrUpdate(historyObject);
                mRealm.commitTransaction();
            }


            ActivityManager am = (ActivityManager) getContext().getSystemService(ACTIVITY_SERVICE);
            ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
            String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
            PackageManager pm = getContext().getPackageManager();
            PackageInfo foregroundAppPackageInfo = null;
            try {
                foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
                String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();
                Log.d(TAG, "app Name---" + foregroundTaskAppName);
                if (foregroundTaskAppName != null && foregroundTaskAppName.equalsIgnoreCase(getContext().getString(R.string.app_name))) {
                    // app is in foreground
                    Intent scrollIntent = new Intent(WeatherApplication.INTENT_UPDATE_VIEW);
                    scrollIntent.putExtra("temp", temp);
                    getContext().sendBroadcast(scrollIntent);
                } else {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    hitNotification(getContext(), intent, "weather update", "Current Tempearture::" + temp);
                    // app is in background

                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "into I/O Exception---" + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "into Exception---" + e.getLocalizedMessage());
        } finally {
        }

    }

    public static void hitNotification(Context context, Intent intent, String summary, String title) {
        NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
        notiStyle.setBigContentTitle(title);
        notiStyle.setSummaryText(summary);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setStyle(notiStyle)
                .setContentText(summary)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) (System.currentTimeMillis() % 1000), notificationBuilder.build());
    }
}
