package com.voyah.cockpit.window.model;

import java.util.Objects;

/**
 * author : jie wang
 * date : 2024/3/25 16:33
 * description : 大字节文本实体类，
 */
public class TypewriterInfo {
    /**
     *  打字机文本
     */
    private String typewriterText;

    /**
     *  文本样式
     */
    private int typeTextStyle = TypeTextStyle.PRIMARY;

    public String getTypewriterText() {
        return typewriterText;
    }

    public void setTypewriterText(String typewriterText) {
        this.typewriterText = typewriterText;
    }

    public int getTypeTextStyle() {
        return typeTextStyle;
    }

    public void setTypeTextStyle(int typeTextStyle) {
        this.typeTextStyle = typeTextStyle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypewriterInfo that = (TypewriterInfo) o;
        return typeTextStyle == that.typeTextStyle
                && Objects.equals(typewriterText, that.typewriterText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typewriterText, typeTextStyle);
    }
}
