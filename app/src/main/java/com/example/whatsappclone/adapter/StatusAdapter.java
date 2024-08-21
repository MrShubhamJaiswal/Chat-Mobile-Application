package com.example.whatsappclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Fragments.ChatFragment;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.SrdStatusBinding;
import com.example.whatsappclone.model.ModelStatus;
import com.example.whatsappclone.model.ModelUserStatus;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder>{
    Context context;
    ArrayList<ModelUserStatus> userStatuses;
    private FragmentManager fragmentManager;
    
    public StatusAdapter(Context context, ArrayList<ModelUserStatus> userStatuses,FragmentManager fragmentManager) {
        this.context = context;
        this.userStatuses = userStatuses;
        this.fragmentManager = fragmentManager;
    }
    
    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.srd_status,parent,false);
        return new StatusViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
        ModelUserStatus userStatus = userStatuses.get(position);
        ModelStatus lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size()-1);
        Glide.with(context).load(lastStatus.getImgUrl()).into(holder.binding.circularStatusImage);
        holder.binding.usernameStatus.setText(userStatus.getName());
        holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for(ModelStatus status: userStatus.getStatuses()){
                    myStories.add(new MyStory(status.getImgUrl()));
                }
                new StoryView.Builder(fragmentManager)
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStatus.getName()) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(userStatus.getProfileImage()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }
                            
                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
                
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return userStatuses.size();
    }
    
    public class StatusViewHolder extends RecyclerView.ViewHolder{
        SrdStatusBinding binding;
        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SrdStatusBinding.bind(itemView);
        }
    }
}
