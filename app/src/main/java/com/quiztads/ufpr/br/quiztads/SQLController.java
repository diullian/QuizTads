package com.quiztads.ufpr.br.quiztads;

/**
 * Created by Diego on 18/02/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLController {

    private BancoDados dbhelper;
    private Context ourcontext;
    private SQLiteDatabase database;

    public SQLController(Context c) {
        ourcontext = c;
    }

    public SQLController open() throws SQLException {
        dbhelper = new BancoDados(ourcontext);
        database = dbhelper.getWritableDatabase();
        return this;

    }

    public void close() {
        dbhelper.close();
    }

    public void insertData(String name, String score) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();

        ContentValues cv = new ContentValues();
        cv.put(BancoDados.MEMBER_DATE, dateFormat.format(date));
        cv.put(BancoDados.MEMBER_NAME, name);
        cv.put(BancoDados.MEMBER_SCORE, score);
        database.insert(BancoDados.TABLE_MEMBER, null, cv);

    }

    public Cursor readEntry() {

        //String[] allColumns = new String[]{BancoDados.MEMBER_NAME, BancoDados.MEMBER_SCORE};

        //Cursor c = database.query(BancoDados.TABLE_MEMBER, allColumns, null, null, null, null, "score DESC");
        Cursor c = database.rawQuery("SELECT name, score_date, score from players_score order by score desc", null);

        if (c != null) {
            c.moveToFirst();
        }
        return c;

    }

}
