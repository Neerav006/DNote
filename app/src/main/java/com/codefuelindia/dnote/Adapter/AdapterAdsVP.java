package com.codefuelindia.dnote.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.codefuelindia.dnote.Common.OnImagePressedListener;
import com.codefuelindia.dnote.R;

import java.util.ArrayList;

public class AdapterAdsVP extends PagerAdapter {

    private ArrayList<Integer> images;
    private LayoutInflater inflater;
    private Context context;
    private OnImagePressedListener onImagePressedListener;


    public AdapterAdsVP(Context context, ArrayList<Integer> images, OnImagePressedListener onImagePressedListener) {
        this.context = context;
        this.images = images;
        inflater = LayoutInflater.from(context);
        this.onImagePressedListener = onImagePressedListener;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.ads_vp_image, view, false);

        myImageLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (onImagePressedListener != null) {
                    onImagePressedListener.onImagePressed(v, event);
                }

                return true;
            }
        });

        ImageView myImage = (ImageView) myImageLayout.findViewById(R.id.ads_image);
        myImage.setImageResource(images.get(position));
        view.addView(myImageLayout, 0);

        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

}
