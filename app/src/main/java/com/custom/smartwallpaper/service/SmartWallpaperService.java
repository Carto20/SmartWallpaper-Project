package com.custom.smartwallpaper.service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.service.wallpaper.WallpaperService;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
public class SmartWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() { return new AdvancedOpenGLRendererEngine(); }
    private class AdvancedOpenGLRendererEngine extends Engine {
        private GestureDetector gestureDetector;
        private float basePovSliderValue = 0.0f;
        private float livePovScrollOffset = 0.0f;
        private float lastTouchX;
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
            gestureDetector = new GestureDetector(SmartWallpaperService.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    sendControlBroadcast("NEXT");
                    return true;
                }
            });
        }
        @Override
        public void onTouchEvent(MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            SharedPreferences prefs = getSharedPreferences("WallpaperPrefs", MODE_PRIVATE);
            String profileMode = prefs.getString("profile_pov_scroller_mode", "B");
            boolean wallpaperConfig = prefs.getBoolean("wallpaper_pov_scroller_enabled", true);
            boolean isScrollEnabled = "A".equals(profileMode) || ("B".equals(profileMode) && wallpaperConfig);
            if (isScrollEnabled) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: lastTouchX = event.getX(); break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getX() - lastTouchX;
                        livePovScrollOffset += (deltaX / getSurfaceHolder().getSurfaceFrame().width());
                        lastTouchX = event.getX();
                        break;
                }
            }
            super.onTouchEvent(event);
        }
        private void sendControlBroadcast(String actionValue) {
            Intent intent = new Intent("com.custom.smartwallpaper.CONTROL");
            intent.putExtra("action", actionValue);
            sendBroadcast(intent);
        }
    }
}
