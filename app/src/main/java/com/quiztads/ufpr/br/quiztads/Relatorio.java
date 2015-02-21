package com.quiztads.ufpr.br.quiztads;

import java.io.Serializable;

/**
 * Created by G0032194 on 21/02/2015.
 */
public class Relatorio implements Serializable {

    public Relatorio(String param, String value) {
        this.param = param;
        this.value = value;
    }

    private String param;

    private String value;

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
