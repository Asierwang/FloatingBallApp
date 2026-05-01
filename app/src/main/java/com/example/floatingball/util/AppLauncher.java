package com.example.floatingball.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import com.example.floatingball.R;

/**
 * 应用启动工具类
 * 用于启动其他应用和返回主屏幕
 */
public class AppLauncher {
    
    /**
     * 返回Android主屏幕
     * @param context 上下文
     */
    public static void goToHomeScreen(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    
    /**
     * 启动指定包名的应用
     * @param context 上下文
     * @param packageName 应用包名
     * @return true表示启动成功，false表示应用未安装
     */
    public static boolean launchApp(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            } else {
                // 应用未安装，提示用户
                Toast.makeText(context, R.string.app_not_installed, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.app_not_installed, Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    
    /**
     * 检查应用是否已安装
     * @param context 上下文
     * @param packageName 应用包名
     * @return true表示已安装，false表示未安装
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
