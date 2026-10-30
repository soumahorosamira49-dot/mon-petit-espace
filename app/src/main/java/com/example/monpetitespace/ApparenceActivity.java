package com.example.monpetitespace;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class ApparenceActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private RelativeLayout rootLayoutApparence;

    // Boutons de couleur
    private Button btnCouleurRose, btnCouleurSauge, btnCouleurVanille;

    // Boutons de taille de texte
    private Button btnTexteStandard, btnTexteGrand;

    // Switch mode sombre
    private SwitchCompat switchModeSombre;

    // Bouton retour
    private Button btnRetourOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_apparence);

        // Initialisation des SharedPreferences et du fond global
        preferences = getSharedPreferences("MonPetitEspace", MODE_PRIVATE);
        rootLayoutApparence = findViewById(R.id.rootLayoutApparence);

        // Association des éléments avec les ID de ton XML
        btnCouleurRose = findViewById(R.id.btnCouleurRose);
        btnCouleurSauge = findViewById(R.id.btnCouleurSauge);
        btnCouleurVanille = findViewById(R.id.btnCouleurVanille);

        btnTexteStandard = findViewById(R.id.btnTexteStandard);
        btnTexteGrand = findViewById(R.id.btnTexteGrand);

        switchModeSombre = findViewById(R.id.switchModeSombre);
        btnRetourOptions = findViewById(R.id.btnRetourOptions);

        // 1. Charger l'état actuel du mode sombre dans le Switch
        boolean modeSombreActuel = preferences.getBoolean("modeSombre", false);
        switchModeSombre.setChecked(modeSombreActuel);

        // 2. Appliquer le thème au démarrage de l'activité
        Theme.appliquerTheme(this, rootLayoutApparence);

        // --- GESTION DU MODE SOMBRE (SWITCH) ---
        switchModeSombre.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("modeSombre", isChecked).apply();
            Theme.appliquerTheme(ApparenceActivity.this, rootLayoutApparence);
            if (isChecked) {
                Toast.makeText(ApparenceActivity.this, "Mode Nuit paisible 🌙", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ApparenceActivity.this, "Mode Clair activé ☀️", Toast.LENGTH_SHORT).show();
            }
        });

        // --- GESTION DES COULEURS D'AMBIANCE ---
        btnCouleurRose.setOnClickListener(v -> {
            preferences.edit()
                    .putString("couleurAmbiance", "Rose")
                    .putBoolean("modeSombre", false)
                    .apply();
            switchModeSombre.setChecked(false); // Désactive le mode sombre si on choisit une couleur
            Theme.appliquerTheme(ApparenceActivity.this, rootLayoutApparence);
            Toast.makeText(ApparenceActivity.this, "Ambiance Rose poudré 🌸", Toast.LENGTH_SHORT).show();
        });

        btnCouleurSauge.setOnClickListener(v -> {
            preferences.edit()
                    .putString("couleurAmbiance", "Sauge")
                    .putBoolean("modeSombre", false)
                    .apply();
            switchModeSombre.setChecked(false);
            Theme.appliquerTheme(ApparenceActivity.this, rootLayoutApparence);
            Toast.makeText(ApparenceActivity.this, "Ambiance Vert sauge 🌿", Toast.LENGTH_SHORT).show();
        });

        btnCouleurVanille.setOnClickListener(v -> {
            preferences.edit()
                    .putString("couleurAmbiance", "Vanille")
                    .putBoolean("modeSombre", false)
                    .apply();
            switchModeSombre.setChecked(false);
            Theme.appliquerTheme(ApparenceActivity.this, rootLayoutApparence);
            Toast.makeText(ApparenceActivity.this, "Ambiance Douceur vanillée ☕", Toast.LENGTH_SHORT).show();
        });

        // --- GESTION DE LA TAILLE DU TEXTE ---
        btnTexteStandard.setOnClickListener(v -> {
            preferences.edit().putString("tailleTexte", "Standard").apply();
            Theme.appliquerTheme(ApparenceActivity.this, rootLayoutApparence);
            Toast.makeText(ApparenceActivity.this, "Taille du texte : Standard", Toast.LENGTH_SHORT).show();
        });

        btnTexteGrand.setOnClickListener(v -> {
            preferences.edit().putString("tailleTexte", "Grand").apply();
            Theme.appliquerTheme(ApparenceActivity.this, rootLayoutApparence);
            Toast.makeText(ApparenceActivity.this, "Taille du texte : Grand", Toast.LENGTH_SHORT).show();
        });

        // --- BOUTON RETOUR / ENREGISTRER ---
        btnRetourOptions.setOnClickListener(v -> {
            finish(); // Ferme l'activité et revient en arrière
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Theme.appliquerTheme(this, rootLayoutApparence);
    }
}