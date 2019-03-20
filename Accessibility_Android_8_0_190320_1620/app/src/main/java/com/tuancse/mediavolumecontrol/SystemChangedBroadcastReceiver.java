package com.tuancse.mediavolumecontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SystemChangedBroadcastReceiver extends BroadcastReceiver {
    public final static String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null) {
            return;
        }

        String action = intent.getAction();

        if (action != null) {

            switch (action) {

                case VOLUME_CHANGED_ACTION:
                    this.handleUpdateMediaVolumeService(context, intent);
                    break;
            }
        }
    }

    private void handleUpdateMediaVolumeService(Context context, Intent intent) {
        Intent intentMediaVolumeService = new Intent(context, MediaVolumeService.class);
        intentMediaVolumeService.setAction(MediaVolumeService.UPDATE_MEDIA_VOLUME_CONTROL);
        context.startForegroundService(intentMediaVolumeService);
    }
}
