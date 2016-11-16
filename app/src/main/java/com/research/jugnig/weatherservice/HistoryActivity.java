package com.research.jugnig.weatherservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.research.jugnig.weatherservice.data.HistoryObject;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by JugniG on 16-11-2016.
 */

public class HistoryActivity extends AppCompatActivity {

    private Realm mRealm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction();
        RealmResults<HistoryObject> result = mRealm.where(HistoryObject.class).findAllSorted("timeStamp", Sort.DESCENDING);
        HistoryObject[] list = result.toArray(new HistoryObject[result.size()]);
        mRealm.commitTransaction();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new MyRecyclerViewAdapter(list));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
