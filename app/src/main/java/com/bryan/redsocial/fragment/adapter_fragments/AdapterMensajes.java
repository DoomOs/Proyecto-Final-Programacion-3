package com.bryan.redsocial.fragment.adapter_fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bryan.redsocial.Persistencia.UsuarioDAO;
import com.bryan.redsocial.R;
import com.bryan.redsocial.fragment.Entidades.Logica.LMensaje;
import com.bryan.redsocial.fragment.Entidades.Logica.LUsuario;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class AdapterMensajes extends RecyclerView.Adapter<HolderMensaje> {
    private List<LMensaje>lisMensaje = new ArrayList<>();
    private Context c;

    public AdapterMensajes( Context c) {
        this.c = c;
    }

    public int addMensaje(LMensaje lmensaje){
        lisMensaje.add(lmensaje);
        int posicion = lisMensaje.size()-1;
        notifyItemInserted(lisMensaje.size());
        return  posicion;
    }

    public void actualizarMensaje(int posicion,LMensaje lmensaje){
        lisMensaje.set(posicion,lmensaje);
        notifyItemChanged(posicion);
    }

    @NonNull
    @Override
    public HolderMensaje onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType==1){
            view = LayoutInflater.from(c).inflate(R.layout.card_view_mensajes_emisor,parent,false);
        }else {
            view =LayoutInflater.from(c).inflate(R.layout.card_view_mensajes_receptor,parent,false);
        }
        return new HolderMensaje(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMensaje holder, int position) {
        LMensaje lMensaje = lisMensaje.get(position);
        LUsuario lUsuario = lMensaje.getlUsuario();

        if (lUsuario!=null){
            holder.getNombre().setText(lUsuario.getUsuario().getNombre());
            Glide.with(c).load(lUsuario.getUsuario().getFotoPerfil()).into(holder.getFotoMensajePerfil());
        }


        holder.getMensaje().setText(lMensaje.getMensaje().getMensaje());

        if (lMensaje.getMensaje().isContieneFoto()){
            holder.getFotoMensaje().setVisibility(View.VISIBLE);
            holder.getMensaje().setVisibility(View.VISIBLE);
            Glide.with(c).load(lMensaje.getMensaje().getUrlFoto()).into(holder.getFotoMensaje());
        }else {
            holder.getFotoMensaje().setVisibility(View.GONE);
            holder.getMensaje().setVisibility(View.VISIBLE);
        }




        holder.getHora().setText(lMensaje.fechaDeCreacionMensaje());
    }

    @Override
    public int getItemCount() {
        return lisMensaje.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(lisMensaje.get(position).getlUsuario()!=null){
            if (lisMensaje.get(position).getlUsuario().getKey().equals(UsuarioDAO.getInstance().getKeyUsuario())){
                return 1;
            }else {
                return -1;
            }
        }else {
            return -1;
        }
    }
}
