package com.example.marlon.telastask.Pastas;

import com.example.marlon.telastask.User.Users;

import java.util.List;

public class Pastas {

    String pastaId;
    String pastaName;

    public Pastas() {

    }

    public Pastas(String pastaId, String pastaName){
        this.pastaId = pastaId;
        this.pastaName = pastaName;
    }

    public String getPastaId() {
        return pastaId;
    }

    public String getPastaName() {
        return pastaName;
    }
}

