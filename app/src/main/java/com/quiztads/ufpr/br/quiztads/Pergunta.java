package com.quiztads.ufpr.br.quiztads;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by DIULLIAN on 14/02/2015.
 */
public class Pergunta {
    public int Id;
    public String Pergunta;
    //public List<Resposta> Respostas;
    public ArrayList<Resposta> Respostas;

    public Pergunta(){

    }

    public Pergunta(int id, String pergunta){
        this.Id = id;
        this.Pergunta = pergunta;
    }

}
