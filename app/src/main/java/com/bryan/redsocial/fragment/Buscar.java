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
import android.widget.Toast;

import com.bryan.redsocial.Persistencia.UsuarioDAO;
import com.bryan.redsocial.R;
import com.bryan.redsocial.Utilidades.Constantes;
import com.bryan.redsocial.fragment.Entidades.Firebase.Usuario;
import com.bryan.redsocial.fragment.Entidades.Logica.LUsuario;
import com.bryan.redsocial.fragment.adapter_fragments.HolderUsuario;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class Buscar extends Fragment {

    private RecyclerView rvUsuarios;
    private FirebaseRecyclerAdapter adapter;


    public Buscar() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buscar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvUsuarios= view.findViewById(R.id.Buscar_recyclerUsuarios);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvUsuarios.setLayoutManager(linearLayoutManager);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(Constantes.NODO_USUARIOS);

        FirebaseRecyclerOptions<Usuario> options =
                new FirebaseRecyclerOptions.Builder<Usuario>()
                        .setQuery(query, Usuario.class)
                        .build();

            adapter = new FirebaseRecyclerAdapter<Usuario, HolderUsuario>(options) {
            @Override
            public HolderUsuario onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_view_usuarios, parent, false);

                return new HolderUsuario(view);
            }

            @Override
            protected void onBindViewHolder(HolderUsuario holder, int position, Usuario model) {

                LUsuario lUsuario = new LUsuario(model,getSnapshots().getSnapshot(position).getKey());
                Glide.with(getActivity()).load(model.getFotoPerfil()).into(holder.getFotoperfil());
                if (!lUsuario.getKey().equals(UsuarioDAO.getInstance().getKeyUsuario())){
                    holder.getTxtNombreUsuario().setText(model.getNombre());
                }else {
                    holder.getTxtNombreUsuario().setText(model.getNombre()+"(Yo)");
                }
                holder.getLinearPrincipal().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Mensajeria(lUsuario.getKey())).commit();
                       // Toast.makeText(getActivity(), "El usuario es "+model.getNombre(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        rvUsuarios.setAdapter(adapter);

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
}