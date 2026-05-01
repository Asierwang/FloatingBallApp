package com.example.floatingball.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;

import com.example.floatingball.helper.PermissionHelper;

/**
 * 保活守护服务
 * 监控FloatingBallService的运行状态，异常时自动重启
 */
public class KeepAliveService extends Service {
    
    private static final long CHECK_INTERVAL = 5000; // 5秒检查一次
    
    private Handler handler;
    private Runnable checkRunnable;
    private boolean isServiceRunning = false;
    
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isServiceRunning = true;
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceRunning = false;
            // 服务断开连接，尝试重启
            restartFloatingBallService();
        }
    };
    
    @Override
    public void onCreate() {
        super.onCreate();
        
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
        // 检查悬浮窗权限
        if (!PermissionHelper.hasOverlayPermission(this)) {
            return;
        }
        
        // 如果服务未运行，尝试启动
        if (!isServiceRunning) {
            restartFloatingBallService();
        }
    }
    
    private void restartFloatingBallService() {
        try {
            Intent intent = new Intent(this, FloatingBallService.class);
            intent.setAction(FloatingBallService.ACTION_SHOW);
            startService(intent);
            isServiceRunning = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
