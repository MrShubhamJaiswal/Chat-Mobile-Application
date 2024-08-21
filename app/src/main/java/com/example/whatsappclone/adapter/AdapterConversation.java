package com.example.whatsappclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.model.User;
import com.example.whatsappclone.activity.ChatActivity;
import com.example.whatsappclone.databinding.SrdConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterConversation extends RecyclerView.Adapter<AdapterConversation.ConversationViewHolder> {
    Context context;
    ArrayList<User> users;

    public AdapterConversation() {
    }

    public AdapterConversation(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.srd_conversation,parent,false);

        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        User user = users.get(position);
        String senderId = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderId+user.getUid();
        FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String lastMessage = snapshot.child("lastMessage").getValue(String.class);
                    long lastTime = snapshot.child("lastMessageTime").getValue(Long.class);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    holder.binding.tvTime.setText(dateFormat.format(new Date(lastTime)));
                    holder.binding.tvLastMessage.setText(lastMessage);

                }
                else {
                    holder.binding.tvLastMessage.setText("Tap to chat");
                    holder.binding.tvTime.setText("");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.binding.tvUserName.setText(user.getName());
        Glide.with(context).load(user.getProfileImage()).into(holder.binding.userProfilePicture);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("name",user.getName());
            intent.putExtra("uid",user.getUid());
            intent.putExtra("profileImage",user.getProfileImage());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder{
        SrdConversationBinding binding;
        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SrdConversationBinding.bind(itemView);
        }
    }

}
