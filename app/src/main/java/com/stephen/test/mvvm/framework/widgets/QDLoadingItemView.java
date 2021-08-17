package com.stephen.test.mvvm.framework.widgets;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stephen.test.mvvm.framework.qmui.QMUIDisplayHelper;
import com.stephen.test.mvvm.framework.qmui.QMUILoadingView;

public class QDLoadingItemView extends FrameLayout {

    private QMUILoadingView mLoadingView;

    public QDLoadingItemView(@NonNull Context context) {
        this(context, null);
    }

    public QDLoadingItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mLoadingView = new QMUILoadingView(context, QMUIDisplayHelper.dp2px(context, 24), Color.LTGRAY);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        addView(mLoadingView, lp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(QMUIDisplayHelper.dp2px(getContext(), 48), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mLoadingView.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mLoadingView.stop();
    }
}
