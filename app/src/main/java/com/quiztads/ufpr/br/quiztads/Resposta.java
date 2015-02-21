package com.quiztads.ufpr.br.quiztads;

/**
 * Created by DIULLIAN on 14/02/2015.
 */
public class Resposta {
    public int Id;
    public String Resposta;
    public boolean BolCorreto;

    public Resposta(){

    }

    public Resposta(int id, String resposta, boolean correto){
        this.Id = id;
        this.Resposta = resposta;
        this.BolCorreto = correto;
    }
}
