package com.sachs.flickrabbit;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public final static String PHOTO_MESSAGE = "com.sachs.flickrabbit.PHOTO_MESSAGE";
	public static ArrayList<Integer> imagesArray = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_new:
	        	Intent logInIntent = new Intent(this, AuthActivity.class);
	    		startActivity(logInIntent);
	        case R.id.action_settings:
	        	// Intent settingsIntent = new Intent(this, SettingsActivity.class);
	    		// startActivity(settingsIntent);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
		Integer photoId = new Integer(0);
		Intent fullScreenPhotoIntent = new Intent(this, FullscreenPhotoViewActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String photoEntered = editText.getText().toString();
		try {
			photoId = Integer.parseInt(photoEntered);
			imagesArray.add(photoId);
			fullScreenPhotoIntent.putExtra(PHOTO_MESSAGE, photoEntered);
			startActivity(fullScreenPhotoIntent);
		} catch(NumberFormatException e) {
			Context context = getApplicationContext();
			CharSequence text = "Photo ID must be an integer!";
			int duration = Toast.LENGTH_SHORT;
			
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	}
	
}
