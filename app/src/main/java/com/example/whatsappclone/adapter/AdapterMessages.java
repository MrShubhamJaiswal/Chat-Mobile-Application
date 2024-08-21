package com.example.whatsappclone.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.SrdReceiveBinding;
import com.example.whatsappclone.databinding.SrdSendBinding;
import com.example.whatsappclone.model.ModelMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

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
        // to delete messages
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you Sure?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String senderRoom = FirebaseAuth.getInstance().getUid() + recId;
                                database.getReference().child("chats").child(senderRoom).child("messages")
                                        .child(modelMessage.getMeesageId()).setValue(null);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return false;
            }
        });
        
        
        if (holder.getClass()== SentViewHolder.class){
            SentViewHolder viewHolder = (SentViewHolder) holder;
            viewHolder.binding.tvSendMessage.setText(modelMessage.getMessage());
        }else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.binding.tvReceiveMessage.setText(modelMessage.getMessage());
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
