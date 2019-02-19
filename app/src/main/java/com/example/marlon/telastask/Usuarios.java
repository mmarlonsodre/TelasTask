package com.example.marlon.telastask;

public class Usuarios {

    String usuarioId;
    String usuarioUid;
    String usuarioName;
    String usuarioEmail;
    String usuarioPhone;


    public Usuarios() {

    }

    public Usuarios(String usuarioId,  String usuarioUid, String usuarioName, String usuarioEmail, String usuarioPhone) {
        this.usuarioId = usuarioId;
        this.usuarioName = usuarioName;
        this.usuarioPhone = usuarioPhone;
        this.usuarioUid = usuarioUid;
        this.usuarioEmail = usuarioEmail;

    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioName() {
        return usuarioName;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }

    public void setUsuarioName(String usuarioName) {
        this.usuarioName = usuarioName;
    }

    public String getUsuarioPhone() {
        return usuarioPhone;
    }

    public void setUsuarioPhone(String usuarioPhone) {
        this.usuarioPhone = usuarioPhone;
    }

    public String getUsuarioUid() {
        return usuarioUid;
    }

    public void setUsuarioUid(String usuarioUid) {
        this.usuarioUid = usuarioUid;
    }
}

