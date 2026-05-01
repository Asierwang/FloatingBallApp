package com.example.floatingball.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.floatingball.R;
import com.example.floatingball.util.ScreenUtils;

/**
 * 悬浮球自定义视图
 * 负责绘制悬浮球和处理触摸事件
 */
public class FloatingBallView extends View {
    
    private Paint paint;
    private int ballSize;
    private boolean isPressed = false;
    
    // 触摸事件相关
    private float touchStartX, touchStartY;
    private float lastTouchX, lastTouchY;
    private long touchStartTime;
    private boolean isDragging = false;
    private static final int TOUCH_SLOP = 10; // 拖动阈值
    
    // 监听器
    private OnBallClickListener clickListener;
    private OnBallDragListener dragListener;
    private OnBallLongPressListener longPressListener;
    
    // 长按检测
    private Handler handler;
    private Runnable longPressRunnable;
    private boolean isLongPressTriggered = false;
    private static final long LONG_PRESS_DURATION = 3000; // 3秒
    
    public FloatingBallView(Context context) {
        this(context, null);
    }
    
    public FloatingBallView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public FloatingBallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        // 计算悬浮球尺寸
        ballSize = ScreenUtils.calculateBallSize(getContext());
        
        // 初始化画笔
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.ball_background));
        paint.setStyle(Paint.Style.FILL);
        
        // 初始化Handler
        handler = new Handler(Looper.getMainLooper());
        longPressRunnable = () -> {
            isLongPressTriggered = true;
            if (longPressListener != null) {
                longPressListener.onLongPress();
            }
        };
        
        // 设置初始透明度
        setAlpha(1.0f);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(ballSize, ballSize);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // 绘制悬浮球
        int color = isPressed ? R.color.ball_pressed : R.color.ball_background;
        paint.setColor(getResources().getColor(color));
        
        float centerX = ballSize / 2f;
        float centerY = ballSize / 2f;
        float radius = ballSize / 2f - 2;
        
        canvas.drawCircle(centerX, centerY, radius, paint);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(event);
                return true;
                
            case MotionEvent.ACTION_MOVE:
                handleActionMove(event);
                return true;
                
            case MotionEvent.ACTION_UP:
                handleActionUp(event);
                return true;
                
            case MotionEvent.ACTION_CANCEL:
                handleActionCancel();
                return true;
        }
        return super.onTouchEvent(event);
    }
    
    private void handleActionDown(MotionEvent event) {
        touchStartX = event.getRawX();
        touchStartY = event.getRawY();
        lastTouchX = touchStartX;
        lastTouchY = touchStartY;
        touchStartTime = System.currentTimeMillis();
        isDragging = false;
        isLongPressTriggered = false;
        
        // 设置按下状态
        isPressed = true;
        invalidate();
        
        // 开始长按检测
        handler.postDelayed(longPressRunnable, LONG_PRESS_DURATION);
        
        // 恢复不透明
        animate().alpha(1.0f).setDuration(200).start();
    }
    
    private void handleActionMove(MotionEvent event) {
        float currentX = event.getRawX();
        float currentY = event.getRawY();
        
        float deltaX = currentX - touchStartX;
        float deltaY = currentY - touchStartY;
        
        // 判断是否开始拖动
        if (!isDragging && (Math.abs(deltaX) > TOUCH_SLOP || Math.abs(deltaY) > TOUCH_SLOP)) {
            isDragging = true;
            // 取消长按检测
            handler.removeCallbacks(longPressRunnable);
        }
        
        if (isDragging && dragListener != null) {
            float moveX = currentX - lastTouchX;
            float moveY = currentY - lastTouchY;
            dragListener.onDrag(moveX, moveY);
        }
        
        lastTouchX = currentX;
        lastTouchY = currentY;
    }
    
    private void handleActionUp(MotionEvent event) {
        // 取消长按检测
        handler.removeCallbacks(longPressRunnable);
        
        // 重置按下状态
        isPressed = false;
        invalidate();
        
        long touchDuration = System.currentTimeMillis() - touchStartTime;
        
        // 如果触发了长按，不处理点击
        if (isLongPressTriggered) {
            return;
        }
        
        // 判断是点击还是拖动
        if (!isDragging && touchDuration < 500) {
            // 触发点击事件
            if (clickListener != null) {
                clickListener.onClick();
            }
        }
    }
    
    private void handleActionCancel() {
        handler.removeCallbacks(longPressRunnable);
        isPressed = false;
        isDragging = false;
        invalidate();
    }
    
    // 设置监听器
    public void setOnBallClickListener(OnBallClickListener listener) {
        this.clickListener = listener;
    }
    
    public void setOnBallDragListener(OnBallDragListener listener) {
        this.dragListener = listener;
    }
    
    public void setOnBallLongPressListener(OnBallLongPressListener listener) {
        this.longPressListener = listener;
    }
    
    // 监听器接口
    public interface OnBallClickListener {
        void onClick();
    }
    
    public interface OnBallDragListener {
        void onDrag(float deltaX, float deltaY);
    }
    
    public interface OnBallLongPressListener {
        void onLongPress();
    }
    
    // 获取悬浮球尺寸
    public int getBallSize() {
        return ballSize;
    }
}
