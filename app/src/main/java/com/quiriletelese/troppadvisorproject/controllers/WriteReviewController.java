package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.dao_interfaces.ReviewDAO;
import com.quiriletelese.troppadvisorproject.factories.DAOFactory;
import com.quiriletelese.troppadvisorproject.models.Review;
import com.quiriletelese.troppadvisorproject.utils.ConfigFileReader;
import com.quiriletelese.troppadvisorproject.views.OverviewActivity;
import com.quiriletelese.troppadvisorproject.views.WriteReviewActivity;

import java.io.IOException;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class WriteReviewController implements View.OnClickListener {
    private DAOFactory daoFactory;
    private ReviewDAO reviewDAO;
    private OverviewActivity overviewActivity;
    private WriteReviewActivity writeReviewActivity;

    public WriteReviewController(OverviewActivity overviewActivity) {
        this.overviewActivity = overviewActivity;
    }

    public WriteReviewController(WriteReviewActivity writeReviewActivity) {
        this.writeReviewActivity = writeReviewActivity;
    }

    public void aggiungiRecensione(String title, String description, int numStars, boolean isAnonymous) {
        Review review = new Review(title, description, numStars, isAnonymous);
        daoFactory = DAOFactory.getInstance();
        try {
            reviewDAO = daoFactory.getReviewDAO(ConfigFileReader.getProperty("review_storage_technology",
                    writeReviewActivity.getApplicationContext()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (reviewDAO.add(review))
            Toast.makeText(writeReviewActivity.getApplicationContext(), "Add true", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(writeReviewActivity.getApplicationContext(), "Expected reviewDAO.add implementation " + review.toString(),
                    Toast.LENGTH_LONG).show();
    }

    public void setListenerOnOverviewActiviyComponents() {
        overviewActivity.getFloatingActionButtonWriteReview().setOnClickListener(this);
    }

    public void setListenerOnWriteReviewActivityComponents() {
        writeReviewActivity.getButtonPublishReview().setOnClickListener(this);
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
                aggiungiRecensione(writeReviewActivity.getTextInputEditTextTitle().getText().toString(),
                        writeReviewActivity.getTextInputEditTextDescription().getText().toString(),
                        writeReviewActivity.getRatingBar().getNumStars(),
                        false); // qui andrà messo qualcosa per leggere lo switch
                break;
        }
    }
}