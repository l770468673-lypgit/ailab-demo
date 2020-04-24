package com.openailab.ailab.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * @author ZyElite
 */
public class Speaker {
    private TextToSpeech mTextToSpeech;
    private static final String TAG = "Speaker";

    public void speak(String text) {
        mTextToSpeech.stop();
        mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void stop() {
        mTextToSpeech.stop();
        mTextToSpeech.shutdown();
    }

    public Speaker(Context context) {

        mTextToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTextToSpeech.setLanguage(Locale.CHINESE);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.i(TAG, "onInit: " + result);
                    }
//                    result = mTextToSpeech.setLanguage(Locale.ENGLISH);
//                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                        Log.i(TAG, "onInit: " + result);
//                    }
                }
            }
        });
        mTextToSpeech.setSpeechRate(100);
    }

}
