/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.gyk001.android.simplehome;



import android.app.ListActivity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Another variation of the list of cheeses. In this case, we use
 * {@link AbsListView#setOnScrollListener(AbsListView.OnScrollListener) 
 * AbsListView#setOnItemScrollListener(AbsListView.OnItemScrollListener)} to display the
 * first letter of the visible range of cheeses.
 */
public abstract class FloatTextListActivity extends ListActivity implements ListView.OnScrollListener {
	private static final String TAG = "FloatTextListActivity";
    private final class RemoveWindow implements Runnable {
        public void run() {
            removeWindow();
        }
    }

    private RemoveWindow mRemoveWindow = new RemoveWindow();
    Handler mHandler = new Handler();
    private WindowManager mWindowManager;
    private TextView mDialogText;
    private boolean mShowing;
    private boolean mReady;
    //第一个可见元素，当滚动影响到这个值时才更新悬浮窗
    private int firstVisibleItem=-1;
    //滚动状态，只有正在滚动才显示悬浮窗(否则点击时会出发滚动事件);
    private int scrollState = ListView.OnScrollListener.SCROLL_STATE_IDLE;
  //  private char mPrevLetter = Character.MIN_VALUE;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        
        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
       /*
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mStrings));
        */
        getListView().setOnScrollListener(this);
        
        LayoutInflater inflate = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        mDialogText = (TextView) inflate.inflate(R.layout.listview_float_text, null);
        mDialogText.setVisibility(View.INVISIBLE);
        
        mHandler.post(new Runnable() {

            public void run() {
                mReady = true;
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                mWindowManager.addView(mDialogText, lp);
            }});
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mReady = true;
    }

    
    @Override
    protected void onPause() {
        super.onPause();
        removeWindow();
        mReady = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWindowManager.removeView(mDialogText);
        mReady = false;
    }

    
   
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
    	Log.v(TAG,"onScroll()");
        if (mReady) {
        	if( this.scrollState != ListView.OnScrollListener.SCROLL_STATE_IDLE ){
	            if (!mShowing ) {
	
	                mShowing = true;
	                mDialogText.setVisibility(View.VISIBLE);
	            }
	            if( this.firstVisibleItem != firstVisibleItem ){
	            	Log.v(TAG,"onScroll() new location");
	                this.firstVisibleItem = firstVisibleItem;
	            	String ft = getFloatText(view, firstVisibleItem, visibleItemCount, totalItemCount);
	                if(ft!=null){
	                    mDialogText.setText( ft );           	
	                }           	
	            }
	
	
	        	/*
	        	char firstLetter = '我';//mStrings[firstVisibleItem].charAt(0);
	            
	            if (!mShowing && firstLetter != mPrevLetter) {
	
	                mShowing = true;
	                mDialogText.setVisibility(View.VISIBLE);
	            }
	            mDialogText.setText(((Character)firstLetter).toString());
	            */
	            mHandler.removeCallbacks(mRemoveWindow);
	            mHandler.postDelayed(mRemoveWindow, 200);
	            //mPrevLetter = firstLetter;
	        }
        }
    }
    
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    	Log.v(TAG,"onScrollStateChanged():"+scrollState);
    	this.scrollState = scrollState;
    } 
    
    
    private void removeWindow() {
        if (mShowing) {
            mShowing = false;
            mDialogText.setVisibility(View.INVISIBLE);
        }
    }

    protected abstract String getFloatText(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) ;
}
