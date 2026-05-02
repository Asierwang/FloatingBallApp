package com.example.floatingball.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.floatingball.AppListActivity;
import com.example.floatingball.R;
import com.example.floatingball.helper.PreferencesHelper;
import com.example.floatingball.util.AppLauncher;
import com.example.floatingball.util.ScreenUtils;
import com.example.floatingball.view.FloatingBallView;
import com.example.floatingball.view.MenuPopupView;

public class FloatingBallService extends Service {
    
    public static final String ACTION_SHOW = "com.example.floatingball.SHOW";
    public static final String ACTION_HIDE = "com.example.floatingball.HIDE";
    public static final String ACTION_REFRESH_MENU = "com.example.floatingball.REFRESH_MENU";
    
    private static final String NOTIFICATION_CHANNEL_ID = "floating_ball";
    private static final int NOTIFICATION_ID = 1;
    
    private WindowManager windowManager;
    private WindowManager.LayoutParams ballParams;
    private WindowManager.LayoutParams menuParams;
    
    private FloatingBallView floatingBallView;
    private MenuPopupView menuPopupView;
    
    private PreferencesHelper preferencesHelper;
    private Handler handler;
    
    private Runnable alphaRunnable;
    private static final long ALPHA_DELAY = 3000;
    
    private int ballX, ballY;
    private int screenWidth, screenHeight;
    private int ballSize;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        startForegroundNotification();
        
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        preferencesHelper = new PreferencesHelper(this);
        handler = new Handler(Looper.getMainLooper());
        
        screenWidth = ScreenUtils.getScreenWidth(this);
        screenHeight = ScreenUtils.getScreenHeight(this);
        
        initAlphaControl();
        createFloatingBall();
        createMenuPopup();
        startKeepAliveService();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            
            switch (action) {
                case ACTION_SHOW:
                    showFloatingBall();
                    break;
                case ACTION_HIDE:
                    hideFloatingBall();
                    break;
                case ACTION_REFRESH_MENU:
                    refreshMenu();
                    break;
            }
        } else if (intent == null) {
            showFloatingBall();
        }
        
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        preferencesHelper.saveBallPosition(ballX, ballY);
        removeFloatingBall();
        removeMenuPopup();
        
        if (handler != null && alphaRunnable != null) {
            handler.removeCallbacks(alphaRunnable);
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    private void initAlphaControl() {
        alphaRunnable = () -> {
            if (floatingBallView != null) {
                floatingBallView.animate()
                        .alpha(0.5f)
                        .setDuration(200)
                        .start();
            }
        };
    }
    
    private void createFloatingBall() {
        floatingBallView = new FloatingBallView(this);
        ballSize = floatingBallView.getBallSize();
        
        ballParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ballParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            ballParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        ballParams.format = PixelFormat.TRANSLUCENT;
        ballParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        ballParams.gravity = Gravity.TOP | Gravity.LEFT;
        ballParams.width = ballSize;
        ballParams.height = ballSize;
        
        int savedX = preferencesHelper.getBallX();
        int savedY = preferencesHelper.getBallY();
        
        if (savedX != -1 && savedY != -1) {
            ballX = savedX;
            ballY = savedY;
        } else {
            ballX = screenWidth - ballSize - 20;
            ballY = screenHeight / 2;
        }
        
        ballParams.x = ballX;
        ballParams.y = ballY;
        
        floatingBallView.setOnBallClickListener(this::onBallClick);
        floatingBallView.setOnBallDragListener(this::onBallDrag);
        floatingBallView.setOnBallLongPressListener(this::onBallLongPress);
        
        if (!preferencesHelper.isBallHidden()) {
            showFloatingBall();
        }
    }
    
    private void createMenuPopup() {
        menuPopupView = new MenuPopupView(this);
        
        menuParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            menuParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            menuParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        menuParams.format = PixelFormat.TRANSLUCENT;
        menuParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        menuParams.gravity = Gravity.TOP | Gravity.LEFT;
        menuParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        menuParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        menuPopupView.setOnMenuItemClickListener(new MenuPopupView.OnMenuItemClickListener() {
            @Override
            public void onHomeClick() {
                AppLauncher.goToHomeScreen(FloatingBallService.this);
            }
            
            @Override
            public void onCustomMenuClick(String packageName) {
                AppLauncher.launchApp(FloatingBallService.this, packageName);
            }
        });
        
        menuPopupView.setOnAddMenuClickListener(() -> {
            Intent intent = new Intent(FloatingBallService.this, AppListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }
    
    private void refreshMenu() {
        if (menuPopupView != null) {
            menuPopupView.rebuildMenu();
        }
    }
    
    private void showFloatingBall() {
        if (floatingBallView != null && floatingBallView.getParent() == null) {
            windowManager.addView(floatingBallView, ballParams);
            startAlphaTimer();
        }
    }
    
    private void hideFloatingBall() {
        removeFloatingBall();
        removeMenuPopup();
    }
    
    private void removeFloatingBall() {
        if (floatingBallView != null && floatingBallView.getParent() != null) {
            windowManager.removeView(floatingBallView);
        }
    }
    
    private void removeMenuPopup() {
        if (menuPopupView != null && menuPopupView.getParent() != null) {
            windowManager.removeView(menuPopupView);
        }
        if (menuPopupView != null) {
            menuPopupView.hideMenuImmediately();
        }
    }
    
    private void onBallClick() {
        resetAlphaTimer();
        
        if (menuPopupView.isShowing()) {
            menuPopupView.hideMenu();
        } else {
            showMenu();
        }
    }
    
    private void onBallDrag(float deltaX, float deltaY) {
        resetAlphaTimer();
        
        ballX += (int) deltaX;
        ballY += (int) deltaY;
        
        int statusBarHeight = ScreenUtils.getStatusBarHeight(this);
        ballX = Math.max(0, Math.min(ballX, screenWidth - ballSize));
        ballY = Math.max(statusBarHeight, Math.min(ballY, screenHeight - ballSize));
        
        ballParams.x = ballX;
        ballParams.y = ballY;
        if (floatingBallView != null && floatingBallView.getParent() != null) {
            windowManager.updateViewLayout(floatingBallView, ballParams);
        }
        
        if (menuPopupView.isShowing()) {
            menuPopupView.hideMenu();
        }
    }
    
    private void onBallLongPress() {
        hideFloatingBall();
        preferencesHelper.saveBallHiddenState(true);
        Toast.makeText(this, R.string.ball_hidden, Toast.LENGTH_SHORT).show();
    }
    
    private void showMenu() {
        int menuX = ballX - ScreenUtils.dp2px(this, 136);
        int menuY = ballY;
        
        menuX = Math.max(10, menuX);
        
        menuParams.x = menuX;
        menuParams.y = menuY;
        
        if (menuPopupView.getParent() == null) {
            windowManager.addView(menuPopupView, menuParams);
        } else {
            windowManager.updateViewLayout(menuPopupView, menuParams);
        }
        
        menuPopupView.showMenu();
    }
    
    private void startAlphaTimer() {
        if (handler != null && alphaRunnable != null) {
            handler.removeCallbacks(alphaRunnable);
            handler.postDelayed(alphaRunnable, ALPHA_DELAY);
        }
    }
    
    private void resetAlphaTimer() {
        if (floatingBallView != null) {
            floatingBallView.animate()
                    .alpha(1.0f)
                    .setDuration(200)
                    .start();
        }
        
        startAlphaTimer();
    }
    
    private void startKeepAliveService() {
        Intent intent = new Intent(this, KeepAliveService.class);
        startService(intent);
    }
    
    private void startForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, getString(R.string.app_name),
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
