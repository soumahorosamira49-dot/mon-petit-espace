package com.example.monpetitespace;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Locale;

public class RappelActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private View rootLayoutRappel;
    private SwitchCompat switchRappelHumeur, switchRappelJournal;
    private TextView tvHeureChoisie;
    private Button btnChoisirHeure, btnRetourRappel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rappel);

        // J'initialise mon espace de préférences locales
        preferences = getSharedPreferences("MonPetitEspace", MODE_PRIVATE);
        rootLayoutRappel = findViewById(R.id.rootLayoutRappel);

        // Je relie mes éléments graphiques du XML
        switchRappelHumeur = findViewById(R.id.switchRappelHumeur);
        switchRappelJournal = findViewById(R.id.switchRappelJournal);
        tvHeureChoisie = findViewById(R.id.tvHeureChoisie);
        btnChoisirHeure = findViewById(R.id.btnChoisirHeure);
        btnRetourRappel = findViewById(R.id.btnRetourRappel);

        // Je charge les états enregistrés de mes interrupteurs et de l'heure
        boolean humeurActive = preferences.getBoolean("rappelHumeurActive", false);
        boolean journalActif = preferences.getBoolean("rappelJournalActif", false);
        String heureEnregistree = preferences.getString("heureRappel", "20:00");

        switchRappelHumeur.setChecked(humeurActive);
        switchRappelJournal.setChecked(journalActif);
        tvHeureChoisie.setText(heureEnregistree);

        // J'applique mon thème et mes couleurs personnalisées
        Theme.appliquerTheme(this, rootLayoutRappel);

        // Je gère le switch du rappel humeur
        switchRappelHumeur.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("rappelHumeurActive", isChecked).apply();
            if (isChecked) {
                Toast.makeText(this, "Rappel humeur activé 💗", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Rappel humeur désactivé", Toast.LENGTH_SHORT).show();
            }
        });

        // Je gère le switch du rappel journal
        switchRappelJournal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("rappelJournalActif", isChecked).apply();
            if (isChecked) {
                Toast.makeText(this, "Rappel journal activé 📖", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Rappel journal désactivé", Toast.LENGTH_SHORT).show();
            }
        });

        // Je clique pour ouvrir le sélecteur d'heure
        btnChoisirHeure.setOnClickListener(v -> {
            String[] partiesHeure = tvHeureChoisie.getText().toString().split(":");
            int heureInitiale = Integer.parseInt(partiesHeure[0]);
            int minuteInitiale = Integer.parseInt(partiesHeure[1]);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    RappelActivity.this,
                    (view, hourOfDay, minute) -> {
                        String nouvelleHeure = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        tvHeureChoisie.setText(nouvelleHeure);

                        // Je sauvegarde la nouvelle heure choisie
                        preferences.edit().putString("heureRappel", nouvelleHeure).apply();
                        Toast.makeText(this, "Heure du rappel mise à jour : " + nouvelleHeure, Toast.LENGTH_SHORT).show();
                    },
                    heureInitiale,
                    minuteInitiale,
                    true // Format 24h
            );
            timePickerDialog.show();
        });

        // Quand je clique sur retour, je ferme la page
        btnRetourRappel.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // J'applique le thème et la taille du texte dès que je reviens sur cette page
        Theme.appliquerTheme(this, rootLayoutRappel);
    }
}