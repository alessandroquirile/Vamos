package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.ReviewDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.OverviewActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class WriteReviewController implements View.OnClickListener, RatingBar.OnRatingBarChangeListener, SwitchCompat.OnCheckedChangeListener {
    private DAOFactory daoFactory;
    private ReviewDAO reviewDAO;
    private OverviewActivity overviewActivity;
    private WriteReviewActivity writeReviewActivity;
    private boolean isAnonymoys = false;

    public WriteReviewController(OverviewActivity overviewActivity) {
        this.overviewActivity = overviewActivity;
    }

    public WriteReviewController(WriteReviewActivity writeReviewActivity) {
        this.writeReviewActivity = writeReviewActivity;
    }

    public void addReview(String title, String description, int numStars, boolean isAnonymous) {
        Review review = new Review(title, description, numStars, isAnonymous);
        daoFactory = DAOFactory.getInstance();
        reviewDAO = daoFactory.getReviewDAO(ConfigFileReader.getProperty("review_storage_technology",
                writeReviewActivity.getApplicationContext()));
        if (reviewDAO.add(review))
            Toast.makeText(writeReviewActivity.getApplicationContext(), "Add true", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(writeReviewActivity.getApplicationContext(), "Expected reviewDAO.add implementation " + review.toString(),
                    Toast.LENGTH_LONG).show();
    }

    public void setListenersOnOverviewActiviyComponents() {
        overviewActivity.getFloatingActionButtonWriteReview().setOnClickListener(this);
    }

    public void setListenersOnWriteReviewActivityComponents() {
        writeReviewActivity.getButtonPublishReview().setOnClickListener(this);
        writeReviewActivity.getRatingBar().setOnRatingBarChangeListener(this);
        writeReviewActivity.getSwitchCompatButton().setOnCheckedChangeListener(this);
    }


    public void showWriteReviewActivity() {
        Intent intent = new Intent(overviewActivity.getApplicationContext(), WriteReviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // necessario per fare lo start di un'activity da una classe che non estende Activity
        overviewActivity.getApplicationContext().startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_action_button_write_review:
                showWriteReviewActivity();
                break;
            case R.id.button_publish_review_review_activity:
                addReview
                        (writeReviewActivity.getTextInputEditTextTitle().getText().toString(),
                                writeReviewActivity.getTextInputEditTextDescription().getText().toString(),
                                (int) writeReviewActivity.getRatingBar().getRating(),
                                isAnonymoys);
                break;
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        switch ((int) rating) {
            case 0:
                writeReviewActivity.getTextViewAboutRating().setText("");
                ratingBar.setRating(1);
                break;
            case 1:
                writeReviewActivity.getTextViewAboutRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.pessimo));
                break;
            case 2:
                writeReviewActivity.getTextViewAboutRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.scarso));
                break;
            case 3:
                writeReviewActivity.getTextViewAboutRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.nella_media));
                break;
            case 4:
                writeReviewActivity.getTextViewAboutRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.molto_buono));
                break;
            case 5:
                writeReviewActivity.getTextViewAboutRating().setText(writeReviewActivity.getApplicationContext().getResources().getString(R.string.eccellente));
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isAnonymoys = isChecked;
    }
}