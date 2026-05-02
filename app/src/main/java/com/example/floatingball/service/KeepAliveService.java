package com.example.floatingball.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

import com.example.floatingball.R;
import com.example.floatingball.helper.PermissionHelper;

/**
 * 保活守护服务
 * 监控FloatingBallService的运行状态，异常时自动重启
 */
public class KeepAliveService extends Service {
    
    private static final String NOTIFICATION_CHANNEL_ID = "keep_alive";
    private static final int NOTIFICATION_ID = 2;
    private static final long CHECK_INTERVAL = 5000;
    
    private Handler handler;
    private Runnable checkRunnable;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        startForegroundNotification();
        
        handler = new Handler(Looper.getMainLooper());
        
        // 初始化检查任务
        checkRunnable = new Runnable() {
            @Override
            public void run() {
                checkAndRestartService();
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        };
        
        // 开始定期检查
        handler.post(checkRunnable);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        if (handler != null && checkRunnable != null) {
            handler.removeCallbacks(checkRunnable);
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    private void checkAndRestartService() {
        if (!PermissionHelper.hasOverlayPermission(this)) {
            return;
        }
        if (!isFloatingBallServiceRunning()) {
            restartFloatingBallService();
        }
    }
    
    private boolean isFloatingBallServiceRunning() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return false;
        for (ActivityManager.RunningServiceInfo info : am.getRunningServices(Integer.MAX_VALUE)) {
            if (FloatingBallService.class.getName().equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
    private void restartFloatingBallService() {
        try {
            Intent intent = new Intent(this, FloatingBallService.class);
            intent.setAction(FloatingBallService.ACTION_SHOW);
            startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void startForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, "保活服务",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setShowBadge(false);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.ball_running))
                .setSmallIcon(R.drawable.ic_ball_notification)
                .setOngoing(true)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }
}
