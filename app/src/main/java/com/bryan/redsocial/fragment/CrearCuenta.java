package com.bryan.redsocial.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bryan.redsocial.Persistencia.UsuarioDAO;
import com.bryan.redsocial.R;
import com.bryan.redsocial.Utilidades.Constantes;
import com.bryan.redsocial.fragment.Entidades.Firebase.Usuario;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class CrearCuenta extends Fragment implements ImagePickerCallback {

    private EditText txtNombre;
    private EditText txtCorreo;
    private EditText txtContrasena;
    private EditText txtConfirmarContrasena;
    private EditText txtFechaNacimiento;
    private RadioButton rdHombre;
    private  RadioButton rdMujer;
    private RadioGroup rgGenero;
    private CircleImageView fotoPerfil;
    private Button btnregistrar;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference referenciaUsuario;
    private long fechaNacimiento;
   // private String path;

    private Uri fotoperfilURI;
    private ImagePicker imagePicker;

    public CrearCuenta() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_crear_cuenta, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        verifyStoragePermissions(getActivity());
        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();

        txtNombre =  view.findViewById(R.id.CrearCuenta_txtNombre);
        txtCorreo =  view.findViewById(R.id.CrearCuenta_txtCorreo);
        txtContrasena =  view.findViewById(R.id.CrearCuenta_txtContrasena);
        txtConfirmarContrasena = view.findViewById(R.id.CrearCuenta_txtConfirmarContrasena);
        btnregistrar=  view.findViewById(R.id.CrearCuenta_btnCrearCuenta);
        txtFechaNacimiento =view.findViewById(R.id.CrearCuenta_txtFechaNac);
        rdHombre = view.findViewById(R.id.CrearCuenta_rdHombre);
        rdMujer = view.findViewById(R.id.CrearCuenta_rdMujer);
        rgGenero = view.findViewById(R.id.CrearCuenta_radioGrup);
        fotoPerfil = view.findViewById(R.id.CrearCuenta_fotoPerfil);

       // imagePicker = new ImagePicker(getActivity());

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageSingle();

            }
        });


        txtFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int anio, int mes, int dia) {

                        Calendar calendarResultado = Calendar.getInstance();
                        calendarResultado.set(Calendar.YEAR,anio);
                        calendarResultado.set(Calendar.MONTH,mes);
                        calendarResultado.set(Calendar.DAY_OF_MONTH,dia);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date date = calendarResultado.getTime();
                        String textofechaNacimiento = simpleDateFormat.format(date);
                        fechaNacimiento = date.getTime();
                        txtFechaNacimiento.setText(textofechaNacimiento);

                    }
                },calendar.get(Calendar.YEAR)-18,calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String correo = txtCorreo.getText().toString();
                final String nombre = txtNombre.getText().toString();

                if (isValidEmail(correo) && validarContrasena() && validarNombre(nombre)){
                    String contrasena=txtContrasena.getText().toString();

                    //crear usuario en firebase
                    mAuth.createUserWithEmailAndPassword(correo, contrasena)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String generoSeleccionado;

                                        if(rdHombre.isChecked()){
                                            generoSeleccionado = "Hombre";
                                        }else{
                                            generoSeleccionado = "Mujer";
                                        }

                                        if(fotoperfilURI !=null){
                                            Toast.makeText(getActivity(),"url imagen "+fotoperfilURI,Toast.LENGTH_SHORT).show();
                                            UsuarioDAO.getInstance().subirFotoUri(fotoperfilURI, new UsuarioDAO.IDevolverUrlFoto() {
                                                @Override
                                                public void devolverUrlString(String url) {
                                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                                    DatabaseReference reference = database.getReference("usuarios").child(currentUser.getUid());
                                                    Toast.makeText(getActivity(),"Registrado con exito"+url,Toast.LENGTH_SHORT).show();
                                                    Usuario usuario = new Usuario();
                                                    usuario.setCorreo(correo);
                                                    usuario.setNombre(nombre);
                                                    usuario.setFechaNacimiento(fechaNacimiento);
                                                    usuario.setGenero(generoSeleccionado);
                                                    usuario.setFotoPerfil(url);


                                                    reference.setValue(usuario);
                                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Login()).commit();
                                                }
                                            });
                                        }else {
                                            Toast.makeText(getActivity(),"Registrado con exito "+fotoperfilURI,Toast.LENGTH_SHORT).show();
                                            Usuario usuario = new Usuario();
                                            usuario.setCorreo(correo);
                                            usuario.setNombre(nombre);
                                            usuario.setFechaNacimiento(fechaNacimiento);
                                            usuario.setGenero(generoSeleccionado);
                                            usuario.setFotoPerfil(Constantes.URL_FOTO_DEFAULT_USUARIO);
                                            FirebaseUser currentUser = mAuth.getCurrentUser();
                                            DatabaseReference reference = database.getReference("usuarios").child(currentUser.getUid());
                                            reference.setValue(usuario);
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Login()).commit();
                                        }

                                    } else {
                                        Toast.makeText(getContext(),"Error al registrarse",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }else {
                    Toast.makeText(getContext(),"Validaciones funcionando",Toast.LENGTH_SHORT).show();
                }

            }
        });

        Glide.with(getActivity()).load(Constantes.URL_FOTO_DEFAULT_USUARIO).into(fotoPerfil);



    }

    public void pickImageSingle() {
        imagePicker = new ImagePicker(this);
        imagePicker.shouldGenerateMetadata(true);
        imagePicker.shouldGenerateThumbnails(true);
        imagePicker.setImagePickerCallback(this);
        imagePicker.pickImage();
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
        
    private final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean validarContrasena (){
        String contrasena, confirmarContrasena;

        contrasena =txtContrasena.getText().toString();
        confirmarContrasena =txtConfirmarContrasena.getText().toString();

        if (contrasena.equals(confirmarContrasena)){

            if (contrasena.length()>=6){
                return  true;
            }else return false;
        }else return false;

    }

    public  boolean validarNombre(String nombre){
        return !nombre.isEmpty();

    }

    @Override
    public void onImagesChosen(List<ChosenImage> list) {
       String path = list.get(0).getOriginalPath();
        fotoperfilURI = Uri.parse(path);
        fotoPerfil.setImageURI(fotoperfilURI);
        Toast.makeText(getActivity(),"Foto cargada " ,Toast.LENGTH_SHORT).show();

        }

    @Override
    public void onError(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }
}