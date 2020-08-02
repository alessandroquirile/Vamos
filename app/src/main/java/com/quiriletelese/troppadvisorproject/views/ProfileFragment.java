package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.ProfileController;

public class ProfileFragment extends Fragment {

    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextEmail;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private FloatingActionButton floatingActionButton;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeViewComponents();
        initializeController();
        return view;
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    public void initializeViewComponents() {
        editTextName = view.findViewById(R.id.edit_text_name_fragment_profile);
        editTextLastName = view.findViewById(R.id.edit_text_lastname_fragment_profile);
        editTextEmail = view.findViewById(R.id.edit_text_email_fragment_profile);
        editTextUsername = view.findViewById(R.id.edit_text_nickname_fragment_profile);
        editTextPassword = view.findViewById(R.id.edit_text_password_fragment_profile);
        floatingActionButton = view.findViewById(R.id.floating_action_button_edit_profile_fragment);
    }

    public void initializeController() {
        ProfileController profileController = new ProfileController(this);
        profileController.setListenersOnProfileFragment();
    }

    public EditText getEditTextPassword() {
        return editTextPassword;
    }

    public FloatingActionButton getFloatingActionButton() {
        return floatingActionButton;
    }

    public EditText getEditTextName() {
        return editTextName;
    }

    public EditText getEditTextLastName() {
        return editTextLastName;
    }

    public EditText getEditTextUsername() {
        return editTextUsername;
    }

    public EditText getEditTextEmail() {
        return editTextEmail;
    }
}