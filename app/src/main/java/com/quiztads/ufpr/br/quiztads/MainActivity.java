package com.quiztads.ufpr.br.quiztads;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Random;
import com.quiztads.ufpr.br.quiztads.WebService;


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
    private ArrayList<Pergunta> arrayPerguntas; //Array de perguntas, sendo populado do WS
    private int tentativas; //Número de tentativas
    private int totalAcerto; //Número de acertos

    private int intPerguntaAtual; //Numero de pergunta Atual
    private int totalOpcaoResposta = 4;
    private int totalMaxPergunta = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonTableLayout = (TableLayout) findViewById(R.id.buttonTableLayout);
        random = new Random();
        handler = new Handler();

        /*Teste WS */


        //String url = "http://quizws.jelastic.websolute.net.br/quizws/service/getRandomQuiz/5";
        //WebService ws = new WebService(url);



        /*Fim teste*/

        resetQuiz();
    }


    public void resetQuiz(){

        tentativas = 0; //Inicializa tentativas como 0
        arrayPerguntas = new ArrayList<Pergunta>(); //Cria array de perguntas
        arrayPerguntas.clear();

        ArrayList<Resposta> resp = new ArrayList<>();
        Pergunta perg1 = new Pergunta(7, "Qual o nome do cara mais viado que vc conhece?");
        resp.add(new Resposta(4,"Robson robinho", false));
        resp.add(new Resposta(7,"Ricardão borracheiro", false));
        resp.add(new Resposta(9,"João Tripé", false));
        resp.add(new Resposta(24,"Alvaro alvinho", true));
        perg1.Respostas = resp;
        arrayPerguntas.add(perg1);

        for (int i = 0; i < 4; i++) {
            boolean achouCorreto = false;
            int randomCorreto = random.nextInt(4);

            Pergunta objPergunta = new Pergunta();
            objPergunta.Id = i;
            objPergunta.Pergunta = "Esta é uma pergunta aleatória [ " + i + " ]";

            ArrayList<Resposta> listaRespostas = new ArrayList<Resposta>();

            for (int j = 0; j < 4; j++) {
                Resposta resposta = new Resposta();
                resposta.Id = j;
                resposta.Resposta = "Res. pergunta " + i + ", id = " + j;

                if(j == randomCorreto && !achouCorreto) {
                    resposta.BolCorreto = true;
                    achouCorreto = true;
                } else {
                    resposta.BolCorreto = false;
                }
                listaRespostas.add(resposta);
            }
            objPergunta.Respostas = listaRespostas;
            arrayPerguntas.add(objPergunta);
        }

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

        perguntaTextView.setText(objPergunta.Pergunta); //Adiciona na UI a pergunta dinâmica
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Limpa os botões da TableRows
        for(int row = 0; row < buttonTableLayout.getChildCount(); ++row) {
            ((TableRow) buttonTableLayout.getChildAt(row)).removeAllViews();
        }

        //popula e infla a UI com as respostas
        for(int row = 0; row < objPergunta.Respostas.size(); row++){
            TableRow currentTableRow = getTableRow(row);

                 //Infla button_resposta.xml para criar novos botões
                 Button btnNovaPergunta = (Button) inflater.inflate(R.layout.button_resposta, null);

                 //Nomeia botões com as respostas
                 String strResposta = objPergunta.Respostas.get(row).Resposta;
                 //btnNovaPergunta.setId(objPergunta.Respostas.get(row).Id); //Pode setar a ID da pergunta mesmo
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
        boolean bolAcertou = perguntaAtual.Respostas.get(respostaId).BolCorreto;

        Log.e(TAG, "Resposta ID = " + respostaId);
        Log.e(TAG, "Pergunta atual = " + perguntaAtual.Id);
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

        //Limpa os botões da TableRows, Respostas
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
