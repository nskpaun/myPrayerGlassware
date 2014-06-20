package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.location.Location;
import android.provider.Telephony.Mms.Part;
import android.util.Log;

public class Prayer {
	
	public interface PrayerCallbackInterface {
		public void callback(Prayer p);
	}

	public static final String PRAYER = "Prayer";
	
	static final String NAME = "Name";
	static final String TAG = "PRAYER";
	String prayerId;
	public ArrayList<PrayerBlock> blocks;
	Integer currentPrayer; 
	
	ParseObject pObj;
	
	public Prayer(){
		currentPrayer = 0;
		blocks = new ArrayList<PrayerBlock>();
	}
	
	public static void getLastPrayer(final PrayerCallbackInterface pci){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(PRAYER);
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> prayerList, ParseException e) {
		        if (e == null) {
		            pci.callback(getPrayerFromParse(prayerList.get(0)));
		        } else {
		            Log.d(TAG, "Error: " + e.getMessage());
		        }
		    }
		});
	}
	
	public static Prayer getPrayerFromParse(ParseObject o){
		Prayer p = new Prayer();
		p.pObj = o;
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(PrayerBlock.PRAYER_BLOCK);
		query.whereEqualTo(PrayerBlock.PARENT, o);
		try {
			List<ParseObject> list = query.find();
			Log.d(TAG, "got pb list of size: "+list.size());
			for(ParseObject pbo : list) {
				p.blocks.add(PrayerBlock.getBlockFromParse(pbo));
			}
			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			Log.d(TAG, "Error: " + e1.getMessage());
			return p;
		}
		
		Log.d(TAG, "got pb list of size: "+p.blocks.size());
		
		return p;
		
	}
	
	public void saveToParse() {
		if ( pObj == null ) {
			pObj = ParseObject.create(PRAYER);
			pObj.add(NAME, UUID.randomUUID().toString());
		}
		Log.d(TAG,"SAVING Prayer");
		final ParseObject fo = pObj;
		
		fo.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException arg0) {
				for (PrayerBlock b : blocks) {
					Log.d(TAG,"SAVING BLOCKS");
					b.serializeForParse(fo).saveInBackground();
				}
				
			}
		});
		
	}
	
	public static Prayer getDefaultPrayer() {
		Prayer p = new Prayer();
		p.currentPrayer = 0;
		PrayerBlock b = new PrayerBlock();
		b.comment = "hello world - comment";
		b.reference = "Hebrews 5:7";
		b.verse = "Loud cries and tears";
		
		b.location = new Location("");
		b.location.setLatitude(33.4);
		b.location.setLongitude(45.7);
	
		
		p.blocks.add(b);
		
		b = new PrayerBlock();
		b.comment = "cry out - comment";
		b.reference = "Colossians 2:3";
		b.verse = "United";
		
		b.location = new Location("dummy");
		b.location.setLatitude(20.1);
		b.location.setLongitude(35.0);
		
		p.blocks.add(b);
		
		
		
		return p;
		
	}
	
	public void next() {
		currentPrayer = 1;
	}
	
	public PrayerBlock getCurrentBlock() {
		Log.d("SIZE", ""+blocks.size());
		Log.d("INDEX", ""+currentPrayer);
		
		return blocks.get(currentPrayer);
	}
	
	public void addBlock(PrayerBlock b){
		blocks.add(b);
	}

}
