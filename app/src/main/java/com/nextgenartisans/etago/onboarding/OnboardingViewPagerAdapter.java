package com.nextgenartisans.etago.onboarding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.nextgenartisans.etago.R;

import org.w3c.dom.Text;

public class OnboardingViewPagerAdapter extends PagerAdapter {


    //Declare variables for onboarding view pager
    Context context;
    int images[] = {

            R.drawable.onboarding_upload_capture,
            R.drawable.onboarding_privacy,
            R.drawable.onboarding_censorship,
            R.drawable.onboarding_alerts

    };

    int headings[] = {

            R.string.feature_1,
            R.string.feature_2,
            R.string.feature_3,
            R.string.feature_4

    };

    int descs[] = {

            R.string.feat_desc_1,
            R.string.feat_desc_2,
            R.string.feat_desc_3,
            R.string.feat_desc_4

    };

    public OnboardingViewPagerAdapter(Context context){
        this.context = context;
    }


    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout, container, false);

        ImageView slideTextImage = (ImageView) view.findViewById(R.id.text_image);
        TextView slideTextHeading = (TextView) view.findViewById(R.id.text_title);
        TextView slideTextHeadingDesc = (TextView) view.findViewById(R.id.text_title_desc);

        slideTextImage.setImageResource(images[position]);
        slideTextHeading.setText(headings[position]);
        slideTextHeadingDesc.setText(descs[position]);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((LinearLayout) object);

    }
}
