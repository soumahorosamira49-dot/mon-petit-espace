package com.example.monpetitespace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JournalActivity extends AppCompatActivity {

    private LinearLayout tabAccueil, tabJournal, tabHumeur, tabOptions;
    private LinearLayout containerJournal;
    private EditText etRecherche;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_journal);

        preferences = getSharedPreferences("MonPetitEspace", MODE_PRIVATE);

        // Liaison des éléments graphiques
        tabAccueil = findViewById(R.id.tabAccueil);
        tabJournal = findViewById(R.id.tabJournal);
        tabHumeur = findViewById(R.id.tabHumeur);
        tabOptions = findViewById(R.id.tabOptions);
        containerJournal = findViewById(R.id.containerJournal);
        etRecherche = findViewById(R.id.etRecherche);

        // Charger toutes les pensées au démarrage
        chargerJournal("");

        // Écouteur pour la barre de recherche (recherche en direct par mot ou date)
        if (etRecherche != null) {
            etRecherche.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    chargerJournal(s.toString().toLowerCase());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // --- NAVIGATION FLUIDE & SANS REDEMANDER LE PIN ---
        tabAccueil.setOnClickListener(v -> {
            Intent intent = new Intent(JournalActivity.this, MainActivity.class);
            intent.putExtra("dejaVerrouille", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        tabJournal.setOnClickListener(v -> {
            // Déjà sur place ♡
        });

        tabHumeur.setOnClickListener(v -> {
            Intent intent = new Intent(JournalActivity.this, HumeurActivity.class);
            intent.putExtra("dejaVerrouille", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        tabOptions.setOnClickListener(v -> {
            Intent intent = new Intent(JournalActivity.this, OptionsActivity.class);
            intent.putExtra("dejaVerrouille", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    // Méthode ultra-robuste pour charger et filtrer le journal (compatible avec toutes les anciennes clés)
    private void chargerJournal(String filtre) {
        if (containerJournal == null) return;
        containerJournal.removeAllViews();

        try {
            // On vérifie d'abord "liste_pensees", et si c'est vide, on récupère l'ancienne clé "pensées"
            String jsonString = preferences.getString("liste_pensees", "");
            if (jsonString.isEmpty() || jsonString.equals("[]")) {
                jsonString = preferences.getString("pensées", "[]");
            }

            JSONArray jsonArray = new JSONArray(jsonString);
            int nombreAffiches = 0;

            // Parcours de la plus récente à la plus ancienne
            for (int i = jsonArray.length() - 1; i >= 0; i--) {
                Object element = jsonArray.get(i);

                String texte = "";
                String date = "Aujourd'hui";
                boolean estFavori = false;

                // Support à la fois des objets JSON propres et des anciennes chaînes brutes
                if (element instanceof JSONObject) {
                    JSONObject objetPensee = (JSONObject) element;
                    texte = objetPensee.optString("texte", "");
                    date = objetPensee.optString("date", "Aujourd'hui");
                    estFavori = objetPensee.optBoolean("favori", false);
                } else if (element instanceof String) {
                    String brut = (String) element;
                    if (brut.contains("\n\n")) {
                        String[] parties = brut.split("\n\n", 2);
                        date = parties[0];
                        texte = parties[1];
                    } else {
                        texte = brut;
                    }
                }

                // Filtrage si l'utilisatrice tape quelque chose dans la recherche
                if (!filtre.isEmpty() && !texte.toLowerCase().contains(filtre) && !date.toLowerCase().contains(filtre)) {
                    continue;
                }

                nombreAffiches++;

                // On fige les variables pour les rendre effectively final et éviter l'erreur de lambda
                final int currentIndex = i;
                final String currentTexte = texte;
                final String currentDate = date;
                final boolean currentFavori = estFavori;

                View cartePensee = getLayoutInflater().inflate(R.layout.item_pensee, containerJournal, false);

                TextView tvDate = cartePensee.findViewById(R.id.tvDatePensee);
                TextView tvTexte = cartePensee.findViewById(R.id.tvTextePensee);
                TextView btnModifier = cartePensee.findViewById(R.id.btnModifierPensee);
                TextView btnSupprimer = cartePensee.findViewById(R.id.btnSupprimerPensee);
                TextView btnFavori = cartePensee.findViewById(R.id.btnFavoriPensee);

                tvDate.setText(currentDate);
                tvTexte.setText(currentTexte);

                if (btnFavori != null) {
                    btnFavori.setText(currentFavori ? "❤️" : "🤍");
                    btnFavori.setOnClickListener(v -> basculerFavori(currentIndex));
                }

                if (btnSupprimer != null) {
                    btnSupprimer.setOnClickListener(v -> supprimerPensee(currentIndex));
                }
                if (btnModifier != null) {
                    btnModifier.setOnClickListener(v -> modifierPensee(currentIndex, currentTexte));
                }

                containerJournal.addView(cartePensee);
            }

            // Si le journal est vide ou que la recherche ne donne rien
            if (jsonArray.length() == 0 || nombreAffiches == 0) {
                TextView tvVide = new TextView(this);
                if (jsonArray.length() == 0) {
                    tvVide.setText("Ton journal est encore tout vide.\nÉcris ta première pensée sur l'accueil ♡");
                } else {
                    tvVide.setText("Aucune pensée ne correspond à ta recherche... 🔍");
                }
                tvVide.setTextColor(getResources().getColor(android.R.color.darker_gray));
                tvVide.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14f);
                tvVide.setPadding(16, 16, 16, 16);
                containerJournal.addView(tvVide);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Gestion de l'état Favori (❤️ / 🤍)
    private void basculerFavori(int index) {
        try {
            String jsonString = preferences.getString("liste_pensees", preferences.getString("pensées", "[]"));
            JSONArray jsonArray = new JSONArray(jsonString);

            JSONObject objetPensee;
            Object element = jsonArray.get(index);
            if (element instanceof JSONObject) {
                objetPensee = (JSONObject) element;
            } else {
                objetPensee = new JSONObject();
                objetPensee.put("texte", element.toString());
                objetPensee.put("date", "Aujourd'hui");
                objetPensee.put("favori", false);
            }

            boolean etatActuel = objetPensee.optBoolean("favori", false);
            objetPensee.put("favori", !etatActuel);

            jsonArray.put(index, objetPensee);
            preferences.edit().putString("liste_pensees", jsonArray.toString()).apply();

            String filtreActuel = etRecherche != null ? etRecherche.getText().toString().toLowerCase() : "";
            chargerJournal(filtreActuel);

            if (!etatActuel) {
                Toast.makeText(this, "Ajouté à tes favoris ♡", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Retiré des favoris", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void supprimerPensee(int index) {
        try {
            String jsonString = preferences.getString("liste_pensees", preferences.getString("pensées", "[]"));
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONArray nouveauTableau = new JSONArray();

            for (int i = 0; i < jsonArray.length(); i++) {
                if (i != index) {
                    nouveauTableau.put(jsonArray.get(i));
                }
            }

            preferences.edit().putString("liste_pensees", nouveauTableau.toString()).apply();
            preferences.edit().remove("pensées").apply();

            String filtreActuel = etRecherche != null ? etRecherche.getText().toString().toLowerCase() : "";
            chargerJournal(filtreActuel);
            Toast.makeText(JournalActivity.this, "Pensée envolée 🍃", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void modifierPensee(final int index, String ancienTexte) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier ta pensée ♡");

        final EditText input = new EditText(this);
        input.setText(ancienTexte);
        input.setPadding(32, 32, 32, 32);
        builder.setView(input);

        builder.setPositiveButton("Enregistrer", (dialog, which) -> {
            String nouveauTexte = input.getText().toString().trim();
            if (!nouveauTexte.isEmpty()) {
                mettreAJourPensee(index, nouveauTexte);
            }
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void mettreAJourPensee(int index, String nouveauTexte) {
        try {
            String jsonString = preferences.getString("liste_pensees", preferences.getString("pensées", "[]"));
            JSONArray jsonArray = new JSONArray(jsonString);

            JSONObject objetPensee;
            Object element = jsonArray.get(index);
            if (element instanceof JSONObject) {
                objetPensee = (JSONObject) element;
            } else {
                objetPensee = new JSONObject();
                objetPensee.put("date", "Aujourd'hui");
                objetPensee.put("favori", false);
            }

            objetPensee.put("texte", nouveauTexte);
            jsonArray.put(index, objetPensee);

            preferences.edit().putString("liste_pensees", jsonArray.toString()).apply();
            preferences.edit().remove("pensées").apply();

            String filtreActuel = etRecherche != null ? etRecherche.getText().toString().toLowerCase() : "";
            chargerJournal(filtreActuel);
            Toast.makeText(JournalActivity.this, "Pensée modifiée avec douceur ✨", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        View rootView = findViewById(R.id.rootLayoutJournal);
        if (rootView != null) {
            Theme.appliquerTheme(this, rootView);
        }
    }
}
