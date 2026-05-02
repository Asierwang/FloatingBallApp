package com.example.floatingball.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.floatingball.R;
import com.example.floatingball.helper.PreferencesHelper;

import java.util.List;

public class MenuPopupView extends LinearLayout {

    private LinearLayout menuContainer;
    private PreferencesHelper preferencesHelper;
    private OnMenuItemClickListener itemClickListener;
    private OnAddMenuClickListener addMenuClickListener;
    private boolean isShowing = false;

    public MenuPopupView(Context context) {
        this(context, null);
    }

    public MenuPopupView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuPopupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.menu_popup, this, true);
        menuContainer = findViewById(R.id.menuContainer);
        preferencesHelper = new PreferencesHelper(context);
        rebuildMenu();
        setVisibility(GONE);
        setAlpha(0f);
        setScaleX(0.5f);
        setScaleY(0.5f);
    }

    public void rebuildMenu() {
        menuContainer.removeAllViews();

        View homeItem = createMenuItem(getContext().getString(R.string.menu_home), null, null);
        homeItem.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onHomeClick();
            }
            hideMenu();
        });
        menuContainer.addView(homeItem);

        int count = preferencesHelper.getCustomMenuCount();
        for (int i = 0; i < count; i++) {
            String pkg = preferencesHelper.getCustomMenuPackageName(i);
            String label = preferencesHelper.getCustomMenuLabel(i);
            if (pkg != null && label != null) {
                Drawable icon = getAppIcon(pkg);
                int index = i;
                View itemView = createMenuItem(label, icon, pkg);
                itemView.setOnClickListener(v -> {
                    if (itemClickListener != null) {
                        itemClickListener.onCustomMenuClick(pkg);
                    }
                    hideMenu();
                });
                itemView.setOnLongClickListener(v -> {
                    preferencesHelper.removeCustomMenu(index);
                    rebuildMenu();
                    return true;
                });
                menuContainer.addView(itemView);
                addDivider();
            }
        }

        if (count < PreferencesHelper.MAX_CUSTOM_MENUS) {
            addDivider();
            ImageView addItem = new ImageView(getContext());
            addItem.setImageResource(android.R.drawable.ic_input_add);
            addItem.setPadding(0, 8, 0, 8);
            LinearLayout.LayoutParams addParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            addItem.setLayoutParams(addParams);
            addItem.setClickable(true);
            addItem.setFocusable(true);
            addItem.setBackgroundResource(R.drawable.menu_item_selector);
            addItem.setOnClickListener(v -> {
                hideMenu();
                if (addMenuClickListener != null) {
                    addMenuClickListener.onAddMenuClick();
                }
            });
            menuContainer.addView(addItem);
        }
    }

    private View createMenuItem(String text, Drawable icon, String pkg) {
        if (icon != null && pkg != null) {
            LinearLayout itemLayout = new LinearLayout(getContext());
            itemLayout.setOrientation(HORIZONTAL);
            itemLayout.setGravity(Gravity.CENTER_VERTICAL);
            itemLayout.setBackgroundResource(R.drawable.menu_item_selector);
            itemLayout.setClickable(true);
            itemLayout.setFocusable(true);
            itemLayout.setPadding(8, 0, 8, 0);

            ImageView iv = new ImageView(getContext());
            iv.setImageDrawable(icon);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(28, 28);
            iconParams.setMarginEnd(8);
            iv.setLayoutParams(iconParams);
            itemLayout.addView(iv);

            TextView tv = new TextView(getContext());
            tv.setText(text);
            tv.setTextSize(14);
            tv.setTextColor(getResources().getColor(R.color.menu_text_color));
            tv.setSingleLine(true);
            tv.setEllipsize(android.text.TextUtils.TruncateAt.END);
            tv.setMaxEms(6);
            itemLayout.addView(tv);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, 48);
            itemLayout.setLayoutParams(params);
            return itemLayout;
        } else {
            TextView tv = new TextView(getContext());
            tv.setText(text);
            tv.setTextSize(16);
            tv.setTextColor(getResources().getColor(R.color.menu_text_color));
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.menu_item_selector);
            tv.setClickable(true);
            tv.setFocusable(true);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, 48);
            tv.setLayoutParams(params);
            return tv;
        }
    }

    private void addDivider() {
        View divider = new View(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        divider.setLayoutParams(params);
        divider.setBackgroundColor(0xFFE0E0E0);
        menuContainer.addView(divider);
    }

    private Drawable getAppIcon(String packageName) {
        try {
            PackageManager pm = getContext().getPackageManager();
            return pm.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public void showMenu() {
        if (isShowing) return;
        isShowing = true;
        setVisibility(VISIBLE);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(this, "scaleX", 0.5f, 1f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(this, "scaleY", 0.5f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(alphaAnim, scaleXAnim, scaleYAnim);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    public void hideMenu() {
        if (!isShowing) return;
        isShowing = false;
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0.5f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(this, "scaleY", 1f, 0.5f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(alphaAnim, scaleXAnim, scaleYAnim);
        animatorSet.setDuration(200);
        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                setVisibility(GONE);
                animatorSet.removeListener(this);
            }
        });
        animatorSet.start();
    }

    public void hideMenuImmediately() {
        setVisibility(GONE);
        setAlpha(0f);
        setScaleX(0.5f);
        setScaleY(0.5f);
        isShowing = false;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnAddMenuClickListener(OnAddMenuClickListener listener) {
        this.addMenuClickListener = listener;
    }

    public interface OnMenuItemClickListener {
        void onHomeClick();
        void onCustomMenuClick(String packageName);
    }

    public interface OnAddMenuClickListener {
        void onAddMenuClick();
    }
}
