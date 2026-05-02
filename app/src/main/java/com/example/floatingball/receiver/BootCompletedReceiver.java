package com.example.floatingball.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.floatingball.helper.PermissionHelper;
import com.example.floatingball.service.FloatingBallService;

/**
 * 开机启动广播接收器
 * 设备启动完成后自动启动悬浮球服务
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // 检查悬浮窗权限
            if (PermissionHelper.hasOverlayPermission(context)) {
                // 启动悬浮球服务
                Intent serviceIntent = new Intent(context, FloatingBallService.class);
                serviceIntent.setAction(FloatingBallService.ACTION_SHOW);
                
                // Android 8.0+ 需要使用startForegroundService
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
            }
        }
    }
}
