package com.example.lab5_1;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Objects;

public class MyProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.example.lab5_1.MyProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/names";
    static final Uri CONTENT_URI = Uri.parse(URL);
    static final String id = "id";
    static final String name = "name";
    static final int uriCode = 1;
    static final UriMatcher uriMatcher;
    private static HashMap<String, String> values;

    // Static block for URI matching
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "names", uriCode);
        uriMatcher.addURI(PROVIDER_NAME, "names/*", uriCode);
    }

    private SQLiteDatabase db;

    // SQL to create the database table
    static final String CREATE_DB_TABLE = "CREATE TABLE " + Constants.TABLE_NAME
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " name TEXT NOT NULL);"; // Fixed AUTOINCREMENT typo

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();

        // Initialize the projection map
        values = new HashMap<>();
        values.put(id, id);
        values.put(name, name);

        return db != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Constants.TABLE_NAME);

        if (uriMatcher.match(uri) == uriCode) {
            qb.setProjectionMap(values);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (sortOrder == null || sortOrder.isEmpty()) {
                sortOrder = name;
            }
        }

        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        if (uriMatcher.match(uri) == uriCode) {
            return "vnd.android.cursor.dir/cte";
        }
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long rowID = db.insert(Constants.TABLE_NAME, null, values); // Changed empty string to null
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count;
        if (uriMatcher.match(uri) == uriCode) {
            count = db.delete(Constants.TABLE_NAME, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int count;
        if (uriMatcher.match(uri) == uriCode) {
            count = db.update(Constants.TABLE_NAME, values, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return count;
    }
}
