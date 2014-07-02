package models;

import com.parse.ParseObject;

import android.location.Location;
import android.util.Log;

public class PrayerBlock {
	
	public static final String PRAYER_BLOCK = "PrayerBlock";
	
	public static final String PARENT = "Parent";
	static final String TAG = "Parent";
	static public final String VERSE = "Verse";
	static public final String REF = "Reference";
	static public final String COMM = "Comment";
	static final String LAT = "Latitude";
	static final String LONG = "Longitude";
	
	public String verse;
	public String reference;
	public String comment;
	
	public Location location;
	
	ParseObject o;
	
	public PrayerBlock() {
		this.location = new Location("Parse");
	}
	
	public static PrayerBlock getBlockFromParse(ParseObject o){
		
		Log.d(TAG,"Getting PB from PARSER");
		
		Log.d(TAG, "Object" + o.toString());
		
		PrayerBlock pb = new PrayerBlock();
		
		pb.verse = o.getString(VERSE);
		pb.reference = o.getString(REF);
		pb.comment = o.getString(COMM);
		pb.location.setLatitude(o.getDouble(LAT));
		pb.location.setLongitude(o.getDouble(LONG));
		pb.o = o;
		
		return pb;
	}
	
	public ParseObject serializeForParse(ParseObject prayer) {
		if ( o == null ) { 
			o = ParseObject.create("PrayerBlock");
		}
		
		o.put(PARENT, prayer);
		o.put(VERSE, verse);
		o.put(REF, reference);
		o.put(COMM, comment);
		o.put(LAT, location.getLatitude());
		o.put(LONG, location.getLongitude());
		
		return o;
	}
	
}
