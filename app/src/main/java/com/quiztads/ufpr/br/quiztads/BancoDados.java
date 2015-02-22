package com.quiztads.ufpr.br.quiztads;

/**
 * Created by Diego on 18/02/2015.
 *
 *
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoDados extends SQLiteOpenHelper {

    // TABLE INFORMATTION
    public static final String TABLE_MEMBER = "players_score";
    public static final String MEMBER_ID = "_id";
    public static final String MEMBER_NAME = "name";
    public static final String MEMBER_SCORE = "score";

    // DATABASE INFORMATION
    static final String DB_NAME = "PLAYERS_SCORE.DB";
    static final int DB_VERSION = 1;

    // TABLE CREATION STATEMENT

    private static final String CREATE_TABLE = "create table " + TABLE_MEMBER
            + "(" + MEMBER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MEMBER_NAME + " TEXT NOT NULL ," + MEMBER_SCORE
            + " INT NOT NULL);";

    public BancoDados(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBER);
        onCreate(db);

    }

}