package com.android.queue.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.queue.R;
import com.android.queue.models.Participant;

import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder>{

    private List<Participant> mListParticipant;

    public ParticipantAdapter(List<Participant> mListParticipant) {
        this.mListParticipant = mListParticipant;
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participant,parent,false);
        //ParticipantViewHolder participantViewHolder= new ParticipantViewHolder(view);
        return new ParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        Participant participant=mListParticipant.get(position);
        if(participant==null){
            return;
        }
        holder.tv_Name.setText("Name: "+participant.getWaiterName());
        holder.tv_Phone.setText("Phone: "+participant.getWaiterPhone());
    }

    @Override
    public int getItemCount() {
        if(mListParticipant!=null){
            return mListParticipant.size();
        }
        return 0;
    }

    public class ParticipantViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_Name;
        private TextView tv_Phone;


        public ParticipantViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_Name = itemView.findViewById(R.id.tv_name);
            tv_Phone = itemView.findViewById(R.id.tv_phone);
        }
    }
}
