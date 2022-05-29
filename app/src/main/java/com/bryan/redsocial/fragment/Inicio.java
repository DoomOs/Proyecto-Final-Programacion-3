package com.bryan.redsocial.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bryan.redsocial.Persistencia.UsuarioDAO;
import com.bryan.redsocial.R;
import com.bryan.redsocial.Utilidades.Constantes;
import com.bryan.redsocial.fragment.Entidades.Firebase.Publicacion;
import com.bryan.redsocial.fragment.Entidades.Firebase.Usuario;
import com.bryan.redsocial.fragment.Entidades.Logica.LUsuario;
import com.bryan.redsocial.fragment.adapter_fragments.AdapterMensajes;
import com.bryan.redsocial.fragment.adapter_fragments.HolderInicioPublicaciones;
import com.bryan.redsocial.fragment.adapter_fragments.HolderUsuario;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class Inicio extends Fragment {

    private Button btnPublicar;
    private CircleImageView fotoPerfil;
    private RecyclerView rvPublicaciones;
    private FirebaseRecyclerAdapter adapter;
    private DatabaseReference mDatabase;
    public Inicio() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPublicaciones = view.findViewById(R.id.rvPublicaciones);
        btnPublicar = view.findViewById(R.id.Inicio_btnPublicar);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        fotoPerfil = view.findViewById(R.id.Inicio_fotoPerfil);

        obtenerfotoPerfil();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvPublicaciones.setLayoutManager(linearLayoutManager);
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(Constantes.NODO_PUBLICACIONES);

        FirebaseRecyclerOptions<Publicacion> options =
                new FirebaseRecyclerOptions.Builder<Publicacion>()
                        .setQuery(query, Publicacion.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Publicacion, HolderInicioPublicaciones>(options) {
            @Override
            public HolderInicioPublicaciones onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_view_publicaciones, parent, false);

                return new HolderInicioPublicaciones(view);
            }

            @Override
            protected void onBindViewHolder(HolderInicioPublicaciones holder, int position, Publicacion model) {
                //LUsuario lUsuario = new LUsuario(model,getSnapshots().getSnapshot(position).getKey());
                Glide.with(getActivity()).load(model.getFotoPerfil()).into(holder.getFotoPerfil());
                holder.getNombre().setText(model.getNombre());
                if (model.isContieneFoto()==true){
                    holder.getImagenPublicacion().setVisibility(View.VISIBLE);
                    Glide.with(getActivity()).load(model.getUrlFotoPublicacion()).into(holder.getImagenPublicacion());
                }
                if (model.isContieneMensaje()){
                    holder.getTextoPublicacion().setVisibility(View.VISIBLE);
                    holder.getTextoPublicacion().setText(model.getMensaje());
                }

            }
        };
        rvPublicaciones.setAdapter(adapter);


        btnPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Crear_Publicacion()).commit();
            }
        });

    }



    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    public void obtenerfotoPerfil(){
        mDatabase.child(Constantes.NODO_USUARIOS).child(UsuarioDAO.getInstance().getKeyUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                LUsuario lUsuario = new LUsuario(usuario,UsuarioDAO.getInstance().getKeyUsuario());
                Glide.with(getActivity()).load(lUsuario.getUsuario().getFotoPerfil()).into(fotoPerfil);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}