package com.voyah.ai.engineer.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

public class SpannableBuilder {
    private final SpannableString spannableString;

    private SpannableBuilder(String text) {
        this.spannableString = new SpannableString(text);
    }

    public static SpannableBuilder withText(String text) {
        return new SpannableBuilder(text);
    }

    public SpannableBuilder setColor(int color) {
        spannableString.setSpan(new ForegroundColorSpan(color), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    public SpannableBuilder setColor(int color, int startIndex, int endIndex) {
        spannableString.setSpan(new ForegroundColorSpan(color), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    public SpannableBuilder setSize(float size) {
        spannableString.setSpan(new RelativeSizeSpan(size), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    // 设置字体大小，支持可选的startIndex和endIndex
    public SpannableBuilder setSize(float size, int startIndex, int endIndex) {
        spannableString.setSpan(new RelativeSizeSpan(size), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    public SpannableBuilder setBold(boolean isBold) {
        spannableString.setSpan(new StyleSpan(isBold ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    public SpannableBuilder setBold(boolean isBold, int startIndex, int endIndex) {
        spannableString.setSpan(new StyleSpan(isBold ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    public SpannableString build() {
        return spannableString;
    }
}
