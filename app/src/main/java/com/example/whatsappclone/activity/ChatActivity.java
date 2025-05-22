package com.example.whatsappclone.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    AdapterMessages adapterMessages;
    ArrayList<ModelMessage> messageArrayList;
    String senderRoom, receiverRoom;
    FirebaseDatabase database;
    
    private static final int CAMERA_REQUEST_CODE = 1001;
    private static final int CAMERA_PERMISSION_CODE = 200;
    private Uri photoUri;
    private File photoFile;
    
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
        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.attachLayout.getVisibility() == View.VISIBLE) {
                    binding.attachLayout.setVisibility(View.GONE);
                } else {
                    binding.attachLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        
        
        binding.recChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.attachLayout.setVisibility(View.GONE);
            }
        });
        
        binding.camera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                openCamera();
            }
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
                binding.recChats.scrollToPosition(messageArrayList.size() - 1);
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
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        photoFile = createImageFile();
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    photoFile
            );
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }
    
    private File createImageFile() {
        String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(storageDir, fileName);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            sendImageToChat(photoUri);
        }
    }
    
    private void sendImageToChat(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, "Image URI is null", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 1. Upload to Firebase Storage
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileName = "chat_images/" + timestamp + ".jpg";
        
        FirebaseStorage.getInstance().getReference()
                .child(fileName)
                .putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // 2. Get download URL
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // 3. Send message with image URL
                        String imageUrl = downloadUri.toString();
                        sendImageMessage(imageUrl);
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void sendImageMessage(String imageUrl) {
        String senderUid = FirebaseAuth.getInstance().getUid();
        Date date = new Date();
        
        ModelMessage imageMessage = new ModelMessage("photo", senderUid, date.getTime());
        imageMessage.setImageUrl(imageUrl); // Ensure ModelMessage has a field for imageUrl
        
        String randomKey = database.getReference().push().getKey();
        
        // Update last message info
        HashMap<String, Object> lastMsgObj = new HashMap<>();
        lastMsgObj.put("lastMessage", "photo");
        lastMsgObj.put("lastMessageTime", date.getTime());
        
        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);
        
        // Push message to sender's chat
        database.getReference().child("chats").child(senderRoom).child("messages").child(randomKey).setValue(imageMessage)
                .addOnSuccessListener(unused -> {
                    // Push message to receiver's chat
                    database.getReference().child("chats").child(receiverRoom).child("messages").child(randomKey).setValue(imageMessage);
                });
    }
    
    
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    
}