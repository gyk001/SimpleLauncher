package com.qin.zdlock;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ZdLockService extends Service {

	private static String TAG = "ZdLockService";
	private Intent zdLockIntent = null;
	private Intent zdUnlockIntent = null;
	// 电话正在使用，如接听等
	private boolean phoneUsing = false;

	// private TelephonyManager manager ;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

		zdLockIntent = new Intent(ZdLockService.this, MainActivity.class);
		zdLockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		zdUnlockIntent = new Intent("gyk001.lockscreen.stop");

		IntentFilter mScreenOnOffFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
		mScreenOnOffFilter.addAction("android.intent.action.SCREEN_ON");
		ZdLockService.this.registerReceiver(screenOnOffReceiver, mScreenOnOffFilter);

		IntentFilter mPhoneStateFilter = new IntentFilter("android.intent.action.PHONE_STATE");
		mPhoneStateFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		ZdLockService.this.registerReceiver(phoneStateReceiver, mPhoneStateFilter);
		/*
		 * //获取电话服务 manager = (TelephonyManager)
		 * this.getSystemService(TELEPHONY_SERVICE); //
		 * 手动注册对PhoneStateListener中的listen_call_state状态进行监听 manager.listen(new
		 * MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
		 */

	}

	public int onStartCommand(Intent intent, int flags, int startId) {

		return Service.START_STICKY;

	}

	public void onDestroy() {
		super.onDestroy();
		ZdLockService.this.unregisterReceiver(screenOnOffReceiver);
		ZdLockService.this.unregisterReceiver(phoneStateReceiver);
		// 在此重新启动
		startService(new Intent(ZdLockService.this, ZdLockService.class));
	}

	private KeyguardManager mKeyguardManager = null;
	private KeyguardManager.KeyguardLock mKeyguardLock = null;
	
	//屏幕变亮的广播,我们要隐藏默认的锁屏界面
	// 屏幕变暗/变亮的广播 ， 我们要调用KeyguardManager类相应方法去解除屏幕锁定
	private BroadcastReceiver screenOnOffReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			Log.d(TAG, action);

			if (action.equals("android.intent.action.SCREEN_OFF") || action.equals("android.intent.action.SCREEN_ON")) {
				mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
				mKeyguardLock = mKeyguardManager.newKeyguardLock("zdLock 1");
				mKeyguardLock.disableKeyguard();
				if (!ZdLockService.this.phoneUsing) {
					Log.d(TAG,"");
					startActivity(zdLockIntent);
				}else{
					Log.d(TAG,"");
					sendBroadcast(zdUnlockIntent	);
				}
			}
		}

	};

	private BroadcastReceiver phoneStateReceiver = new BroadcastReceiver() {
		private boolean incomingFlag = false;
		private String incoming_number = null;

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, intent.getAction());

			// 如果是拨打电话
			if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				incomingFlag = false;
				ZdLockService.this.phoneUsing = true;
				String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
				Log.d(TAG, "call OUT:" + phoneNumber);
			} else {
				// 如果是来电
				TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
				switch (tm.getCallState()) {
					case TelephonyManager.CALL_STATE_RINGING:
						ZdLockService.this.phoneUsing = true;
						incomingFlag = true;// 标识当前是来电
						incoming_number = intent.getStringExtra("incoming_number");
						Log.d(TAG, "RINGING :" + incoming_number);
						break;
					case TelephonyManager.CALL_STATE_OFFHOOK:
						ZdLockService.this.phoneUsing = false;
						if (incomingFlag) {
							Log.d(TAG, "incoming ACCEPT :" + incoming_number);
						}
						break;
					case TelephonyManager.CALL_STATE_IDLE:
						ZdLockService.this.phoneUsing = false;
						if (incomingFlag) {
							Log.d(TAG, "incoming IDLE");
						}
						break;
				}
			}
		}
	};

	/***
	 * 继承PhoneStateListener类，我们可以重新其内部的各种监听方法 然后通过手机状态改变时，系统自动触发这些方法来实现我们想要的功能
	 */
	/*
	 * class MyPhoneStateListener extends PhoneStateListener{
	 * 
	 * @Override public void onCallStateChanged(int state, String
	 * incomingNumber) { switch (state) { case TelephonyManager.CALL_STATE_IDLE:
	 * Log.d(TAG," 手机空闲起来了  "); break; case TelephonyManager.CALL_STATE_RINGING:
	 * Log.d(TAG,"  手机铃声响了，来电号码:" + incomingNumber); break; case
	 * TelephonyManager.CALL_STATE_OFFHOOK: Log.d(TAG, " 电话被挂起了 "); default:
	 * Log.d(TAG,"other:"+state); break; } super.onCallStateChanged(state,
	 * incomingNumber); }
	 * 
	 * }
	 */
}
