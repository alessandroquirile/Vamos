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

public class BottomSheetHotelMap extends BottomSheetDialogFragment implements View.OnClickListener,
        TextWatcher, SeekBar.OnSeekBarChangeListener {

    private BottomSheetBehavior bottomSheetBehavior;
    private ImageView imageViewGoBackHotelMapFilter, imageViewSearchHotelMapFilter;
    private AutoCompleteTextView autoCompleteTextViewMapHotelName, autoCompleteTextViewMapHotelCity;
    private TextView textViewHotelMapPrice, textViewHotelMapRating, textViewHotelMapStars,
            textViewHotelMapDistance;
    private SeekBar seekBarHotelMapPrice, seekBarHotelMapRating, seekBarHotelMapStars,
            seekBarHotelMapDistance;
    private SwitchCompat switchCompatHotelMapCertificateOfExcellence;
    private OnImageViewSearchMapFilterClick onImageViewSearchMapFilterClick;
    private AutoCompleteTextViewsAccomodationMapTextChangeListener autoCompleteTextViewsAccomodationMapTextChangeListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View bottomSheetView = View.inflate(getContext(), R.layout.hotel_filter_map, null);
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
        imageViewGoBackHotelMapFilter = bottomSheetView.findViewById(R.id.image_view_go_back_hotel_map_filter);
        imageViewSearchHotelMapFilter = bottomSheetView.findViewById(R.id.image_view_search_hotel_map_filter);
        autoCompleteTextViewMapHotelName = bottomSheetView.findViewById(R.id.auto_complete_text_view_map_hotel_name);
        autoCompleteTextViewMapHotelCity = bottomSheetView.findViewById(R.id.auto_complete_text_view_map_hotel_city);
        textViewHotelMapPrice = bottomSheetView.findViewById(R.id.text_view_hotel_map_price);
        textViewHotelMapRating = bottomSheetView.findViewById(R.id.text_view_hotel_map_rating);
        textViewHotelMapStars = bottomSheetView.findViewById(R.id.text_view_hotel_map_stars);
        textViewHotelMapDistance = bottomSheetView.findViewById(R.id.text_view_hotel_map_distance);
        seekBarHotelMapPrice = bottomSheetView.findViewById(R.id.seek_bar_hotel_map_price);
        seekBarHotelMapRating = bottomSheetView.findViewById(R.id.seek_bar_hotel_map_rating);
        seekBarHotelMapStars = bottomSheetView.findViewById(R.id.seek_bar_hotel_map_stars);
        seekBarHotelMapDistance = bottomSheetView.findViewById(R.id.seek_bar_hotel_map_distance);
        switchCompatHotelMapCertificateOfExcellence = bottomSheetView.findViewById(R.id.switch_compat_hotel_map_certificate_of_excellence);
    }

    private void setListenerOnComponents() {
        imageViewGoBackHotelMapFilter.setOnClickListener(this);
        imageViewSearchHotelMapFilter.setOnClickListener(this);
        autoCompleteTextViewMapHotelName.addTextChangedListener(this);
        autoCompleteTextViewMapHotelCity.addTextChangedListener(this);
        seekBarHotelMapPrice.setOnSeekBarChangeListener(this);
        seekBarHotelMapRating.setOnSeekBarChangeListener(this);
        seekBarHotelMapStars.setOnSeekBarChangeListener(this);
        seekBarHotelMapDistance.setOnSeekBarChangeListener(this);
    }

    private void detectAutoCompleteTextView(Editable editable) {
        if (autoCompleteTextViewsAccomodationMapTextChangeListener != null) {
            if (editable.equals(autoCompleteTextViewMapHotelName.getEditableText()))
                autoCompleteTextViewsAccomodationMapTextChangeListener.onAutoCompleteTextViewAccomodationNameTextChanged(
                        autoCompleteTextViewMapHotelName.getText().toString());
            if (editable.equals(autoCompleteTextViewMapHotelCity.getEditableText()))
                autoCompleteTextViewsAccomodationMapTextChangeListener.onAutoCompleteTextViewAccomodtionCityTextChanged(
                        autoCompleteTextViewMapHotelCity.getText().toString());
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
            textViewHotelMapPrice.setText(getResources().getString(R.string.price_search_page));
        else if (progress >= 150)
            textViewHotelMapPrice.setText(String.format(getResources().getString(R.string.price_over_limit), progress));
        else
            textViewHotelMapPrice.setText(String.format(getResources().getString(R.string.price_up_to_something), progress));
    }

    private void setTextViewRatingText(int progress) {
        if (progress == 0)
            textViewHotelMapRating.setText(getResources().getString(R.string.rank_search_page));
        else
            textViewHotelMapRating.setText(String.format(getResources().getString(R.string.rank_up_to_something), progress));
    }

    private void setTextViewStarsText(int progress) {
        if (progress == 0)
            textViewHotelMapStars.setText(getResources().getString(R.string.hotel_stars_filter));
        else
            textViewHotelMapStars.setText(String.format(getResources().getString(R.string.hotel_stars_filter_up_to), progress));
    }

    private void setTextViewDistanceText(int progress) {
        if (progress == 0)
            textViewHotelMapDistance.setText(getResources().getString(R.string.distance_search_page));
        else
            textViewHotelMapDistance.setText(String.format(getResources().getString(R.string.distance_up_to_something), progress));
    }

    public void setOnImageViewSearchMapFilterClick(OnImageViewSearchMapFilterClick onImageViewSearchMapFilterClick) {
        this.onImageViewSearchMapFilterClick = onImageViewSearchMapFilterClick;
    }

    public void setAutoCompleteTextViewsAccomodationMapTextChangeListener(AutoCompleteTextViewsAccomodationMapTextChangeListener autoCompleteTextViewsAccomodationMapTextChangeListener) {
        this.autoCompleteTextViewsAccomodationMapTextChangeListener = autoCompleteTextViewsAccomodationMapTextChangeListener;
    }

    public AutoCompleteTextView getAutoCompleteTextViewMapHotelName() {
        return autoCompleteTextViewMapHotelName;
    }

    public AutoCompleteTextView getAutoCompleteTextViewMapHotelCity() {
        return autoCompleteTextViewMapHotelCity;
    }

    public SeekBar getSeekBarHotelMapDistance() {
        return seekBarHotelMapDistance;
    }

    public String getAutoCompleteTextViewMapHotelNameValue() {
        return autoCompleteTextViewMapHotelName.getText().toString();
    }

    public String getAutoCompleteTextViewMapHotelCityValue() {
        return autoCompleteTextViewMapHotelCity.getText().toString();
    }

    public int getSeekBarHotelMapPriceValue() {
        return seekBarHotelMapPrice.getProgress();
    }

    public int getSeekBarHotelMapRatingValue() {
        return seekBarHotelMapRating.getProgress();
    }

    public int getSeekBarHotelMapStarsValue() {
        return seekBarHotelMapStars.getProgress();
    }

    public int getSeekBarHotelMapDistanceValue() {
        return seekBarHotelMapDistance.getProgress();
    }

    public boolean getSwitchCompatHotelMapCertificateOfExcellenceIsSelected() {
        return switchCompatHotelMapCertificateOfExcellence.isChecked();
    }

}
