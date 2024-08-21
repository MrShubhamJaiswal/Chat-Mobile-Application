package com.example.whatsappclone.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.AdapterConversation;
import com.example.whatsappclone.databinding.FragmentChatBinding;
import com.example.whatsappclone.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
    FragmentChatBinding binding;
    FirebaseDatabase database;
    ArrayList<User> userArrayList;
    AdapterConversation adapterConversation;
    public ChatFragment() {
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        binding = FragmentChatBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        
        userArrayList = new ArrayList<>();
        adapterConversation = new AdapterConversation(getContext(),userArrayList);
        binding.recConversation.setAdapter(adapterConversation);
        
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    if (!user.getUid().equals(FirebaseAuth.getInstance().getUid()))
                        userArrayList.add(user);
                }
                adapterConversation.notifyDataSetChanged();
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            
            }
        });
        return binding.getRoot();
    }
}