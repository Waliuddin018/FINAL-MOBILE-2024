package com.example.asli.help;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.asli.api.ApiService;
import com.example.asli.api.DestinationClient;

public class DBHelper extends SQLiteOpenHelper {
    private ApiService apiService;
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "User.db";

    public static class UserEntry {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_LOGGED_IN = "logged_in";
    }

    public static class FavoriteEntry {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_DESTINATION_ID = "destination_id";
    }


    private static final String SQL_CREATE_USER_ENTRIES =
            "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                    UserEntry.COLUMN_NAME_USERNAME + " TEXT PRIMARY KEY," +
                    UserEntry.COLUMN_NAME_PASSWORD + " TEXT," +
                    UserEntry.COLUMN_NAME_LOGGED_IN + " INTEGER DEFAULT 0)";

    private static final String SQL_CREATE_FAVORITE_ENTRIES =
            "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                    FavoriteEntry.COLUMN_NAME_USERNAME + " TEXT," +
                    FavoriteEntry.COLUMN_NAME_DESTINATION_ID + " TEXT," +
                    "PRIMARY KEY (" + FavoriteEntry.COLUMN_NAME_USERNAME + ", " + FavoriteEntry.COLUMN_NAME_DESTINATION_ID + "))";


    private static final String SQL_DELETE_USER_ENTRIES =
            "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;

    private static final String SQL_DELETE_FAVORITE_ENTRIES =
            "DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.apiService = DestinationClient.getClient().create(ApiService.class);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USER_ENTRIES);
        db.execSQL(SQL_CREATE_FAVORITE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_USER_ENTRIES);
        db.execSQL(SQL_DELETE_FAVORITE_ENTRIES);
        onCreate(db);
    }
    @SuppressLint("Range")
    public String getPasswordForUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String password = null;

        Cursor cursor = db.query(UserEntry.TABLE_NAME,
                new String[]{UserEntry.COLUMN_NAME_PASSWORD},
                UserEntry.COLUMN_NAME_USERNAME + " = ?",
                new String[]{username},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            password = cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_NAME_PASSWORD));
            cursor.close();
        }

        return password;
    }
    public boolean updateAkun(String oldUsername, String newUsername, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            if (!oldUsername.equals(newUsername) && isUsernameExists(newUsername)) {
                Log.d("DBHelper", "Username already exists in the database");
                return false;
            }

            ContentValues values = new ContentValues();
            values.put(UserEntry.COLUMN_NAME_USERNAME, newUsername);
            values.put(UserEntry.COLUMN_NAME_PASSWORD, newPassword);


            String selection = UserEntry.COLUMN_NAME_USERNAME + " = ?";
            String[] selectionArgs = {oldUsername};


            int rowsUpdated = db.update(UserEntry.TABLE_NAME, values, selection, selectionArgs);

            ContentValues favoriteValues = new ContentValues();
            favoriteValues.put(FavoriteEntry.COLUMN_NAME_USERNAME, newUsername);
            db.update(FavoriteEntry.TABLE_NAME, favoriteValues, FavoriteEntry.COLUMN_NAME_USERNAME + " = ?", new String[]{oldUsername});

            db.setTransactionSuccessful();

            return rowsUpdated > 0;
        } catch (Exception e) {
            Log.e("DBHelper", "Error updating account", e);
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }


    public boolean updateFavoritesUsername(String oldUsername, String newUsername) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FavoriteEntry.COLUMN_NAME_USERNAME, newUsername);
        String whereClause = FavoriteEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] whereArgs = {oldUsername};
        int rowsUpdated = db.update(FavoriteEntry.TABLE_NAME, values, whereClause, whereArgs);
        db.close();
        return rowsUpdated > 0;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                UserEntry.TABLE_NAME,
                null,
                UserEntry.COLUMN_NAME_USERNAME + " = ?",
                new String[]{username},
                null,
                null,
                null
        );
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public void addFavorite(String username, String destinationId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FavoriteEntry.COLUMN_NAME_USERNAME, username);
        values.put(FavoriteEntry.COLUMN_NAME_DESTINATION_ID, destinationId);
        db.insert(FavoriteEntry.TABLE_NAME, null, values);
    }

    public void removeFavorite(String username, String destinationId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FavoriteEntry.TABLE_NAME,
                FavoriteEntry.COLUMN_NAME_USERNAME + " = ? AND " + FavoriteEntry.COLUMN_NAME_DESTINATION_ID + " = ?",
                new String[]{username, destinationId});
    }

    public boolean isFavorite(String username, String destinationId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(FavoriteEntry.TABLE_NAME,
                null,
                FavoriteEntry.COLUMN_NAME_USERNAME + " = ? AND " + FavoriteEntry.COLUMN_NAME_DESTINATION_ID + " = ?",
                new String[]{username, destinationId},
                null, null, null);
        boolean isFavorite = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return isFavorite;
    }

    public String getLoggedInUser() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String username = null;
        try {
            cursor = db.rawQuery("SELECT " + UserEntry.COLUMN_NAME_USERNAME + " FROM " + UserEntry.TABLE_NAME + " WHERE " + UserEntry.COLUMN_NAME_LOGGED_IN + " = 1", null);
            if (cursor.moveToFirst()) {
                username = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USERNAME));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        Log.d("DBHelper", "Logged in user: " + (username != null ? username : "No user logged in"));
        return username;
    }


    public void logout() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_LOGGED_IN, 0);
        int rowsUpdated = db.update(UserEntry.TABLE_NAME, values, UserEntry.COLUMN_NAME_LOGGED_IN + " = 1", null);
        Log.d("DBHelper", "Logout: Rows affected: " + rowsUpdated);
    }

}
