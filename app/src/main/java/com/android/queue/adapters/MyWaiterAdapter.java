package com.android.queue.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.android.queue.models.Participant;

import java.util.ArrayList;
import java.util.List;

public class MyWaiterAdapter extends ArrayAdapter<Participant> {

    private Context mContext;
    public MyWaiterAdapter(@NonNull Context context, @NonNull List<Participant> objects) {
        super(context, 0, objects);
        mContext = context;
    }


}
