package com.kastudio.doit;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class DoItContentProvider extends ContentProvider {


    public static final String PROVIDER_NAME = "com.kastudio.doit.DoIdContentProvider";
    public static final Uri ZINCIRI_KIRMA_URI = Uri.parse("content://" + PROVIDER_NAME + "/zincirikirma");
    public static final Uri NOTE_URI = Uri.parse("content://" + PROVIDER_NAME + "/note");
    public static final Uri TO_DO_LIST_URI = Uri.parse("content://" + PROVIDER_NAME + "/todolist");
    public static final Uri KANBAN_TO_DO_URI = Uri.parse("content://" + PROVIDER_NAME + "/kanbantodo");
    public static final Uri KANBAN_DOING_URI = Uri.parse("content://" + PROVIDER_NAME + "/kanbandoing");
    public static final Uri KANBAN_DONE_URI = Uri.parse("content://" + PROVIDER_NAME + "/kanbandone");

    static final int ZINCIRI_KIRMA = 1;
    static final int NOTE = 2;
    static final int TO_DO_LIST = 3;
    static final int KANBAN_TO_DO = 4;
    static final int KANBAN_DOING = 5;
    static final int KANBAN_DONE = 6;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"zincirikirma",ZINCIRI_KIRMA);
        uriMatcher.addURI(PROVIDER_NAME,"note",NOTE);
        uriMatcher.addURI(PROVIDER_NAME,"todolist",TO_DO_LIST);
        uriMatcher.addURI(PROVIDER_NAME,"kanbantodo",KANBAN_TO_DO);
        uriMatcher.addURI(PROVIDER_NAME,"kanbandoing",KANBAN_DOING);
        uriMatcher.addURI(PROVIDER_NAME,"kanbandone",KANBAN_DONE);
    }

    public static final String ZINCIRI_KIRMA_ID = "id";
    public static final String ZINCIRI_KIRMA_NAME = "name";
    public static final String ZINCIRI_KIRMA_DAYS = "days";
    public static final String ZINCIRI_KIRMA_COMPLETED = "completed";
    public static final String ZINCIRI_KIRMA_START = "start";
    public static final String ZINCIRI_KIRMA_LAST = "last";
    public static final String ZINCIRI_KIRMA_FINISH = "finish";
    public static final String ZINCIRI_KIRMA_UUID = "uuid";
    //----------------------------------------------------
    public static final String NOTE_TITLE = "title";
    public static final String NOTE_NOTE = "note";
    public static final String NOTE_COLOR = "color";
    public static final String NOTE_DATE = "date";
    public static final String NOTE_UUID = "uuid";
    //----------------------------------------------------
    public static final String TO_DO_LIST_LIST_NAME = "listName";
    public static final String TO_DO_LIST_COLOR = "color";
    public static final String TO_DO_LIST_TASK = "task";
    public static final String TO_DO_LIST_COMPLETED = "completed";
    public static final String TO_DO_LIST_DATE = "date";
    public static final String TO_DO_LIST_NOTE = "note";
    public static final String TO_DO_LIST_UUID_TASK = "uuidTask";
    public static final String TO_DO_LIST_UUID = "uuid";
    public static final String TO_DO_LIST_REMINDER = "reminder";
    public static final String TO_DO_LIST_REMIND_TIME = "remindTime";
    //-----------------------------------------------------
    public static final String KANBAN_TO_DO_NOTE = "note";
    public static final String KANBAN_TO_DO_COLOR = "color";
    public static final String KANBAN_TO_DO_UUID = "uuid";
    //-----------------------------------------------------
    public static final String KANBAN_DOING_NOTE = "note";
    public static final String KANBAN_DOING_COLOR = "color";
    public static final String KANBAN_DOING_UUID = "uuid";
    //-----------------------------------------------------
    public static final String KANBAN_DONE_NOTE = "note";
    public static final String KANBAN_DONE_COLOR = "color";
    public static final String KANBAN_DONE_UUID = "uuid";

    private static HashMap<String, String> ZINCRICI_KIRMA_PROJECTION_MAP;
    private static HashMap<String, String> NOTE_PROJECTION_MAP;
    private static HashMap<String, String> TO_DO_LIST_PROJECTION_MAP;
    private static HashMap<String, String> KANBAN_TO_DO_PROJECTION_MAP;
    private static HashMap<String, String> KANBAN_DOING_PROJECTION_MAP;
    private static HashMap<String, String> KANBAN_DONE_PROJECTION_MAP;

    //-------------------DATABASE-----------------------

    private SQLiteDatabase sqLiteDatabase;
    static final String DATABASE_NAME = "DoIT";
    static final String ZINCIRI_KIRMA_TABLE_NAME = "zincirikirma";
    static final String NOTE_TABLE_NAME = "note";
    static final String TO_DO_LIST_TABLE_NAME = "todolist";
    static final String KANBAN_TO_DO_TABLE_NAME = "kanbantodo";
    static final String KANBAN_DOING_TABLE_NAME = "kanbandoing";
    static final String KANBAN_DONE_TABLE_NAME = "kanbandone";
    static final int DATABASE_VERSION = 1;
    static final String ZINCIRI_KIRMA_CREATE_DATABASE_TABLE = "CREATE TABLE " +
                        ZINCIRI_KIRMA_TABLE_NAME + "(id INTEGER, name TEXT, " +
                        "days INTEGER, completed BOOLEAN, start INTEGER, last INTEGER, finish BOOLEAN, uuid TEXT);";
    static final String NOTE_CREATE_DATABASE_TABLE = "CREATE TABLE " +
                        NOTE_TABLE_NAME + "(title TEXT, note TEXT, color INTEGER, date TEXT, uuid TEXT);";
    static final String TO_DO_LIST_CREATE_DATABASE_TABLE = "CREATE TABLE " +
                        TO_DO_LIST_TABLE_NAME + "(listName TEXT, color INTEGER, " +
                        "task TEXT, completed BOOLEAN, date TEXT, note TEXT, uuid TEXT, uuidTask TEXT, reminder BOOLEAN, remindTime INTEGER);";
    static final String KANBAN_TO_DO_CREATE_DATABASE_TABLE = "CREATE TABLE " +
                        KANBAN_TO_DO_TABLE_NAME + "(note TEXT, color INTEGER, uuid TEXT);";
    static final String KANBAN_DOING_CREATE_DATABASE_TABLE = "CREATE TABLE " +
            KANBAN_DOING_TABLE_NAME + "(note TEXT, color INTEGER, uuid TEXT);";
    static final String KANBAN_DONE_CREATE_DATABASE_TABLE = "CREATE TABLE " +
            KANBAN_DONE_TABLE_NAME + "(note TEXT, color INTEGER, uuid TEXT);";

    private static class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ZINCIRI_KIRMA_CREATE_DATABASE_TABLE);
            db.execSQL(NOTE_CREATE_DATABASE_TABLE);
            db.execSQL(TO_DO_LIST_CREATE_DATABASE_TABLE);
            db.execSQL(KANBAN_TO_DO_CREATE_DATABASE_TABLE);
            db.execSQL(KANBAN_DOING_CREATE_DATABASE_TABLE);
            db.execSQL(KANBAN_DONE_CREATE_DATABASE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ZINCIRI_KIRMA_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TO_DO_LIST_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + KANBAN_TO_DO_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + KANBAN_DOING_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + KANBAN_DONE_TABLE_NAME);
            onCreate(db);
        }
    }

    //-------------------DATABASE-----------------------

    @Override
    public boolean onCreate() {

        Context context = getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        sqLiteDatabase = databaseHelper.getWritableDatabase();

        return sqLiteDatabase != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(ZINCIRI_KIRMA_TABLE_NAME);
        sqLiteQueryBuilder.setTables(NOTE_TABLE_NAME);
        sqLiteQueryBuilder.setTables(TO_DO_LIST_TABLE_NAME);
        sqLiteQueryBuilder.setTables(KANBAN_TO_DO_TABLE_NAME);
        sqLiteQueryBuilder.setTables(KANBAN_DOING_TABLE_NAME);
        sqLiteQueryBuilder.setTables(KANBAN_DONE_TABLE_NAME);

        Cursor cursor = null;

        switch (uriMatcher.match(uri)){

            case ZINCIRI_KIRMA:
                sqLiteQueryBuilder.setProjectionMap(ZINCRICI_KIRMA_PROJECTION_MAP);
                cursor = sqLiteDatabase.query(ZINCIRI_KIRMA_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case NOTE:
                sqLiteQueryBuilder.setProjectionMap(NOTE_PROJECTION_MAP);
                cursor = sqLiteDatabase.query(NOTE_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case TO_DO_LIST:
                sqLiteQueryBuilder.setProjectionMap(TO_DO_LIST_PROJECTION_MAP);
                cursor = sqLiteDatabase.query(TO_DO_LIST_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case KANBAN_TO_DO:
                sqLiteQueryBuilder.setProjectionMap(KANBAN_TO_DO_PROJECTION_MAP);
                cursor = sqLiteDatabase.query(KANBAN_TO_DO_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case KANBAN_DOING:
                sqLiteQueryBuilder.setProjectionMap(KANBAN_DOING_PROJECTION_MAP);
                cursor = sqLiteDatabase.query(KANBAN_DOING_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case KANBAN_DONE:
                sqLiteQueryBuilder.setProjectionMap(KANBAN_DONE_PROJECTION_MAP);
                cursor = sqLiteDatabase.query(KANBAN_DONE_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
        }

        if (sortOrder == null || sortOrder.matches("")){
            sortOrder = ZINCIRI_KIRMA_NAME;
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        Uri newUri = null;

        switch (uriMatcher.match(uri)){

            case ZINCIRI_KIRMA:
                long rowID1 = sqLiteDatabase.insert(ZINCIRI_KIRMA_TABLE_NAME,"",values);

                if (rowID1 > 0) {
                    newUri = ContentUris.withAppendedId(ZINCIRI_KIRMA_URI,rowID1);
                    getContext().getContentResolver().notifyChange(newUri,null);
                    return newUri;
                }
                break;
            case NOTE:
                long rowID2 = sqLiteDatabase.insert(NOTE_TABLE_NAME,"",values);

                if (rowID2 > 0) {
                    newUri = ContentUris.withAppendedId(NOTE_URI,rowID2);
                    getContext().getContentResolver().notifyChange(newUri,null);
                    return newUri;
                }
                break;
            case TO_DO_LIST:
                long rowID3 = sqLiteDatabase.insert(TO_DO_LIST_TABLE_NAME,"",values);

                if (rowID3 > 0) {
                    newUri = ContentUris.withAppendedId(TO_DO_LIST_URI,rowID3);
                    getContext().getContentResolver().notifyChange(newUri,null);
                    return newUri;
                }
                break;
            case KANBAN_TO_DO:
                long rowID4 = sqLiteDatabase.insert(KANBAN_TO_DO_TABLE_NAME,"",values);

                if (rowID4 > 0) {
                    newUri = ContentUris.withAppendedId(KANBAN_TO_DO_URI,rowID4);
                    getContext().getContentResolver().notifyChange(newUri,null);
                    return newUri;
                }
                break;
            case KANBAN_DOING:
                long rowID5 = sqLiteDatabase.insert(KANBAN_DOING_TABLE_NAME,"",values);

                if (rowID5 > 0) {
                    newUri = ContentUris.withAppendedId(KANBAN_DOING_URI,rowID5);
                    getContext().getContentResolver().notifyChange(newUri,null);
                    return newUri;
                }
                break;
            case KANBAN_DONE:
                long rowID6 = sqLiteDatabase.insert(KANBAN_DONE_TABLE_NAME,"",values);

                if (rowID6 > 0) {
                    newUri = ContentUris.withAppendedId(KANBAN_DONE_URI,rowID6);
                    getContext().getContentResolver().notifyChange(newUri,null);
                    return newUri;
                }
                break;
        }
        throw new SQLException("Error!");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int rowCount = 0;

        switch (uriMatcher.match(uri)){
            case ZINCIRI_KIRMA:
                rowCount = sqLiteDatabase.delete(ZINCIRI_KIRMA_TABLE_NAME,selection,selectionArgs);
                break;
            case NOTE:
                rowCount = sqLiteDatabase.delete(NOTE_TABLE_NAME,selection,selectionArgs);
                break;
            case TO_DO_LIST:
                rowCount = sqLiteDatabase.delete(TO_DO_LIST_TABLE_NAME,selection,selectionArgs);
                break;
            case KANBAN_TO_DO:
                rowCount = sqLiteDatabase.delete(KANBAN_TO_DO_TABLE_NAME,selection,selectionArgs);
                break;
            case KANBAN_DOING:
                rowCount = sqLiteDatabase.delete(KANBAN_DOING_TABLE_NAME,selection,selectionArgs);
                break;
            case KANBAN_DONE:
                rowCount = sqLiteDatabase.delete(KANBAN_DONE_TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Failed URI");
        }
        getContext().getContentResolver().notifyChange(uri,null );

        return rowCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int rowCount = 0;

        switch (uriMatcher.match(uri)){

            case ZINCIRI_KIRMA:
                rowCount = sqLiteDatabase.update(ZINCIRI_KIRMA_TABLE_NAME,values,selection,selectionArgs);
                break;
            case NOTE:
                rowCount = sqLiteDatabase.update(NOTE_TABLE_NAME,values,selection,selectionArgs);
                break;
            case TO_DO_LIST:
                rowCount = sqLiteDatabase.update(TO_DO_LIST_TABLE_NAME,values,selection,selectionArgs);
                break;
            case KANBAN_TO_DO:
                rowCount = sqLiteDatabase.update(KANBAN_TO_DO_TABLE_NAME,values,selection,selectionArgs);
                break;
            case KANBAN_DOING:
                rowCount = sqLiteDatabase.update(KANBAN_DOING_TABLE_NAME,values,selection,selectionArgs);
                break;
            case KANBAN_DONE:
                rowCount = sqLiteDatabase.update(KANBAN_DONE_TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Failed URI");
        }
        getContext().getContentResolver().notifyChange(uri,null );

        return rowCount;    }
}
