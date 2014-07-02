/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.spaun.glass.prayerapp;

import com.spaun.glass.prayerapp.R;
import com.google.android.glass.touchpad.Gesture;

import Listeners.MathUtils;
import Listeners.OrientationManager;
import Listeners.OrientationManager.OnChangedListener;
import Listeners.PrayerLocationListener;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import models.Prayer;
import models.PrayerBlock;

/**
 * The concrete, non-tutorial implementation of the game: one minute long with ten randomly
 * selected phrases.
 */
public class GameplayActivity extends BaseGameActivity implements Prayer.PrayerCallbackInterface, OnChangedListener {
	
	final static String TAG = "PRAYER_PLAY_ACTIVITY";

    /** The number of phrases that will be selected for the game. */
    private static final int NUMBER_OF_VERSES = 10;

    /** The maximum duration of the game. */
    private static final int PRAYER_TIME_SECONDS = (int) TimeUnit.MINUTES.toSeconds(20);

    /** Handler used to keep the game ticking once per second. */
    private final Handler mHandler = new Handler();
    int interval = 3;
    int totalTime = 3;
    boolean scorable = false;
    ImageView direction;
    
    OrientationManager orientationManager;

    /**
     * Runner that is called once per second during the game to advance the state or end the game
     * when time runs out.
     */
    
    
    LocationManager locationManager;
    
    private final Runnable mTick = new Runnable() {
        @Override
        public void run() {
            mSecondsRemaining--;
            updateTimer();

            if (mSecondsRemaining <= 0) {
                endGame();
            } else if ( mSecondsRemaining%TimeUnit.MINUTES.toSeconds(interval) == 0 ) {
            	if(scorable) {
            		scoringCommand();
            	} else {
            		nextTick();
            	}
            }
            else {
                nextTick();
            }
        }
    };

    /** Keeps track of the amount of time remaining in the game. */
    private int mSecondsRemaining;

    /** TextView that displays the amount of time remaining in the game. */
    private TextView mTimer;
    
    Prayer prayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Prayer.getLastPrayer(this);
    	PrayerLocationListener pll = new PrayerLocationListener(prayer,this);
        super.onCreate(savedInstanceState);
        mTimer = (TextView) findViewById(R.id.timer);
        
        direction = (ImageView) findViewById(R.id.directionImage);
        
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        
        orientationManager = new OrientationManager(sensorManager, locationManager);
        
        orientationManager.addOnChangedListener(this);
        orientationManager.start();
        
         // initialized elsewhere

     // This example requests fine accuracy and requires altitude, but
     // these criteria could be whatever you want.
     Criteria criteria = new Criteria();
     criteria.setAccuracy(Criteria.ACCURACY_FINE);
     criteria.setAltitudeRequired(true);

     List<String> providers = locationManager.getProviders(
             criteria, true /* enabledOnly */);

     for (String provider : providers) {
    	 Log.d(TAG, "provider found!");
         locationManager.requestLocationUpdates(provider, 1,
                 1, pll);
     }
    }
    
   

    @Override
    protected void onStart() {
        super.onStart();
        mSecondsRemaining = PRAYER_TIME_SECONDS;
        updateTimer();
        nextTick();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mTick);
    }

    /** Overridden to select ten random phrases from the application's resources. */
    @Override
    protected CharadesModel createCharadesModel() {
        List<String> allPhrases = Arrays.asList(getResources().getStringArray(
                R.array.phrases));
        Collections.shuffle(allPhrases);
        return new CharadesModel(allPhrases.subList(0, NUMBER_OF_VERSES));
    }
    
    @Override
    protected Prayer createPrayerModel() {
        
        return Prayer.getDefaultPrayer();
    }
    
    void scoringCommand() {
    	score();
        if (superPrayer.getCurrentPrayerIndex() == superPrayer.getBlockCount()) {
            endGame();
        }
    }

    @Override
    protected void handleGameGesture(Gesture gesture) {
        switch (gesture) {
            case TAP:
                scoringCommand();
                break;
//            case SWIPE_RIGHT:
//                pass();
//                break;
        }
    }
    
    

    /** Enqueues the next timer tick into the message queue after one second. */
    private void nextTick() {
        mHandler.postDelayed(mTick, 1000);
    }

    /** Updates the timer display with the current number of seconds remaining. */
    private void updateTimer() {
        // The code point U+EE01 in Roboto is the vertically centered colon used in the clock on
        // the Glass home screen.
        String timeString = String.format(
            "%d\uee01%02d", mSecondsRemaining / 60, mSecondsRemaining % 60);
        if (timeString.substring(timeString.length()-1).equalsIgnoreCase("0")){
        	onLocationChanged(orientationManager);
        }
        
        
        mTimer.setText(timeString);
    }
    
    public Location getLastLocation() {
        LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        List<String> providers = manager.getProviders(criteria, true);
        List<Location> locations = new ArrayList<Location>();
        for (String provider : providers) {
        	Log.d(TAG,"provider found: "+provider);
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
        return new Location("dummyprovider");
   }

    /**
     * Called either when the last phrase is guessed correctly or time has run out to finish the
     * game play activity and display the game results screen.
     */
    private void endGame() {
        Intent intent = new Intent(this, GameResultsActivity.class);
        intent.putExtra(GameResultsActivity.EXTRA_MODEL, getCharadesModel());
        startActivity(intent);
        finish();
    }



	@Override
	public void callback(Prayer p) {
		prayer = p;
		superPrayer = prayer;
		mSecondsRemaining =   (int) TimeUnit.MINUTES.toSeconds( (long) (superPrayer.getBlockCount()*interval));
		totalTime = mSecondsRemaining;
        updateTimer();
        //nextTick();
		Log.d(TAG, "hi" + p.getBlockCount());
		updateDisplay();
		
	}



	@Override
	public void onOrientationChanged(OrientationManager orientationManager) {
//		Log.d(TAG, "heading " + orientationManager.getHeading());
//		Log.d(TAG, "pitch " + orientationManager.getPitch());
		Location  myLocation =  orientationManager.getLocation();
		
		Location blockLocation = superPrayer.getCurrentBlock().location;
		float heading = orientationManager.getHeading();
		float bearing = MathUtils.getBearing(myLocation.getLatitude(), myLocation.getLongitude(), blockLocation.getLatitude(), blockLocation.getLongitude());
		
		float angle = bearing - heading;
		
		if ( angle < 360 ) {
			angle = angle +360;
		} if ( angle > 360 ) {
			angle = angle -360;
		}
		
		//Log.d(TAG, "angle: " + angle);
		
		if ( angle <= 45 || angle > 315 ) {
			direction.setImageResource(R.drawable.swipe_up);
		} else if ( angle <= 135 && angle > 45 ) {
			direction.setImageResource(R.drawable.swipe_right);
		} else if ( angle <= 225 && angle > 135 ) {
			direction.setImageResource(R.drawable.swipe_down);
		} else {
			direction.setImageResource(R.drawable.swipe_left);
		}
		
	}



	@Override
	public void onLocationChanged(OrientationManager orientationManager) {
		Location  myLocation =  orientationManager.getLocation();
		
		Location blockLocation = superPrayer.getCurrentBlock().location;
		
		float circleDistance = MathUtils.getDistance(myLocation.getLatitude(), myLocation.getLongitude(), blockLocation.getLatitude(), blockLocation.getLongitude());
		Log.d(TAG, "circleDist: " + circleDistance);
		if ( circleDistance < 0.010 ) {
			scoringCommand();
		}
		
	}



	@Override
	public void onAccuracyChanged(OrientationManager orientationManager) {
		// TODO Auto-generated method stub
		
	}
}
