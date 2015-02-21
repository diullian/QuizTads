package com.quiztads.ufpr.br.quiztads;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

/**
 * Created by G0032194 on 21/02/2015.
 */
public class RelatorioActivity extends ActionBarActivity {

    private static final String TAG = "RelatorioActivity";

    private ArrayList<Relatorio> relatorio = new ArrayList<Relatorio>();

    private String score;

    TableLayout tableRelatorio;

    EditText playerName;

    SQLController sqlcon;

    ProgressDialog PD;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatorio);

        Intent intent = getIntent();
        relatorio = (ArrayList<Relatorio>) intent.getSerializableExtra("relatorio");

        tableRelatorio = (TableLayout) findViewById(R.id.tableRelatorio);

        sqlcon = new SQLController(this);

        playerName = (EditText) findViewById(R.id.playerName);

        buildTable();

    }

    private void buildTable() {

        for (Relatorio rel : relatorio) {

            TableRow row = new TableRow(this);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));

            // inner for loop
            for (int z = 0; z < 2; z++) {

                TextView tv = new TextView(this);
                tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
                tv.setBackgroundResource(R.drawable.cell_shape);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(18);
                tv.setPadding(0, 5, 0, 5);

                if (z == 0) {
                    tv.setText(rel.getParam());
                } else {
                    tv.setText(rel.getValue());
                }

                if ("Nota Final".equalsIgnoreCase(rel.getParam())) {
                    score = rel.getValue();
                }

                row.addView(tv);

            }

            tableRelatorio.addView(row);

        }
    }

    public void onClick(View view) {

        new MyAsync().execute();

        Intent it = new Intent(this, BDActivity.class);
        startActivity(it);
    }

    private class MyAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            PD = new ProgressDialog(RelatorioActivity.this);
            PD.setTitle("Please Wait..");
            PD.setMessage("Loading...");
            PD.setCancelable(false);
            PD.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String name = playerName.getText().toString();

            // inserting data
            sqlcon.open();
            sqlcon.insertData(name, score);
            // BuildTable();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            PD.dismiss();
        }
    }

}
