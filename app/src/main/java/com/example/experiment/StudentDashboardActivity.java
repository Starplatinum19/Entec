package com.example.experiment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView;


public class StudentDashboardActivity extends Activity {
    private EditText searchBar;
    private ListView classListView;
    private List<String> availableClasses;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // Initialize UI elements
        searchBar = (EditText)findViewById(R.id.searchBar);
        classListView = (ListView) findViewById(R.id.classListView);

        // Sample class list
        availableClasses = new ArrayList<>();
        availableClasses.add("Intro to AI");
        availableClasses.add("Cybersecurity Basics");
        availableClasses.add("Python for Beginners");
        availableClasses.add("Data Structures");
        availableClasses.add("Networking Fundamentals");

        // Set up ListView adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, availableClasses);
        classListView.setAdapter(adapter);

        // Handle class enrollment
        classListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedClass = availableClasses.get(position);
                enrollInClass(selectedClass);
            }
        });

    }

    // Search Functionality
    public void searchClasses(View view) {
        String query = searchBar.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "Enter a class name to search", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> filteredClasses = new ArrayList<>();
        for (String className : availableClasses) {
            if (className.toLowerCase().contains(query.toLowerCase())) {
                filteredClasses.add(className);
            }
        }

        if (filteredClasses.isEmpty()) {
            Toast.makeText(this, "No classes found", Toast.LENGTH_SHORT).show();
        } else {
            adapter.clear();
            adapter.addAll(filteredClasses);
            adapter.notifyDataSetChanged();
        }
    }


    // Enrollment Functionality
    private void enrollInClass(String className) {
        Toast.makeText(this, "Enrolled in " + className, Toast.LENGTH_SHORT).show();
    }

    // Logout Functionality
    public void logoutUser(View view) {
        // Clear login session
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to login screen
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
