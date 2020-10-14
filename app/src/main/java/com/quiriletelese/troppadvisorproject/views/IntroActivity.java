package com.quiriletelese.troppadvisorproject.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.adapters.ViewPagerIntroAdapter;
import com.quiriletelese.troppadvisorproject.controllers.IntroActivityController;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;

public class IntroActivity extends AppCompatActivity {

    private IntroActivityController introActivityController;
    private ViewPager viewPager;
    private ViewPagerIntroAdapter viewPagerIntroAdapter;
    private LinearLayout linearLayoutDots;
    private Button buttonPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        initializeViewComponents();
        initializeController();
        setListenerOnComponents();
        initializeViewPagerAdapter();
        setViewPagerAdapter();
        addDots();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestPermissionsResultHelper(requestCode, grantResults);
    }

    private void onRequestPermissionsResultHelper(int requestCode, @NonNull int[] grantResults){
        introActivityController.onRequestPermissionsResult(requestCode, grantResults);
    }

    private void initializeViewComponents() {
        viewPager = findViewById(R.id.view_pager_intro);
        linearLayoutDots = findViewById(R.id.linear_layout_dots);
        buttonPermission = findViewById(R.id.button_permission);
    }

    private void initializeController(){
        introActivityController = new IntroActivityController(this);
    }

    private void setListenerOnComponents(){
        introActivityController.setListenerOnComponents();
    }

    private void initializeViewPagerAdapter() {
        viewPagerIntroAdapter = new ViewPagerIntroAdapter(this);
    }

    private void setViewPagerAdapter() {
        viewPager.setAdapter(viewPagerIntroAdapter);
    }

    private void addDots(){
        introActivityController.addDots(0);
    }

    public LinearLayout getLinearLayoutDots() {
        return linearLayoutDots;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public ViewPagerIntroAdapter getViewPagerIntroAdapter() {
        return viewPagerIntroAdapter;
    }

    public Button getButtonPermission() {
        return buttonPermission;
    }
}