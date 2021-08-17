package com.qeeyou.accelerator.overseas.overwall.qmui;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public abstract class QMUIDefaultStickySectionAdapter<H extends QMUISection.Model<H>, T extends QMUISection.Model<T>> extends QMUIStickySectionAdapter<H, T, QMUIStickySectionAdapter.ViewHolder> {

    public QMUIDefaultStickySectionAdapter() {}

    public QMUIDefaultStickySectionAdapter(boolean removeSectionTitleIfOnlyOneSection) {
        super(removeSectionTitleIfOnlyOneSection);
    }

    @NonNull
    @Override
    protected ViewHolder onCreateSectionLoadingViewHolder(@NonNull ViewGroup viewGroup) {
        return new ViewHolder(new View(viewGroup.getContext()));
    }

    @NonNull
    @Override
    protected ViewHolder onCreateCustomItemViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(new View(viewGroup.getContext()));
    }
}
