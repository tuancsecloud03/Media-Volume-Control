package com.tuancse.mediavolumecontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*setContentView(R.layout.activity_main);*/
		
		this.startMediaVolumeService();
		finish();		
	}
	
	private void startMediaVolumeService()
	{
		Intent intentMediaVolumeService = new Intent(this, MediaVolumeService.class);		
		intentMediaVolumeService.putExtra(MediaVolumeService.ACTION, MediaVolumeService.SHOW_MEDIA_VOLUME_CONTROL);			
		this.startService(intentMediaVolumeService);
	}
}
