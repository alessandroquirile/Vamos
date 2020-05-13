package com.quiriletelese.troppadvisorproject.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.quiriletelese.troppadvisorproject.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textViewSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViewComponent();
        setListenerOnViewComponent();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewSignIn:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                break;
        }
    }

    private void initializeViewComponent() {
        textViewSignIn = findViewById(R.id.textViewSignIn);
    }

    private void setListenerOnViewComponent() {
        textViewSignIn.setOnClickListener(this);
    }

}
