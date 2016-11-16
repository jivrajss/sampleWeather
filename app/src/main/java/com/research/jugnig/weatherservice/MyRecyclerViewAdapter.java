package com.research.jugnig.weatherservice;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.research.jugnig.weatherservice.data.HistoryObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by JugniG on 16-11-2016.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private final HistoryObject[] mList;
    private final Calendar calendar;
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

    public MyRecyclerViewAdapter(HistoryObject[] data) {
        super();
        calendar = Calendar.getInstance();
        this.mList = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        HistoryObject obj = mList[position];
        calendar.setTimeInMillis(obj.getTimeStamp());
        holder.title.setText(dateFormat.format(calendar.getTime()) + "       " + obj.getTemp());
    }

    @Override
    public int getItemCount() {
        return mList.length;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.history_item);
        }
    }
}
