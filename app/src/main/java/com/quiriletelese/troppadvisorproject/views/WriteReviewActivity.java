package com.quiriletelese.troppadvisorproject.views;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.quiriletelese.troppadvisorproject.R;

public class WriteReviewActivity extends AppCompatActivity implements RatingBar.OnRatingBarChangeListener {

    private EditText textInputEditTextTitle;
    private EditText textInputEditTextDescription;
    private RatingBar ratingBar;
    TextView textViewAboutRating;
    private SwitchCompat switchCompatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        initViewComponents();
        setListenerOnViewComponents();
    }

    public void initViewComponents() {
        textInputEditTextTitle = findViewById(R.id.text_input_edit_text_title_write_review_activity);
        textInputEditTextDescription = findViewById(R.id.text_input_edit_text_description_review_activity);
        ratingBar = findViewById(R.id.rating_bar_review_activity);
        textViewAboutRating = findViewById(R.id.text_view_about_rating_review_activity);
        switchCompatButton = findViewById(R.id.switch_button_review_activity);
    }

    public void setListenerOnViewComponents() {
        ratingBar.setOnRatingBarChangeListener(this);
        // TODO: aggiungere il listener sullo switchCompatButton
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        switch ((int) rating) {
            case 1:
                textViewAboutRating.setText(getResources().getString(R.string.pessimo));
                break;
            case 2:
                textViewAboutRating.setText(getResources().getString(R.string.scarso));
                break;
            case 3:
                textViewAboutRating.setText(getResources().getString(R.string.nella_media));
                break;
            case 4:
                textViewAboutRating.setText(getResources().getString(R.string.molto_buono));
                break;
            case 5:
                textViewAboutRating.setText(getResources().getString(R.string.eccellente));
                break;
        }
    }
}
