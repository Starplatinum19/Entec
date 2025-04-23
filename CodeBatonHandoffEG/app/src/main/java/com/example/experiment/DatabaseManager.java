package com.example.experiment;

import static com.example.experiment.DatabaseHelper.TABLE_CAREERS;

import android.content.Context;
import android.database.Cursor;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private Context context;

    public DatabaseManager(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    // USER OPERATIONS

    public String authenticateUser(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String role = null;

        Cursor cursor = db.rawQuery(
                "SELECT role FROM users WHERE username = ? AND password = ?",
                new String[]{username, password});

        if (cursor.moveToFirst()) {
            role = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return role;
    }

    public boolean registerUser(String username, String password, String role) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("role", role);

        long result = db.insert("users", null, values);
        db.close();

        return result != -1;
    }

    public boolean checkUserExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM users WHERE username = ?",
                new String[]{username});

        boolean exists = cursor.getCount() > 0;

        cursor.close();
        db.close();
        return exists;
    }

    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int userId = -1;

        Cursor cursor = db.rawQuery(
                "SELECT id FROM users WHERE username = ?",
                new String[]{username});

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return userId;
    }

    // COURSE OPERATIONS

    public Cursor getAllCourses() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT id, title, description FROM courses ORDER BY title",
                null);
    }

    public Cursor searchCourses(String query) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT id, title, description FROM courses WHERE title LIKE ?",
                new String[]{"%" + query + "%"});
    }

    // CAREER OPERATIONS

    public Cursor getAllCareers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT id, occupation_title, occupation_code, employment_2023, " +
                        "employment_percent_change, median_annual_wage, education_work_experience " +
                        "FROM careers ORDER BY occupation_title",
                null);
    }

    public Cursor searchCareers(String query) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT id, occupation_title, occupation_code, employment_2023, " +
                        "employment_percent_change, median_annual_wage, education_work_experience " +
                        "FROM careers WHERE occupation_title LIKE ? OR occupation_code LIKE ?",
                new String[]{"%" + query + "%", "%" + query + "%"});
    }

    public boolean addCareer(String occupationTitle, String occupationCode,
                             int employment2023, float employmentChange,
                             float medianWage, String education) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("occupation_title", occupationTitle);
        values.put("occupation_code", occupationCode);
        values.put("employment_2023", employment2023);
        values.put("employment_percent_change", employmentChange);
        values.put("median_annual_wage", medianWage);
        values.put("education_work_experience", education);

        long result = db.insert("careers", null, values);
        db.close();

        return result != -1;
    }

    // CAREER-COURSE RELATIONSHIP

    // linking occupations with related courses
    public void createCareerCoursesTable() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if the table already exists
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='career_courses'",
                null);

        boolean tableExists = cursor.getCount() > 0;
        cursor.close();

        if (!tableExists) {
            String CREATE_CAREER_COURSES_TABLE = "CREATE TABLE career_courses ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "career_id INTEGER, "
                    + "course_id INTEGER, "
                    + "relevance INTEGER, "
                    + "UNIQUE(career_id, course_id))";
            db.execSQL(CREATE_CAREER_COURSES_TABLE);
        }

        db.close();
    }

    public boolean addRecommendedCourse(int careerId, int courseId, int relevance) {
        // First ensure the table exists
        createCareerCoursesTable();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("career_id", careerId);
        values.put("course_id", courseId);
        values.put("relevance", relevance);

        long result = db.insert("career_courses", null, values);
        db.close();

        return result != -1;
    }

    public boolean removeRecommendedCourse(int careerId, int courseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int result = db.delete("career_courses",
                "career_id = ? AND course_id = ?",
                new String[]{String.valueOf(careerId), String.valueOf(courseId)});

        db.close();
        return result > 0;
    }

    public Cursor getRecommendedCourses(int careerId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT c.id, c.title, c.description, cc.relevance " +
                        "FROM courses c " +
                        "JOIN career_courses cc ON c.id = cc.course_id " +
                        "WHERE cc.career_id = ? " +
                        "ORDER BY cc.relevance DESC",
                new String[]{String.valueOf(careerId)});
    }

    public int getRecommendationCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM career_courses", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
    // CSV IMPORT METHODS

    public boolean importCareersFromCSV(String[] csvLines) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = true;
        int insertCount = 0;

        try {
            db.beginTransaction();

            for (int i = 1; i < csvLines.length; i++) { // Skip header row
                // Use tab as delimiter since your CSV is tab-separated
                String[] values = csvLines[i].split("\t");

                if (values.length >= 6) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("occupation_title", values[0].trim());
                    contentValues.put("occupation_code", values[1].trim());

                    try {
                        contentValues.put("employment_2023",
                                Float.parseFloat(values[2].trim()));
                    } catch (Exception e) {
                        contentValues.put("employment_2023", 0);
                    }

                    try {
                        contentValues.put("employment_percent_change",
                                Float.parseFloat(values[3].trim()));
                    } catch (Exception e) {
                        contentValues.put("employment_percent_change", 0.0f);
                    }

                    try {
                        contentValues.put("median_annual_wage",
                                Float.parseFloat(values[4].trim()));
                    } catch (Exception e) {
                        contentValues.put("median_annual_wage", 0.0f);
                    }

                    contentValues.put("education_work_experience", values[5].trim());

                    long result = db.insert("careers", null, contentValues);
                    if (result != -1) {
                        insertCount++;
                    }
                }
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return success;
    }

    public void clearCareersTable() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CAREERS);
        db.close();
    }

    public int getCareerCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CAREERS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public Cursor getAllRecommendations() {
        createCareerCoursesTable(); // Ensure the table exists
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT cc.id AS _id, " +
                        "ca.occupation_title || ' → ' || co.title AS recommendation_info, " +
                        "'Relevance: ' || cc.relevance || '/10' AS relevance_info " +
                        "FROM career_courses cc " +
                        "JOIN careers ca ON cc.career_id = ca.id " +
                        "JOIN courses co ON cc.course_id = co.id " +
                        "ORDER BY ca.occupation_title, cc.relevance DESC", null);
    }

    public static class CareerIdPair {
        public int id;
        public String title;

        public CareerIdPair(int id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    public List<CareerIdPair> getCareerIdTitlePairs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<CareerIdPair> pairs = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT id, occupation_title FROM careers", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("occupation_title"));
            pairs.add(new CareerIdPair(id, title));
        }

        cursor.close();
        db.close();
        return pairs;
    }

    public boolean courseExists(int courseId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM courses WHERE id = ?",
                new String[]{String.valueOf(courseId)});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    public boolean updateOrAddRecommendedCourse(int careerId, int courseId, int relevance) {
        createCareerCoursesTable(); // Ensure the table exists
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if this recommendation already exists
        Cursor cursor = db.rawQuery(
                "SELECT id FROM career_courses WHERE career_id = ? AND course_id = ?",
                new String[]{String.valueOf(careerId), String.valueOf(courseId)});

        boolean exists = cursor.getCount() > 0;
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("career_id", careerId);
        values.put("course_id", courseId);
        values.put("relevance", relevance);

        boolean success;

        if (exists) {
            // Update existing record
            int result = db.update("career_courses", values,
                    "career_id = ? AND course_id = ?",
                    new String[]{String.valueOf(careerId), String.valueOf(courseId)});
            success = (result > 0);
        } else {
            // Insert new record
            long result = db.insert("career_courses", null, values);
            success = (result != -1);
        }

        db.close();
        return success;

    }

    public boolean deleteRecommendation(int recommendationId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int result = db.delete(
                "career_courses",
                "id = ?",
                new String[]{String.valueOf(recommendationId)});

        db.close();
        return result > 0;
    }
}




