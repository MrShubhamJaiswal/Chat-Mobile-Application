package com.example.whatsappclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.whatsappclone.Fragments.CallsFragment;
import com.example.whatsappclone.Fragments.ChatFragment;
import com.example.whatsappclone.Fragments.StatusFragment;
import com.example.whatsappclone.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.green)); // Make sure this color is correct or omit if not needed
        replaceFrameLayout(new ChatFragment());
        
        
        

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()== R.id.menuItemChats){
                    replaceFrameLayout(new ChatFragment());
                    getSupportActionBar().setTitle("Chat App");
                } else if (item.getItemId()== R.id.menuItemStatus) {
                    replaceFrameLayout(new StatusFragment());
                    getSupportActionBar().setTitle("Status");
                } else if (item.getItemId()==R.id.meniItemCalls) {
                    replaceFrameLayout(new CallsFragment());
                    getSupportActionBar().setTitle("Settings");
                    
                }
                return true ;
            }
        });
    }

    private void replaceFrameLayout(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }

  

  
}