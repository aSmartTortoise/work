package com.voyah.voice.main.anim;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.voyah.voice.main.R;

public class HomeViewTransformer implements ViewPager.PageTransformer {

    private static final String TAG = "HomeViewTransformer";
    private float mask = 0.3173691f;

    private static final float MIN_ALPHA = 0.5f;

    private static final float MIN_SCALE = 0.95f;

    private final Context context;

    public HomeViewTransformer(Context context) {
        this.context = context.getApplicationContext();
    }

    public void setMask(float mask) {
        this.mask = mask;
    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        position = position - mask;
        float scale = Math.abs(1.0f - Math.abs(position));
        Log.i(TAG, "View:" + page.getTag() + ",position:" + position + ",mask:" + mask + ",scale:" + scale);
        if (scale < 0 || scale > 1) {
            scale = 1;
        }
        page.setAlpha(getScale(MIN_ALPHA, scale));
        ConstraintLayout layout = page.findViewById(R.id.layout_main);
        ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
        layoutParams.width = (int) (context.getResources().getDimension(R.dimen.home_photo_width) * getScale(MIN_SCALE, scale));
        layoutParams.height = (int) (context.getResources().getDimension(R.dimen.home_photo_height) * getScale(MIN_SCALE, scale));
        layout.setLayoutParams(layoutParams);
        ImageView view = page.findViewById(R.id.bg_bottom);
        ViewGroup.MarginLayoutParams layoutParams2 = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams2.width = (int) (context.getResources().getDimension(R.dimen.home_bottom_width) * getScale(MIN_SCALE, scale));
        layoutParams2.height = (int) (context.getResources().getDimension(R.dimen.home_bottom_height) * getScale(MIN_SCALE, scale));
        float top = layoutParams.height - layoutParams2.height * 0.49593f + context.getResources().getDimension(R.dimen.dp_186);
        layoutParams2.setMargins(0, (int) top, 0, 0);
        view.setLayoutParams(layoutParams2);
    }

    private float getScale(float minScale, float scale) {
        return minScale + (1 - minScale) * scale;
    }
}
