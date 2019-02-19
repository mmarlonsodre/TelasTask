package com.example.marlon.telastask.Tarefas;

import com.example.marlon.telastask.Subtarefas.Subtarefa;

import java.util.ArrayList;
import java.util.List;

public class Tarefa {

    String tarefaId;
    String tarefaName;
    String tarefaTexto;

    public Tarefa() {

    }

    public Tarefa(String tarefaId, String tarefaName, String tarefaTexto) {
        this.tarefaId= tarefaId;
        this.tarefaName = tarefaName;
        this.tarefaTexto = tarefaTexto;
    }

    public String getTarefaId() {
        return tarefaId;
    }

    public String getTarefaName() {
        return tarefaName;
    }

    public  String getTarefaTexto(){
        return tarefaTexto;
    }
}

