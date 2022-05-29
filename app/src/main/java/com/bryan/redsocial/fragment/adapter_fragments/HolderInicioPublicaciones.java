package com.bryan.redsocial.fragment.adapter_fragments;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bryan.redsocial.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HolderInicioPublicaciones extends RecyclerView.ViewHolder{
    private ImageView imagenPublicacion;
    private CircleImageView fotoPerfil;
    private TextView textoPublicacion;
    private TextView nombre;

    public HolderInicioPublicaciones(@NonNull View itemView) {
        super(itemView);
        imagenPublicacion = itemView.findViewById(R.id.Publicacion_imagen);
        fotoPerfil = itemView.findViewById(R.id.Publicacion_fotoPerfil);
        textoPublicacion = itemView.findViewById(R.id.Publicacion_texto);
        nombre = itemView.findViewById(R.id.Publicacion_nombre);
    }

    public ImageView getImagenPublicacion() {
        return imagenPublicacion;
    }

    public void setImagenPublicacion(ImageView imagenPublicacion) {
        this.imagenPublicacion = imagenPublicacion;
    }

    public CircleImageView getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(CircleImageView fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public TextView getTextoPublicacion() {
        return textoPublicacion;
    }

    public void setTextoPublicacion(TextView textoPublicacion) {
        this.textoPublicacion = textoPublicacion;
    }

    public TextView getNombre() {
        return nombre;
    }

    public void setNombre(TextView nombre) {
        this.nombre = nombre;
    }
}
