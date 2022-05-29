package com.bryan.redsocial;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.bryan.redsocial.fragment.Buscar;
import com.bryan.redsocial.fragment.Inicio;
import com.bryan.redsocial.fragment.Login;
import com.bryan.redsocial.fragment.Mensajeria;
import com.bryan.redsocial.fragment.Menu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    Inicio inicio = new Inicio();
    //Mensajeria mensajes = new Mensajeria();
    Buscar buscar = new Buscar();
    Menu menu = new Menu();
    Login login = new Login();
    BottomNavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = findViewById(R.id.navegacion);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, login).commit();

        navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                switch (item.getItemId()){

                    case R.id.Inicio:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, inicio).commit();
                        return true;

                        /*
                    case R.id.Mensaje:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mensajes).commit();
                        return true;

                         */

                    case R.id.Buscar:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, buscar).commit();
                        return true;

                    case R.id.Menu:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, menu).commit();
                        return true;
                }
                return false;
            }
        });
    }


}