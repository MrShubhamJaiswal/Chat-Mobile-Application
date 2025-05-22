package com.example.whatsappclone.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.SrdReceiveBinding;
import com.example.whatsappclone.databinding.SrdSendBinding;
import com.example.whatsappclone.model.ModelMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterMessages extends RecyclerView.Adapter {
    Context context;
    ArrayList<ModelMessage> messageArrayList;
    final int ITEM_SENT=1;
    final int ITEM_RECEIVED=2;
    String recId;

    public AdapterMessages(Context context, ArrayList<ModelMessage> messageArrayList) {
        this.context = context;
        this.messageArrayList = messageArrayList;
    }
    
    public AdapterMessages(Context context, ArrayList<ModelMessage> messageArrayList, String recId) {
        this.context = context;
        this.messageArrayList = messageArrayList;
        this.recId = recId;
    }
    
    public AdapterMessages() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.srd_send,parent,false);
            return new SentViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.srd_receive,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ModelMessage modelMessage = messageArrayList.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(modelMessage.getSenderId())){
            return ITEM_SENT;
        }else {
            return ITEM_RECEIVED;
        }

    }
    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelMessage modelMessage = messageArrayList.get(position);
        
        // Delete logic (same as before)
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete")
                    .setMessage("Are you sure?")
                    .setPositiveButton("YES", (dialog, which) -> {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        String senderRoom = FirebaseAuth.getInstance().getUid() + recId;
                        database.getReference().child("chats").child(senderRoom).child("messages")
                                .child(modelMessage.getMeesageId()).setValue(null);
                    })
                    .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                    .show();
            return false;
        });
        
        // Show sent message
        if (holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder) holder;
            
            // Time formatting
            long timestamp = modelMessage.getTimestamp();
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            String time = sdf.format(date);
            viewHolder.binding.tvSendTime.setText(time);
            
            // Text message
            if (modelMessage.getMessage() != null && !modelMessage.getMessage().equals("photo")) {
                viewHolder.binding.tvSendMessage.setVisibility(View.VISIBLE);
                viewHolder.binding.tvSendMessage.setText(modelMessage.getMessage());
            } else {
                viewHolder.binding.tvSendMessage.setVisibility(View.GONE);
            }
            
            // Image message
            if (modelMessage.getImageUrl() != null && !modelMessage.getImageUrl().isEmpty()) {
                viewHolder.binding.imgSend.setVisibility(View.VISIBLE);
                Glide.with(context).load(modelMessage.getImageUrl()).into(viewHolder.binding.imgSend);
            } else {
                viewHolder.binding.imgSend.setVisibility(View.GONE);
            }
            
        } else { // Show received message
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            
            long timestamp = modelMessage.getTimestamp();
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            String time = sdf.format(date);
            viewHolder.binding.tvReceiveTime.setText(time);
            
            if (modelMessage.getMessage() != null && !modelMessage.getMessage().equals("photo")) {
                viewHolder.binding.tvReceiveMessage.setVisibility(View.VISIBLE);
                viewHolder.binding.tvReceiveMessage.setText(modelMessage.getMessage());
            } else {
                viewHolder.binding.tvReceiveMessage.setVisibility(View.GONE);
            }
            
            if (modelMessage.getImageUrl() != null && !modelMessage.getImageUrl().isEmpty()) {
                viewHolder.binding.imgReceive.setVisibility(View.VISIBLE);
                Glide.with(context).load(modelMessage.getImageUrl()).into(viewHolder.binding.imgReceive);
            } else {
                viewHolder.binding.imgReceive.setVisibility(View.GONE);
            }
        }
    }
    
    
    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder{
        SrdSendBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SrdSendBinding.bind(itemView);
        }
    }
    public class ReceiverViewHolder extends RecyclerView.ViewHolder{
        SrdReceiveBinding binding;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SrdReceiveBinding.bind(itemView);
        }
    }
}
