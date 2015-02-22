package com.quiztads.ufpr.br.quiztads;

import java.util.ArrayList;

/**
 * Created by ALVARO, DIEGO E DIULLIAN
 */
public class Pergunta {
    public int idPergunta;
    public String pergunta;
    //public List<resposta> respostas;
    public ArrayList<Resposta> respostas;

    public Pergunta() {

    }

    public int getIdPergunta() {
        return idPergunta;
    }

    public void setIdPergunta(int idPergunta) {
        this.idPergunta = idPergunta;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public ArrayList<Resposta> getRespostas() {
        return respostas;
    }

    public void setRespostas(ArrayList<Resposta> respostas) {
        this.respostas = respostas;
    }

    public Pergunta(int id, String pergunta) {
        this.idPergunta = id;
        this.pergunta = pergunta;
    }

}
