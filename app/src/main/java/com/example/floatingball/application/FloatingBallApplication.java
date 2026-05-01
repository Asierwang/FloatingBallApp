package com.example.floatingball.application;

import android.app.Application;
import android.content.Intent;
import android.os.Process;

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
            // 记录异常日志
            ex.printStackTrace();
            
            // 尝试重启悬浮球服务
            try {
                if (PermissionHelper.hasOverlayPermission(getApplicationContext())) {
                    Intent intent = new Intent(getApplicationContext(), FloatingBallService.class);
                    intent.setAction(FloatingBallService.ACTION_SHOW);
                    startService(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // 调用默认处理器
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, ex);
            } else {
                // 结束进程
                Process.killProcess(Process.myPid());
                System.exit(1);
            }
        }
    }
}
