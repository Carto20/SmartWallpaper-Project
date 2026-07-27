package com.custom.smartwallpaper.receiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.custom.smartwallpaper.service.SmartWallpaperService;
import java.io.File;
import java.util.Arrays;
public class AutomationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !"com.custom.smartwallpaper.CONTROL".equals(intent.getAction())) return;
        SharedPreferences prefs = context.getSharedPreferences("WallpaperPrefs", Context.MODE_PRIVATE);
        String currentProfile = prefs.getString("current_profile", "Default");
        if (intent.hasExtra("profile")) {
            currentProfile = intent.getStringExtra("profile");
            prefs.edit().putString("current_profile", currentProfile).apply();
        }
        File dir = new File(context.getExternalFilesDir(null), "Profiles/" + currentProfile);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return;
        Arrays.sort(files);
        int targetIndex = prefs.getInt("current_index", 0);
        if (intent.hasExtra("action")) {
            String action = intent.getStringExtra("action");
            if ("RANDOM".equalsIgnoreCase(action)) {
                targetIndex = (int) (Math.random() * files.length);
            } else if ("NEXT".equalsIgnoreCase(action)) {
                targetIndex = (targetIndex + 1) % files.length;
            }
        } else if (intent.hasExtra("index")) {
            targetIndex = intent.getIntExtra("index", 0) % files.length;
        }
        prefs.edit().putInt("current_index", targetIndex).putString("active_file_path", files[targetIndex].getAbsolutePath()).apply();
        Intent updateIntent = new Intent(context, SmartWallpaperService.class);
        updateIntent.setAction("REFRESH_WALLPAPER");
        context.startService(updateIntent);
    }
}
