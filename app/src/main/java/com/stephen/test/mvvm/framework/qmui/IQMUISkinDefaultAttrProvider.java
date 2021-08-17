package com.stephen.test.mvvm.framework.qmui;

import androidx.annotation.Nullable;
import androidx.collection.SimpleArrayMap;

public interface IQMUISkinDefaultAttrProvider {
    @Nullable
    SimpleArrayMap<String, Integer> getDefaultSkinAttrs();
}
