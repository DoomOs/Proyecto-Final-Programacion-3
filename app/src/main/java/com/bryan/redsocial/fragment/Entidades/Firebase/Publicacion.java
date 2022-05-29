package com.bryan.redsocial.fragment.Entidades.Firebase;

public class Publicacion {

    private String urlFotoPublicacion;
    private boolean contieneFoto;
    private String mensaje;
    private boolean contieneMensaje;
    private String nombre;
    private String fotoPerfil;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getUrlFotoPublicacion() {
        return urlFotoPublicacion;
    }

    public void setUrlFotoPublicacion(String urlFotoPublicacion) {
        this.urlFotoPublicacion = urlFotoPublicacion;
    }

    public boolean isContieneFoto() {
        return contieneFoto;
    }

    public void setContieneFoto(boolean contieneFoto) {
        this.contieneFoto = contieneFoto;
    }

    public boolean isContieneMensaje() {
        return contieneMensaje;
    }

    public void setContieneMensaje(boolean contieneMensaje) {
        this.contieneMensaje = contieneMensaje;
    }
}
