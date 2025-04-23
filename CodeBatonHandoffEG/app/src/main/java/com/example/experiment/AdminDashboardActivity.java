package com.example.experiment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends Activity {
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize DatabaseManager
        dbManager = new DatabaseManager(this);

        // Find the import button
        Button btnImportCareers = (Button) findViewById(R.id.btnImportCareers);

        // Find the manage recommendations button
        Button btnManageRecommendations = (Button) findViewById(R.id.btnManageRecommendations);

        // Set click listener for import careers button
        btnImportCareers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importCareersFromAssets();
            }
        });

        // Set click listener for manage recommendations button
        btnManageRecommendations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the ManageRecommendationsActivity
                Intent intent = new Intent(AdminDashboardActivity.this, ManageRecommendationsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void logout(View view) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Remove user data
        editor.apply();

        // Restart app at MainActivity (which will send user to LoginActivity)
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void importCareersFromAssets() {
        try {
            // Open CSV file
            InputStream is = getAssets().open("occupation_15_filtered.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            // Read all lines
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            Toast.makeText(this, "Read " + lines.size() + " lines from CSV", Toast.LENGTH_SHORT).show();

            // Clear existing careers
            dbManager.clearCareersTable();

            // Import data
            String[] linesArray = lines.toArray(new String[0]);
            boolean success = dbManager.importCareersFromCSV(linesArray);

            // Check results
            int recordCount = dbManager.getCareerCount();

            if (success) {
                Toast.makeText(this, "Successfully imported " + recordCount + " careers", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error during import. Records found: " + recordCount, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Import error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}