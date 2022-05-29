package com.bryan.redsocial.fragment;

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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bryan.redsocial.Persistencia.UsuarioDAO;
import com.bryan.redsocial.R;
import com.bryan.redsocial.Utilidades.Constantes;
import com.bryan.redsocial.fragment.Entidades.Firebase.Publicacion;
import com.bryan.redsocial.fragment.Entidades.Firebase.Usuario;
import com.bryan.redsocial.fragment.Entidades.Logica.LUsuario;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Crear_Publicacion extends Fragment implements ImagePickerCallback {


    private ImageView imagenPublicacion;
    private EditText textoPublicacion;
    private Button volver,publicar;

    private ImagePicker imagePicker;

    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference referenceFotoPublicacion;

    private Uri fotoGuardar;
    private String urlFoto;

    private Publicacion publicacion = new Publicacion();
    private Usuario usuario ;
    private boolean foto, mensaje;
    private String fotourl,textomensaje;
    private String nombre ="";
    private String fotoperfil="";


    public Crear_Publicacion() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crear__publicacion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        verifyStoragePermissions(getActivity());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        database= FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        referenceFotoPublicacion = storage.getReference("Fotos/FotoPublicacion");


        obtenerNombreyFoto();
        imagenPublicacion = view.findViewById(R.id.CrearPublicacion_foto);
        textoPublicacion = view.findViewById(R.id.CrearPublicacion_texto);
        volver = view.findViewById(R.id.CrearPublicacion_volver);
        publicar = view.findViewById(R.id.CrearPublicacion_publicar);

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Inicio()).commit();
            }
        });

        imagenPublicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageSingle();
            }
        });

        publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = database.getReference(Constantes.NODO_PUBLICACIONES).push();
                String textoPubli= textoPublicacion.getText().toString();
                if (fotoGuardar !=null){

                    fotourl = urlFoto;
                    foto = true;

                }else {
                    fotourl = "";
                    foto = false;
                }
                if (!textoPubli.isEmpty()){
                    mensaje=true;
                    Log.d("ErrorKey: ",UsuarioDAO.getInstance().getKeyUsuario());

                }else {
                    textoPubli = "";
                    mensaje=true;
                }


                publicacion.setUrlFotoPublicacion(fotourl);
                publicacion.setContieneFoto(foto);
                publicacion.setMensaje(textoPubli);
                publicacion.setContieneMensaje(mensaje);
                publicacion.setNombre(nombre);
                publicacion.setFotoPerfil(fotoperfil);
                textoPublicacion.setText("");
                reference.setValue(publicacion);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Inicio()).commit();
            }
        });

    }
    public void pickImageSingle() {
        imagePicker = new ImagePicker(this);
        imagePicker.shouldGenerateMetadata(true);
        imagePicker.shouldGenerateThumbnails(true);
        imagePicker.setImagePickerCallback(this);
        imagePicker.pickImage();
    }
    @Override
    public void onImagesChosen(List<ChosenImage> list) {

        String path = list.get(0).getOriginalPath();
        fotoGuardar = Uri.parse(path);
        imagenPublicacion.setImageURI(fotoGuardar);
        subirFotoUri(fotoGuardar);
        Toast.makeText(getActivity(),"Foto cargada" ,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String s) {
        Toast.makeText(getActivity(), "Error: "+s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                if (imagePicker == null) {
                    imagePicker = new ImagePicker(this);
                    imagePicker.setImagePickerCallback(this);
                }
                imagePicker.submit(data);

            }
        }
    }


    public void subirFotoUri(Uri uri){
        String nombreFoto = "";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("SSS.ss-mm-hh-dd-MM-yyyy", Locale.getDefault());
        nombreFoto = simpleDateFormat.format(date);
        StorageReference fotoReferencia = referenceFotoPublicacion.child(nombreFoto);
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
                    urlFoto = uriFirebase.toString();

                }else{
                    Log.d("ImagenPerfil: ","Error: "+task.getException().getMessage());
                }
            }
        });
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

public void obtenerNombreyFoto(){
    mDatabase.child(Constantes.NODO_USUARIOS).child(UsuarioDAO.getInstance().getKeyUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Usuario usuario = snapshot.getValue(Usuario.class);
            LUsuario lUsuario = new LUsuario(usuario,UsuarioDAO.getInstance().getKeyUsuario());
            nombre = lUsuario.getUsuario().getNombre();
            fotoperfil = lUsuario.getUsuario().getFotoPerfil();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

}
}