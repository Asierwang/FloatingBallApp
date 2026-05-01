package com.example.floatingball.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.example.floatingball.R;

/**
 * 权限处理辅助类
 * 负责检测和申请悬浮窗权限
 */
public class PermissionHelper {
    
    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 1001;
    
    /**
     * 检测是否有悬浮窗权限
     * @param context 上下文
     * @return true表示有权限，false表示无权限
     */
    public static boolean hasOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }
    
    /**
     * 申请悬浮窗权限
     * @param activity Activity实例
     */
    public static void requestOverlayPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION);
            }
        }
    }
    
    /**
     * 打开应用详情设置页面
     * @param context 上下文
     */
    public static void openAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }
    
    /**
     * 检查权限并在无权限时提示
     * @param context 上下文
     * @return true表示有权限，false表示无权限
     */
    public static boolean checkAndRequestPermission(Activity activity) {
        if (!hasOverlayPermission(activity)) {
            Toast.makeText(activity, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            requestOverlayPermission(activity);
            return false;
        }
        return true;
    }
}
