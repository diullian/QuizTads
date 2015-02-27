package com.quiztads.ufpr.br.quiztads;

/**
 * Created by Diego on 18/02/2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class RankingActivity extends ActionBarActivity {

    private final String TAG = "RankingActivity";

    TableLayout table_layout;

    ProgressDialog PD;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bd_layout);

        Intent intent = getIntent();
        name = (String) intent.getSerializableExtra("name");

        table_layout = (TableLayout) findViewById(R.id.tableLayout1);

        buildRankingTable();

    }

    /**
     * Método chamado ao iniciar a activity para criar a tabela de Ranking, com acesso ao BD.
     */
    private void buildRankingTable() {

        SQLController sqlcon = new SQLController(this);
        sqlcon.open();
        Cursor c = sqlcon.readEntry();

        int rows = c.getCount();
        int cols = c.getColumnCount();

        c.moveToFirst();

        // outer for loop
        for (int i = 0; i < rows; i++) {

            TableRow row = new TableRow(this);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));

            // inner for loop and occults id
            for (int j = 0; j < cols; j++) {

                TextView tv = new TextView(this);
                tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
                tv.setBackgroundResource(R.drawable.cell_shape);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(18);
                tv.setPadding(0, 5, 0, 5);

                tv.setText(c.getString(j));

                row.addView(tv);

            }

            c.moveToNext();

            table_layout.addView(row);

        }
        sqlcon.close();
    }

    /**
     * Método utilizado pelo btnRestart que reinicia a plicação, chamando a MainActivity
     *
     * @param view
     */
    public void onClickRestart(View view) {
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
    }

    @Override
    /**
     * Desabilita botão volta, para não retornar as activities anteriores.
     */
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
