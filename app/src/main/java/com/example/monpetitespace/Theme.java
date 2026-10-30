package com.example.monpetitespace;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Theme {

    public static void appliquerTheme(Activity activity, View rootView) {
        if (rootView == null) return;

        SharedPreferences preferences = activity.getSharedPreferences("MonPetitEspace", Context.MODE_PRIVATE);
        String couleurAmbiance = preferences.getString("couleurAmbiance", "Rose");
        boolean modeSombre = preferences.getBoolean("modeSombre", false);
        String tailleTexte = preferences.getString("tailleTexte", "Standard");

        // 1. Application de la couleur de fond
        if (modeSombre) {
            rootView.setBackgroundColor(Color.parseColor("#2C2524"));
        } else {
            switch (couleurAmbiance) {
                case "Sauge":
                    rootView.setBackgroundColor(Color.parseColor("#EEF4EE"));
                    break;
                case "Vanille":
                    rootView.setBackgroundColor(Color.parseColor("#FAF4ED"));
                    break;
                case "Rose":
                default:
                    rootView.setBackgroundColor(Color.parseColor("#F7EFE8"));
                    break;
            }
        }

        // 2. Application de la taille du texte sur toute la vue
        appliquerTailleTexteRec(rootView, tailleTexte);
    }

    // Parcours tous les éléments de l'écran pour ajuster les TextView
    private static void appliquerTailleTexteRec(View view, String tailleTexte) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            // Si "Grand", on augmente la taille de 3sp par rapport à sa taille de base, ou un ratio
            float tailleFacteur = tailleTexte.equals("Grand") ? 1.2f : 1.0f;

            // On conserve la taille relative définie au départ
            Object tag = tv.getTag();
            float tailleOriginale;
            if (tag instanceof Float) {
                tailleOriginale = (Float) tag;
            } else {
                tailleOriginale = tv.getTextSize(); // Taille en pixels
                tv.setTag(tailleOriginale); // Sauvegarde de la taille initiale
            }

            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tailleOriginale * tailleFacteur);
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                appliquerTailleTexteRec(group.getChildAt(i), tailleTexte);
            }
        }
    }
}