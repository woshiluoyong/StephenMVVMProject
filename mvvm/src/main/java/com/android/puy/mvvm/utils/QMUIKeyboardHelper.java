package com.android.puy.mvvm.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class QMUIKeyboardHelper {
    /**
     * 显示软键盘的延迟时间
     */
    public static final int SHOW_KEYBOARD_DELAY_TIME = 200;
    private static final String TAG = "QMUIKeyboardHelper";
    public final static int KEYBOARD_VISIBLE_THRESHOLD_DP = 100;


    public static void showKeyboard(final EditText editText, boolean delay) {
        showKeyboard(editText, delay ? SHOW_KEYBOARD_DELAY_TIME : 0);
    }


    /**
     * 针对给定的editText显示软键盘（editText会先获得焦点）. 可以和{@link #hideKeyboard(View)}
     * 搭配使用，进行键盘的显示隐藏控制。
     */

    public static void showKeyboard(final EditText editText, int delay) {
        if (null == editText)
            return;

        if (!editText.requestFocus()) {
            Log.w(TAG, "showSoftInput() can not get focus");
            return;
        }
        if (delay > 0) {
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) editText.getContext().getApplicationContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
            }, delay);
        } else {
            InputMethodManager imm = (InputMethodManager) editText.getContext().getApplicationContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * 隐藏软键盘 可以和{@link #showKeyboard(EditText, boolean)}搭配使用，进行键盘的显示隐藏控制。
     *
     * @param view 当前页面上任意一个可用的view
     */
    public static boolean hideKeyboard(final View view) {
        if (null == view)return false;
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);// 即使当前焦点不在editText，也是可以隐藏的。
    }

    //输入法是否展示
    public static boolean isShouldHideKeyboard(View view, MotionEvent event) {
        if (null != view && (view instanceof EditText)) {
            int[] location = {0, 0};
            view.getLocationInWindow(location);
            int left = location[0], top = location[1], bottom = top + view.getHeight(), right = left + view.getWidth();
            return !(event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom);
        }//end of if
        return false;
    }

    public static void setVisibilityEventListener(final Activity activity, final KeyboardVisibilityEventListener listener) {
        if (activity == null) {
            throw new NullPointerException("Parameter:activity must not be null");
        }

        if (listener == null) {
            throw new NullPointerException("Parameter:listener must not be null");
        }

        final View activityRoot = QMUIViewHelper.getActivityRoot(activity);

        final ViewTreeObserver.OnGlobalLayoutListener layoutListener =
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    private final Rect r = new Rect();

                    private final int visibleThreshold = Math.round(dip2px(activity, KEYBOARD_VISIBLE_THRESHOLD_DP));

                    private boolean wasOpened = false;

                    @Override
                    public void onGlobalLayout() {
                        activityRoot.getWindowVisibleDisplayFrame(r);

                        int heightDiff = activityRoot.getRootView().getHeight() - r.height();

                        boolean isOpen = heightDiff > visibleThreshold;

                        if (isOpen == wasOpened) {
                            // keyboard state has not changed
                            return;
                        }

                        wasOpened = isOpen;

                        boolean removeListener = listener.onVisibilityChanged(isOpen, heightDiff);
                        if (removeListener) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                activityRoot.getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);
                            } else {
                                activityRoot.getViewTreeObserver()
                                        .removeGlobalOnLayoutListener(this);
                            }
                        }
                    }
                };
        activityRoot.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        activity.getApplication().registerActivityLifecycleCallbacks(new QMUIActivityLifecycleCallbacks(activity) {
                    @Override
                    protected void onTargetActivityDestroyed() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            activityRoot.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(layoutListener);
                        } else {
                            activityRoot.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(layoutListener);
                        }
                    }
                });
    }

    /**
     * Determine if keyboard is visible
     *
     * @param activity Activity
     * @return Whether keyboard is visible or not
     */
    public static boolean isKeyboardVisible(Activity activity) {
        Rect r = new Rect();

        View activityRoot = QMUIViewHelper.getActivityRoot(activity);
        int visibleThreshold = Math.round(dip2px(activity, KEYBOARD_VISIBLE_THRESHOLD_DP));

        activityRoot.getWindowVisibleDisplayFrame(r);

        int heightDiff = activityRoot.getRootView().getHeight() - r.height();

        return heightDiff > visibleThreshold;
    }


    public interface KeyboardVisibilityEventListener {

        /**
         * @return to remove global listener or not
         */
        boolean onVisibilityChanged(boolean isOpen, int heightDiff);
    }

    public abstract static class QMUIActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        private final Activity mTargetActivity;

        public QMUIActivityLifecycleCallbacks(Activity targetActivity) {
            mTargetActivity = targetActivity;
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (activity == mTargetActivity) {
                mTargetActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
                onTargetActivityDestroyed();
            }
        }

        protected abstract void onTargetActivityDestroyed();
    }

    /**
     * convert dp to its equivalent px
     * <p>
     * 将dp转换为与之相等的px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
