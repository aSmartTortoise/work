package com.voyah.h37z;


import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class SpannableUtils {

    private SpannableUtils() {
    }

    public static void formatString(TextView view, String text, int start, int end, int color) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
        //用颜色标记
        stringBuilder.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(stringBuilder);
    }
}
