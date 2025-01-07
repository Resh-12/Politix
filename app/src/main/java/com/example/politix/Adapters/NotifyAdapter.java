package com.example.politix.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.politix.R;
import com.example.politix.models.Notification;

import java.util.List;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.ViewHolder> {
    Context context;
    List<Notification> notif;
    public NotifyAdapter(Context context, List<Notification> notif) {
        this.context = context;
        this.notif = notif;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.notify,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Notification notifymodel=notif.get(position);
    holder.text.setText(notifymodel.getText());
    holder.time.setText(notifymodel.getTimestamp());

    }

    @Override
    public int getItemCount() {
        return notif.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        TextView text,time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text=itemView.findViewById(R.id.texts);
            time=itemView.findViewById(R.id.time);
        }
    }
}

