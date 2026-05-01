package com.example.floatingball.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {
    
    private static final String PREF_NAME = "FloatingBallPrefs";
    private static final String KEY_BALL_X = "ball_x";
    private static final String KEY_BALL_Y = "ball_y";
    private static final String KEY_BALL_HIDDEN = "ball_hidden";
    private static final String KEY_CUSTOM_MENU_PREFIX = "custom_menu_";
    private static final String KEY_CUSTOM_MENU_COUNT = "custom_menu_count";
    public static final int MAX_CUSTOM_MENUS = 6;
    
    private SharedPreferences sharedPreferences;
    
    public PreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void saveBallPosition(int x, int y) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_BALL_X, x);
        editor.putInt(KEY_BALL_Y, y);
        editor.apply();
    }
    
    public int getBallX() {
        return sharedPreferences.getInt(KEY_BALL_X, -1);
    }
    
    public int getBallY() {
        return sharedPreferences.getInt(KEY_BALL_Y, -1);
    }
    
    public void saveBallHiddenState(boolean hidden) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_BALL_HIDDEN, hidden);
        editor.apply();
    }
    
    public boolean isBallHidden() {
        return sharedPreferences.getBoolean(KEY_BALL_HIDDEN, false);
    }
    
    public void clearHiddenState() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_BALL_HIDDEN);
        editor.apply();
    }
    
    public int getCustomMenuCount() {
        return sharedPreferences.getInt(KEY_CUSTOM_MENU_COUNT, 0);
    }
    
    public void saveCustomMenu(int index, String packageName, String label) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CUSTOM_MENU_PREFIX + index + "_pkg", packageName);
        editor.putString(KEY_CUSTOM_MENU_PREFIX + index + "_label", label);
        int count = getCustomMenuCount();
        if (index >= count) {
            editor.putInt(KEY_CUSTOM_MENU_COUNT, index + 1);
        }
        editor.apply();
    }
    
    public String getCustomMenuPackageName(int index) {
        return sharedPreferences.getString(KEY_CUSTOM_MENU_PREFIX + index + "_pkg", null);
    }
    
    public String getCustomMenuLabel(int index) {
        return sharedPreferences.getString(KEY_CUSTOM_MENU_PREFIX + index + "_label", null);
    }
    
    public void removeCustomMenu(int index) {
        int count = getCustomMenuCount();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = index; i < count - 1; i++) {
            String nextPkg = getCustomMenuPackageName(i + 1);
            String nextLabel = getCustomMenuLabel(i + 1);
            if (nextPkg != null) {
                editor.putString(KEY_CUSTOM_MENU_PREFIX + i + "_pkg", nextPkg);
                editor.putString(KEY_CUSTOM_MENU_PREFIX + i + "_label", nextLabel);
            }
        }
        editor.remove(KEY_CUSTOM_MENU_PREFIX + (count - 1) + "_pkg");
        editor.remove(KEY_CUSTOM_MENU_PREFIX + (count - 1) + "_label");
        editor.putInt(KEY_CUSTOM_MENU_COUNT, count - 1);
        editor.apply();
    }
    
    public void clearAllCustomMenus() {
        int count = getCustomMenuCount();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < count; i++) {
            editor.remove(KEY_CUSTOM_MENU_PREFIX + i + "_pkg");
            editor.remove(KEY_CUSTOM_MENU_PREFIX + i + "_label");
        }
        editor.remove(KEY_CUSTOM_MENU_COUNT);
        editor.apply();
    }
}
