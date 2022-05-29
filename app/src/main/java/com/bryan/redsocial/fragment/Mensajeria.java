package com.bryan.redsocial.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bryan.redsocial.Persistencia.MensajeriaDAO;
import com.bryan.redsocial.Persistencia.UsuarioDAO;
import com.bryan.redsocial.R;
import com.bryan.redsocial.Utilidades.Constantes;
import com.bryan.redsocial.fragment.Entidades.Firebase.Mensaje;
import com.bryan.redsocial.fragment.Entidades.Firebase.Usuario;
import com.bryan.redsocial.fragment.Entidades.Logica.LMensaje;
import com.bryan.redsocial.fragment.Entidades.Logica.LUsuario;
import com.bryan.redsocial.fragment.adapter_fragments.AdapterMensajes;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class Mensajeria extends Fragment {

    private CircleImageView fotoPerfil;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;
    private ImageButton btnEnviarFoto;
    private AdapterMensajes adapter;
    private String fotoPerfilCadena;

    private FirebaseDatabase database;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    private String Nombre_usuario;
    private String KEY_RECEPTOR;

    private  Uri fotoReceptor;

    private static final int PHOTO_SEND = 1;
    private static final int PHOTO_PERFIL = 2;

    public Mensajeria(String key) {
        // Required empty public constructor
        this.KEY_RECEPTOR = key;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mensajes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        verifyStoragePermissions(getActivity());
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();


        fotoPerfil = (CircleImageView) view.findViewById(R.id.fotoPerfil);
        nombre = (TextView) view.findViewById(R.id.nombre);
        rvMensajes = (RecyclerView) view.findViewById(R.id.rvMensajes);
        txtMensaje = (EditText) view.findViewById(R.id.txtMensaje);
        btnEnviar = (Button) view.findViewById(R.id.btnEnviar);
        btnEnviarFoto = (ImageButton) view.findViewById(R.id.btnEnviarFoto);
        fotoPerfilCadena ="";


        adapter = new AdapterMensajes(getContext());
        LinearLayoutManager l = new LinearLayoutManager(getContext());
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mensajeEnviar = txtMensaje.getText().toString();
                if (!mensajeEnviar.isEmpty()){
                    Mensaje mensaje = new Mensaje();
                    mensaje.setMensaje(mensajeEnviar);
                    mensaje.setContieneFoto(false);
                    mensaje.setKeyEmisor(UsuarioDAO.getInstance().getKeyUsuario());
                    MensajeriaDAO.getInstance().nuevoMenjase(UsuarioDAO.getInstance().getKeyUsuario(), KEY_RECEPTOR,mensaje);
                    txtMensaje.setText("");
                }
            }
        });

        btnEnviarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_SEND);
            }
        });

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });

        FirebaseDatabase.getInstance().getReference(Constantes.NODO_MENSAJES).
                child(UsuarioDAO.getInstance().getKeyUsuario()).
                child(KEY_RECEPTOR).addChildEventListener(new ChildEventListener() {

            Map<String, LUsuario> mapUsuariosTemporales = new HashMap<>();


            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                final Mensaje mensaje = snapshot.getValue(Mensaje.class);
                final LMensaje lMensaje = new LMensaje(mensaje,snapshot.getKey());
                final int posicion = adapter.addMensaje(lMensaje);

                if (mapUsuariosTemporales.get(mensaje.getKeyEmisor())!=null){
                    lMensaje.setlUsuario(mapUsuariosTemporales.get(mensaje.getKeyEmisor()));
                    adapter.actualizarMensaje(posicion,lMensaje);


                }else {
                    UsuarioDAO.getInstance().obtenerInformacionUsuarioKey(mensaje.getKeyEmisor(), new UsuarioDAO.IDevolverUsuario() {
                        @Override
                        public void devolverUsuario(LUsuario lUsuario) {
                            mapUsuariosTemporales.put(mensaje.getKeyEmisor(), lUsuario);
                            lMensaje.setlUsuario(lUsuario);
                            adapter.actualizarMensaje(posicion,lMensaje);
                        }
                        @Override
                        public void devolverError(String error) {
                            Toast.makeText(getActivity(),"Error: "+error,Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //metodo para dar nombre de que usuario esta logeado
    //esto se ejecuta la inciio del llamado del fragment

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //observacion a futuro
            //si por alguna razon se tarda en cargar el nombre de usuario, se deberia de
            //inhabilitar los botones para mandar mensaje y foto aqui mismo
            //y volverlos a habilitar despues de cargar el nombre de usuario
            DatabaseReference reference = database.getReference("usuarios/"+KEY_RECEPTOR);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Usuario usuario = snapshot.getValue(Usuario.class);

                    fotoReceptor = Uri.parse(usuario.getFotoPerfil());
                    //Toast.makeText(getActivity(), ""+fotoReceptor, Toast.LENGTH_SHORT).show();
                    Nombre_usuario = usuario.getNombre();
                    Glide.with(getActivity()).load(fotoReceptor).into(fotoPerfil);
                    //fotoPerfil.setImageURI(fotoReceptor);
                   // fotoPerfil.setImageResource(R.drawable.anadir_foto);
                    nombre.setText(Nombre_usuario);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else{
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Login()).commit();
        }
    }




    //para que el chat sea scroleable
    private void  setScrollbar(){
        rvMensajes.scrollToPosition(adapter.getItemCount()-1);
    }

    public static boolean verifyStoragePermissions(Activity activity) {
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int REQUEST_EXTERNAL_STORAGE = 1;
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }else{
            return true;
        }
    }
    //seleccionar imagen y mandarla
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PHOTO_SEND && resultCode== RESULT_OK){
            Uri u = data.getData();
            storageReference = storage.getReference("imagenes_chat");//imagens del chat
            final  StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());

            fotoReferencia.putFile(u).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return fotoReferencia.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri uri = task.getResult();
                        Mensaje mensaje = new Mensaje();
                        mensaje.setMensaje(Nombre_usuario+" te ha enviado una foto");
                        mensaje.setUrlFoto(uri.toString());
                        mensaje.setContieneFoto(true);
                        mensaje.setKeyEmisor(UsuarioDAO.getInstance().getKeyUsuario());
                        MensajeriaDAO.getInstance().nuevoMenjase(UsuarioDAO.getInstance().getKeyUsuario(), KEY_RECEPTOR,mensaje);

                    }
                }
            });

        }/*else if (requestCode==PHOTO_PERFIL && resultCode== RESULT_OK){


            Uri u = data.getData();
            storageReference = storage.getReference("foto_perfil");//imagens del chat
            final  StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());

            fotoReferencia.putFile(u).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return fotoReferencia.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri uri = task.getResult();
                        fotoPerfilCadena = uri.toString();
                        MensajeEmisor m = new MensajeEmisor(Nombre_usuario+" ha actualizado su foto de perfil",uri.toString(), Nombre_usuario,fotoPerfilCadena,"2", ServerValue.TIMESTAMP);
                        databaseReference.push().setValue(m);
                        Glide.with(getContext()).load(u.toString()).into(fotoPerfil);
                    }
                }
            });
        }*/
    }
}