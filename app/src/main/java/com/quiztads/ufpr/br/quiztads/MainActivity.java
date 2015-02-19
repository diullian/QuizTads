package com.quiztads.ufpr.br.quiztads;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

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
    private List<Pergunta> arrayPerguntas; //Array de perguntas, sendo populado do WS
    private int tentativas; //Número de tentativas
    private int totalAcerto; //Número de acertos

    private int intPerguntaAtual; //Numero de pergunta Atual
    private int totalOpcaoResposta = 4;
    private int totalMaxPergunta = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayPerguntas = new ArrayList<Pergunta>(); //Cria array de perguntas
        buttonTableLayout = (TableLayout) findViewById(R.id.buttonTableLayout);
        random = new Random();
        handler = new Handler();

        /*Teste WS */

        new Thread(){

            public void run(){
                String url = "http://quizws.jelastic.websolute.net.br/quizws/service/getRandomQuiz/5";
                WebService ws = new WebService(url);
                Map params = new HashMap();
                String response = ws.webGet("",params);

                try{

                    //JSONArray arrayJson = new JSONArray(response);
                    JSONObject arrayJson = new JSONObject(response);

                    //String obj = arrayJson.getString("pergunta").toString();
                    //funfou tb
//                    JSONArray json = new JSONArray(response);
//                    JSONObject arrays = json.optJSONObject(0);
//                    Log.e(TAG, "array PAU [2] = " + arrays);


                    //Teste google
                    //GsonBuilder gsonBuilder = new GsonBuilder();
//                    Gson gson = gsonBuilder.create();
//
//                    List<Pergunta> perguntas = new ArrayList<Pergunta>();
//                    perguntas = Arrays.asList(gson.fromJson(response, Pergunta[].class));
//
//                    Type type = new TypeToken<List<Pergunta>>(){}.getType();
//                    List<Pergunta> inpList = new Gson().fromJson(obj,type);
//                    for (int i=0;i<inpList.size();i++) {
//                        Pergunta x = inpList.get(i);
//                        arrayPerguntas.add(x);
//                    }

                }catch(Exception ex){
                    Log.e(TAG," GOOGLE JSON[2]", ex);
                    ex.printStackTrace();
                }

            }
        }.start();




        /*Fim teste*/

        resetQuiz();
    }


    public void resetQuiz(){

        tentativas = 0; //Inicializa tentativas como 0

        //arrayPerguntas.clear();

        ArrayList<Resposta> resp = new ArrayList<>();
        Pergunta perg1 = new Pergunta(7, "Qual o nome do cara mais viado que vc conhece?");
        resp.add(new Resposta(4,"Robson robinho", false));
        resp.add(new Resposta(7,"Ricardão borracheiro", false));
        resp.add(new Resposta(9,"João Tripé", false));
        resp.add(new Resposta(24,"Alvaro alvinho", true));
        perg1.respostas = resp;
        arrayPerguntas.add(perg1);

//        for (int i = 0; i < 4; i++) {
//            boolean achouCorreto = false;
//            int randomCorreto = random.nextInt(4);
//
//            Pergunta objPergunta = new Pergunta();
//            objPergunta.idPergunta = i;
//            objPergunta.pergunta = "Esta é uma pergunta aleatória [ " + i + " ]";
//
//            ArrayList<Resposta> listaRespostas = new ArrayList<Resposta>();
//
//            for (int j = 0; j < 4; j++) {
//                Resposta resposta = new Resposta();
//                resposta.idResposta = j;
//                resposta.resposta = "Res. pergunta " + i + ", id = " + j;
//
//                if(j == randomCorreto && !achouCorreto) {
//                    resposta.respostaCerta = true;
//                    achouCorreto = true;
//                } else {
//                    resposta.respostaCerta = false;
//                }
//                listaRespostas.add(resposta);
//            }
//            objPergunta.respostas = listaRespostas;
//            arrayPerguntas.add(objPergunta);
//        }

        intPerguntaAtual = 1; //Inicia em 1
        respostaTextView = (TextView) findViewById(R.id.respostaTextView);
        perguntaTextView = (TextView) findViewById(R.id.perguntaTextView);

        iniciaQuiz();
    }

    public void iniciaQuiz(){

        numeroQuestaoTextView = (TextView) findViewById(R.id.numeroQuestaoTextView);
        numeroQuestaoTextView.setText(
                getResources().getString(R.string.questao) + " " + intPerguntaAtual +  " " +
                        getResources().getString(R.string.de) + " " + totalMaxPergunta
        );

        Pergunta objPergunta = arrayPerguntas.get(intPerguntaAtual-1);
        Log.e(TAG,"intPerguntaAtual == " + intPerguntaAtual);

        perguntaTextView.setText(objPergunta.pergunta); //Adiciona na UI a pergunta dinâmica
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Limpa os botões da TableRows
        for(int row = 0; row < buttonTableLayout.getChildCount(); ++row) {
            ((TableRow) buttonTableLayout.getChildAt(row)).removeAllViews();
        }

        //popula e infla a UI com as respostas
        for(int row = 0; row < objPergunta.respostas.size(); row++){
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
    private View.OnClickListener respostaButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v){
            selectResposta((Button) v); //Passa o componente selecionado
        }
    };

    //Chama quando usuario escolhe uma resposta
    private void selectResposta(Button respostaButton){
        //REVISAR LÓGICA, POIS O getId pega ID DA PERGUTNA, certo entao seria o INDICE!
        tentativas++;
        disableButtons(); //Desabilita outros botões de resposta

        Log.e(TAG," select resposta = tentativa " + tentativas + " // totalMax = " + totalMaxPergunta);


        Pergunta perguntaAtual = arrayPerguntas.get(intPerguntaAtual - 1);
        int respostaId = respostaButton.getId();
        boolean bolAcertou = perguntaAtual.respostas.get(respostaId).respostaCerta;

        Log.e(TAG, "resposta ID = " + respostaId);
        Log.e(TAG, "pergunta atual = " + perguntaAtual.idPergunta);
        Log.e(TAG, "Acertei? " + bolAcertou);

        if (bolAcertou) {
            respostaTextView.setText("Acertou! :D");
            respostaTextView.setTextColor(getResources().getColor(R.color.correct_answer));
        } else {
            respostaTextView.setText("Errou! :/");
            respostaTextView.setTextColor(getResources().getColor(R.color.incorrect_answer));
        }


        if(tentativas >= totalMaxPergunta){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Finish!");

            //Resultado do jogo
            builder.setMessage("Acabou o quiz, vc ja respondeu todas as " + totalMaxPergunta + " perguntas");
            builder.setCancelable(false);

            //Adiciona botão para Resetar o quiz
            builder.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    resetQuiz();
                }
            });

            AlertDialog resetDialog = builder.create();
            resetDialog.show();

        }else {
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



    public void carregaProximaPergunta(){

        //Limpa a pergunta
        perguntaTextView.setText("");

        //Limpa resposta(acertou ou errou)
        respostaTextView.setText("");

        //Limpa os botões da TableRows, respostas
        for(int row = 0; row < buttonTableLayout.getChildCount(); ++row) {
            ((TableRow) buttonTableLayout.getChildAt(row)).removeAllViews();
        }

        //Carrega próxima bandeira após delay de 1 segundo
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                iniciaQuiz();
            }
        },1000);

    }

    //Desabilita botões de resposta quando a resposta CORRETA é escolhida
    private void disableButtons(){
        for(int row = 0; row < buttonTableLayout.getChildCount();row++){
            TableRow tableRow = (TableRow) buttonTableLayout.getChildAt(row);
            for(int i = 0; i <tableRow.getChildCount(); i++){
                tableRow.getChildAt(i).setEnabled(false);
            }
        }
    }

    private TableRow getTableRow(int row){
        return (TableRow) buttonTableLayout.getChildAt(row);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
