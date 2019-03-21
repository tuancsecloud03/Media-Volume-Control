package com.tuancse.mediavolumecontrol;

import java.lang.reflect.Method;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.tommycse.Services.NonstopIntentService;

public class MediaVolumeService extends NonstopIntentService {
    private final static String MEDIA_VOLUME_SERVICE_NAME = "Media_Volume_Service";
    private final static String MEDIA_VOLUME_NOTIFICATION_TITLE = "Media volume";
    private static final int MEDIA_VOLUME_NOTIFICATION_ID = 1;
    private static final String MEDIA_VOLUME_PROGRESS = "Media: ";

    private boolean mediaVolumeViewCreated;
    private boolean mediaVolumeViewVisible;
    private boolean mediaVolumeNotificationCreated;

    private DevicePolicyManager policyManager;
    private WindowManager windowManager;
    private AudioManager audioManager;
    private WindowManager.LayoutParams layoutParams;
    private View customView;
    private SeekBar seekBar_mediaVolume;
    private TextView textView_mediaProgress;
    private Button btn_close;

    public static final String ACTION = "Action";
    public static final String START = "Start";
    public static final String STOP = "Stop";
    public static final String UPDATE_MEDIA_VOLUME_CONTROL = "Update_Media_Volume_Control";
    public static final String SHOW_MEDIA_VOLUME_CONTROL = "Show_Media_Volume_Control";
    public static final String LOCK_SCREEN_REQUEST = "Lock_Screen_Request";
    public static final String LAUNCH_HOME_SCREEN_REQUEST = "Launch_Home_Screen_Request";

    public MediaVolumeService() {
        super(MEDIA_VOLUME_SERVICE_NAME);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        this.initialize();
    }

    @Override
    protected void onHandleIntent(Intent arg0) {
        // In case OS kill the service,
        // then re-start it with intent = null
        if (arg0 == null) {
            this.handleRestart();
            return;
        }

        // Gets data from the incoming Intent
        String msg = arg0.getStringExtra(ACTION);

        if (msg == null) {
            return;
        }

        switch (msg) {

            case START:
                this.handleStart();
                break;

            case SHOW_MEDIA_VOLUME_CONTROL:
                this.handleShowMediaVolumeView();
                break;

            case UPDATE_MEDIA_VOLUME_CONTROL:
                this.handleUpdateMediaVolumeView();
                break;

            case LAUNCH_HOME_SCREEN_REQUEST:
                this.handleLaunchHomeScreen();
                break;

            case LOCK_SCREEN_REQUEST:
                this.handleLockScreen(this);
                break;

            default:
                break;
        }
    }

    private void initialize() {
        this.windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        this.audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        this.policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

        this.layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);

