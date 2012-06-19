package com.github.gyk001.android.simplehome;

import java.io.File;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qin.zdlock.ZdLockService;

public class SimpleHomeActivity extends Activity {
	private static final String TAG = "SimpleHomeActivity";
	private TextView tvBatteryLevel;
	private static final File WALLPAPER_DIR = new File(
			  "/data/data/com.android.settings/files");
	private static final String WALLPAPER = "wallpaper";
	private static final File WALLPAPER_FILE = new File(WALLPAPER_DIR, WALLPAPER);
	
	
	private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(tvBatteryLevel!=null){
				String action = intent.getAction();
				if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
					
					int level = intent.getIntExtra("level", 0);
					int scale = intent.getIntExtra("scale", 100);
					int batteryStatus = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
					String status;
					switch(batteryStatus){
					case BatteryManager.BATTERY_STATUS_CHARGING :
						status = "正在充电:";
						break;
					case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
						status="剩余电量:";
						break;
					case BatteryManager.BATTERY_STATUS_FULL:
						status="电量满：";
						break;
					case BatteryManager.BATTERY_STATUS_DISCHARGING:
						status="正在放电：";
						break;
					default:
							status = "";
					}
					tvBatteryLevel.setText(status + String.valueOf(level * 100 / scale) + "%");
				}
			}
		}
	};
	
	

	@Override
	public void onResume() {
		super.onResume();
		
		registerReceiver(mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}

	@Override
	public void onPause() {
		super.onPause();

		unregisterReceiver(mBatteryInfoReceiver);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.app_main);
		View main = (View) findViewById(R.id.main);
		
		 WallpaperManager wpm = (WallpaperManager) getSystemService(WALLPAPER_SERVICE);
		if(main!=null && wpm !=null){
			main.setBackgroundDrawable(wpm.getFastDrawable());
		}
		 
		 tvBatteryLevel = (TextView) findViewById(R.id.tvBatteryLevel);  
	}

	@Override
	public void onAttachedToWindow() { // TODO Auto-generated method stub
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		startService(new Intent(SimpleHomeActivity.this, ZdLockService.class));
		super.onAttachedToWindow();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "key down > " + keyCode);
		/*
		 * // TODO Auto-generated method stub if (KeyEvent.KEYCODE_HOME ==
		 * keyCode) android.os.Process.killProcess(android.os.Process.myPid());
		 * return super.onKeyDown(keyCode, event);
		 */
		return true;
	}

	public void btnExit(View v) {
		this.finish();
	}
}