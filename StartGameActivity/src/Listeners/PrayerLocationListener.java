package Listeners;

import com.spaun.glass.prayerapp.BaseGameActivity;

import models.Prayer;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class PrayerLocationListener extends Service implements LocationListener{
	
	Prayer prayer;
	Location previousLocation;
	BaseGameActivity bga;
	final static String TAG = "NATE LOCATION CHANGE";
	public PrayerLocationListener(Prayer p , BaseGameActivity b) {
		prayer = p;	
		bga = b;
	}

	@Override
	public void onLocationChanged(Location location) {
		if (previousLocation == null) {
			previousLocation = location;
		} else {
//			if ( Math.abs(location.getLatitude() - previousLocation.getLatitude()) > 0.00001) {
//				bga.updateDisplay();
//			} else if (Math.abs(location.getLatitude() - previousLocation.getLatitude()) > 0.00001) {
//				bga.updateDisplay();
//			}
		}
		Log.d(TAG, "lat "+ location.getLatitude() +" Long" + location.getLongitude());		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(TAG, "enabling location thing");
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(TAG, "status change");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


}
