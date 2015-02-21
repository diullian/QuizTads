package com.quiztads.ufpr.br.quiztads;

/**
 * Created by Diego on 18/02/2015.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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

    public void insertData(String name, String lname) {

        ContentValues cv = new ContentValues();
        cv.put(BancoDados.MEMBER_FIRSTNAME, name);
        cv.put(BancoDados.MEMBER_LASTNAME, lname);
        database.insert(BancoDados.TABLE_MEMBER, null, cv);

    }

    public Cursor readEntry() {

        String[] allColumns = new String[] { BancoDados.MEMBER_ID, BancoDados.MEMBER_FIRSTNAME,
                BancoDados.MEMBER_LASTNAME };

        Cursor c = database.query(BancoDados.TABLE_MEMBER, allColumns, null, null, null,
                null, null);

        if (c != null) {
            c.moveToFirst();
        }
        return c;

    }

}
