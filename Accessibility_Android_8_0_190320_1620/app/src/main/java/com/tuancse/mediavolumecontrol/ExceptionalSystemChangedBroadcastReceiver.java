package com.tuancse.mediavolumecontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ExceptionalSystemChangedBroadcastReceiver extends BroadcastReceiver {

	private final static String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {

		try {
			if (intent == null) {
				return;
			}

			String action = intent.getAction();

			if (action != null) {

				switch (action) {

					case BOOT_COMPLETED:
						this.handleStartMediaVolumeService(context, intent);
						break;
				}
			}
		}
		catch (Exception ex)
		{
		}
	}
	
	private void handleStartMediaVolumeService(Context context, Intent intent)
	{		
		Intent intentMediaVolumeService = new Intent(context, MediaVolumeService.class);
		intentMediaVolumeService.setAction(MediaVolumeService.START);
		context.startForegroundService(intentMediaVolumeService);
	}
}
