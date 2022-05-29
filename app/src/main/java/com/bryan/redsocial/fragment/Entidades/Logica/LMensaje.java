package com.bryan.redsocial.fragment.Entidades.Logica;

import com.bryan.redsocial.fragment.Entidades.Firebase.Mensaje;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LMensaje {
    private Mensaje mensaje;
    private String key;
    private LUsuario lUsuario;

    public LMensaje(Mensaje mensaje, String key) {
        this.mensaje = mensaje;
        this.key = key;
    }

    public Mensaje getMensaje() {
        return mensaje;
    }

    public void setMensaje(Mensaje mensaje) {
        this.mensaje = mensaje;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getCreatedTimestampLong(){
        return (long) mensaje.getCreatedTimestamp();
    }

    public LUsuario getlUsuario() {
        return lUsuario;
    }

    public void setlUsuario(LUsuario lUsuario) {
        this.lUsuario = lUsuario;
    }

    public String fechaDeCreacionMensaje(){
        Date date = new Date(getCreatedTimestampLong());
        PrettyTime prettyTime = new PrettyTime(new Date(),Locale.getDefault());
        return prettyTime.format(date);


        /*Date date = new Date(getCreatedTimestampLong());
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());//a es pm o am
        return sdf.format(date);

         */
    }



}
