package com.example.monpetitespace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HumeurActivity extends AppCompatActivity {

    private View tabAccueil, tabJournal, tabHumeur, tabOptions;
    private TextView humeurHeureuse, humeurTriste, humeurColere, humeurApaisee, humeurAimee, humeurFatiguee;
    private TextView tvPhraseDouce;
    private EditText etNoteHumeur;
    private Button btnEnregistrerHumeur;
    private LinearLayout containerHistoriqueHumeur;
    private SharedPreferences preferences;
    private String humeurSelectionnee = "😊"; // Humeur par défaut
    private boolean afficherTout = false; // État pour le bouton "Voir plus / Voir moins"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_humeur);

        preferences = getSharedPreferences("MonPetitEspace", MODE_PRIVATE);

        // Liaison de la navigation
        tabAccueil = findViewById(R.id.tabAccueil);
        tabJournal = findViewById(R.id.tabJournal);
        tabHumeur = findViewById(R.id.tabHumeur);
        tabOptions = findViewById(R.id.tabOptions);

        // Liaison des émojis
        humeurHeureuse = findViewById(R.id.humeurHeureuse);
        humeurTriste = findViewById(R.id.humeurTriste);
        humeurColere = findViewById(R.id.humeurColere);
        humeurApaisee = findViewById(R.id.humeurApaisee);
        humeurAimee = findViewById(R.id.humeurAimee);
        humeurFatiguee = findViewById(R.id.humeurFatiguee);

        tvPhraseDouce = findViewById(R.id.tvPhraseDouce);
        etNoteHumeur = findViewById(R.id.etNoteHumeur);
        btnEnregistrerHumeur = findViewById(R.id.btnEnregistrerHumeur);
        containerHistoriqueHumeur = findViewById(R.id.containerHistoriqueHumeur);

        // Charger l'historique au démarrage
        chargerHistoriqueHumeurs();

        // Clics sur les émojis
        humeurHeureuse.setOnClickListener(v -> {
            humeurSelectionnee = "😊";
            donnerEffetSelection(humeurHeureuse);
            tvPhraseDouce.setText("Quelle jolie lumière ! Profite de chaque petit sourire que la journée t'offre. ♡");
        });

        humeurTriste.setOnClickListener(v -> {
            humeurSelectionnee = "😔";
            donnerEffetSelection(humeurTriste);
            tvPhraseDouce.setText("C'est totalement ok de ne pas aller bien. Prends le temps de souffler, tout va bien se passer. 🤍");
        });

        humeurColere.setOnClickListener(v -> {
            humeurSelectionnee = "😡";
            donnerEffetSelection(humeurColere);
            tvPhraseDouce.setText("Pose ce qui pèse lourd. Tu as le droit d'exprimer ce qui te bouscule. 🍃");
        });

        humeurApaisee.setOnClickListener(v -> {
            humeurSelectionnee = "😌";
            donnerEffetSelection(humeurApaisee);
            tvPhraseDouce.setText("Goûte à cette douce quiétude. Rien ne presse, tu es exactement là où tu dois être. ☕");
        });

        humeurAimee.setOnClickListener(v -> {
            humeurSelectionnee = "🥰";
            donnerEffetSelection(humeurAimee);
            tvPhraseDouce.setText("Ton cœur est plein de chaleur. Laisse cette belle énergie t'envelopper toute la journée. ✨");
        });

        humeurFatiguee.setOnClickListener(v -> {
            humeurSelectionnee = "😴";
            donnerEffetSelection(humeurFatiguee);
            tvPhraseDouce.setText("Offre-toi le droit de ralentir et de te reposer. Tu as fait de ton mieux aujourd'hui. 🌙");
        });

        // Enregistrement
        btnEnregistrerHumeur.setOnClickListener(v -> sauvegarderNouvelleHumeur());

        // Navigation
        tabAccueil.setOnClickListener(v -> {
            Intent intent = new Intent(HumeurActivity.this, MainActivity.class);
            intent.putExtra("dejaVerrouille", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        tabJournal.setOnClickListener(v -> {
            Intent intent = new Intent(HumeurActivity.this, JournalActivity.class);
            intent.putExtra("dejaVerrouille", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        tabHumeur.setOnClickListener(v -> {});

        tabOptions.setOnClickListener(v -> {
            Intent intent = new Intent(HumeurActivity.this, OptionsActivity.class);
            intent.putExtra("dejaVerrouille", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    private void donnerEffetSelection(TextView vueSelectionnee) {
        humeurHeureuse.setAlpha(0.6f);
        humeurTriste.setAlpha(0.6f);
        humeurColere.setAlpha(0.6f);
        humeurApaisee.setAlpha(0.6f);
        humeurAimee.setAlpha(0.6f);
        humeurFatiguee.setAlpha(0.6f);
        vueSelectionnee.setAlpha(1.0f);
    }

    private void sauvegarderNouvelleHumeur() {
        String note = etNoteHumeur.getText().toString().trim();
        String dateActuelle = new SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault()).format(new Date());

        try {
            String jsonString = preferences.getString("historique_humeurs", "[]");
            JSONArray jsonArray = new JSONArray(jsonString);

            JSONObject objetHumeur = new JSONObject();
            objetHumeur.put("emoji", humeurSelectionnee);
            objetHumeur.put("note", note);
            objetHumeur.put("date", dateActuelle);

            jsonArray.put(objetHumeur);
            preferences.edit().putString("historique_humeurs", jsonArray.toString()).commit();

            // Quand on ajoute une nouvelle humeur, on réinitialise l'affichage pour voir la plus récente en haut
            afficherTout = false;
            chargerHistoriqueHumeurs();

            etNoteHumeur.setText("");
            Toast.makeText(this, "Humeur enregistrée avec succès " + humeurSelectionnee + " ♡", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void chargerHistoriqueHumeurs() {
        if (containerHistoriqueHumeur == null) return;
        containerHistoriqueHumeur.removeAllViews();

        try {
            String jsonString = preferences.getString("historique_humeurs", "[]");
            JSONArray jsonArray = new JSONArray(jsonString);

            if (jsonArray.length() == 0) {
                TextView tvVide = new TextView(this);
                tvVide.setText("Aucune humeur enregistrée pour le moment 🌷");
                tvVide.setTextColor(getResources().getColor(android.R.color.darker_gray));
                tvVide.setTextSize(14f);
                tvVide.setPadding(8, 8, 8, 8);
                containerHistoriqueHumeur.addView(tvVide);
                return;
            }

            // Déterminer combien d'éléments afficher
            int total = jsonArray.length();
            int limite = afficherTout ? total : Math.min(3, total);

            // Afficher du plus récent au plus ancien
            int startIndex = total - 1;
            int endIndex = total - limite;

            for (int i = startIndex; i >= endIndex; i--) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String emoji = obj.optString("emoji", "😊");
                String note = obj.optString("note", "");
                String date = obj.optString("date", "Aujourd'hui");

                // Création de la carte d'humeur
                LinearLayout carte = new LinearLayout(this);
                carte.setOrientation(LinearLayout.VERTICAL);
                carte.setPadding(24, 20, 24, 20);
                carte.setBackgroundResource(R.drawable.fond_carte_blanche);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 16);
                carte.setLayoutParams(params);

                // Ligne supérieure (Emoji + Date)
                LinearLayout ligneHaut = new LinearLayout(this);
                ligneHaut.setOrientation(LinearLayout.HORIZONTAL);
                ligneHaut.setGravity(android.view.Gravity.CENTER_VERTICAL);

                TextView tvEmoji = new TextView(this);
                tvEmoji.setText(emoji + "  ");
                tvEmoji.setTextSize(22f);

                TextView tvDate = new TextView(this);
                tvDate.setText(date);
                tvDate.setTextColor(getResources().getColor(android.R.color.darker_gray));
                tvDate.setTextSize(12f);

                ligneHaut.addView(tvEmoji);
                ligneHaut.addView(tvDate);
                carte.addView(ligneHaut);

                // Note associée si elle existe
                if (!note.isEmpty()) {
                    TextView tvNote = new TextView(this);
                    tvNote.setText(note);
                    tvNote.setTextColor(getResources().getColor(android.R.color.black));
                    tvNote.setTextSize(14f);
                    tvNote.setPadding(0, 8, 0, 0);
                    carte.addView(tvNote);
                }

                containerHistoriqueHumeur.addView(carte);
            }

            // Ajouter le bouton "Voir plus / Voir moins" s'il y a plus de 3 éléments
            if (total > 3) {
                TextView btnVoirPlus = new TextView(this);
                if (afficherTout) {
                    btnVoirPlus.setText("Voir moins ▴");
                } else {
                    btnVoirPlus.setText("Voir plus (" + (total - 3) + " de plus) ▾");
                }

                // Style aux couleurs de ton application (#8D5968)
                btnVoirPlus.setTextColor(android.graphics.Color.parseColor("#8D5968"));
                btnVoirPlus.setTextSize(14f);
                btnVoirPlus.setTypeface(null, Typeface.BOLD);
                btnVoirPlus.setGravity(android.view.Gravity.CENTER);
                btnVoirPlus.setPadding(0, 16, 0, 24);
                btnVoirPlus.setClickable(true);
                btnVoirPlus.setFocusable(true);

                btnVoirPlus.setOnClickListener(v -> {
                    afficherTout = !afficherTout;
                    chargerHistoriqueHumeurs();
                });

                containerHistoriqueHumeur.addView(btnVoirPlus);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        View rootView = findViewById(R.id.rootLayoutHumeur);
        if (rootView != null) {
            Theme.appliquerTheme(this, rootView);
        }
    }
}