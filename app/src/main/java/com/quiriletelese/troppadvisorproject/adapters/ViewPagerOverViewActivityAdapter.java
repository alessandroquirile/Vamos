package com.quiriletelese.troppadvisorproject.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.quiriletelese.troppadvisorproject.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class ViewPagerOverViewActivityAdapter extends PagerAdapter {

    private List<String> images;
    private Context context;

    public ViewPagerOverViewActivityAdapter(List<String> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageViewOverviewActivity = new ImageView(context);
        setViewPagerImages(imageViewOverviewActivity, position);

        container.addView(imageViewOverviewActivity);
        return imageViewOverviewActivity;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private void setViewPagerImages(ImageView imageViewOverviewActivity, int position) {
        Picasso.with(context)
                .load(images.get(position))
                .fit()
                .centerCrop()
                .placeholder(R.drawable.troppadvisor_logo)
                .into(imageViewOverviewActivity);
    }

}
