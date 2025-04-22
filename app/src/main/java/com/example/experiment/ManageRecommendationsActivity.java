package com.example.experiment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ManageRecommendationsActivity extends Activity {
    private DatabaseManager dbManager;
    private Spinner spinnerCareers;
    private Spinner spinnerCourses;
    private SeekBar seekBarRelevance;
    private TextView tvRelevanceValue;
    private Button btnAddRecommendation;
    private ListView listViewRecommendations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_recommendations);

        // Initialize DatabaseManager
        dbManager = new DatabaseManager(this);

        // Initialize UI components
        initializeUI();

        // Load data
        loadCareersIntoSpinner();
        loadCoursesIntoSpinner();
        loadRecommendations();
    }

    private void initializeUI() {
        spinnerCareers = (Spinner) findViewById(R.id.spinnerCareers);
        spinnerCourses = (Spinner) findViewById(R.id.spinnerCourses);
        seekBarRelevance = (SeekBar) findViewById(R.id.seekBarRelevance);
        tvRelevanceValue = (TextView) findViewById(R.id.tvRelevanceValue);
        btnAddRecommendation = (Button) findViewById(R.id.btnAddRecommendation);
        listViewRecommendations = (ListView) findViewById(R.id.listViewRecommendations);

        // Set up seek bar
        seekBarRelevance.setMax(10);
        seekBarRelevance.setProgress(5); // Set default value to 5
        tvRelevanceValue.setText("5/10");

        seekBarRelevance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRelevanceValue.setText(progress + "/10");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Set up add recommendation button
        btnAddRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrUpdateRecommendation();
            }
        });

        // Set up item click listener for recommendations list
        listViewRecommendations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteConfirmationDialog((int) id);
            }
        });
    }

    private void loadCareersIntoSpinner() {
        List<DatabaseManager.CareerIdPair> careers = dbManager.getCareerIdTitlePairs();

        // Create an adapter to display the career titles in the spinner
        List<String> careerTitles = new ArrayList<>();

        for (DatabaseManager.CareerIdPair career : careers) {
            careerTitles.add(career.title);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, careerTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCareers.setAdapter(adapter);
    }

    private void loadCoursesIntoSpinner() {
        Cursor cursor = dbManager.getAllCourses();

        // Create an adapter to display the course titles in the spinner
        List<String> courseTitles = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                courseTitles.add(title);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, courseTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapter);
    }

    private void addOrUpdateRecommendation() {
        try {
            // Get selected career ID
            int careerPosition = spinnerCareers.getSelectedItemPosition();
            List<DatabaseManager.CareerIdPair> careers = dbManager.getCareerIdTitlePairs();
            int careerId = careers.get(careerPosition).id;

            // Get selected course ID
            int coursePosition = spinnerCourses.getSelectedItemPosition();
            Cursor cursor = dbManager.getAllCourses();
            cursor.moveToPosition(coursePosition);
            int courseId = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();

            // Get relevance from the seek bar
            int relevance = seekBarRelevance.getProgress();

            // Save the recommendation
            boolean success = dbManager.updateOrAddRecommendedCourse(careerId, courseId, relevance);
            if (success) {
                Toast.makeText(this, "Recommendation saved", Toast.LENGTH_SHORT).show();
                loadRecommendations(); // Refresh the list
            } else {
                Toast.makeText(this, "Failed to save recommendation", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadRecommendations() {
        // Retrieve all recommendations from the database
        Cursor cursor = dbManager.getAllRecommendations();

        // Create an adapter to display the recommendations in the list
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                new String[]{"recommendation_info", "relevance_info"},
                new int[]{android.R.id.text1, android.R.id.text2},
                0);

        listViewRecommendations.setAdapter(adapter);
    }

    private void showDeleteConfirmationDialog(final int recId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Recommendation");
        builder.setMessage("Are you sure you want to delete this recommendation?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteRecommendation(recId);
            }
        });

        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void deleteRecommendation(int recommendationId) {
        boolean success = dbManager.deleteRecommendation(recommendationId);

        if (success) {
            Toast.makeText(this, "Recommendation deleted", Toast.LENGTH_SHORT).show();
            loadRecommendations(); // Refresh the list
        } else {
            Toast.makeText(this, "Failed to delete recommendation", Toast.LENGTH_SHORT).show();
        }
    }
}

