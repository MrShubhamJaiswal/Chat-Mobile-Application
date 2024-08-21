package com.example.whatsappclone.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.StatusAdapter;
import com.example.whatsappclone.model.ModelStatus;
import com.example.whatsappclone.model.ModelUserStatus;
import com.example.whatsappclone.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class StatusFragment extends Fragment {
     RecyclerView recStatus;
    StatusAdapter statusAdapter;
    ArrayList<ModelUserStatus> userStatusArrayList;
    FloatingActionButton addStatus;
    ProgressDialog dialog;
    User user;
    FirebaseDatabase database;
    public StatusFragment() {
        // Required empty public constructor
    }
  

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        recStatus = view.findViewById(R.id.recStatus);
        addStatus = view.findViewById(R.id.addStatusFloatingButton);
        userStatusArrayList = new ArrayList<>();
        statusAdapter = new StatusAdapter(getContext(),userStatusArrayList,getChildFragmentManager());
        recStatus.setLayoutManager(new LinearLayoutManager(getContext()));
        recStatus.setAdapter(statusAdapter);
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("uploading image");
        dialog.setCancelable(false);
        database = FirebaseDatabase.getInstance();
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            
            }
        });
        database.getReference().child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userStatusArrayList.clear();
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        ModelUserStatus userStatus = new ModelUserStatus();
                        userStatus.setName(snapshot1.child("name").getValue(String.class));
                        userStatus.setProfileImage(snapshot1.child("profileImage").getValue(String.class));
                        userStatus.setLastUpdated(snapshot1.child("lastUpdated").getValue(Long.class));
                        
                        ArrayList<ModelStatus> statuses = new ArrayList<>();
                        for (DataSnapshot statusSnapshot : snapshot1.child("statuses").getChildren()){
                            ModelStatus sampleStatus = statusSnapshot.getValue(ModelStatus.class);
                            statuses.add(sampleStatus);
                            
                        }
                        userStatus.setStatuses(statuses);
                        userStatusArrayList.add(userStatus);
                    }
                    statusAdapter.notifyDataSetChanged();
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            
            }
        });
        addStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,75);
               
                
            }
        });
        
        return view;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (data.getData()!=null){
                dialog.show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
               StorageReference reference=  storage.getReference().child("status").child(date.getTime()+ "");
               reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ModelUserStatus modelUserStatus = new ModelUserStatus();
                            modelUserStatus.setName(user.getName());
                            modelUserStatus.setProfileImage(user.getProfileImage());
                            modelUserStatus.setLastUpdated(date.getTime());
                            HashMap<String,Object> obj = new HashMap<>();
                            obj.put("name",modelUserStatus.getName());
                            obj.put("profileImage",modelUserStatus.getProfileImage());
                            obj.put("lastUpdated",modelUserStatus.getLastUpdated());
                            String imgUrl = uri.toString();
                            ModelStatus status = new ModelStatus(imgUrl,modelUserStatus.getLastUpdated());
                            database.getReference().child("status").child(FirebaseAuth.getInstance().getUid()).updateChildren(obj);
                            database.getReference().child("status").child(FirebaseAuth.getInstance().getUid()).child("statuses").push().setValue(status);
                            dialog.dismiss();
                        }
                    });
                   }
               });
               
            }
        }
    }
}