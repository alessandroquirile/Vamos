package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.ProfileController;

public class ProfileFragment extends Fragment {

    private TextView textViewNameProfile, textViewLastnameProfile, textViewEmaiProfile,
            textViewUserNameProfile, textViewPasswordProfile;
    private FloatingActionButton floatingActionButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeViewComponents(view);
        initializeController();
        return view;
    }

    public void initializeViewComponents(View view) {
        textViewNameProfile = view.findViewById(R.id.text_view_name_profile);
        textViewLastnameProfile = view.findViewById(R.id.text_view_last_name_profile);
        textViewEmaiProfile = view.findViewById(R.id.text_view_email_profile);
        textViewUserNameProfile = view.findViewById(R.id.text_view_username_profile);
        textViewPasswordProfile = view.findViewById(R.id.text_view_password_profile);
        floatingActionButton = view.findViewById(R.id.floating_action_button_edit_profile_fragment);
    }

    public void initializeController() {
        ProfileController profileController = new ProfileController(this);
        profileController.setListenersOnProfileFragment();
    }

    public FloatingActionButton getFloatingActionButton() {
        return floatingActionButton;
    }

    public TextView getTextViewNameProfile() {
        return textViewNameProfile;
    }

    public TextView getTextViewLastnameProfile() {
        return textViewLastnameProfile;
    }

    public TextView getTextViewEmaiProfile() {
        return textViewEmaiProfile;
    }

    public TextView getTextViewUserNameProfile() {
        return textViewUserNameProfile;
    }

    public TextView getTextViewPasswordProfile() {
        return textViewPasswordProfile;
    }

}