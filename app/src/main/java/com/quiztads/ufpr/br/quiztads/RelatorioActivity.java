package com.quiztads.ufpr.br.quiztads;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

import java.util.ArrayList;

/**
 *
 */
public class RelatorioActivity extends ActionBarActivity {

    private static final String TAG = "RelatorioActivity";

    private ArrayList<Relatorio> relatorio = new ArrayList<Relatorio>();

    private String score;

    TableLayout tableRelatorio;

    EditText playerName;

    String name;

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

        Intent it = new Intent(this, RankingActivity.class);
        it.putExtra("name", name);
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

            name = playerName.getText().toString();

            if (name == null || "".equalsIgnoreCase(name)) {
                name = "Anônimo";
            }

            // inserting data
            sqlcon.open();
            sqlcon.insertData(name, score);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            PD.dismiss();
        }
    }

    /**
     * Caso aperte o botao voltar, confirma se quer sair do aplicativo
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Deseja realmente sair do Quiz?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RelatorioActivity.this.finish();
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
