package com.android.queue.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.queue.R;
import com.android.queue.models.Participant;

import java.util.ArrayList;
import java.util.List;

public class MyWaiterAdapter extends ArrayAdapter<Participant> {

    private Context mContext;


    public MyWaiterAdapter(@NonNull Context context, @NonNull List<Participant> objects) {
        super(context, 0, objects);
        mContext = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Create view
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_participant, parent, false);
        }

        //Init view
        TextView waiterName = convertView.findViewById(R.id.waiterName);
        TextView waiterNumber = convertView.findViewById(R.id.waiterNumber);
        TextView waiterPhone = convertView.findViewById(R.id.waiterPhone);
        //BindView
        Participant participant = getItem(position);
        waiterName.setText(participant.waiterName);
        waiterPhone.setText(participant.waiterPhone);
        waiterNumber.setText(participant.waiterNumber + "");

        return convertView;
    }


}
