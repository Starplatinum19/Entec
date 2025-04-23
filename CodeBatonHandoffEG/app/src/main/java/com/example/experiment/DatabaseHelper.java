package com.example.experiment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "EntecDB";
    private static final int DATABASE_VERSION = 6;  // bumped from 5 to 6

    public static final String TABLE_USERS           = "users";
    public static final String TABLE_COURSES         = "courses";
    public static final String TABLE_CAREERS         = "careers";
    public static final String TABLE_CAREER_COURSES  = "career_courses";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // users
        db.execSQL(
            "CREATE TABLE " + TABLE_USERS + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT UNIQUE, " +
            "password TEXT, " +
            "role TEXT" +
            ")"
        );

        // courses
        db.execSQL(
            "CREATE TABLE " + TABLE_COURSES + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT UNIQUE, " +
            "description TEXT" +
            ")"
        );

        // careers
        db.execSQL(
            "CREATE TABLE " + TABLE_CAREERS + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "occupation_title TEXT UNIQUE, " +
            "occupation_code TEXT, " +
            "employment_2023 REAL, " +
            "employment_percent_change REAL, " +
            "median_annual_wage REAL, " +
            "education_work_experience TEXT" +
            ")"
        );

        // career_courses join table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS " + TABLE_CAREER_COURSES + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "career_id INTEGER, " +
            "course_id INTEGER, " +
            "relevance INTEGER, " +
            "UNIQUE(career_id, course_id)" +
            ")"
        );

        // seed default data
        db.execSQL("INSERT INTO users (username, password, role) VALUES ('admin', 'adminpass', 'Admin')");
        insertSampleCourses(db);
    }

    private void insertSampleCourses(SQLiteDatabase db) {
        db.execSQL("INSERT INTO courses (title, description) VALUES ('Intro to AI', 'Introduction to artificial intelligence concepts')");
        db.execSQL("INSERT INTO courses (title, description) VALUES ('Cybersecurity Basics', 'Fundamentals of cybersecurity')");
        db.execSQL("INSERT INTO courses (title, description) VALUES ('Python for Beginners', 'Introduction to Python programming')");
        db.execSQL("INSERT INTO courses (title, description) VALUES ('Data Structures', 'Common data structures and algorithms')");
        db.execSQL("INSERT INTO courses (title, description) VALUES ('Networking Fundamentals', 'Basic concepts of computer networking')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop everything
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAREERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAREER_COURSES);
        // recreate
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // treat downgrade same as upgrade
        onUpgrade(db, oldVersion, newVersion);
    }
}
