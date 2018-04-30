package com.kuro.proui;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * @author Administrator
 * @date 2017/5/25
 */

public class ScreenUtil {

    private static final String TAG = "proui.ScreenUtil";

    /**
     * 获取屏幕宽度
     *
     * @return 屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        if (context == null) {
            Log.w(TAG, "获取屏幕宽度失败");
            return 0;
        }
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return 屏幕高度
     */
    public static int getScreenHeight(Context context) {
        if (context == null) {
            Log.w(TAG, "获取屏幕高度失败");
            return 0;
        }
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    /**
     * 获取状态栏高度
     *
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        if (context == null) {
            Log.w(TAG, "获取状态栏高度失败");
            return 0;
        }
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属性density）
     * @return px
     */
    public static int dp2px(Context context, float dipValue) {
        if (context == null) {
            Log.w(TAG, "dp转px失败");
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证尺寸大小不变
     *
     * @param spValue sp
     * @return px
     */
    public static int sp2px(Context context, float spValue) {
        if (context == null) {
            Log.w(TAG, "sp转px失败");
            return 0;
        }
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
