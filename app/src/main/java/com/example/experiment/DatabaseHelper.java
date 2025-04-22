package com.example.experiment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "EntecDB";
    private static final int DATABASE_VERSION = 5;
    public static final String TABLE_USERS = "users";
    public static final String TABLE_COURSES = "courses";
    public static final String TABLE_CAREERS = "careers";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT UNIQUE, "
                + "password TEXT, "
                + "role TEXT)";
        db.execSQL(CREATE_USERS_TABLE);



        // Create courses table
        String CREATE_COURSES_TABLE = "CREATE TABLE " + TABLE_COURSES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT UNIQUE, "
                + "description TEXT)";
        db.execSQL(CREATE_COURSES_TABLE);

        // Create Careers table
        String CREATE_CAREERS_TABLE = "CREATE TABLE " + TABLE_CAREERS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "occupation_title TEXT UNIQUE, "
                + "occupation_code TEXT, "
                + "employment_2023 REAL, "  // Using REAL instead of INTEGER for decimal values
                + "employment_percent_change REAL, "
                + "median_annual_wage REAL, "
                + "education_work_experience TEXT"
                + ")";
        db.execSQL(CREATE_CAREERS_TABLE);

        // Insert default admin account
        db.execSQL("INSERT INTO users (username, password, role) VALUES ('admin', 'adminpass', 'Admin')");

        // Insert sample courses
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAREERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        onCreate(db);
    }



}


