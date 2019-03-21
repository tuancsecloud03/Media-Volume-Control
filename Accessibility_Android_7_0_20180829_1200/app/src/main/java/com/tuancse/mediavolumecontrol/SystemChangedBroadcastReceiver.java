package com.tuancse.mediavolumecontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SystemChangedBroadcastReceiver extends BroadcastReceiver {

	private final static String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	private final static String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
	
	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent == null) {
			return;
		}

		String action = intent.getAction();

		if (action != null) {
			
			switch (action) {
			
			case BOOT_COMPLETED:
				this.handleStartMediaVolumeService(context, intent);
				break;

			case VOLUME_CHANGED_ACTION:
				this.handleUpdateMediaVolumeService(context, intent);
				break;
			}
		}
	}
	
	private void handleStartMediaVolumeService(Context context, Intent intent)
	{		
		Intent intentMediaVolumeService = new Intent(context, MediaVolumeService.class);		
		intentMediaVolumeService.putExtra(MediaVolumeService.ACTION, MediaVolumeService.START);			
		context.startService(intentMediaVolumeService);
	}
	
	private void handleUpdateMediaVolumeService(Context context, Intent intent)
	{		
		Intent intentMediaVolumeService = new Intent(context, MediaVolumeService.class);		
		intentMediaVolumeService.putExtra(MediaVolumeService.ACTION, MediaVolumeService.UPDATE_MEDIA_VOLUME_CONTROL);			
		context.startService(intentMediaVolumeService);
	}
}
