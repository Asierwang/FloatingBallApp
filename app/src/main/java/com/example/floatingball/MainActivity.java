package com.example.floatingball;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.floatingball.helper.PermissionHelper;
import com.example.floatingball.service.FloatingBallService;

/**
 * 主Activity
 * 负责权限申请引导和服务启动
 */
public class MainActivity extends AppCompatActivity {
    
    private Button btnGoSettings;
    private Button btnLater;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        checkPermission();
    }
    
    private void initViews() {
        btnGoSettings = findViewById(R.id.btnGoSettings);
        btnLater = findViewById(R.id.btnLater);
        
        btnGoSettings.setOnClickListener(v -> {
            PermissionHelper.requestOverlayPermission(MainActivity.this);
        });
        
        btnLater.setOnClickListener(v -> {
            finish();
        });
    }
    
    private void checkPermission() {
        if (PermissionHelper.hasOverlayPermission(this)) {
            // 已有权限，启动服务并关闭Activity
            startFloatingBallService();
            finish();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 每次返回时检查权限
        if (PermissionHelper.hasOverlayPermission(this)) {
            Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
            startFloatingBallService();
            finish();
        }
    }
    
    private void startFloatingBallService() {
        Intent intent = new Intent(this, FloatingBallService.class);
        intent.setAction(FloatingBallService.ACTION_SHOW);
        startService(intent);
    }
}
