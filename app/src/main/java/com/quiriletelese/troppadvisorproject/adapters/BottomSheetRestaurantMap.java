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

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.interfaces.AutoCompleteTextViewsAccomodationMapTextChangeListener;
import com.quiriletelese.troppadvisorproject.interfaces.OnImageViewSearchMapFilterClick;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetRestaurantMap extends BottomSheetDialogFragment implements View.OnClickListener,
        TextWatcher, SeekBar.OnSeekBarChangeListener {

    private BottomSheetBehavior bottomSheetBehavior;
    private ImageView imageViewGoBackRestaurantMapFilter, imageViewSearchHRestaurantMapFilter;
    private AutoCompleteTextView autoCompleteTextViewMapRestaurantName, autoCompleteTextViewMapRestaurantCity;
    private TextView textViewRestaurantMapPrice, textViewRestaurantMapRating, textViewRestaurantMapDistance;
    private SeekBar seekBarRestaurantMapPrice, seekBarRestaurantMapRating, seekBarRestaurantMapDistance;
    private MultiSpinnerSearch multiSpinnerSearchRestaurantMapFilter;
    private SwitchCompat switchCompatRestaurantMapCertificateOfExcellence;
    private OnImageViewSearchMapFilterClick onImageViewSearchMapFilterClick;
    private AutoCompleteTextViewsAccomodationMapTextChangeListener autoCompleteTextViewsAccomodationMapTextChangeListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View bottomSheetView = View.inflate(getContext(), R.layout.restaurant_filter_map, null);
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
            case R.id.image_view_go_back_restaurant_map_filter:
                dismiss();
                break;
            case R.id.image_view_search_restaurant_map_filter:
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
        imageViewGoBackRestaurantMapFilter = bottomSheetView.findViewById(R.id.image_view_go_back_restaurant_map_filter);
        imageViewSearchHRestaurantMapFilter = bottomSheetView.findViewById(R.id.image_view_search_restaurant_map_filter);
        autoCompleteTextViewMapRestaurantName = bottomSheetView.findViewById(R.id.auto_complete_text_view_map_restaurant_name);
        autoCompleteTextViewMapRestaurantCity = bottomSheetView.findViewById(R.id.auto_complete_text_view_map_restaurant_city);
        textViewRestaurantMapPrice = bottomSheetView.findViewById(R.id.text_view_restaurant_map_price);
        textViewRestaurantMapRating = bottomSheetView.findViewById(R.id.text_view_restaurant_map_rating);
        textViewRestaurantMapDistance = bottomSheetView.findViewById(R.id.text_view_restaurant_map_distance);
        seekBarRestaurantMapPrice = bottomSheetView.findViewById(R.id.seek_bar_restaurant_map_price);
        seekBarRestaurantMapRating = bottomSheetView.findViewById(R.id.seek_bar_restaurant_map_rating);
        seekBarRestaurantMapDistance = bottomSheetView.findViewById(R.id.seek_bar_restaurant_map_distance);
        switchCompatRestaurantMapCertificateOfExcellence = bottomSheetView.findViewById(R.id.switch_compat_restaurant_map_certificate_of_excellence);
        multiSpinnerSearchRestaurantMapFilter = bottomSheetView.findViewById(R.id.multi_spinner_search_restaurant_map_filter);
    }

    private void setMultiSpinnerSearchProperties(List<KeyPairBoolData> keyPairBoolDataList) {
        multiSpinnerSearchRestaurantMapFilter.setSearchEnabled(true);
        multiSpinnerSearchRestaurantMapFilter.setSearchHint(getResources().getString(R.string.search));
        multiSpinnerSearchRestaurantMapFilter.setHintText(getResources().getString(R.string.type_of_cuisine));
        multiSpinnerSearchRestaurantMapFilter.setShowSelectAllButton(true);
        multiSpinnerSearchRestaurantMapFilter.setItems(keyPairBoolDataList, selectedItems -> {

        });
    }

    private List<KeyPairBoolData> getTypeOfCuisineItems(List<String> typeOfCuisine, List<KeyPairBoolData> keyPairBoolDataList) {
        for (int i = 0; i < typeOfCuisine.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(typeOfCuisine.get(i));
            h.setSelected(false);
            keyPairBoolDataList.add(h);
        }
        return keyPairBoolDataList;
    }

    public void setTypeOfCuisineList(List<String> typeOfCuisineList) {
        List<KeyPairBoolData> keyPairBoolDataList = new ArrayList<>();
        keyPairBoolDataList = getTypeOfCuisineItems(typeOfCuisineList, keyPairBoolDataList);
        setMultiSpinnerSearchProperties(keyPairBoolDataList);
    }

    private void setListenerOnComponents() {
        imageViewGoBackRestaurantMapFilter.setOnClickListener(this);
        imageViewSearchHRestaurantMapFilter.setOnClickListener(this);
        autoCompleteTextViewMapRestaurantName.addTextChangedListener(this);
        autoCompleteTextViewMapRestaurantCity.addTextChangedListener(this);
        seekBarRestaurantMapPrice.setOnSeekBarChangeListener(this);
        seekBarRestaurantMapRating.setOnSeekBarChangeListener(this);
        seekBarRestaurantMapDistance.setOnSeekBarChangeListener(this);
    }

    private void detectAutoCompleteTextView(Editable editable) {
        if (autoCompleteTextViewsAccomodationMapTextChangeListener != null) {
            if (editable.equals(autoCompleteTextViewMapRestaurantName.getEditableText()))
                autoCompleteTextViewsAccomodationMapTextChangeListener.onAutoCompleteTextViewAccomodationNameTextChanged(
                        autoCompleteTextViewMapRestaurantName.getText().toString());
            if (editable.equals(autoCompleteTextViewMapRestaurantCity.getEditableText()))
                autoCompleteTextViewsAccomodationMapTextChangeListener.onAutoCompleteTextViewAccomodtionCityTextChanged(
                        autoCompleteTextViewMapRestaurantCity.getText().toString());
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
            textViewRestaurantMapPrice.setText(getResources().getString(R.string.price_search_page));
        else if (progress >= 150)
            textViewRestaurantMapPrice.setText(String.format(getResources().getString(R.string.price_over_limit), progress));
        else
            textViewRestaurantMapPrice.setText(String.format(getResources().getString(R.string.price_up_to_something), progress));
    }

    private void setTextViewRatingText(int progress) {
        if (progress == 0)
            textViewRestaurantMapRating.setText(getResources().getString(R.string.rank_search_page));
        else
            textViewRestaurantMapRating.setText(String.format(getResources().getString(R.string.rank_up_to_something), progress));
    }

    private void setTextViewDistanceText(int progress) {
        if (progress == 0)
            textViewRestaurantMapDistance.setText(getResources().getString(R.string.distance_search_page));
        else
            textViewRestaurantMapDistance.setText(String.format(getResources().getString(R.string.distance_up_to_something), progress));
    }

    public void setOnImageViewSearchMapFilterClick(OnImageViewSearchMapFilterClick onImageViewSearchMapFilterClick) {
        this.onImageViewSearchMapFilterClick = onImageViewSearchMapFilterClick;
    }

    public void setAutoCompleteTextViewsAccomodationMapTextChangeListener(AutoCompleteTextViewsAccomodationMapTextChangeListener autoCompleteTextViewsAccomodationMapTextChangeListener) {
        this.autoCompleteTextViewsAccomodationMapTextChangeListener = autoCompleteTextViewsAccomodationMapTextChangeListener;
    }

    public AutoCompleteTextView getAutoCompleteTextViewMapRestaurantName() {
        return autoCompleteTextViewMapRestaurantName;
    }

    public AutoCompleteTextView getAutoCompleteTextViewMapRestaurantCity() {
        return autoCompleteTextViewMapRestaurantCity;
    }

    public SeekBar getSeekBarRestaurantMapDistance() {
        return seekBarRestaurantMapDistance;
    }

    public String getAutoCompleteTextViewMapRestaurantNameValue() {
        return autoCompleteTextViewMapRestaurantName.getText().toString();
    }

    public String getAutoCompleteTextViewMapRestaurantCityValue() {
        return autoCompleteTextViewMapRestaurantCity.getText().toString();
    }

    public int getSeekBarRestaurantMapPriceValue() {
        return seekBarRestaurantMapPrice.getProgress();
    }

    public int getSeekBarRestaurantMapRatingValue() {
        return seekBarRestaurantMapRating.getProgress();
    }

    public int getSeekBarRestaurantMapDistanceValue() {
        return seekBarRestaurantMapDistance.getProgress();
    }

    public boolean getSwitchCompatRestaurantMapCertificateOfExcellenceIsSelected() {
        return switchCompatRestaurantMapCertificateOfExcellence.isChecked();
    }

    public List<KeyPairBoolData> getMultiSpinnerSearchSelectedItems() {
        return multiSpinnerSearchRestaurantMapFilter.getSelectedItems();
    }

}
