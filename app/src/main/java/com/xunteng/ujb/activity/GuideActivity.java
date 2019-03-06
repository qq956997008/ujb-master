package com.xunteng.ujb.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xunteng.ujb.R;
import com.xunteng.ujb.adapter.GuideAdapter;
import com.xunteng.ujb.sp.Preference;

import java.util.ArrayList;

public class GuideActivity extends AppCompatActivity {

    private int[] images;
    private ViewPager viewPager;
    private Button button;
    private LinearLayout linearLayout;
    private GuideAdapter guideAdapter;

    private ImageView[] indicatorImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        images = new int[] {R.drawable.welcome_1, R.drawable.welcome_2, R.drawable.welcome_3};

        viewPager = findViewById(R.id.view_pager);

        linearLayout = findViewById(R.id.indicator);

        ArrayList<View> imageViews = new ArrayList<>();
        indicatorImages = new ImageView[images.length];
        for (int i=0; i<images.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(images[i]);
            imageViews.add(imageView);

            indicatorImages[i] = new ImageView(this);
            indicatorImages[i].setBackgroundResource(R.drawable.indicators_default);
            if (i == 0) {
                indicatorImages[i].setBackgroundResource(R.drawable.indicators_now);
            }

            linearLayout.addView(indicatorImages[i]);
        }
        guideAdapter  = new GuideAdapter(imageViews);
        viewPager.setAdapter(guideAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == indicatorImages.length - 1) {
                    button.setVisibility(View.VISIBLE);
                } else {
                    button.setVisibility(View.INVISIBLE);
                }

                for (int i=0;i<indicatorImages.length;i++) {
                    indicatorImages[i].setBackgroundResource(R.drawable.indicators_now);
                    if (position != i) {
                        indicatorImages[i].setBackgroundResource(R.drawable.indicators_default);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        button = findViewById(R.id.start_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preference.setIsFristOpen(false);
                if (Preference.getIsLogin()) {
                    startActivity(new Intent(GuideActivity.this, HomeActivity.class));
                } else {
                    startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                }
                finish();
            }
        });

    }
}