        this.layoutParams.gravity = Gravity.CENTER;

    }

    private void handleStart() {
        // show media volume notification
        this.handleShowMediaVolumeNotification();
    }

    private void handleRestart() {
        this.handleStart();
    }

    private void handleShowMediaVolumeView() {
        // show media volume notification
        this.handleShowMediaVolumeNotification();

        if (!this.mediaVolumeViewCreated) {
            this.createMediaVolumeView();
        }

        if (!this.mediaVolumeViewVisible) {
            this.showMediaVolumeView();
        }

        // collapse status bar
        this.collapseStatusBar();
    }

    private void handleCloseMediaVolumeView() {
        this.closeMediaVolumeView();
    }

    private void handleUpdateMediaVolumeView() {
        if (!this.mediaVolumeViewCreated) {
            return;
        }

        int maxVolume = this.audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int progressVolume = this.audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);

        seekBar_mediaVolume.setMax(maxVolume);
        seekBar_mediaVolume.setProgress(progressVolume);

        textView_mediaProgress.setText(MEDIA_VOLUME_PROGRESS
                + seekBar_mediaVolume.getProgress() + "/" + maxVolume);
    }

    private void handleShowMediaVolumeNotification() {
        if (this.mediaVolumeNotificationCreated) {
            return;
        }

        Notification notification = this.createMediaVolumeNotification();
        this.showMediaVolumeNotification(notification);
        this.mediaVolumeNotificationCreated = true;
        ;
    }

    private void handleLaunchHomeScreen() {

        // collapse status bar
        this.collapseStatusBar();

        // Launch home screen
        this.launchHomeScreen();
    }

    private void handleLockScreen(Context context) {
        // collapse status bar
        this.collapseStatusBar();

        // Lock screen
        this.lockScreen(context);
    }

    private Notification createMediaVolumeNotification() {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.media_volume_notification);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this)
                // Set Icon
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(MEDIA_VOLUME_NOTIFICATION_TITLE)
                .setContentText(MEDIA_VOLUME_NOTIFICATION_TITLE)
                .setOngoing(true).setTicker(MEDIA_VOLUME_NOTIFICATION_TITLE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // Show media volume control
        Intent mediaVolumeBroadcastReceiverIntent = new Intent(
                MediaVolumeBroadcastReceiver.BROADCAST_RECEIVER_ACTION);
        mediaVolumeBroadcastReceiverIntent.putExtra(
                MediaVolumeBroadcastReceiver.ACTION,
                MediaVolumeBroadcastReceiver.SHOW_MEDIA_VOLUME_CONTROL);
        PendingIntent pendingStopIntent = PendingIntent.getBroadcast(this, 0,
                mediaVolumeBroadcastReceiverIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.volume_notificationIcon,
                pendingStopIntent);

        // Lock screen
        Intent lockScreenBroadcastReceiverIntent = new Intent(
                MediaVolumeBroadcastReceiver.BROADCAST_RECEIVER_ACTION);
        lockScreenBroadcastReceiverIntent.putExtra(
                MediaVolumeBroadcastReceiver.ACTION,
                MediaVolumeBroadcastReceiver.LOCK_SCREEN_REQUEST);
        PendingIntent pendingLockScreenIntent = PendingIntent.getBroadcast(this, 1,
                lockScreenBroadcastReceiverIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.lock_notificationIcon,
                pendingLockScreenIntent);

        // Launch home screen
        Intent launchHomeScreenBroadcastReceiverIntent = new Intent(
                MediaVolumeBroadcastReceiver.BROADCAST_RECEIVER_ACTION);
        launchHomeScreenBroadcastReceiverIntent.putExtra(
                MediaVolumeBroadcastReceiver.ACTION,
                MediaVolumeBroadcastReceiver.LAUNCH_HOME_SCREEN_REQUEST);
        PendingIntent pendingLaunchHSIntent = PendingIntent.getBroadcast(this, 2,
                launchHomeScreenBroadcastReceiverIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.home_notificationIcon,
                pendingLaunchHSIntent);

        builder.setContent(remoteViews);

        Notification notification = builder.build();

        return notification;
    }

    private void showMediaVolumeNotification(Notification notification) {
        if (notification == null) {
            return;
        }

        this.startForeground(MEDIA_VOLUME_NOTIFICATION_ID, notification);
    }

    private void collapseStatusBar() {
        try {
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;

            Object service = getSystemService("statusbar");

            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");

            Method collapse = null;
            if (currentapiVersion <= 16) {
                collapse = statusbarManager.getMethod("collapse");
            } else {
                collapse = statusbarManager.getMethod("collapsePanels");
            }

            collapse.setAccessible(true);
            collapse.invoke(service);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createMediaVolumeView() {
        if (this.mediaVolumeViewCreated) {
            return;
        }

        this.customView = LayoutInflater.from(this).inflate(
                R.layout.media_volume_control, null);

        this.seekBar_mediaVolume = (SeekBar) this.customView
                .findViewById(R.id.sB_media_volume);
        this.textView_mediaProgress = (TextView) this.customView
                .findViewById(R.id.txtV_media_progress);
        this.btn_close = (Button) this.customView.findViewById(R.id.btn_close);

        int maxVolume = this.audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int progressVolume = this.audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);

        seekBar_mediaVolume.setMax(maxVolume);
        seekBar_mediaVolume.setProgress(progressVolume);

        textView_mediaProgress.setText(MEDIA_VOLUME_PROGRESS
                + seekBar_mediaVolume.getProgress() + "/" + maxVolume);

        seekBar_mediaVolume
                .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                    int progress = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        progress = progresValue;

                        // Display the value in text view
                        textView_mediaProgress.setText(MEDIA_VOLUME_PROGRESS
                                + progress + "/" + seekBar.getMax());
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                progress, 0);

                        // Display the value in text view
                        textView_mediaProgress.setText(MEDIA_VOLUME_PROGRESS
                                + progress + "/" + seekBar.getMax());
                    }
                });

        this.btn_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCloseMediaVolumeView();
            }
        });

        this.mediaVolumeViewCreated = true;
    }

    private void showMediaVolumeView() {
        if (this.mediaVolumeViewVisible || !this.mediaVolumeViewCreated) {
            return;
        }

        windowManager.addView(this.customView, this.layoutParams);

        this.collapseStatusBar();

        this.mediaVolumeViewVisible = true;
    }

    private void closeMediaVolumeView() {
        if (!this.mediaVolumeViewVisible || !this.mediaVolumeViewCreated) {
            return;
        }

        windowManager.removeView(this.customView);

        this.mediaVolumeViewVisible = false;
    }

    private void launchHomeScreen() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void lockScreen(Context context) {

        ComponentName adminReceiver = new ComponentName(context, ScreenOffAdminReceiver.class);

        boolean admin = this.policyManager.isAdminActive(adminReceiver);

        if (admin) {
            this.policyManager.lockNow();
        } else {
            Toast.makeText(context, R.string.device_admin_not_enabled,
                    Toast.LENGTH_LONG).show();
        }
    }
}
