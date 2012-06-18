package com.github.gyk001.android.simplehome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.qin.zdlock.ZdLockService;

public class SimpleHomeActivity extends Activity {
	private static final String TAG = "SimpleHomeActivity";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_main);
	}

	@Override
	public void onAttachedToWindow() { // TODO Auto-generated method stub
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		startService(new Intent(SimpleHomeActivity.this, ZdLockService.class));
		super.onAttachedToWindow();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "key down > "+keyCode);
		/*
		// TODO Auto-generated method stub
		if (KeyEvent.KEYCODE_HOME == keyCode)
			android.os.Process.killProcess(android.os.Process.myPid());
		return super.onKeyDown(keyCode, event);
		*/
		return true;
	}
	
	
	public void  btnExit(View v){
		this.finish();
	}
}