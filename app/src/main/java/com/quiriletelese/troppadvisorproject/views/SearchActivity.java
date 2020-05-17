package com.quiriletelese.troppadvisorproject.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.quiriletelese.troppadvisorproject.R;

import java.util.Objects;

public class SearchActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        View.OnClickListener {
    private RadioGroup radioGroup;
    private TextView textViewPrice;
    private TextView textViewDistance;
    private TextView textViewRank;
    private SeekBar seekBarPrice;
    private SeekBar seekbarDistance;
    private SeekBar seekBarRank;
    private Button buttonSeeResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar_search_page);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initViewComponents();
        setListenerOnViewComponents();
    }

    public void initViewComponents() {
        radioGroup = findViewById(R.id.radiogroup);
        textViewPrice = findViewById(R.id.textview_price);
        textViewDistance = findViewById(R.id.textview_distance);
        textViewRank = findViewById(R.id.textview_rank);
        seekBarPrice = findViewById(R.id.seekbar_price);
        seekbarDistance = findViewById(R.id.seekbar_distance);
        seekBarRank = findViewById(R.id.seekbar_rank);
        buttonSeeResults = findViewById(R.id.button_see_results);
    }

    private void setListenerOnViewComponents() {
        seekBarPrice.setOnSeekBarChangeListener(this);
        seekbarDistance.setOnSeekBarChangeListener(this);
        seekBarRank.setOnSeekBarChangeListener(this);
        buttonSeeResults.setOnClickListener(this);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekbar_price:
                if (progress == 0)
                    textViewPrice.setText(getResources().getString(R.string.price_search_page));
                else
                    textViewPrice.setText(String.format(getResources().getString(R.string.price_up_to_something), progress));
                break;

            case R.id.seekbar_distance:
                if (progress == 0)
                    textViewDistance.setText(getResources().getString(R.string.distance_search_page));
                else
                    textViewDistance.setText(String.format(getResources().getString(R.string.distance_up_to_something), progress));
                break;

            case R.id.seekbar_rank:
                if (progress == 0)
                    textViewRank.setText(getResources().getString(R.string.rank_search_page));
                else
                    textViewRank.setText(String.format(getResources().getString(R.string.rank_up_to_something), progress));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_see_results:
                startActivity(new Intent(SearchActivity.this, SeeResultsActivity.class));
                /*RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                Toast.makeText(this, "Selected Radio Button: " + radioButton.getText() + "" +
                        "\nPrezzo fino a " + seekBarPrice.getProgress() + "" +
                        "\nDistanza fino a " + seekbarDistance.getProgress() + "" +
                        "\nVoto medio da " + seekBarRank.getProgress() + "" +
                        "\n\nNota che quando i parametri sono su Any, progress è 0", Toast.LENGTH_SHORT).show();*/
            break;
        }
    }
}
