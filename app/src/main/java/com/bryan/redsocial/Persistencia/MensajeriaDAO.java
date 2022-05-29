package com.bryan.redsocial.Persistencia;

import com.bryan.redsocial.Utilidades.Constantes;
import com.bryan.redsocial.fragment.Entidades.Firebase.Mensaje;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;


public class MensajeriaDAO {
    private FirebaseDatabase database;
    private DatabaseReference referenciaMensajeria;

    private static MensajeriaDAO mensajeriaDAO;

    public static MensajeriaDAO getInstance(){
        if(mensajeriaDAO==null){
            mensajeriaDAO= new MensajeriaDAO();
        }
        return mensajeriaDAO;
    }

    public MensajeriaDAO(){
        database = FirebaseDatabase.getInstance();
        referenciaMensajeria = database.getReference(Constantes.NODO_MENSAJES);
       // storage = FirebaseStorage.getInstance();
        //referenceUsuarios= database.getReference(Constantes.NODO_USUARIOS);
        //referenceFotoDePerfil = storage.getReference("Fotos/FotoPerfil/"+getKeyUsuario());
    }

    public void nuevoMenjase(String keyEmisor, String keyReceptor, Mensaje mensaje){
        DatabaseReference referenceEmisor = referenciaMensajeria.child(keyEmisor).child(keyReceptor);
        DatabaseReference referenceReceptor = referenciaMensajeria.child(keyReceptor).child(keyEmisor);
        referenceEmisor.push().setValue(mensaje);
        referenceReceptor.push().setValue(mensaje);

    }
}
