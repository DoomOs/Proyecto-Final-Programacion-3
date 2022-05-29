package com.bryan.redsocial.fragment.adapter_fragments;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bryan.redsocial.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HolderUsuario extends RecyclerView.ViewHolder {
    private CircleImageView fotoperfil;
    private TextView txtNombreUsuario;
    private LinearLayout linearPrincipal;
    public HolderUsuario(@NonNull View itemView) {
        super(itemView);
        fotoperfil = itemView.findViewById(R.id.fotoPerfilUsuario);
        txtNombreUsuario = itemView.findViewById(R.id.nombreUsuario);
        linearPrincipal = itemView.findViewById(R.id.linearLayoutUsuatio);

    }

    public CircleImageView getFotoperfil() {
        return fotoperfil;
    }

    public void setFotoperfil(CircleImageView fotoperfil) {
        this.fotoperfil = fotoperfil;
    }

    public TextView getTxtNombreUsuario() {
        return txtNombreUsuario;
    }

    public void setTxtNombreUsuario(TextView txtNombreUsuario) {
        this.txtNombreUsuario = txtNombreUsuario;
    }

    public LinearLayout getLinearPrincipal() {
        return linearPrincipal;
    }

    public void setLinearPrincipal(LinearLayout linearPrincipal) {
        this.linearPrincipal = linearPrincipal;
    }
}
