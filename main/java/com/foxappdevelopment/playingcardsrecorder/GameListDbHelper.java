package com.foxappdevelopment.playingcardsrecorder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class GameListDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "GameList.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String DATE_TYPE = " DATETIME";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES_GAME_LISTS = "CREATE TABLE " + GameList.GameListEntry.TABLE_NAME + " (" +
            GameList.GameListEntry._ID + " INTEGER PRIMARY KEY," +
            GameList.GameListEntry.COLUMN_NAME_PLYR_1 + TEXT_TYPE + COMMA_SEP +
            GameList.GameListEntry.COLUMN_NAME_PLYR_2 + TEXT_TYPE + COMMA_SEP +
            GameList.GameListEntry.COLUMN_NAME_PLYR_3 + TEXT_TYPE + COMMA_SEP +
            GameList.GameListEntry.COLUMN_NAME_PLYR_4 + TEXT_TYPE + COMMA_SEP +
            GameList.GameListEntry.COLUMN_NAME_SCORE_1 + INT_TYPE + COMMA_SEP +
            GameList.GameListEntry.COLUMN_NAME_SCORE_2 + INT_TYPE + COMMA_SEP +
            GameList.GameListEntry.COLUMN_NAME_SCORE_3 + INT_TYPE + COMMA_SEP +
            GameList.GameListEntry.COLUMN_NAME_SCORE_4 + INT_TYPE + COMMA_SEP +
            GameList.GameListEntry.COLUMN_NAME_DATE + DATE_TYPE + " )";

    private static final String SQL_CREATE_ENTRIES_ROUNDS = "CREATE TABLE " + Round.RoundEntry.TABLE_NAME + " (" +
            Round.RoundEntry._ID + " INTEGER PRIMARY KEY," +
            Round.RoundEntry.COLUMN_NAME_NUM + INT_TYPE + COMMA_SEP +
            Round.RoundEntry.COLUMN_NAME_POINT1 + INT_TYPE + COMMA_SEP +
            Round.RoundEntry.COLUMN_NAME_POINT2 + INT_TYPE + COMMA_SEP +
            Round.RoundEntry.COLUMN_NAME_POINT3 + INT_TYPE + COMMA_SEP +
            Round.RoundEntry.COLUMN_NAME_POINT4 + INT_TYPE + COMMA_SEP +
            Round.RoundEntry.COLUMN_NAME_PASS1 + INT_TYPE + COMMA_SEP +
            Round.RoundEntry.COLUMN_NAME_PASS2 + INT_TYPE + COMMA_SEP +
            Round.RoundEntry.COLUMN_NAME_PASS3 + INT_TYPE + COMMA_SEP +
            Round.RoundEntry.COLUMN_NAME_PASS4 + INT_TYPE + COMMA_SEP +
            Round.RoundEntry.COLUMN_NAME_FINISHED + INT_TYPE + COMMA_SEP +
            Round.RoundEntry.COLUMN_NAME_GAME_ID + INT_TYPE + COMMA_SEP +
            " FOREIGN KEY(" + Round.RoundEntry.COLUMN_NAME_GAME_ID + ") REFERENCES " + GameList.GameListEntry.TABLE_NAME + "(" + GameList.GameListEntry._ID + ")" + " ON DELETE CASCADE" + " )";

    private static final String SQL_DELETE_ENTRIES_GAME_LIST =
            "DROP TABLE IF EXISTS " + GameList.GameListEntry.TABLE_NAME;

    private static final String SQL_DELETE_ENTRIES_ROUNDS =
            "DROP TABLE IF EXISTS " + Round.RoundEntry.TABLE_NAME;


    public GameListDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_GAME_LISTS);
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_ROUNDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_GAME_LIST);
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_ROUNDS);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
