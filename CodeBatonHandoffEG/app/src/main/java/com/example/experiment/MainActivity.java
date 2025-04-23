package com.example.experiment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve user role from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userRole = sharedPreferences.getString("userRole", null);

        Intent intent;
        if (userRole == null) {
            // No user is logged in, go to LoginActivity
            intent = new Intent(this, LoginActivity.class);
        } else {
            // Direct users based on their role
            switch (userRole) {
                case "Student":
                    intent = new Intent(this, StudentDashboardActivity.class);
                    break;
                case "Admin":
                    intent = new Intent(this, AdminDashboardActivity.class);
                    break;
                default:
                    intent = new Intent(this, LoginActivity.class);
                    break;
            }
        }

        startActivity(intent);
        finish(); // Close MainActivity to prevent users from coming back
    }
}
