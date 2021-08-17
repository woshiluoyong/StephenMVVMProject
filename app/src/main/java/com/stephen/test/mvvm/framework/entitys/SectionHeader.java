package com.stephen.test.mvvm.framework.entitys;

import android.text.TextUtils;

import com.stephen.test.mvvm.framework.qmui.QMUISection;

public class SectionHeader implements QMUISection.Model<SectionHeader> {
    public final static String AUTO_ACC = "AUTOACC";
    private final String text;
    private final String imgUrl;

    public SectionHeader(String text) {
        this(text, null);
    }

    public SectionHeader(String text, String imgUrl) {
        this.text = text;
        this.imgUrl = imgUrl;
    }

    public String getText() {
        return TextUtils.isEmpty(text) ? "-" : text;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    @Override
    public SectionHeader cloneForDiff() {
        return new SectionHeader(getText());
    }

    @Override
    public boolean isSameItem(SectionHeader other) {
        return (text != null && other != null && text.equals(other.getText()));
    }

    @Override
    public boolean isSameContent(SectionHeader other) {
        return true;
    }
}
