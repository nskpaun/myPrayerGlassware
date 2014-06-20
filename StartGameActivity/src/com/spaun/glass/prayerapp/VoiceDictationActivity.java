package com.spaun.glass.prayerapp;

import Helpers.VoiceDemoConstants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.spaun.glass.prayerapp.R;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import java.util.ArrayList;
import java.util.List;

import models.PrayerBlock;


public class VoiceDictationActivity extends Activity
{
	static final String TAG = "VoiceDictation";
	String verse = "";
	String ref = "";
	String comment = "";
    // For tap events
    private GestureDetector mGestureDetector;
    // The main content TextView.
    
    int currentReq = -1;
    
    private TextView verseView = null;
    private TextView referenceView = null;
    private TextView commentView = null;

    @Override
    protected void onStop()
    {
    	Intent intent = new Intent();
    	intent.putExtra(PrayerBlock.VERSE, verse);
    	intent.putExtra(PrayerBlock.REF, ref);
    	intent.putExtra(PrayerBlock.COMM, comment);
    	setResult(RESULT_OK, intent);	
        super.onDestroy();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate() called.");

        setContentView(R.layout.activity_voice_dictation);
        verseView = (TextView) findViewById(R.id.verse_content);
        referenceView = (TextView) findViewById(R.id.ref_content);
        commentView = (TextView) findViewById(R.id.comment_content);

        // For gesture handling.
        mGestureDetector = createGestureDetector(this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == VoiceDemoConstants.VERSE_SPEECH_REQUEST && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            if(spokenText == null) {
                spokenText = "";
            }
            verse = spokenText;
            verseView.setText(spokenText);
        }
        if (requestCode == VoiceDemoConstants.REFERENCE_SPEECH_REQUEST && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            if(spokenText == null) {
                spokenText = "";
            }
            ref = spokenText;
            referenceView.setText(spokenText);
        }
        if (requestCode == VoiceDemoConstants.COMMENT_SPEECH_REQUEST && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            if(spokenText == null) {
                spokenText = "";
            }
            comment = spokenText;
            commentView.setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    ///////////////////////////////////////////////////////
    /// Gesture handling
    //

    @Override
    public boolean onGenericMotionEvent(MotionEvent event)
    {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return super.onGenericMotionEvent(event);
    }

    private GestureDetector createGestureDetector(Context context)
    {
        GestureDetector gestureDetector = new GestureDetector(context);
        //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                Log.d(TAG,"gesture = " + gesture);
                if (gesture == Gesture.TAP) {
                    handleGestureTap();
                    return true;
                }// IS this necessary???

                return false;
            }
        });
        return gestureDetector;
    }

    
    
    private void handleGestureTap()
    {
        Log.d(TAG,"handleGestureTap() called.");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        
        if (currentReq == VoiceDemoConstants.VERSE_SPEECH_REQUEST) {
        	currentReq = VoiceDemoConstants.REFERENCE_SPEECH_REQUEST;
        	startActivityForResult(intent, VoiceDemoConstants.REFERENCE_SPEECH_REQUEST);
        } else if (currentReq == VoiceDemoConstants.REFERENCE_SPEECH_REQUEST) {
        	currentReq = VoiceDemoConstants.COMMENT_SPEECH_REQUEST;
        	startActivityForResult(intent, VoiceDemoConstants.COMMENT_SPEECH_REQUEST);
        } else {
        	currentReq = VoiceDemoConstants.VERSE_SPEECH_REQUEST;
        	startActivityForResult(intent, VoiceDemoConstants.VERSE_SPEECH_REQUEST);
        }
        
//        startActivityForResult(intent, VoiceDemoConstants.REFERENCE_SPEECH_REQUEST);
//        startActivityForResult(intent, VoiceDemoConstants.COMMENT_SPEECH_REQUEST);
    }

}
