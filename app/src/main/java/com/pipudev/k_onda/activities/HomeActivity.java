package com.pipudev.k_onda.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.pipudev.k_onda.Adapters.ViewPagerAdapter;
import com.pipudev.k_onda.R;
import com.pipudev.k_onda.fragments.CameraFragment;
import com.pipudev.k_onda.fragments.ChatsFragment;
import com.pipudev.k_onda.fragments.ContactsFragment;
import com.pipudev.k_onda.fragments.StatusFragment;
import com.pipudev.k_onda.providers.AuthProvider;

/**
 * Clase que implementa el searchBar para poder trabajar con el
 */

public class HomeActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {

    private AuthProvider authProvider;
    private MaterialSearchBar searchBar;
    //para la barra que esta debajo de la busqueda - estas 2 variables
    private TabLayout tabLayout;
    private ViewPager viewPager; //Maneja la animacion y permite desplazarse horizontalmente(swiping) entre fragmentos
    //Los Fragmentos que seran añadidos a la actividad
    private CameraFragment cameraFragment;
    private ChatsFragment chatsFragment;
    private ContactsFragment contactsFragment;
    private StatusFragment statusFragment;
    private int INITAL_FRAGMENT_SHOW = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        authProvider = new AuthProvider();
        searchBar = findViewById(R.id.home_searchBar);
        tabLayout = findViewById(R.id.home_tabLayout);
        viewPager = findViewById(R.id.home_viewPager);

        viewPager.setOffscreenPageLimit(3); // numero de fragmentos
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager()); //clase que creamos , recibe un parametro de objeto
        //FragmentManager (invocamos al metodo getSupportFragmentManager de la libreria FragmentActivity) y este metodo
        // Return the FragmentManager for interacting with fragments associated with this activity.

        //incializacion de las clases
        cameraFragment = new CameraFragment();
        chatsFragment = new ChatsFragment();
        contactsFragment = new ContactsFragment();
        statusFragment = new StatusFragment();

        //añadimos los fragmentos a la actividad
        viewPagerAdapter.addFragments(cameraFragment, "");//vacio por que es solamente el icono de la camara
        viewPagerAdapter.addFragments(chatsFragment, "CHATS");
        viewPagerAdapter.addFragments(statusFragment, "ESTADOS");
        viewPagerAdapter.addFragments(contactsFragment, "CONTACTOS");


        viewPager.setAdapter(viewPagerAdapter);//agregamos el adaptador a la vista
        tabLayout.setupWithViewPager(viewPager);//agregamos la vista al layout
        viewPager.setCurrentItem(INITAL_FRAGMENT_SHOW);//empieza en el fragmento CHATS
        //configuracion para el icono de camara estilo whatsapp (el tab mas pequeño en tamaño que los demas)
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);//agregamos el icono de la camara al tabLayout
        LinearLayout linearLayout = (LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(0);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.weight = 0.5f; //para hacerlo chiquito en tamaño
        linearLayout.setLayoutParams(layoutParams);


        searchBar.setOnSearchActionListener(HomeActivity.this); // listener para la searchBar
        searchBar.inflateMenu(R.menu.main_menu); //referencia al menu creado y lo muestra
        searchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { // listener del menu
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) { //al seleccionar un item del menu desplegable

                switch (item.getItemId()) {
                    case R.id.item_signOut:
                        logOutCurrentUser(); //sale a main actividad
                    case R.id.item_profile:
                        goToProfileUser(); //iniciar profile actividad
                }

                return true;
            }
        });


    }

    private void goToProfileUser() {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void logOutCurrentUser() {
        authProvider.signOutSessionUser();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Metodos que implementa SearchBar
     */

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}