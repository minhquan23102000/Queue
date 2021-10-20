package com.android.queue.adapter;

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
        if(participant.waiterNumber!=-1){
            holder.tv_Name.setText(participant.getWaiterName());
            holder.sTT.setText("STT: "+participant.getWaiterNumber());
            holder.state.setText(participant.waiterState);
        }

    }

    @Override
    public int getItemCount() {
        if (mListParticipant != null) {
            return mListParticipant.size();
        }
        return 0;
    }

    public int getIsWaiterCount(){
        if(mListParticipant!=null){
            int i=0;
            for (Participant temp:mListParticipant) {
                if(temp.waiterNumber!=-1)
                    i++;
            }
            return i;
        }
        return 0;
    }

    public class ParticipantViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_Name;
        private TextView sTT;
        private TextView state;


        public ParticipantViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_Name = itemView.findViewById(R.id.tv_name);
            sTT = itemView.findViewById(R.id.soTT);
            state = itemView.findViewById(R.id.stateTv);
        }
    }
}
