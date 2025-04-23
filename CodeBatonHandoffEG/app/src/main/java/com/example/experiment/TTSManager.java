package com.example.experiment;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

public class TTSManager {

    private static final String TAG = "TTSManager";
    private static TTSManager instance;

    private TextToSpeech textToSpeech;
    private boolean isInitialized = false;
    private String queuedText = null;
    private Context context;

    public static TTSManager getInstance(Context context) {
        if (instance == null) {
            instance = new TTSManager(context.getApplicationContext());
        }
        return instance;
    }

    private TTSManager(Context context) {
        this.context = context;

        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    isInitialized = (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED);

                    // Optional: smooth pitch and rate, not volume
                    textToSpeech.setPitch(1.0f);         // Normal pitch
                    textToSpeech.setSpeechRate(1.0f);    // Normal speed

                    if (isInitialized && queuedText != null) {
                        speak(queuedText);
                        queuedText = null;
                    }
                } else {
                    Log.e(TAG, "TTS Initialization failed");
                }
            }
        });
    }

    public void speak(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        if (!isInitialized) {
            queuedText = text;
            Log.w(TAG, "TTS not initialized yet, text queued.");
            return;
        }

        text = expandAbbreviations(text);

        // No volume/speakerphone change here — clean and static-free
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        instance = null;
    }

    private String expandAbbreviations(String input) {
        HashMap<String, String> replacements = new HashMap<String, String>();
        replacements.put("AI", "Artificial Intelligence");
        replacements.put("MDC", "M D C");
        replacements.put("B.S.", "Bachelor of Science");
        replacements.put("A.S.", "Associate of Science");

        for (String key : replacements.keySet()) {
            input = input.replace(key, replacements.get(key));
        }
        return input;
    }
}
