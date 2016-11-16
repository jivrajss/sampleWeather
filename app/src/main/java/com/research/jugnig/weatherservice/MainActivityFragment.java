package com.research.jugnig.weatherservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.research.jugnig.weatherservice.data.HistoryObject;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    private TextView labelTemp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        labelTemp = (TextView) view.findViewById(R.id.label_temp);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String temp = "";
        Realm realm = Realm.getDefaultInstance();
        RealmResults<HistoryObject> list = realm.where(HistoryObject.class).findAllSorted("timeStamp", Sort.DESCENDING);
        if (list.size() > 0)
            temp = Float.toString(list.get(0).getTemp());
        realm.close();

        labelTemp.setText("Current temperature---" + temp);

    }

    private BroadcastReceiver temperatureUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (labelTemp != null)
                labelTemp.setText("Current temperature---" + intent.getExtras().getString("temp", ""));

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(temperatureUpdateReceiver, new IntentFilter(WeatherApplication.INTENT_UPDATE_VIEW));
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(temperatureUpdateReceiver);
    }
}
