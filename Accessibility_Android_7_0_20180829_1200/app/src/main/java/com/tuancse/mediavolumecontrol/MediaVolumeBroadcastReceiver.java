package com.tuancse.mediavolumecontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MediaVolumeBroadcastReceiver extends BroadcastReceiver {
    public static final String BROADCAST_RECEIVER_ACTION = "com.tuancse.mediavolumecontrol.ACTION";
    public static final String ACTION = "ACTION";
    public static final String SHOW_MEDIA_VOLUME_CONTROL = "Show_Media_Volume_Control";
    public static final String LOCK_SCREEN_REQUEST = "Lock_Screen_Request";
    public static final String LAUNCH_HOME_SCREEN_REQUEST = "Launch_Home_Screen_Request";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Gets data from the incoming Intent
        String msg = intent.getStringExtra(ACTION);

        switch (msg) {

            case SHOW_MEDIA_VOLUME_CONTROL:
                this.handleShowMediaVolumeControl(context, intent);
                break;

            case LAUNCH_HOME_SCREEN_REQUEST:
                this.handleLaunchHomeScreen(context, intent);
                break;

            case LOCK_SCREEN_REQUEST:
                this.handleLockScreen(context, intent);
                break;
        }
    }

    private void handleShowMediaVolumeControl(Context context, Intent intent) {
        Intent intentMediaVolumeService = new Intent(context, MediaVolumeService.class);
        intentMediaVolumeService.putExtra(MediaVolumeService.ACTION, MediaVolumeService.SHOW_MEDIA_VOLUME_CONTROL);
        context.startService(intentMediaVolumeService);
    }

    private void handleLaunchHomeScreen(Context context, Intent intent) {
        Intent intentMediaVolumeService = new Intent(context, MediaVolumeService.class);
        intentMediaVolumeService.putExtra(MediaVolumeService.ACTION, MediaVolumeService.LAUNCH_HOME_SCREEN_REQUEST);
        context.startService(intentMediaVolumeService);
    }

    private void handleLockScreen(Context context, Intent intent) {
        Intent intentMediaVolumeService = new Intent(context, MediaVolumeService.class);
        intentMediaVolumeService.putExtra(MediaVolumeService.ACTION, MediaVolumeService.LOCK_SCREEN_REQUEST);
        context.startService(intentMediaVolumeService);
    }
}
