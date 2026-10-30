package com.example.monpetitespace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {

    // Je déclare mes éléments graphiques (champs de texte, boutons, conteneurs)
    private EditText etPensee;
    private Button btnEnregistrer;
    private TextView tvDernierSouvenirTexte;
    private TextView tvHeureDernierSouvenir;
    private TextView tvMantraTexte;
    private TextView tvMantraSource;
    private TextView tvHumeurActuelle;
    private TextView btnRafraichirMantra;
    private LinearLayout tabAccueil;
    private LinearLayout tabJournal;
    private LinearLayout tabHumeur;
    private LinearLayout tabOptions;

    // Je déclare les boutons pour la sélection de la météo intérieure (humeur)
    private LinearLayout btnHumeurSereine;
    private LinearLayout btnHumeurJoyeuse;
    private LinearLayout btnHumeurFatiguee;
    private LinearLayout btnHumeurPensive;
    private LinearLayout btnHumeurTriste;
    private LinearLayout btnHumeurEnervee;

    // Mon outil pour stocker les données en local sur le téléphone
    private SharedPreferences preferences;

    // Ma liste de messages doux et réconfortants adaptés à toutes les émotions
    private final String[] listeMantras = {
            "« C'est OK de se sentir fatigué(e) aujourd'hui. Prends le temps dont tu as besoin. »",
            "« Chaque petit pas, même le plus discret, compte énormément. »",
            "« Tes émotions sont valides. Tu as le droit de ressentir tout ce qui traverse ton esprit. »",
            "« Un jour après l'autre. Tu fais de ton mieux, et c'est déjà beaucoup. »",
            "« Cet espace est le tien, sans jugement ni attente. Respire un grand coup. »",
            "« Même les nuits les plus longues finissent par laisser place au matin. »",
            "« Sois indulgent(e) envers toi-même aujourd'hui ♡ »",
            "« Tu n’as pas besoin d’aller vite pour avancer. »"
    };

    // Les signatures poétiques associées
    private final String[] listeSignatures = {
            "— DOUCEUR & REPOS",
            "— PETIT PAS",
            "— ÉCOUTE DE SOI",
            "— PATIENCE",
            "— ESPACE LIBRE",
            "— LUMIÈRE",
            "— BIENVEILLANCE",
            "— SOUFFLE LÉGER"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ----- VÉRIFICATION DU VERROUILLAGE SÉCURISÉ AU DÉMARRAGE ---
        SharedPreferences prefsVerification = getSharedPreferences("MonPetitEspace", MODE_PRIVATE);
        boolean codeActive = prefsVerification.getBoolean("codeSecretActif", false);
        boolean dejaVerrouille = getIntent().getBooleanExtra("dejaVerrouille", false);

        if (codeActive && !dejaVerrouille) {
            Intent intentVerrouillage = new Intent(MainActivity.this, VerrouillageActivity.class);
            startActivity(intentVerrouillage);
            finish();
            return;
        }
        // -----------------------------------------------------------

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("MonPetitEspace", MODE_PRIVATE);

        // Je relie mes variables Java aux ID définis dans mon fichier activity_main.xml
        etPensee = findViewById(R.id.etPensee);
        btnEnregistrer = findViewById(R.id.btnEnregistrer);
        tvDernierSouvenirTexte = findViewById(R.id.tvDernierSouvenirTexte);
        tvHeureDernierSouvenir = findViewById(R.id.tvHeureDernierSouvenir);
        tvMantraTexte = findViewById(R.id.tvMantraTexte);
        tvMantraSource = findViewById(R.id.tvMantraSource);
        tvHumeurActuelle = findViewById(R.id.tvHumeurActuelle);
        btnRafraichirMantra = findViewById(R.id.btnRafraichirMantra);
        tabAccueil = findViewById(R.id.tabAccueil);
        tabJournal = findViewById(R.id.tabJournal);
        tabHumeur = findViewById(R.id.tabHumeur);
        tabOptions = findViewById(R.id.tabOptions);

        btnHumeurSereine = findViewById(R.id.btnHumeurSereine);
        btnHumeurJoyeuse = findViewById(R.id.btnHumeurJoyeuse);
        btnHumeurFatiguee = findViewById(R.id.btnHumeurFatiguee);
        btnHumeurPensive = findViewById(R.id.btnHumeurPensive);
        btnHumeurTriste = findViewById(R.id.btnHumeurTriste);
        btnHumeurEnervee = findViewById(R.id.btnHumeurEnervee);

        // Je récupère l'humeur enregistrée
        String humeurSauvegardee = preferences.getString("humeurActuelle", "Sereine 🍃");
        tvHumeurActuelle.setText(humeurSauvegardee);

        if (humeurSauvegardee.contains("Sereine")) mettreAJourVisuelHumeur(btnHumeurSereine);
        else if (humeurSauvegardee.contains("Joyeuse")) mettreAJourVisuelHumeur(btnHumeurJoyeuse);
        else if (humeurSauvegardee.contains("Fatiguée")) mettreAJourVisuelHumeur(btnHumeurFatiguee);
        else if (humeurSauvegardee.contains("Pensive")) mettreAJourVisuelHumeur(btnHumeurPensive);
        else if (humeurSauvegardee.contains("Triste")) mettreAJourVisuelHumeur(btnHumeurTriste);
        else if (humeurSauvegardee.contains("Énervée")) mettreAJourVisuelHumeur(btnHumeurEnervee);

        afficherMantraAleatoire();

        // ----- GESTION DES CLICS SUR LES BOUTONS D'HUMEUR ---
        btnHumeurSereine.setOnClickListener(v -> {
            changerHumeur("Sereine 🍃");
            mettreAJourVisuelHumeur(btnHumeurSereine);
        });
        btnHumeurJoyeuse.setOnClickListener(v -> {
            changerHumeur("Joyeuse ☀️");
            mettreAJourVisuelHumeur(btnHumeurJoyeuse);
        });
        btnHumeurFatiguee.setOnClickListener(v -> {
            changerHumeur("Fatiguée 😴");
            mettreAJourVisuelHumeur(btnHumeurFatiguee);
        });
        btnHumeurPensive.setOnClickListener(v -> {
            changerHumeur("Pensive 🌙");
            mettreAJourVisuelHumeur(btnHumeurPensive);
        });
        btnHumeurTriste.setOnClickListener(v -> {
            changerHumeur("Triste 😔");
            mettreAJourVisuelHumeur(btnHumeurTriste);
        });
        btnHumeurEnervee.setOnClickListener(v -> {
            changerHumeur("Énervée 😤");
            mettreAJourVisuelHumeur(btnHumeurEnervee);
        });

        // --- CHARGEMENT DU DERNIER SOUVENIR ENREGISTRÉ ---
        try {
            String penseeJson = preferences.getString("liste_pensees", "[]");
            JSONArray listePensees = new JSONArray(penseeJson);

            if (listePensees.length() > 0) {
                JSONObject dernierObjet = listePensees.getJSONObject(listePensees.length() - 1);

                // On récupère la date courte stockée (ou une valeur par défaut)
                String dateCourte = dernierObjet.optString("dateCourte", "Aujourd'hui");
                tvHeureDernierSouvenir.setText(dateCourte);
                tvDernierSouvenirTexte.setText("« " + dernierObjet.optString("texte", "") + " »");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnRafraichirMantra.setOnClickListener(v -> afficherMantraAleatoire());

        // --- NAVIGATION FLUIDE VERS LES AUTRES ÉCRANS ---
        if (tabAccueil != null) {
            tabAccueil.setOnClickListener(v -> {
                // Déjà sur place ♡
            });
        }

        if (tabJournal != null) {
            tabJournal.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, JournalActivity.class);
                intent.putExtra("dejaVerrouille", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        if (tabHumeur != null) {
            tabHumeur.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, HumeurActivity.class);
                intent.putExtra("dejaVerrouille", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        if (tabOptions != null) {
            tabOptions.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, OptionsActivity.class);
                intent.putExtra("dejaVerrouille", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        // --- ENREGISTREMENT D'UNE NOUVELLE PENSÉE ---
        btnEnregistrer.setOnClickListener(v -> {
            String textSaisi = etPensee.getText().toString().trim();

            if (!textSaisi.isEmpty()) {
                try {
                    String jsonString = preferences.getString("liste_pensees", "[]");
                    JSONArray jsonArray = new JSONArray(jsonString);

                    JSONObject objetPensee = new JSONObject();
                    objetPensee.put("texte", textSaisi);

                    String dateActuelleComprehensive = new SimpleDateFormat("dd MMMM yyyy 'à' HH:mm", Locale.forLanguageTag("fr-FR")).format(new Date());
                    String dateCourteBadge = new SimpleDateFormat("dd MMM", Locale.forLanguageTag("fr-FR")).format(new Date());

                    objetPensee.put("date", dateActuelleComprehensive);
                    objetPensee.put("dateCourte", dateCourteBadge);
                    objetPensee.put("favori", false);

                    jsonArray.put(objetPensee);
                    preferences.edit().putString("liste_pensees", jsonArray.toString()).apply();

                    Toast.makeText(MainActivity.this, "Pensée enregistrée avec succès ♡", Toast.LENGTH_SHORT).show();

                    tvHeureDernierSouvenir.setText(dateCourteBadge);
                    tvDernierSouvenirTexte.setText("« " + textSaisi + " »");
                    etPensee.setText("");

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivity.this, "Écris quelques mots sur ce que tu ressens ♡", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        View rootView = findViewById(R.id.rootLayoutMain);
        if (rootView != null) {
            Theme.appliquerTheme(this, rootView);
        }
    }

    private void afficherMantraAleatoire() {
        int indexAleatoire = (int) (Math.random() * listeMantras.length);
        tvMantraTexte.setText(listeMantras[indexAleatoire]);
        tvMantraSource.setText(listeSignatures[indexAleatoire]);
    }

    private void changerHumeur(String nouvelleHumeur) {
        tvHumeurActuelle.setText(nouvelleHumeur);
        preferences.edit().putString("humeurActuelle", nouvelleHumeur).apply();
        Toast.makeText(MainActivity.this, "Météo intérieure : " + nouvelleHumeur, Toast.LENGTH_SHORT).show();
    }

    private void mettreAJourVisuelHumeur(LinearLayout vueSelectionnee) {
        btnHumeurSereine.setBackground(null);
        btnHumeurSereine.setElevation(0f);
        btnHumeurJoyeuse.setBackground(null);
        btnHumeurJoyeuse.setElevation(0f);
        btnHumeurFatiguee.setBackground(null);
        btnHumeurFatiguee.setElevation(0f);
        btnHumeurPensive.setBackground(null);
        btnHumeurPensive.setElevation(0f);
        btnHumeurTriste.setBackground(null);
        btnHumeurTriste.setElevation(0f);
        btnHumeurEnervee.setBackground(null);
        btnHumeurEnervee.setElevation(0f);

        if (vueSelectionnee != null) {
            vueSelectionnee.setBackgroundColor(android.graphics.Color.parseColor("#EFE5DC"));
            vueSelectionnee.setElevation(4f);
        }
    }
}