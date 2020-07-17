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
import com.quiriletelese.troppadvisorproject.controllers.UpdateProfileController;

public class ProfileFragment extends Fragment {

    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextNickname;
    private EditText editTextPassword;
    private FloatingActionButton floatingActionButton;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_profile, container, false);
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeViewComponents();
        initializeController();
        return view;
    }

    public void initializeViewComponents() {
        editTextName = view.findViewById(R.id.edit_text_name_fragment_profile);
        editTextLastName = view.findViewById(R.id.edit_text_lastname_fragment_profile);
        editTextNickname = view.findViewById(R.id.edit_text_nickname_fragment_profile);
        editTextPassword = view.findViewById(R.id.edit_text_password_fragment_profile);
        floatingActionButton = view.findViewById(R.id.floating_action_button_edit_profile_fragment);
    }

    public void initializeController() {
        UpdateProfileController updateProfileController = new UpdateProfileController(this);
        updateProfileController.setListenersOnProfileFragment();
    }

    public EditText getEditTextName() {
        return editTextName;
    }

    public void setEditTextName(EditText editTextName) {
        this.editTextName = editTextName;
    }

    public EditText getEditTextLastName() {
        return editTextLastName;
    }

    public void setEditTextLastName(EditText editTextLastName) {
        this.editTextLastName = editTextLastName;
    }

    public EditText getEditTextNickname() {
        return editTextNickname;
    }

    public void setEditTextNickname(EditText editTextNickname) {
        this.editTextNickname = editTextNickname;
    }

    public EditText getEditTextPassword() {
        return editTextPassword;
    }

    public void setEditTextPassword(EditText editTextPassword) {
        this.editTextPassword = editTextPassword;
    }

    public FloatingActionButton getFloatingActionButton() {
        return floatingActionButton;
    }

    public void setFloatingActionButton(FloatingActionButton floatingActionButton) {
        this.floatingActionButton = floatingActionButton;
    }
}