package com.quiriletelese.troppadvisorproject.adapters;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
import com.quiriletelese.troppadvisorproject.interfaces.AutoCompleteTextViewsAccomodationMapTextChangeListener;
import com.quiriletelese.troppadvisorproject.interfaces.OnImageViewSearchMapFilterClick;

public class BottomSheetAttractionMap extends BottomSheetDialogFragment implements View.OnClickListener,
        TextWatcher, SeekBar.OnSeekBarChangeListener {

    private BottomSheetBehavior bottomSheetBehavior;
    private ImageView imageViewGoBackAttractionMapFilter, imageViewSearchAttractionMapFilter;
    private AutoCompleteTextView autoCompleteTextViewMapAttractionName, autoCompleteTextViewMapAttractionCity;
    private TextView textViewAttractionMapPrice, textViewAttractionMapRating, textViewAttractionMapDistance;
    private SeekBar seekBarAttractionMapPrice, seekBarAttractionMapRating, seekBarAttractionMapDistance;
    private SwitchCompat switchCompatAttractionMapCertificateOfExcellence;
    private OnImageViewSearchMapFilterClick onImageViewSearchMapFilterClick;
    private AutoCompleteTextViewsAccomodationMapTextChangeListener autoCompleteTextViewsAccomodationMapTextChangeListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View bottomSheetView = View.inflate(getContext(), R.layout.attraction_filter_map, null);
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
            case R.id.image_view_go_back_attraction_map_filter:
                dismiss();
                break;
            case R.id.image_view_search_attraction_map_filter:
                if (onImageViewSearchMapFilterClick != null)
                    onImageViewSearchMapFilterClick.onImageViewSearchHotelMapFilterClick();
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
        imageViewGoBackAttractionMapFilter = bottomSheetView.findViewById(R.id.image_view_go_back_attraction_map_filter);
        imageViewSearchAttractionMapFilter = bottomSheetView.findViewById(R.id.image_view_search_attraction_map_filter);
        autoCompleteTextViewMapAttractionName = bottomSheetView.findViewById(R.id.auto_complete_text_view_map_attraction_name);
        autoCompleteTextViewMapAttractionCity = bottomSheetView.findViewById(R.id.auto_complete_text_view_map_attraction_city);
        textViewAttractionMapPrice = bottomSheetView.findViewById(R.id.text_view_attraction_map_price);
        textViewAttractionMapRating = bottomSheetView.findViewById(R.id.text_view_attraction_map_rating);
        textViewAttractionMapDistance = bottomSheetView.findViewById(R.id.text_view_attraction_map_distance);
        seekBarAttractionMapPrice = bottomSheetView.findViewById(R.id.seek_bar_attraction_map_price);
        seekBarAttractionMapRating = bottomSheetView.findViewById(R.id.seek_bar_attraction_map_rating);
        seekBarAttractionMapDistance = bottomSheetView.findViewById(R.id.seek_bar_attraction_map_distance);
        switchCompatAttractionMapCertificateOfExcellence = bottomSheetView.findViewById(R.id.switch_attraction_map_certificate_of_excellence);
    }

    private void setListenerOnComponents() {
        imageViewGoBackAttractionMapFilter.setOnClickListener(this);
        imageViewSearchAttractionMapFilter.setOnClickListener(this);
        autoCompleteTextViewMapAttractionName.addTextChangedListener(this);
        autoCompleteTextViewMapAttractionCity.addTextChangedListener(this);
        seekBarAttractionMapPrice.setOnSeekBarChangeListener(this);
        seekBarAttractionMapRating.setOnSeekBarChangeListener(this);
        seekBarAttractionMapDistance.setOnSeekBarChangeListener(this);
    }

    private void detectAutoCompleteTextView(Editable editable) {
        if (autoCompleteTextViewsAccomodationMapTextChangeListener != null) {
            if (editable.equals(autoCompleteTextViewMapAttractionName.getEditableText()))
                autoCompleteTextViewsAccomodationMapTextChangeListener.onAutoCompleteTextViewAccomodationNameTextChanged(
                        autoCompleteTextViewMapAttractionName.getText().toString());
            if (editable.equals(autoCompleteTextViewMapAttractionCity.getEditableText()))
                autoCompleteTextViewsAccomodationMapTextChangeListener.onAutoCompleteTextViewAccomodtionCityTextChanged(
                        autoCompleteTextViewMapAttractionCity.getText().toString());
        }
    }

    private void detectSeekBar(SeekBar seekBar, int progress) {
        switch (seekBar.getId()) {
            case R.id.seek_bar_restaurant_map_price:
                setTextViewPriceText(progress);
                break;
            case R.id.seek_bar_restaurant_map_rating:
                setTextViewRatingText(progress);
                break;
            case R.id.seek_bar_restaurant_map_distance:
                setTextViewDistanceText(progress);
                break;
        }
    }

    private void setTextViewPriceText(int progress) {
        if (progress == 0)
            textViewAttractionMapPrice.setText(getResources().getString(R.string.price_search_page));
        else if (progress >= 150)
            textViewAttractionMapPrice.setText(String.format(getResources().getString(R.string.price_over_limit), progress));
        else
            textViewAttractionMapPrice.setText(String.format(getResources().getString(R.string.price_up_to_something), progress));
    }

    private void setTextViewRatingText(int progress) {
        if (progress == 0)
            textViewAttractionMapRating.setText(getResources().getString(R.string.rank_search_page));
        else
            textViewAttractionMapRating.setText(String.format(getResources().getString(R.string.rank_up_to_something), progress));
    }

    private void setTextViewDistanceText(int progress) {
        if (progress == 0)
            textViewAttractionMapDistance.setText(getResources().getString(R.string.distance_search_page));
        else
            textViewAttractionMapDistance.setText(String.format(getResources().getString(R.string.distance_up_to_something), progress));
    }

    public void setOnImageViewSearchMapFilterClick(OnImageViewSearchMapFilterClick onImageViewSearchMapFilterClick) {
        this.onImageViewSearchMapFilterClick = onImageViewSearchMapFilterClick;
    }

    public void setAutoCompleteTextViewsAccomodationMapTextChangeListener(AutoCompleteTextViewsAccomodationMapTextChangeListener autoCompleteTextViewsAccomodationMapTextChangeListener) {
        this.autoCompleteTextViewsAccomodationMapTextChangeListener = autoCompleteTextViewsAccomodationMapTextChangeListener;
    }

    public AutoCompleteTextView getAutoCompleteTextViewMapAttractionName() {
        return autoCompleteTextViewMapAttractionName;
    }

    public AutoCompleteTextView getAutoCompleteTextViewMapAttractionCity() {
        return autoCompleteTextViewMapAttractionCity;
    }

    public SeekBar getSeekBarAttractionMapDistance() {
        return seekBarAttractionMapDistance;
    }

    public String getAutoCompleteTextViewMapAttractionNameValue() {
        return autoCompleteTextViewMapAttractionName.getText().toString();
    }

    public String getAutoCompleteTextViewMapAttractionCityValue() {
        return autoCompleteTextViewMapAttractionCity.getText().toString();
    }

    public int getSeekBarAttractionMapPriceValue() {
        return seekBarAttractionMapPrice.getProgress();
    }

    public int getSeekBarAttractionMapRatingValue() {
        return seekBarAttractionMapRating.getProgress();
    }

    public int getSeekBarAttractionMapDistanceValue() {
        return seekBarAttractionMapDistance.getProgress();
    }

    public boolean getSwitchCompatAttractionMapCertificateOfExcellenceIsSelected() {
        return switchCompatAttractionMapCertificateOfExcellence.isChecked();
    }

}
