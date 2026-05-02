package com.example.floatingball.application;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.os.SystemClock;

import com.example.floatingball.helper.PermissionHelper;
import com.example.floatingball.service.FloatingBallService;

/**
 * 应用Application类
 * 处理全局异常和应用生命周期
 */
public class FloatingBallApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // 设置未捕获异常处理器
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
        
        // 清除隐藏状态（应用重启时恢复显示）
        // 注意：这里不直接清除，而是在服务启动时检查
    }
    
    /**
     * 未捕获异常处理器
     * 捕获应用崩溃异常并尝试重启服务
     */
    private class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        
        private Thread.UncaughtExceptionHandler defaultHandler;
        
        public UncaughtExceptionHandler() {
            defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        }
        
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            ex.printStackTrace();
            
            try {
                if (PermissionHelper.hasOverlayPermission(getApplicationContext())) {
                    Intent intent = new Intent(getApplicationContext(), FloatingBallService.class);
                    intent.setAction(FloatingBallService.ACTION_SHOW);
                    int flags = PendingIntent.FLAG_ONE_SHOT;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        flags |= PendingIntent.FLAG_IMMUTABLE;
                    }
                    PendingIntent pendingIntent = PendingIntent.getService(
                            getApplicationContext(), 0, intent, flags);
                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    am.set(AlarmManager.ELAPSED_REALTIME,
                            SystemClock.elapsedRealtime() + 3000, pendingIntent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, ex);
            } else {
                Process.killProcess(Process.myPid());
                System.exit(1);
            }
        }
    }
}
