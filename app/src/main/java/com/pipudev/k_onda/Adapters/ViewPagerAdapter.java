package com.pipudev.k_onda.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase para los fragmentos de la actividad Home que son Chats,Estados,Llamadas
 * usa metodos de FragmentPageAdapter
 * Represents n ScreenSlidePageFragment objects, in sequence.
 */


public class ViewPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> lstFragment = new ArrayList<>(); //es una lista por que no sabes cuantos fragmentos se añadiran y permite flexibilidad
    List<String> lstTitleFragment = new ArrayList<>();

    /**
     * constructor propio de FragmentPagerAdapter
     */
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    /**
     * Agregar los fragmentos a la actividad
     */
    public void addFragments(Fragment fragment, String title) {
        lstFragment.add(fragment);
        lstTitleFragment.add(title);
    }


    /**
     * estos 4 metodos son implementados de FragmentPageAdapter
     * provisionar instancias de ScreenSlidePageFragment como páginas nuevas
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return lstFragment.get(position);
    }

    /**
     * muestra la cantidad de páginas que creará el adaptador
     */
    @Override
    public int getCount() {
        return lstFragment.size();
    }

    /**
     * Sobreescibimos estos 2 metodos que no aparece inicilamente al implementar FragmentPageAdapter
     */
    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    /**
     * Titulo del fragmento
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return lstTitleFragment.get(position);
    }
}
