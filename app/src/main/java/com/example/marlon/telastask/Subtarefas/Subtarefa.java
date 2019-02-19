package com.example.marlon.telastask.Subtarefas;

public class Subtarefa {

    String subId;
    String subName;
    String subTexto;


    public Subtarefa() {}

    public Subtarefa(String subId, String subName, String subTexto) {
        this.subId= subId;
        this.subName = subName;
        this.subTexto = subTexto;
    }

    public String getSubId() {
        return subId;
    }

    public String getSubName() {
        return subName;
    }

    public  String getSubTexto(){
        return subTexto;
    }



}
