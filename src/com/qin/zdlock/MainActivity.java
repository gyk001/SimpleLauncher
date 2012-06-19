package com.qin.zdlock;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.github.gyk001.android.simplehome.R;

public class MainActivity extends Activity {

	private static String TAG = "QINZDLOCK";

	private SliderRelativeLayout sliderLayout = null;

	private ImageView imgView_getup_arrow; // ����ͼƬ
	private AnimationDrawable animArrowDrawable = null;

    private Context mContext = null ;
	
    public static int MSG_LOCK_SUCESS = 1;
    
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = MainActivity.this;
		/*����ȫ�����ޱ���*/
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.lock_screen);
		initViews();
		
		
		sliderLayout.setMainHandler(mHandler);
	}

	private void initViews(){    	
    	sliderLayout = (SliderRelativeLayout)findViewById(R.id.slider_layout);
    	//��ö���������ʼת��
    	imgView_getup_arrow = (ImageView)findViewById(R.id.getup_arrow);
    	animArrowDrawable = (AnimationDrawable) imgView_getup_arrow.getBackground() ;
    }

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("gyk001.lockscreen.stop");
		registerReceiver(broadcastReceiver, intentFilter);
		
		//���ö���
		mHandler.postDelayed(AnimationDrawableTask, 300);  //��ʼ���ƶ���
	}
	@Override
	protected void onPause() {
		super.onPause();
		animArrowDrawable.stop();
	}

	protected void onDestory(){
		super.onDestroy();
	}
	
	//ͨ����ʱ���Ƶ�ǰ����bitmap��λ������
	private Runnable AnimationDrawableTask = new Runnable(){
		
		public void run(){
			animArrowDrawable.start();
			mHandler.postDelayed(AnimationDrawableTask, 100);
		}
	};
	
	private Handler mHandler =new Handler (){
		
		public void handleMessage(Message msg){
			
			Log.i(TAG, "handleMessage :  #### " );
			
			if(MSG_LOCK_SUCESS == msg.what)
				finish(); // �����ɹ�ʱ���������ǵ�Activity����
		}
	};
	
	//���ε�Home��
	public void onAttachedToWindow() {
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
	    super.onAttachedToWindow();
    }
	
	//���ε�Back��
	public boolean onKeyDown(int keyCode ,KeyEvent event){
		
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK)
			return true ;
		else
			return super.onKeyDown(keyCode, event);
		
	}
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG,intent.toString());
				if(intent.getAction().equals("gyk001.lockscreen.stop")){
					Log.d(TAG,"unlock...");
					finish();
				}
			}
		};
}