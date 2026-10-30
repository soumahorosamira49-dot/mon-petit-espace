package com.example.monpetitespace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;

public class ConfidentialiteActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private RelativeLayout rootLayoutConfidentialite;
    private SwitchCompat switchCodeSecret;
    private LinearLayout cardExporter;
    private LinearLayout cardSupprimer;
    private Button btnRetourConfidentialite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confidentialite);

        preferences = getSharedPreferences("MonPetitEspace", MODE_PRIVATE);

        // Liaison avec les ID du fichier XML
        rootLayoutConfidentialite = findViewById(R.id.rootLayoutConfidentialite);
        switchCodeSecret = findViewById(R.id.switchCodeSecret);
        cardExporter = findViewById(R.id.cardExporter);
        cardSupprimer = findViewById(R.id.cardSupprimer);
        btnRetourConfidentialite = findViewById(R.id.btnRetourConfidentialite);

        // Application du thème global
        Theme.appliquerTheme(this, rootLayoutConfidentialite);

        // État initial du Switch selon les préférences enregistrées
        boolean codeActif = preferences.getBoolean("codeSecretActif", false);
        switchCodeSecret.setChecked(codeActif);

        // Gestion du Switch pour activer/désactiver ou configuration du code
        switchCodeSecret.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Si l'utilisatrice active le switch, on l'envoie configurer son code
                Intent intent = new Intent(ConfidentialiteActivity.this, VerrouillageActivity.class);
                intent.putExtra("modeModification", true);
                startActivity(intent);
            } else {
                // Si elle le désactive, on met à jour l'état dans les préférences
                preferences.edit().putBoolean("codeSecretActif", false).apply();
                Toast.makeText(ConfidentialiteActivity.this, "Verrouillage par code désactivé.", Toast.LENGTH_SHORT).show();
            }
        });

        // Clic sur la carte d'exportation des pensées
        if (cardExporter != null) {
            cardExporter.setOnClickListener(v -> exporterPensees());
        }

        // Clic sur la carte de suppression des données avec confirmation
        if (cardSupprimer != null) {
            cardSupprimer.setOnClickListener(v -> afficherDialogueSuppression());
        }

        // Bouton de retour aux options
        if (btnRetourConfidentialite != null) {
            btnRetourConfidentialite.setOnClickListener(v -> finish());
        }
    }

    /**
     * Méthode permettant de lire les pensées, de générer un fichier .txt
     * et d'ouvrir le menu de partage Android.
     */
    private void exporterPensees() {
        String penseesJson = preferences.getString("pensées", "[]");

        try {
            JSONArray listePensees = new JSONArray(penseesJson);

            if (listePensees.length() == 0) {
                Toast.makeText(this, "Tu n'as pas encore écrit de pensées à exporter ♡", Toast.LENGTH_SHORT).show();
                return;
            }

            // Construction du contenu du fichier texte
            StringBuilder contenuExport = new StringBuilder();
            contenuExport.append("===========================================\n");
            contenuExport.append("  MON PETIT ESPACE 🌿 - MES PENSÉES  \n");
            contenuExport.append("===========================================\n\n");

            for (int i = 0; i < listePensees.length(); i++) {
                contenuExport.append(listePensees.getString(i))
                        .append("\n\n-------------------------------------------\n\n");
            }

            // Création d'un fichier temporaire dans le cache local
            File dossierCache = getCacheDir();
            File fichierExport = new File(dossierCache, "mon_petit_espace_pensees.txt");

            FileOutputStream fos = new FileOutputStream(fichierExport);
            fos.write(contenuExport.toString().getBytes());
            fos.close();

            // Partage sécurisé via FileProvider
            Uri uriFichier = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    fichierExport
            );

            Intent intentPartage = new Intent(Intent.ACTION_SEND);
            intentPartage.setType("text/plain");
            intentPartage.putExtra(Intent.EXTRA_STREAM, uriFichier);
            intentPartage.putExtra(Intent.EXTRA_SUBJECT, "Mon petit espace - Sauvegarde de mes pensées");
            intentPartage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intentPartage, "Exporter mes pensées vers..."));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Une erreur est survenue lors de l'exportation.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Affiche une boîte de dialogue pour confirmer la suppression définitive des données.
     */
    private void afficherDialogueSuppression() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer mes données 🗑️")
                .setMessage("Es-tu sûr de vouloir supprimer toutes tes pensées et ton code secret ? Cette action est irréversible et effacera tout ton contenu.")
                .setPositiveButton("Oui, tout supprimer", (dialog, which) -> {
                    // On vide toutes les préférences partagées
                    preferences.edit().clear().apply();

                    Toast.makeText(ConfidentialiteActivity.this, "Toutes tes données ont été supprimées. ♡", Toast.LENGTH_LONG).show();

                    // Redémarrage de l'application sur l'accueil
                    Intent intent = new Intent(ConfidentialiteActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mise à jour de l'état du switch au retour sur l'activité
        if (switchCodeSecret != null) {
            boolean codeActif = preferences.getBoolean("codeSecretActif", false);
            switchCodeSecret.setChecked(codeActif);
        }
        Theme.appliquerTheme(this, rootLayoutConfidentialite);
    }
}