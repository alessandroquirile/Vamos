package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.controllers.ProfileFragmentController;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class ProfileFragment extends Fragment {

    private ProfileFragmentController profileFragmentController;
    private TextView textViewName, textViewFamilyName, textViewEmail,
            textViewUserName, textViewPassword;
    private View viewNoLoginProfileError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        initializeViewComponents(view);
        initializeController();
        checkLogin();
        setProfileFields();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflateMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onOptionsItemSelectedHelper(item);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFragment();
        checkLogin();
        setProfileFields();
        invalidateOptionsMenu();
    }

    private void inflateMenu(Menu menu, MenuInflater inflater) {
        if (!isLogged())
            inflater.inflate(R.menu.menu_profile_login, menu);
        else
            inflater.inflate(R.menu.menu_profile_logout, menu);
    }

    private void onOptionsItemSelectedHelper(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login_profile:
                startLoginActivity();
                break;
            case R.id.logout_profile:
                startLoginActivityFromLogOut();
                break;
        }
    }

    public void initializeViewComponents(View view) {
        textViewName = view.findViewById(R.id.text_view_name_profile);
        textViewFamilyName = view.findViewById(R.id.text_view_family_name_profile);
        textViewEmail = view.findViewById(R.id.text_view_email_profile);
        textViewUserName = view.findViewById(R.id.text_view_username_profile);
        textViewPassword = view.findViewById(R.id.text_view_password_profile);
        viewNoLoginProfileError = view.findViewById(R.id.no_login_profile_error_layout);
    }

    public void initializeController() {
        profileFragmentController = new ProfileFragmentController(this);
    }

    private void refreshFragment() {
        FragmentTransaction tr = getParentFragmentManager().beginTransaction();
        tr.replace(R.id.nav_host_fragment_container, this);
        tr.commit();
    }

    private void checkLogin() {
        profileFragmentController.checkLogin();
    }

    private void setProfileFields() {
        profileFragmentController.setProfileFields();
    }

    private void invalidateOptionsMenu() {
        getActivity().invalidateOptionsMenu();
    }

    private boolean isLogged() {
        return profileFragmentController.isLogged();
    }

    private void startLoginActivity() {
        profileFragmentController.startLoginActivity();
    }

    private void startLoginActivityFromLogOut() {
        profileFragmentController.startLoginActivityFromLogOut();
    }

    public TextView getTextViewName() {
        return textViewName;
    }

    public TextView getTextViewFamilyName() {
        return textViewFamilyName;
    }

    public TextView getTextViewEmail() {
        return textViewEmail;
    }

    public TextView getTextViewUserName() {
        return textViewUserName;
    }

    public TextView getTextViewPassword() {
        return textViewPassword;
    }

    public View getViewNoLoginProfileError() {
        return viewNoLoginProfileError;
    }
}