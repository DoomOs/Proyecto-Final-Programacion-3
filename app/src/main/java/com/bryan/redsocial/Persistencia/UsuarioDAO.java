package com.bryan.redsocial.Persistencia;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bryan.redsocial.Utilidades.Constantes;
import com.bryan.redsocial.fragment.Entidades.Firebase.Mensaje;
import com.bryan.redsocial.fragment.Entidades.Firebase.Usuario;
import com.bryan.redsocial.fragment.Entidades.Logica.LUsuario;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UsuarioDAO {

    public  interface IDevolverUsuario{
        public void  devolverUsuario(LUsuario lUsuario);
        public void  devolverError(String error);
    }
    public interface IDevolverUrlFoto{
        public void devolverUrlString(String url);
    }
    private static UsuarioDAO usuarioDAO;

    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference referenceFotoDePerfil;
    private  DatabaseReference referenceUsuarios;

    public UsuarioDAO(){
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        referenceUsuarios= database.getReference(Constantes.NODO_USUARIOS);
        referenceFotoDePerfil = storage.getReference("Fotos/FotoPerfil/"+getKeyUsuario());
    }
    public static UsuarioDAO getInstance(){
        if(usuarioDAO==null){
            usuarioDAO= new UsuarioDAO();
        }
        return usuarioDAO;
    }

    private FirebaseAuth firebaseAuth;

    public  String getKeyUsuario(){
        return FirebaseAuth.getInstance().getUid();
    }

    public long fechaDeCreacionLong(){
        return FirebaseAuth.getInstance().getCurrentUser().getMetadata().getCreationTimestamp();
    }

    public long fechaUltimoLogeoLong(){
        return FirebaseAuth.getInstance().getCurrentUser().getMetadata().getLastSignInTimestamp();
    }

    public  void obtenerInformacionUsuarioKey( String key, IDevolverUsuario iDevolverUsuario){
        referenceUsuarios.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                LUsuario lUsuario = new LUsuario(usuario,key);
                iDevolverUsuario.devolverUsuario(lUsuario);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                iDevolverUsuario.devolverError(error.getMessage());

            }
        });
    }

    public void subirFotoUri(Uri uri, IDevolverUrlFoto iDevolverUrlFoto){
        String nombreFoto = "";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("SSS.ss-mm-hh-dd-MM-yyyy", Locale.getDefault());
        nombreFoto = simpleDateFormat.format(date);

        StorageReference fotoReferencia = referenceFotoDePerfil.child(nombreFoto);

        fotoReferencia.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                    Uri uriFirebase = task.getResult();
                    iDevolverUrlFoto.devolverUrlString(uriFirebase.toString());

                }else{
                    Log.d("ImagenPerfil: ","Error: "+task.getException().getMessage());
                }
            }
        });
    }
}
