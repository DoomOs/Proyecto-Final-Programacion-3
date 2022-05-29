package com.bryan.redsocial.fragment.Entidades.Logica;

import com.bryan.redsocial.Persistencia.UsuarioDAO;
import com.bryan.redsocial.fragment.Entidades.Firebase.Usuario;
import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LUsuario {
    private Usuario usuario;
    private String key;

    public LUsuario(Usuario usuario, String key) {
        this.usuario = usuario;
        this.key = key;
    }


    public String obtenerFechaDeCreacion(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(UsuarioDAO.getInstance().fechaDeCreacionLong());
        return simpleDateFormat.format(date);
    }

    public String obtenerFechaDeUltomoLogeo(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(UsuarioDAO.getInstance().fechaUltimoLogeoLong());
        return simpleDateFormat.format(date);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getKey() {
        return key;
    }
}
