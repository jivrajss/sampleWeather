package com.research.jugnig.weatherservice.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by JugniG on 16-11-2016.
 */

public class HistoryObject extends RealmObject {
    @PrimaryKey
    private long timeStamp;
    private float temp;

    public HistoryObject() {
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public float getTemp() {
        return temp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }
}
