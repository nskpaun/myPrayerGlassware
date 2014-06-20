package Helpers;

public final class VoiceDemoConstants
{
    // The name of the "extra" key for VoiceDemo intent service.
    public static final String EXTRA_KEY_VOICE_ACTION = "extra_action";

    // Action commands. (One word)
    public static final String ACTION_START_DICTATION = "dictate";
    public static final String ACTION_STOP_VOICEDEMO = "stop";
    // ...

    // For onActivityResult()
    public static final int VERSE_SPEECH_REQUEST = 0;
    public static final int REFERENCE_SPEECH_REQUEST = 1;
    public static final int COMMENT_SPEECH_REQUEST = 2;
    // ...

}
