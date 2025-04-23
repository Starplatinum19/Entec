package com.example.experiment;



import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;



public class CareerDetailActivity extends Activity {
    private static final String TAG = "CareerDetailActivity";

    // TTS reference (with queued text logic)
    private TTSManager ttsManager;

    // TextViews in your layout
    private TextView tvOccupationName;
    private TextView tvDescription;
    private TextView tvMedianPay;
    private TextView tvEducationRequired;
    private TextView tvJobOutlook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_career_detail);

        // 1) Initialize TTS
        ttsManager = TTSManager.getInstance(this);

        // 2) Find the TextViews
        tvOccupationName = (TextView) findViewById(R.id.tvOccupationName);
        tvDescription = (TextView)findViewById(R.id.tvDescription);
        tvMedianPay = (TextView)findViewById(R.id.tvMedianPay);
        tvEducationRequired = (TextView)findViewById(R.id.tvEducationRequired);
        tvJobOutlook = (TextView) findViewById(R.id.tvJobOutlook);

        try {
            // 3) Retrieve the occupation name passed from the previous screen
            String[] careerData = getIntent().getStringArrayExtra("CAREER_DATA");

            if (careerData == null || careerData.length == 0) {
                Log.e(TAG, "No career data received");
                Toast.makeText(this, "No career details available", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Suppose the occupation's name is in careerData[0]
            String occupationName = careerData[0];

            // 4) Read the row from CSV and store columns in a Map
            Map<String, String> careerFields = readCareerFields(occupationName);

            // 5) Display them in the TextViews (skip empty ones)
            setCareerDetailUI(careerFields);

            // 6) Build a dynamic speech string and speak it
            String infoToSpeak = buildSpeechString(careerFields);
            ttsManager.speak(infoToSpeak);

        } catch (Exception e) {
            Log.e(TAG, "Error displaying career details", e);
            Toast.makeText(this, "Error loading career details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Reads the CSV for the given occupationName and returns a Map of key->value pairs.
     * Keys are human-friendly labels. Values are from CSV columns (if present).
     */
    private Map<String, String> readCareerFields(String occupationName) {
        Map<String, String> fields = new LinkedHashMap<>();

        try {
            InputStream inputStream = getAssets().open("occupation_15_filtered.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Skip header row if present
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                // For tab-separated CSV
                String[] cols = line.split("\t");

                // Safety check for not enough columns
                if (cols.length < 6) continue;

                // Compare first column with our occupationName
                // (Ignore case and trim extra spaces)
                if (cols[0].trim().equalsIgnoreCase(occupationName.trim())) {

                    // We'll store them in the map with descriptive keys:
                    fields.put("Occupation Name", cols[0].trim());

                    // If you have a second column for code, you can store it or not
                    if (cols.length > 1) {
                        fields.put("Occupation Code", cols[1].trim());
                    }

                    if (cols.length > 2) {
                        fields.put("Employment 2023", cols[2].trim());
                    }

                    if (cols.length > 3) {
                        fields.put("Employment % Change", cols[3].trim());
                    }

                    if (cols.length > 4) {
                        fields.put("Median Annual Wage", cols[4].trim());
                    }

                    if (cols.length > 5) {
                        fields.put("Education/Experience", cols[5].trim());
                    }

                    // If your CSV has a 7th column for "Description," do this:
                    // if (cols.length > 6) {
                    //     fields.put("Description", cols[6].trim());
                    // }

                    break; // We found the match, so break out of the loop
                }
            }

            reader.close();
        } catch (Exception e) {
            Log.e(TAG, "Error reading CSV data", e);
        }

        return fields;
    }

    /**
     * Set the TextViews based on the map. Skip or label them as needed.
     */
    private void setCareerDetailUI(Map<String, String> fields) {
        // Clear them first
        tvOccupationName.setText("");
        tvDescription.setText("");
        tvMedianPay.setText("");
        tvEducationRequired.setText("");
        tvJobOutlook.setText("");

        // "Occupation Name"
        if (fields.containsKey("Occupation Name")) {
            tvOccupationName.setText(fields.get("Occupation Name"));
        }

        // We'll treat "Description" as tvDescription if you have it.
        // If you don't have a real description in the CSV, you can skip or set a placeholder
        if (fields.containsKey("Description")) {
            tvDescription.setText("Description: " + fields.get("Description"));
        } else {
            tvDescription.setText("Description: Not provided in CSV");
        }

        // "Median Annual Wage"
        if (fields.containsKey("Median Annual Wage")) {
            tvMedianPay.setText("Median Annual Wage: $" + fields.get("Median Annual Wage"));
        }

        // "Education/Experience"
        if (fields.containsKey("Education/Experience")) {
            tvEducationRequired.setText("Education: " + fields.get("Education/Experience"));
        }

        // "Employment % Change"
        if (fields.containsKey("Employment % Change")) {
            tvJobOutlook.setText("Employment % Change (2023–2033): "
                    + fields.get("Employment % Change") + "%");
        }
    }

    /**
     * Builds a dynamic string from the map for TTS.
     * Skips any fields that are empty. You can customize the format as you like.
     */
    private String buildSpeechString(Map<String, String> fields) {
        // Example: We’ll read out (in order) Occupation Name, Description, Wage, Education, Outlook
        StringBuilder sb = new StringBuilder();

        // If we have "Occupation Name"
        if (fields.containsKey("Occupation Name") && !fields.get("Occupation Name").isEmpty()) {
            sb.append("Occupation name: ").append(fields.get("Occupation Name")).append(". ");
        }

        // If "Description" is in the map
        if (fields.containsKey("Description") && !fields.get("Description").isEmpty()) {
            sb.append("Description: ").append(fields.get("Description")).append(". ");
        }

        // If "Median Annual Wage"
        if (fields.containsKey("Median Annual Wage") && !fields.get("Median Annual Wage").isEmpty()) {
            sb.append("Median Annual Wage: ").append(fields.get("Median Annual Wage")).append(" dollars. ");
        }

        // If "Education/Experience"
        if (fields.containsKey("Education/Experience") && !fields.get("Education/Experience").isEmpty()) {
            sb.append("Education required: ").append(fields.get("Education/Experience")).append(". ");
        }

        // If "Employment % Change"
        if (fields.containsKey("Employment % Change") && !fields.get("Employment % Change").isEmpty()) {
            sb.append("Employment percent change by 2033: ")
                    .append(fields.get("Employment % Change"))
                    .append(" percent. ");
        }

        // You can add or remove keys as you prefer

        return sb.toString().trim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing() && ttsManager != null) {
            ttsManager.shutdown();
        }
    }
}
