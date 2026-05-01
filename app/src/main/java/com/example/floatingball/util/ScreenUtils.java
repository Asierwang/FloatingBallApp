package com.example.floatingball.util;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

/**
 * 屏幕工具类
 * 用于获取屏幕尺寸和状态栏高度
 */
public class ScreenUtils {
    
    /**
     * 获取屏幕宽度
     * @param context 上下文
     * @return 屏幕宽度（像素）
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            return point.x;
        }
        return 0;
    }
    
    /**
     * 获取屏幕高度
     * @param context 上下文
     * @return 屏幕高度（像素）
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            return point.y;
        }
        return 0;
    }
    
    /**
     * 获取状态栏高度
     * @param context 上下文
     * @return 状态栏高度（像素）
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    
    /**
     * 根据屏幕密度计算合适的悬浮球尺寸
     * @param context 上下文
     * @return 悬浮球直径（像素）
     */
    public static int calculateBallSize(Context context) {
        int screenWidth = getScreenWidth(context);
        // 小屏幕设备（480x320）使用较小尺寸
        if (screenWidth <= 480) {
            return dp2px(context, 40);
        } else if (screenWidth <= 720) {
            return dp2px(context, 48);
        } else {
            return dp2px(context, 56);
        }
    }
    
    /**
     * dp转px
     * @param context 上下文
     * @param dp dp值
     * @return px值
     */
    public static int dp2px(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
    
    /**
     * px转dp
     * @param context 上下文
     * @param px px值
     * @return dp值
     */
    public static int px2dp(Context context, float px) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f);
    }
}
