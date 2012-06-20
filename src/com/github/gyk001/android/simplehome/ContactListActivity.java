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

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * A list view example where the
 * data comes from a cursor.
 */
public class ContactListActivity extends FloatTextListActivity {
	private static final String TAG = "ContactListActivity";
	private ListAdapter adapter ;
	
    private static final int COL_ID = 0;
    private static final int COL_DISPLAY_NAME = 1;
    private static final int COL_PHOTO_ID = 2;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a cursor with all people
        Cursor c = getContentResolver().query(Contacts.CONTENT_URI,
                CONTACT_PROJECTION, null, null, Contacts.DISPLAY_NAME);

        startManagingCursor(c);

        adapter = new SimpleCursorAdapter(this,
                // Use a template that displays a text view
               	R.layout.contact_list_item,
                // Give the cursor to the list adatper
                c,
                // Map the NAME column in the people database to...
                new String[] {Contacts.DISPLAY_NAME,Contacts.PHOTO_ID},
                // The "text1" view defined in the XML template
                new int[] {R.id.contactDisplayName,R.id.contactPhoto});
        setListAdapter(adapter);
    }

    private static final String[] CONTACT_PROJECTION = new String[] {
        Contacts._ID,
        Contacts.DISPLAY_NAME,
        Contacts.PHOTO_ID,
        
       
    };
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Object o =adapter.getItem(position);
    	Log.v(TAG,"onListItemClick");
    	if (position >= 0) {
            //Get current cursor
            Cursor c = (Cursor) l.getItemAtPosition(position);
            //long cid = c.getLong(COL_ID);
            //Log.v(TAG,">>"+ContactsUtils.loadContactPhoto(c, COL_PHOTO_ID, null));
           /*
            Intent in = new Intent(this, ViewContactActivity.class);
            startActivity( in );
            */
            final Uri uri = getContactUri(position);
            Log.v(TAG,uri.toString());
            final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
          //  finish();
            /*
            int type = c.getInt( COL_ID );
            String phone = c.getString(1);
            Log.v(TAG,type+":"+phone);
           
            String label = null;
            //Custom type? Then get the custom label
            if (type == Phone.TYPE_CUSTOM) {
                label = c.getString(COLUMN_PHONE_LABEL);
            }
            //Get the readable string
            String numberType = (String) Phone.getTypeLabel(getResources(), type, label);
            String text = numberType + ": " + phone;
            mPhone.setText(text);
            */
        }
    }
    /*
    private Uri getSelectedUri(int position) {
        if (position == ListView.INVALID_POSITION) {
            throw new IllegalArgumentException("Position not in list bounds");
        }

        final long id = mAdapter.getItemId(position);
      
        switch(mMode) {
            case MODE_LEGACY_PICK_PERSON:
            case MODE_LEGACY_PICK_OR_CREATE_PERSON: {
                return ContentUris.withAppendedId(People.CONTENT_URI, id);
            }
            case MODE_PICK_PHONE:
            case MODE_QUERY_PICK_PHONE: {
                return ContentUris.withAppendedId(Data.CONTENT_URI, id);
            }
            case MODE_LEGACY_PICK_PHONE: {
                return ContentUris.withAppendedId(Phones.CONTENT_URI, id);
            }
            case MODE_PICK_POSTAL: {
                return ContentUris.withAppendedId(Data.CONTENT_URI, id);
            }
            case MODE_LEGACY_PICK_POSTAL: {
                return ContentUris.withAppendedId(ContactMethods.CONTENT_URI, id);
            }
            default: {
                return getContactUri(position);
            }
        }
        
    }
    */
    /**
     * Build the {@link Contacts#CONTENT_LOOKUP_URI} for the given
     * {@link ListView} position, using {@link #mAdapter}.
     */
    private Uri getContactUri(int position) {
        if (position == ListView.INVALID_POSITION) {
            throw new IllegalArgumentException("Position not in list bounds");
        }

        final Cursor cursor = (Cursor)adapter.getItem(position);
        if (cursor == null) {
            return null;
        }
        
        final long personId = cursor.getLong(COL_ID);
        return ContentUris.withAppendedId(People.CONTENT_URI, personId);

       /* switch(mMode) {
            case MODE_LEGACY_PICK_PERSON:
            case MODE_LEGACY_PICK_OR_CREATE_PERSON: {
                final long personId = cursor.getLong(SUMMARY_ID_COLUMN_INDEX);
                return ContentUris.withAppendedId(People.CONTENT_URI, personId);
            }

            default: {
           
                // Build and return soft, lookup reference
                final long contactId = cursor.getLong(SUMMARY_ID_COLUMN_INDEX);
                final String lookupKey = cursor.getString(SUMMARY_LOOKUP_KEY_COLUMN_INDEX);
                return Contacts.getLookupUri(contactId, lookupKey);
            }
        }
         */
    }
    
	@Override
	protected String getFloatText(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		try {
			//用listview控件item获取text的方法有的拿不到！
			//View item = view.getChildAt(firstVisibleItem+(visibleItemCount/2));
			Cursor c = (Cursor) view.getItemAtPosition( firstVisibleItem+(visibleItemCount/2) );
			
			if(c != null){
				if(c.getColumnCount() > 1){
					String string = c.getString( 1 );
					if( string != null && string.length()>0){
						return string.substring(0,1);
					}
				}
			}
		} catch (Exception doNothing) {}
		return null;
	}

}