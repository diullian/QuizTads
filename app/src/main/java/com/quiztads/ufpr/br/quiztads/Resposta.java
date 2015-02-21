package com.quiztads.ufpr.br.quiztads;

/**
 * Created by ALVARO, DIEGO E DIULLIAN
 */
public class Resposta {
    public int idResposta;
    public int idPergunta;
    public String resposta;
    public boolean respostaCerta;

    public Resposta(){

    }

    public Resposta(int id, String resposta, boolean correto){
        this.idResposta = id;
        this.resposta = resposta;
        this.respostaCerta = correto;
    }

    public int getIdResposta() {
        return idResposta;
    }

    public void setIdResposta(int idResposta) {
        this.idResposta = idResposta;
    }

    public int getIdPergunta() {
        return idPergunta;
    }

    public void setIdPergunta(int idPergunta) {
        this.idPergunta = idPergunta;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public boolean isRespostaCerta() {
        return respostaCerta;
    }

    public void setRespostaCerta(boolean respostaCerta) {
        this.respostaCerta = respostaCerta;
    }
}
