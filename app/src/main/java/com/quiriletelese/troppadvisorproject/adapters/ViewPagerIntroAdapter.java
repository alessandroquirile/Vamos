package com.quiriletelese.troppadvisorproject.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.quiriletelese.troppadvisorproject.R;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class ViewPagerIntroAdapter extends PagerAdapter {

    private Context context;

    public ViewPagerIntroAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return getIntroImages().length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = createLayoutInflater();
        View view = createView(layoutInflater, container);
        ImageView imageViewIntro = view.findViewById(R.id.image_view_intro);
        TextView textViewIntro = view.findViewById(R.id.text_view_intro);
        imageViewIntro.setImageResource(getIntroImages()[position]);
        textViewIntro.setText(getIntroStrings()[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }

    private LayoutInflater createLayoutInflater(){
        return (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    private View createView(LayoutInflater layoutInflater, ViewGroup container){
        return layoutInflater.inflate(R.layout.view_pager_intro_layout, container, false);
    }

    private int[] getIntroImages(){
        int[] introImages = {
                R.drawable.troppadvisor_logo_no_text,
                R.drawable.travel_intro,
                R.drawable.see_on_map_intro,
                R.drawable.see_reviews_intro,
                R.drawable.permission_intro
        };
        return introImages;
    }

    private String[] getIntroStrings(){
        String[] introImages = {
                getString(R.string.welcome_tropp_advisor),
                getString(R.string.travel_intro),
                getString(R.string.see_on_map_intro),
                getString(R.string.see_reviews_intro),
                getString(R.string.enjoy_intro)
        };
        return introImages;
    }

    private Resources getResources(){
        return context.getResources();
    }

    private String getString(int string){
        return getResources().getString(string);
    }

}
