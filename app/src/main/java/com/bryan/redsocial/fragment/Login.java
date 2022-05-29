package com.bryan.redsocial.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bryan.redsocial.Persistencia.UsuarioDAO;
import com.bryan.redsocial.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends Fragment {
    BottomNavigationView navigation;


    private FirebaseAuth mAuth;

    private EditText txtCorreo, txtContrasena;


    public Login() {

        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //ocultamos la barra de navegacion
        navigation = getActivity().findViewById(R.id.navegacion);
        navigation.setVisibility(View.GONE);
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigation = view.findViewById(R.id.navegacion);
        Button iniciar = view.findViewById(R.id.Login_btnIniciarSesion);
        TextView crearCuenta = view.findViewById(R.id.Login_crearCuenta);
        txtCorreo = view.findViewById(R.id.Login_txtCorreo);
        txtContrasena = view.findViewById(R.id.Login_txtContra);

        mAuth = FirebaseAuth.getInstance();


        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = txtCorreo.getText().toString();
                if (isValidEmail(correo) && validarContrasena()) {
                    String contrasenia = txtContrasena.getText().toString();

                    mAuth.signInWithEmailAndPassword(correo, contrasenia)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //FirebaseUser user = mAuth.getCurrentUser();
                                        navigation = getActivity().findViewById(R.id.navegacion);
                                        navigation.setVisibility(View.VISIBLE);
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Inicio()).commit();
                                    } else {

                                        Toast.makeText(getContext(), "Credenciales incorrectas",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getContext(), "Correo o Contraseña invalidas",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        crearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CrearCuenta()).commit();
            }
        });

    }

    private final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean validarContrasena() {
        String contrasena;
        contrasena = txtContrasena.getText().toString();
        if (contrasena.length() >= 6) {
            return true;
        } else return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(getContext(), "Iniciando Sesion",
                    Toast.LENGTH_SHORT).show();
            navigation = getActivity().findViewById(R.id.navegacion);
           navigation.setVisibility(View.VISIBLE);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Inicio()).commit();

        }



    }
}