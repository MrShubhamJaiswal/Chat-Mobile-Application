package com.example.whatsappclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.AdapterMessages;
import com.example.whatsappclone.databinding.ActivityChatBinding;
import com.example.whatsappclone.model.ModelMessage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    AdapterMessages adapterMessages;
    ArrayList<ModelMessage> messageArrayList;
    String senderRoom, receiverRoom;
    FirebaseDatabase database;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getSupportActionBar().hide();
        
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.greenDark));
        
        String name = getIntent().getStringExtra("name");
        String receiverUid = getIntent().getStringExtra("uid");
        String profilePic = getIntent().getStringExtra("profileImage");
        String senderUid = FirebaseAuth.getInstance().getUid();
        
        
        binding.tvusernamechat.setText(name);
        Glide.with(getApplicationContext()).load(profilePic).placeholder(R.drawable.avatar).into(binding.userProfilePicture);
        binding.backButtonChat.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
        
        messageArrayList = new ArrayList<>();
        adapterMessages = new AdapterMessages(this, messageArrayList,receiverUid);
        binding.recChats.setLayoutManager(new LinearLayoutManager(this));
        binding.recChats.setAdapter(adapterMessages);
        
        
        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;
        database = FirebaseDatabase.getInstance();
// fetching chats
        database.getReference().child("chats").child(senderRoom).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageArrayList.clear();// if we don't add this then as we send new message for loop will get all the message again and again
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ModelMessage modelMessage = snapshot1.getValue(ModelMessage.class);
                    modelMessage.setMeesageId(snapshot1.getKey());
                    messageArrayList.add(modelMessage);
                }
                adapterMessages.notifyDataSetChanged(); // for updating recyclerview in realtime
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            
            }
        });
// inserting sender chats to database
        binding.sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = binding.edtMessageBox.getText().toString();
                Date date = new Date();
                ModelMessage modelMessage = new ModelMessage(messageText, senderUid, date.getTime());
                binding.edtMessageBox.setText("");
                String randomKey = database.getReference().push().getKey();
                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMessage", modelMessage.getMessage());
                lastMsgObj.put("lastMessageTime", date.getTime());
                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(senderRoom).child("messages").child(randomKey).setValue(modelMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // inserting receiver chats to database
                        database.getReference().child("chats").child(receiverRoom).child("messages").child(randomKey).setValue(modelMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                            
                            }
                        });
                        // to show last message and time
                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                        lastMsgObj.put("lastMessage", modelMessage.getMessage());
                        lastMsgObj.put("lastMessageTime", date.getTime());
                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);
                    }
                });
            }
        });
        
        
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    
}