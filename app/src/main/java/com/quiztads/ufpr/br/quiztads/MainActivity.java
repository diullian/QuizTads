package com.quiztads.ufpr.br.quiztads;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    /* Controle da UI */
    private TextView perguntaTextView;
    private TextView respostaTextView;
    private TextView numeroQuestaoTextView;
    private TextView txtResposta; //Para mostrar Correto ou Incorreto
    private TableLayout buttonTableLayout; //Tabela dos botões da resposta

    private Random random; //Gerador de número aleatório
    private Handler handler; //Usada para o delay da próxima pergunta

    /* controle do QUIZ */
    private boolean quizIniciado = false;
    private List<Pergunta> arrayPerguntas; //Array de perguntas, sendo populado do WS
    private int tentativas; //Número de tentativas
    private int totalAcerto; //Número de acertos

    private int intPerguntaAtual; //Numero de pergunta Atual
    private int totalOpcaoResposta = 4;
    private int totalMaxPergunta = 5;
    private ArrayList<Relatorio> relatorio = new ArrayList<Relatorio>();
    int notaFinal = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        arrayPerguntas = new ArrayList<Pergunta>(); //Cria array de perguntas
        buttonTableLayout = (TableLayout) findViewById(R.id.buttonTableLayout);
        numeroQuestaoTextView = (TextView) findViewById(R.id.numeroQuestaoTextView);
        respostaTextView = (TextView) findViewById(R.id.respostaTextView);
        perguntaTextView = (TextView) findViewById(R.id.perguntaTextView);
        random = new Random();
        handler = new Handler();
        /*Fim teste*/

        if(existeConexao(getBaseContext()) && !quizIniciado) {
            resetQuiz();
            quizIniciado = true;
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Falha na internet");

            //Resultado do jogo
            builder.setMessage("ATENÇÃO: Não foi detectado conexão com a Internet!");
            builder.setCancelable(false);
            builder.setPositiveButton("Tentar novamente", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });

            AlertDialog resetDialog = builder.create();
            resetDialog.show();
        }
    }


    public void resetQuiz() {
        tentativas = 0; //Inicializa tentativas como 0
        arrayPerguntas.clear();
        intPerguntaAtual = 1; //Inicia em 1
        respostaTextView.setText("");

        new Thread() {
            public void run() {


                try {
//                String url = "http://quizws.jelastic.websolute.net.br/quizws/service/getRandomQuiz/5";
//                String url = "http://default-environment-jjppvgvpnp.elasticbeanstalk.com/service/getRandomQuiz"; //com objeto quiz : {}
                    //String url = "http://default-environment-jjppvgvpnp.elasticbeanstalk.com/service/getRandomQuiz/5"; //sem objeto quiz {}
                    String url = "http://default-environment-jjppvgvpnp.elasticbeanstalk.com/service/getRandomQuiz";
                    WebService ws = new WebService(url);
                    Map params = new HashMap();
                    String response = ws.webGet("", params);
                    // HttpResponse response = ws.response;

                    InputStream inputStream = null;
                    String result = null;

                    try {
                        JSONArray jsonWS = new JSONArray(response);

                        for (int i = 0; i < jsonWS.length(); i++) {
                            try {
                                JSONObject objPerguntaWS = jsonWS.getJSONObject(i).getJSONObject("pergunta");
                                Pergunta pergunta = new Pergunta();

                                pergunta.setIdPergunta(objPerguntaWS.getInt("idPergunta"));
                                pergunta.setPergunta(objPerguntaWS.getString("pergunta"));
                                ArrayList<Resposta> respostas = new ArrayList<Resposta>();

                                JSONArray arrayRespostas = objPerguntaWS.getJSONArray("respostas");

                                for (int j = 0; j < arrayRespostas.length(); j++) {
                                    JSONObject objRespostaWS = arrayRespostas.getJSONObject(j);
                                    respostas.add(new Resposta(objRespostaWS.getInt("idResposta"), objRespostaWS.getString("resposta"), objRespostaWS.getBoolean("respostaCerta")));
                                    //Log.d(TAG, "obj RESPOSTA ID " + i + " = " + objRespostaWS.getInt("idResposta") + "// correto?" + objRespostaWS.getBoolean("respostaCerta"));
                                }

                                pergunta.setRespostas(respostas);
                                arrayPerguntas.add(pergunta);
                                //Log.d(TAG, "Pergunta do WS " + i + " = " + objPerguntaWS.getString("pergunta"));
                            } catch (JSONException ex) {
                                Log.d(TAG, "JSON ERRO ! " + ex);
                            }
                        }


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                iniciaQuiz();
                            }
                        });

                        //Log.e(TAG, "RESULTADO *** 27 **** = ");

                    } catch (Exception ex) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("WebService indisponível");

                                //Resultado do jogo
                                builder.setMessage("O WebService está indisponível neste momento. Por favor entre em contato conosco que iremos verificar o problema o mais rápido possível: \n Alvaro: (41) 9969-8029 \n Diullian: (41) 9641-9422 \n Diego: (41) 9804-8572");
                                builder.setCancelable(false);

                                builder.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                        System.exit(0);
                                    }
                                });

                                builder.setPositiveButton("Tentar novamente", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = getBaseContext().getPackageManager()
                                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                    }
                                });

                                AlertDialog resetDialog = builder.create();
                                resetDialog.show();
                            }
                        });
                    }

                }catch(Exception ex){
                    Log.d(TAG,"WS desconectado");

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("WebService indisponível");

                            //Resultado do jogo
                            builder.setMessage("O WebService está indisponível neste momento. Por favor entre em contato conosco que iremos verificar imediatamente o problema: \n Alvaro Infante: (41) 9969-8029 \n Diullian: (41) 9641-9422 \n  Diego: (41) 9804-8572");
                            builder.setCancelable(false);

                            builder.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    System.exit(0);
                                }
                            });

                            builder.setPositiveButton("Tentar novamente", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = getBaseContext().getPackageManager()
                                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                }
                            });

                            AlertDialog resetDialog = builder.create();
                            resetDialog.show();
                        }
                    });
                }

            }
        }.start();

    }

    public void iniciaQuiz() {


        numeroQuestaoTextView.setText(
                getResources().getString(R.string.questao) + " " + intPerguntaAtual + " " +
                        getResources().getString(R.string.de) + " " + totalMaxPergunta
        );

        Pergunta objPergunta = arrayPerguntas.get(intPerguntaAtual - 1);
        //Log.e(TAG, "intPerguntaAtual == " + intPerguntaAtual);

        perguntaTextView.setText(objPergunta.pergunta); //Adiciona na UI a pergunta dinâmica
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Limpa os botões da TableRows
        for (int row = 0; row < buttonTableLayout.getChildCount(); ++row) {
            ((TableRow) buttonTableLayout.getChildAt(row)).removeAllViews();
        }

        //popula e infla a UI com as respostas
        for (int row = 0; row < objPergunta.respostas.size(); row++) {
            TableRow currentTableRow = getTableRow(row);

            //Infla button_resposta.xml para criar novos botões
            Button btnNovaPergunta = (Button) inflater.inflate(R.layout.button_resposta, null);

            //Nomeia botões com as respostas
            String strResposta = objPergunta.respostas.get(row).resposta;
            //btnNovaPergunta.setId(objPergunta.respostas.get(row).idResposta); //Pode setar a ID da pergunta mesmo
            btnNovaPergunta.setId(row);
            btnNovaPergunta.setText(strResposta);

            //Registra answerButtonListener para responder aos cliques
            btnNovaPergunta.setOnClickListener(respostaButtonListener);
            currentTableRow.addView(btnNovaPergunta);
        }
    }

    //Chama quando um botao resposta é pressionado
    private View.OnClickListener respostaButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectResposta((Button) v); //Passa o componente selecionado
        }
    };

    //Chama quando usuario escolhe uma resposta
    private void selectResposta(Button respostaButton) {
        //REVISAR LÓGICA, POIS O getId pega ID DA PERGUTNA, certo entao seria o INDICE!
        tentativas++;
        disableButtons(); //Desabilita outros botões de resposta

        //Log.e(TAG, " select resposta = tentativa " + tentativas + " // totalMax = " + totalMaxPergunta);

        Pergunta perguntaAtual = arrayPerguntas.get(intPerguntaAtual - 1);
        int respostaId = respostaButton.getId();

        boolean bolAcertou = perguntaAtual.respostas.get(respostaId).respostaCerta;

//        Log.e(TAG, "resposta ID = " + respostaId);
//        Log.e(TAG, "pergunta atual = " + perguntaAtual.idPergunta);
//        Log.e(TAG, "Acertei? " + bolAcertou);

        if (bolAcertou) {
            respostaTextView.setText("ACERTOU!");
            respostaTextView.setTextColor(getResources().getColor(R.color.correct_answer));
            // Acrescenta na nota final caso a resposta esteja correta.
            notaFinal += 20;
        } else {
            respostaTextView.setText("ERROU!");
            respostaTextView.setTextColor(getResources().getColor(R.color.incorrect_answer));
        }

        // Adiciona o resultado no HashMap.
        relatorio.add(new Relatorio("Questão " + intPerguntaAtual, bolAcertou ? "Correta" : "Incorreta"));


        if (tentativas >= totalMaxPergunta) {

            // Define a nota final.
            relatorio.add(new Relatorio("Nota Final", String.valueOf(notaFinal)));
            //hmRelatorio.put("Nota Final", String.valueOf(notaFinal));

            //for (Relatorio rel : relatorio) {
                //Log.e(TAG, rel.getParam() + rel.getValue());
            //}

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("PARABÉNS!");

            //Resultado do jogo
            builder.setMessage("Você respondeu todas as " + totalMaxPergunta + " perguntas!");
            builder.setCancelable(false);

            builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent it = new Intent(MainActivity.this, RelatorioActivity.class);
                    it.putExtra("relatorio", relatorio);
                    startActivity(it);
                }
            });

            AlertDialog resetDialog = builder.create();
            resetDialog.show();

        } else {

            intPerguntaAtual++;

            //Carrega próxima bandeira após delay de 1 segundo
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    carregaProximaPergunta();
                }
            }, 1000);
        }

    }


    public void carregaProximaPergunta() {

        //Limpa a pergunta
        perguntaTextView.setText("");

        //Limpa resposta(acertou ou errou)
        respostaTextView.setText("");

        //Limpa os botões da TableRows, respostas
        for (int row = 0; row < buttonTableLayout.getChildCount(); ++row) {
            ((TableRow) buttonTableLayout.getChildAt(row)).removeAllViews();
        }

        //Carrega próxima bandeira após delay de 1 segundo
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                iniciaQuiz();
            }
        }, 1000);

    }

    //Desabilita botões de resposta quando a resposta CORRETA é escolhida
    private void disableButtons() {
        for (int row = 0; row < buttonTableLayout.getChildCount(); row++) {
            TableRow tableRow = (TableRow) buttonTableLayout.getChildAt(row);
            for (int i = 0; i < tableRow.getChildCount(); i++) {
                tableRow.getChildAt(i).setEnabled(false);
            }
        }
    }

    private TableRow getTableRow(int row) {
        return (TableRow) buttonTableLayout.getChildAt(row);
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
                        MainActivity.this.finish();
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

    public boolean existeConexao(Context context) {

        Context _context = context;
        ConnectivityManager connectivity = (ConnectivityManager)
                _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo netInfo = connectivity.getActiveNetworkInfo();

            // Se não existe nenhum tipo de conexão retorna false
            if (netInfo == null) {
                return false;
            }

            int netType = netInfo.getType();
            // Verifica se a conexão é do tipo WiFi ou Mobile e
            // retorna true se estiver conectado ou false em
            // caso contrário
            if (netType == ConnectivityManager.TYPE_WIFI ||
                    netType == ConnectivityManager.TYPE_MOBILE) {
                return netInfo.isConnected();

            } else {
                return false;
            }

        } else {
            return false;
        }
    }

}
