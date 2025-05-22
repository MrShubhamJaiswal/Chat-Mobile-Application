package com.example.whatsappclone.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.whatsappclone.R;

import java.util.Objects;

public class UserProfileDetails extends AppCompatActivity {

    TextView tabMedia, tabFiles, tabLinks, tabVoice;
    LinearLayout layoutMedia, layoutFiles, layoutLinks, layoutVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile_details);

        Objects.requireNonNull(getSupportActionBar()).hide();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabMedia = findViewById(R.id.tabMedia);
        tabFiles = findViewById(R.id.tabFiles);
        tabLinks = findViewById(R.id.tabLinks);
        tabVoice = findViewById(R.id.tabVoice);

        layoutMedia = findViewById(R.id.layoutMedia);
        layoutFiles = findViewById(R.id.layoutFiles);
        layoutLinks = findViewById(R.id.layoutLinks);
        layoutVoice = findViewById(R.id.layoutVoice);

        // Set default selected tab
        showTab("media");

        tabMedia.setOnClickListener(v -> showTab("media"));
        tabFiles.setOnClickListener(v -> showTab("files"));
        tabLinks.setOnClickListener(v -> showTab("links"));
        tabVoice.setOnClickListener(v -> showTab("voice"));
    }

    private void showTab(String tab) {
        layoutMedia.setVisibility(tab.equals("media") ? View.VISIBLE : View.GONE);
        layoutFiles.setVisibility(tab.equals("files") ? View.VISIBLE : View.GONE);
        layoutLinks.setVisibility(tab.equals("links") ? View.VISIBLE : View.GONE);
        layoutVoice.setVisibility(tab.equals("voice") ? View.VISIBLE : View.GONE);

        int selectedColor = 0xFF03A9F4;  // #03A9F4 with full alpha (opaque)
        int defaultColor = getResources().getColor(android.R.color.black); // white color for unselected text

        tabMedia.setTextColor(tab.equals("media") ? selectedColor : defaultColor);
        tabFiles.setTextColor(tab.equals("files") ? selectedColor : defaultColor);
        tabLinks.setTextColor(tab.equals("links") ? selectedColor : defaultColor);
        tabVoice.setTextColor(tab.equals("voice") ? selectedColor : defaultColor);
    }

}
