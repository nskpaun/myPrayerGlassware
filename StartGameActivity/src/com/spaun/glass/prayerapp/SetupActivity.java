package com.spaun.glass.prayerapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import models.Prayer;
import models.PrayerBlock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.spaun.glass.prayerapp.R;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

public class SetupActivity extends Activity{
	
	static final String TAG = "SETUP_ACTIVITY";

	
	private boolean mGesturesEnabled = true;
	private GestureDetector mGestureDetector;
	
	public Prayer prayer;
	
	protected void handleGameGesture(Gesture gesture) {
		switch (gesture) {
        case TAP:
            Log.d(TAG,getLastLocation().toString());
            Intent intent = new Intent(this, VoiceDictationActivity.class);
            int requestCode = 1; // Or some number you choose
            startActivityForResult(intent, requestCode);
            break;
//        case SWIPE_RIGHT:
//            pass();
//            break;
    }

	}
	
	@Override
	protected void onStop() {
		prayer.saveToParse();
		super.onDestroy();
	}
	
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		  // Collect data from the intent and use it
		PrayerBlock pb = new PrayerBlock();
		Log.d(TAG,"NEW PRAYER BLOCK ADDED");
		pb.location = getLastLocation();
		pb.verse = data.getStringExtra(PrayerBlock.VERSE);
		pb.comment = data.getStringExtra(PrayerBlock.COMM);
		pb.reference = data.getStringExtra(PrayerBlock.REF);
		
		prayer.addBlock(pb);
		
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_setup_layout);
        
        prayer = new Prayer();
        
        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);

//        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);
    }
	
	@Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector.onMotionEvent(event);
    }
	
	private final GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
        @Override
        public boolean onGesture(Gesture gesture) {
            if (areGesturesEnabled()) {
                switch (gesture) {
                    case SWIPE_LEFT:
                        // Swipe left (backward) is always handled here to provide a brief
                        // "disallowed" tug animation.
                        //tugPhrase();
                        return true;
                    case TAP:
                    case SWIPE_RIGHT:
                        // Delegate tap and swipe right (forward) to the subclass so that the
                        // tutorial and actual game can handle them differently.
                        handleGameGesture(gesture);
                        return true;
                    default:
                        return false;
                }
            }
            return false;
        }
    };
    
    private boolean areGesturesEnabled() {
        return mGesturesEnabled;
    }
    
	public  Location getLastLocation() {
	      LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	      Criteria criteria = new Criteria();
	      criteria.setAccuracy(Criteria.NO_REQUIREMENT);
	      List<String> providers = manager.getProviders(criteria, true);
	      List<Location> locations = new ArrayList<Location>();
	      for (String provider : providers) {
	           Location location = manager.getLastKnownLocation(provider);
	           if (location != null && location.getAccuracy()!=0.0) {
	               locations.add(location);
	           }
	      }
	      Collections.sort(locations, new Comparator<Location>() {
	          @Override
	          public int compare(Location location, Location location2) {
	              return (int) (location.getAccuracy() - location2.getAccuracy());
	          }
	      });
	      if (locations.size() > 0) {
	          return locations.get(0);
	      }
	      return null;
	 }

}
