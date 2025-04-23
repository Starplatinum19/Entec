package com.example.experiment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StudentLandingPage extends Activity {
    private TextView tvWelcome;
    private Button btnCareerInfo;
    private Button btnSearchCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_landing_page);

        // Initialize UI components
        initializeUI();

        // Set welcome message with username
        setWelcomeMessage();
    }

    private void initializeUI() {
        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        btnCareerInfo = (Button) findViewById(R.id.btnCareerInfo);
        btnSearchCourses = (Button) findViewById(R.id.btnSearchCourses);

        // Set up Career Information button
        btnCareerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Career Information Activity
                Intent intent = new Intent(StudentLandingPage.this, CareerInfoActivity.class);
                startActivity(intent);
            }
        });

        // Set up Search Courses button - link to existing activity
        btnSearchCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to your existing StudentDashboardActivity (which handles course search)
                Intent intent = new Intent(StudentLandingPage.this, StudentDashboardActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setWelcomeMessage() {
        // Get username from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "Student");

        // Extract first name (everything before the first space)
        String firstName = username;
        if (username.contains(" ")) {
            firstName = username.substring(0, username.indexOf(" "));
        }

        // Set welcome message with first name
        tvWelcome.setText("Welcome, " + firstName + "!");
    }

    public void logout(View view) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Remove user data
        editor.apply();

        // Return to login screen
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}