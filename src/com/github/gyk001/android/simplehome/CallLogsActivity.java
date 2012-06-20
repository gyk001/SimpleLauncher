package com.github.gyk001.android.simplehome;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CallLogsActivity extends ListActivity {
	private static final String TAG = "CallLogsActivity";

	//private static final String[] PROJECTION = new String[] {CallLog.Calls.NUMBER,CallLog.Calls.CACHED_NAME,CallLog.Calls.TYPE, CallLog.Calls.DATE,}; 
	
	private Cursor cursor = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,
				null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
		startManagingCursor(cursor);

		CallLogCursorAdapter adapter = new CallLogCursorAdapter(this,cursor);
		setListAdapter(adapter);
	}
	
	
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		TextView numberTxt = (TextView)v.findViewById(R.id.number);
		CharSequence telNum = numberTxt.getText();
		 Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+telNum));
		 startActivity(intent);
    }


	public class CallLogCursorAdapter extends CursorAdapter{
		private LayoutInflater mInflater;
		
        public CallLogCursorAdapter(Context context, Cursor c) {
			super(context, c);
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		}
        

 
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			setChildView(context, view, cursor);  
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(R.layout.calllog_item, null);  
	        setChildView(context, view, cursor);  
	        return view; 
		}
		
		private String convertSecReadable(int sec){
			StringBuffer res = new StringBuffer();
			if(sec >3600 ){
				res.append( sec /3600 ).append("小时");
			}
			if(sec>60){
				res.append( sec %3600/60 ).append("分");
			}
			res.append(sec%60).append("秒");
			return res.toString();
		}
		
		private void setChildView(Context context, View view, Cursor cursor) { 
			
			 ImageView callTypeImg = (ImageView)view.findViewById(R.id.calltype);
             TextView nameTxt = (TextView)view.findViewById(R.id.name);
             TextView numberTxt = (TextView)view.findViewById(R.id.number);
             TextView epochTxt = (TextView)view.findViewById(R.id.epoch);
             TextView durationTxt = (TextView)view.findViewById(R.id.duration);
             
             String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
             String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
             Long epoch = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
             Integer duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
             Integer callType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
             
             int flags = 0;  
             String when = null;  
             if (DateUtils.isToday(epoch))  
             {  
                 flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR;  
                 when = (String)DateUtils.formatDateTime(context, epoch, flags);  
             }   
             else  
             {  
                 flags = DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR;  
                 when = (String)DateUtils.formatDateTime(context, epoch, flags);  
             }  
             
             nameTxt.setText( name );
             numberTxt.setText( number);
             epochTxt.setText(when);
             int color = 0;
             String durationType = "通话：";
             switch( callType ){
	             case CallLog.Calls.INCOMING_TYPE:{
	            	 color = Color.BLUE ;
	            	 break;
	             	}
	             case CallLog.Calls.OUTGOING_TYPE:{
	            	 color = Color.GREEN;
	            	 break;
	             	}
	             case CallLog.Calls.MISSED_TYPE:{
	            	 color = Color.RED;
	            	 durationType="响铃：";
	            	 break;
	             }
             }
             
             durationTxt.setText(durationType+convertSecReadable(duration));
             callTypeImg.setBackgroundColor(color);
	    }         
         
    }
	
}