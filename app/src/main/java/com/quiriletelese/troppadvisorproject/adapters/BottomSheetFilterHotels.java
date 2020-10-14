package com.quiriletelese.troppadvisorproject.adapters;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.interfaces.AutoCompleteTextViewsAccomodationFilterTextChangeListener;
import com.quiriletelese.troppadvisorproject.interfaces.BottomSheetFilterSearchButtonClick;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class BottomSheetFilterHotels extends BottomSheetDialogFragment implements View.OnClickListener,
        TextWatcher, SeekBar.OnSeekBarChangeListener {

    private BottomSheetBehavior bottomSheetBehavior;
    private ImageView imageViewGoBack, imageViewSearch;
    private AutoCompleteTextView autoCompleteTextViewName, autoCompleteTextViewCity;
    private TextView textViewPrice, textViewRating, textViewStars, textViewDistance;
    private SeekBar seekBarPrice, seekBarRating, seekBarStars, seekBarDistance;
    private SwitchCompat switchCompatCertificateOfExcellence;
    private BottomSheetFilterSearchButtonClick bottomSheetFilterSearchButtonClick;
    private AutoCompleteTextViewsAccomodationFilterTextChangeListener autoCompleteTextViewsAccomodationFilterTextChangeListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View bottomSheetView = View.inflate(getContext(), R.layout.hotel_filter_layout, null);
        initializeBottomSheetComponents(bottomSheetView);
        setListenerOnComponents();
        bottomSheet.setContentView(bottomSheetView);
        bottomSheetBehavior = BottomSheetBehavior.from((View) (bottomSheetView.getParent()));
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
        return bottomSheet;
    }

    @Override
    public void onStart() {
        super.onStart();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_go_back_hotel_map_filter:
                dismiss();
                break;
            case R.id.image_view_search_hotel_map_filter:
                if (bottomSheetFilterSearchButtonClick != null)
                    bottomSheetFilterSearchButtonClick.OnBottomSheetFilterSearchButtonClick();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        detectAutoCompleteTextView(editable);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        detectSeekBar(seekBar, i);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void initializeBottomSheetComponents(View bottomSheetView) {
        imageViewGoBack = bottomSheetView.findViewById(R.id.image_view_go_back_hotel_map_filter);
        imageViewSearch = bottomSheetView.findViewById(R.id.image_view_search_hotel_map_filter);
        autoCompleteTextViewName = bottomSheetView.findViewById(R.id.auto_complete_text_view_map_hotel_name);
        autoCompleteTextViewCity = bottomSheetView.findViewById(R.id.auto_complete_text_view_map_hotel_city);
        textViewPrice = bottomSheetView.findViewById(R.id.text_view_hotel_map_price);
        textViewRating = bottomSheetView.findViewById(R.id.text_view_hotel_map_rating);
        textViewStars = bottomSheetView.findViewById(R.id.text_view_hotel_map_stars);
        textViewDistance = bottomSheetView.findViewById(R.id.text_view_hotel_map_distance);
        seekBarPrice = bottomSheetView.findViewById(R.id.seek_bar_hotel_map_price);
        seekBarRating = bottomSheetView.findViewById(R.id.seek_bar_hotel_map_rating);
        seekBarStars = bottomSheetView.findViewById(R.id.seek_bar_hotel_map_stars);
        seekBarDistance = bottomSheetView.findViewById(R.id.seek_bar_hotel_map_distance);
        switchCompatCertificateOfExcellence = bottomSheetView.findViewById(R.id.switch_compat_hotel_map_certificate_of_excellence);
    }

    private void setListenerOnComponents() {
        imageViewGoBack.setOnClickListener(this);
        imageViewSearch.setOnClickListener(this);
        autoCompleteTextViewName.addTextChangedListener(this);
        autoCompleteTextViewCity.addTextChangedListener(this);
        seekBarPrice.setOnSeekBarChangeListener(this);
        seekBarRating.setOnSeekBarChangeListener(this);
        seekBarStars.setOnSeekBarChangeListener(this);
        seekBarDistance.setOnSeekBarChangeListener(this);
    }

    private void detectAutoCompleteTextView(Editable editable) {
        if (autoCompleteTextViewsAccomodationFilterTextChangeListener != null) {
            if (editable.equals(autoCompleteTextViewName.getEditableText()))
                autoCompleteTextViewsAccomodationFilterTextChangeListener.onAutoCompleteTextViewAccomodationNameTextChanged(
                        autoCompleteTextViewName.getText().toString());
            if (editable.equals(autoCompleteTextViewCity.getEditableText()))
                autoCompleteTextViewsAccomodationFilterTextChangeListener.onAutoCompleteTextViewAccomodtionCityTextChanged(
                        autoCompleteTextViewCity.getText().toString());
        }
    }

    private void detectSeekBar(SeekBar seekBar, int progress) {
        switch (seekBar.getId()) {
            case R.id.seek_bar_hotel_map_price:
                setTextViewPriceText(progress);
                break;
            case R.id.seek_bar_hotel_map_rating:
                setTextViewRatingText(progress);
                break;
            case R.id.seek_bar_hotel_map_stars:
                setTextViewStarsText(progress);
                break;
            case R.id.seek_bar_hotel_map_distance:
                setTextViewDistanceText(progress);
                break;
        }
    }

    private void setTextViewPriceText(int progress) {
        if (progress == 0)
            textViewPrice.setText(getResources().getString(R.string.price_search_page));
        else if (progress >= 150)
            textViewPrice.setText(String.format(getResources().getString(R.string.price_over_limit), progress));
        else
            textViewPrice.setText(String.format(getResources().getString(R.string.price_up_to_something), progress));
    }

    private void setTextViewRatingText(int progress) {
        if (progress == 0)
            textViewRating.setText(getResources().getString(R.string.rank_search_page));
        else
            textViewRating.setText(String.format(getResources().getString(R.string.rank_up_to_something), progress));
    }

    private void setTextViewStarsText(int progress) {
        if (progress == 0)
            textViewStars.setText(getResources().getString(R.string.hotel_stars_filter));
        else
            textViewStars.setText(String.format(getResources().getString(R.string.hotel_stars_filter_up_to), progress));
    }

    private void setTextViewDistanceText(int progress) {
        if (progress == 0)
            textViewDistance.setText(getResources().getString(R.string.distance_filter_page));
        else
            textViewDistance.setText(String.format(getResources().getString(R.string.distance_up_to_something), progress));
    }

    public void setBottomSheetFilterSearchButtonClick(BottomSheetFilterSearchButtonClick bottomSheetFilterSearchButtonClick) {
        this.bottomSheetFilterSearchButtonClick = bottomSheetFilterSearchButtonClick;
    }

    public void setAutoCompleteTextViewsAccomodationFilterTextChangeListener(AutoCompleteTextViewsAccomodationFilterTextChangeListener autoCompleteTextViewsAccomodationFilterTextChangeListener) {
        this.autoCompleteTextViewsAccomodationFilterTextChangeListener = autoCompleteTextViewsAccomodationFilterTextChangeListener;
    }

    public AutoCompleteTextView getAutoCompleteTextViewName() {
        return autoCompleteTextViewName;
    }

    public AutoCompleteTextView getAutoCompleteTextViewCity() {
        return autoCompleteTextViewCity;
    }

    public SeekBar getSeekBarDistance() {
        return seekBarDistance;
    }

    public String getAutoCompleteTextViewNameValue() {
        return autoCompleteTextViewName.getText().toString();
    }

    public String getAutoCompleteTextViewCityValue() {
        return autoCompleteTextViewCity.getText().toString();
    }

    public void setAutoCompleteTextViewNameAdapter(ArrayAdapter<String> arrayAdapter ) {
        autoCompleteTextViewName.setAdapter(arrayAdapter);
    }

    public void setAutoCompleteTextViewCityAdapter(ArrayAdapter<String> arrayAdapter ) {
        autoCompleteTextViewCity.setAdapter(arrayAdapter);
    }

    public int getSeekBarPriceValue() {
        return seekBarPrice.getProgress();
    }

    public int getSeekBarRatingValue() {
        return seekBarRating.getProgress();
    }

    public int getSeekBarStarsValue() {
        return seekBarStars.getProgress();
    }

    public int getSeekBarDistanceValue() {
        return seekBarDistance.getProgress();
    }

    public boolean getSwitchCompatCertificateOfExcellenceIsSelected() {
        return switchCompatCertificateOfExcellence.isChecked();
    }

    public void setAutoCompleteTextViewNameText(String value) {
        autoCompleteTextViewName.setText(value);
    }

    public void setAutoCompleteTextViewCityText(String value) {
        autoCompleteTextViewCity.setText(value);
    }

    public void setSeekBarPriceEnabled(boolean value) {
        seekBarPrice.setEnabled(value);
    }

    public void setSeekBarRatingEnabled(boolean value) {
        seekBarRating.setEnabled(value);
    }

    public void setSeekBarStarsEnabled(boolean value) {
        seekBarStars.setEnabled(value);
    }

    public void setSeekBarDistanceEnabled(boolean value) {
        seekBarDistance.setEnabled(value);
    }

    public void setSwitchCompatCertificateOfExcellenceEnabled(boolean value) {
        switchCompatCertificateOfExcellence.setEnabled(value);
    }

    public void setSeekBarPriceProgress(Integer progress) {
        seekBarPrice.setProgress(progress);
    }

    public void setSeekBarRatingProgress(Integer progress) {
        seekBarRating.setProgress(progress);
    }

    public void setSeekBarStarsProgress(Integer progress) {
        seekBarStars.setProgress(progress);
    }

    public void setSeekBarDistanceProgress(Integer progress) {
        seekBarDistance.setProgress(progress);
    }

    public void setSwitchCompatCertificateOfExcellenceChecked(boolean value) {
        switchCompatCertificateOfExcellence.setChecked(value);
    }

}
