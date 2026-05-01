package com.example.floatingball;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.floatingball.helper.PreferencesHelper;
import com.example.floatingball.service.FloatingBallService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppListActivity extends Activity {

    private List<AppInfo> appList = new ArrayList<>();
    private AppListAdapter adapter;
    private PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        preferencesHelper = new PreferencesHelper(this);

        loadInstalledApps();

        adapter = new AppListAdapter();
        ListView listView = findViewById(R.id.appListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            AppInfo info = appList.get(position);
            int index = preferencesHelper.getCustomMenuCount();
            preferencesHelper.saveCustomMenu(index, info.packageName, info.label);

            Intent refreshIntent = new Intent(this, FloatingBallService.class);
            refreshIntent.setAction(FloatingBallService.ACTION_REFRESH_MENU);
            startService(refreshIntent);

            finish();
        });
    }

    private void loadInstalledApps() {
        PackageManager pm = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);

        String selfPkg = getPackageName();

        for (ResolveInfo ri : resolveInfos) {
            if (ri.activityInfo == null) continue;
            String pkg = ri.activityInfo.packageName;
            if (pkg.equals(selfPkg)) continue;

            String label = ri.loadLabel(pm).toString();
            Drawable icon = ri.loadIcon(pm);
            appList.add(new AppInfo(pkg, label, icon));
        }

        Collections.sort(appList, Comparator.comparing(a -> a.label.toLowerCase()));
    }

    private static class AppInfo {
        String packageName;
        String label;
        Drawable icon;

        AppInfo(String packageName, String label, Drawable icon) {
            this.packageName = packageName;
            this.label = label;
            this.icon = icon;
        }
    }

    private class AppListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return appList.size();
        }

        @Override
        public Object getItem(int position) {
            return appList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(AppListActivity.this)
                        .inflate(R.layout.item_app_list, parent, false);
                holder = new ViewHolder();
                holder.icon = convertView.findViewById(R.id.appIcon);
                holder.name = convertView.findViewById(R.id.appName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AppInfo info = appList.get(position);
            holder.icon.setImageDrawable(info.icon);
            holder.name.setText(info.label);
            return convertView;
        }

        private class ViewHolder {
            ImageView icon;
            TextView name;
        }
    }
}
