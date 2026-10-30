package com.example.monpetitespace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class OptionsActivity extends AppCompatActivity {

    // Je déclare mes cartes de réglages et les onglets de navigation du bas
    private LinearLayout cardApparence, cardRappels, cardConfidentialite, cardMessages, cardAPropos;
    private LinearLayout tabAccueil, tabJournal, tabHumeur, tabOptions;
    private View rootLayoutApparence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_options);

        // Je récupère la vue racine pour appliquer le thème
        rootLayoutApparence = findViewById(R.id.rootLayoutApparence);

        // Je relie mes cartes d'options du XML
        cardApparence = findViewById(R.id.cardApparence);
        cardRappels = findViewById(R.id.cardRappels);
        cardConfidentialite = findViewById(R.id.cardConfidentialite);
        cardMessages = findViewById(R.id.cardMessages);
        cardAPropos = findViewById(R.id.cardAPropos);

        // Je relie les onglets de la barre de navigation du bas
        tabAccueil = findViewById(R.id.tabAccueil);
        tabJournal = findViewById(R.id.tabJournal);
        tabHumeur = findViewById(R.id.tabHumeur);
        tabOptions = findViewById(R.id.tabOptions);

        // --- GESTION DES CLICS SUR LES CARTES DE RÉGLAGES ---

        // 1. Clic sur Apparence -> Ouvre la page de personnalisation (thème, couleurs, texte)
        if (cardApparence != null) {
            cardApparence.setOnClickListener(v -> {
                Intent intent = new Intent(OptionsActivity.this, ApparenceActivity.class);
                startActivity(intent);
            });
        }

        // 2. Clic sur Rappels -> Ouvre la page des rappels doux
        if (cardRappels != null) {
            cardRappels.setOnClickListener(v -> {
                Intent intent = new Intent(OptionsActivity.this, RappelActivity.class);
                startActivity(intent);
            });
        }

        // 3. Clic sur Confidentialité -> Ouvre la page de sécurité et gestion des données
        if (cardConfidentialite != null) {
            cardConfidentialite.setOnClickListener(v -> {
                Intent intent = new Intent(OptionsActivity.this, ConfidentialiteActivity.class);
                startActivity(intent);
            });
        }

        // 4. Clic sur Messages doux -> Ouvre la page des messages réconfortants 💌
        if (cardMessages != null) {
            cardMessages.setOnClickListener(v -> {
                Intent intent = new Intent(OptionsActivity.this, MessageDouceurActivity.class);
                startActivity(intent);
            });
        }

        // 5. Clic sur À propos -> Ouvre notre jolie page À propos 🌷
        if (cardAPropos != null) {
            cardAPropos.setOnClickListener(v -> {
                Intent intent = new Intent(OptionsActivity.this, AProposActivity.class);
                startActivity(intent);
            });
        }

        // --- GESTION DE LA NAVIGATION EN BAS DE L'ÉCRAN (FLUIDE & SANS REDEMANDER LE PIN) ---

        if (tabAccueil != null) {
            tabAccueil.setOnClickListener(v -> {
                Intent intent = new Intent(OptionsActivity.this, MainActivity.class);
                intent.putExtra("dejaVerrouille", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        if (tabJournal != null) {
            tabJournal.setOnClickListener(v -> {
                Intent intent = new Intent(OptionsActivity.this, JournalActivity.class);
                intent.putExtra("dejaVerrouille", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        if (tabHumeur != null) {
            tabHumeur.setOnClickListener(v -> {
                Intent intent = new Intent(OptionsActivity.this, HumeurActivity.class);
                intent.putExtra("dejaVerrouille", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        if (tabOptions != null) {
            tabOptions.setOnClickListener(v -> {
                // Déjà sur place ♡
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // J'applique le thème et la taille du texte dès que je reviens sur cette page
        if (rootLayoutApparence != null) {
            Theme.appliquerTheme(this, rootLayoutApparence);
        }
    }
}