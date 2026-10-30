package com.example.monpetitespace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class VerrouillageActivity extends AppCompatActivity {

    // Je déclare mes outils de stockage et mes éléments graphiques
    private SharedPreferences preferences;
    private EditText editCodeSecret, editCodeConfirmation;
    private Button btnValiderCode;
    private TextView txtTitreVerrouillage, txtSousTitreVerrouillage;
    private LinearLayout rootLayoutVerrouillage, layoutConfirmation;

    private boolean modeCreation;
    private boolean modeModification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verrouillage);

        // J'initialise mon fichier de préférences
        preferences = getSharedPreferences("MonPetitEspace", MODE_PRIVATE);

        // Je relie mes variables Java aux éléments du layout XML
        editCodeSecret = findViewById(R.id.editCodeSecret);
        editCodeConfirmation = findViewById(R.id.editCodeConfirmation);
        btnValiderCode = findViewById(R.id.btnValiderCode);
        txtTitreVerrouillage = findViewById(R.id.txtTitreVerrouillage);
        txtSousTitreVerrouillage = findViewById(R.id.txtSousTitreVerrouillage);
        rootLayoutVerrouillage = findViewById(R.id.rootLayoutVerrouillage);
        layoutConfirmation = findViewById(R.id.layoutConfirmation);

        // J'applique mon thème personnalisé
        Theme.appliquerTheme(this, rootLayoutVerrouillage);

        // Je configure les deux champs pour qu'ils n'acceptent que des chiffres masqués
        editCodeSecret.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        editCodeConfirmation.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        // Je vérifie si l'on vient pour modifier le code depuis les options/confidentialité
        if (getIntent().getBooleanExtra("modeModification", false)) {
            modeModification = true;
        }

        // Je prépare l'affichage selon le contexte (Création, Modification ou Déverrouillage)
        afficherEcranApproprie();

        // Validation au clic du bouton
        btnValiderCode.setOnClickListener(v -> verifierCode());

        // Validation directe via la touche Entrée du clavier
        editCodeSecret.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                verifierCode();
                return true;
            }
            return false;
        });
    }

    private void afficherEcranApproprie() {
        String codeEnregistre = preferences.getString("valeurCodeSecret", "");

        // Si aucun code n'existe, on force le mode création
        modeCreation = codeEnregistre.isEmpty() || modeModification;

        editCodeSecret.setText("");
        editCodeConfirmation.setText("");

        if (modeCreation) {
            // Mode création ou modification : double saisie
            if (modeModification) {
                txtTitreVerrouillage.setText("Modifier mon code 🔑");
                txtSousTitreVerrouillage.setText("Entre ton nouveau code à 4 chiffres.");
                btnValiderCode.setText("Mettre à jour mon code ♡");
            } else {
                txtTitreVerrouillage.setText("Créer ton code secret 🔑");
                txtSousTitreVerrouillage.setText("Choisis un code à 4 chiffres pour protéger ton espace.");
                btnValiderCode.setText("Enregistrer mon code ♡");
            }

            editCodeSecret.setHint("✨ Choisis ton code...");
            if (layoutConfirmation != null) {
                layoutConfirmation.setVisibility(View.VISIBLE);
            }
        } else {
            // Mode déverrouillage standard au démarrage
            txtTitreVerrouillage.setText("Mon petit espace 🌿");
            txtSousTitreVerrouillage.setText("Tes pensées sont bien gardées ici ♡");
            editCodeSecret.setHint("✨ Entre ton code secret...");
            btnValiderCode.setText("Ouvrir mon espace 🔓");

            if (layoutConfirmation != null) {
                layoutConfirmation.setVisibility(View.GONE);
            }
        }
    }

    private void verifierCode() {
        String codeSaisi = editCodeSecret.getText().toString().trim();

        if (!codeSaisi.matches("[0-9]{4}")) {
            editCodeSecret.setError("Entre exactement 4 chiffres.");
            return;
        }

        if (modeCreation) {
            String confirmationSaisie = editCodeConfirmation.getText().toString().trim();

            if (!confirmationSaisie.matches("[0-9]{4}")) {
                editCodeConfirmation.setError("Confirme ton code à 4 chiffres.");
                return;
            }

            if (!codeSaisi.equals(confirmationSaisie)) {
                Toast.makeText(this, "Les codes ne correspondent pas. Réessaie.", Toast.LENGTH_LONG).show();
                editCodeConfirmation.setText("");
                return;
            }

            // Enregistrement local sécurisé
            preferences.edit()
                    .putString("valeurCodeSecret", codeSaisi)
                    .putBoolean("codeSecretActif", true)
                    .apply();

            Toast.makeText(this, "Ton code est enregistré avec succès ! 🌿", Toast.LENGTH_SHORT).show();

            if (modeModification) {
                finish(); // Si on venait de la page confidentialité, on ferme l'activité pour y revenir
            } else {
                lancerApplication(); // Sinon on lance l'appli principale
            }

        } else {
            // Vérification du code d'accès au démarrage
            String codeEnregistre = preferences.getString("valeurCodeSecret", "");
            boolean codeActif = preferences.getBoolean("codeSecretActif", true);

            if (!codeActif) {
                lancerApplication();
                return;
            }

            if (codeSaisi.equals(codeEnregistre)) {
                lancerApplication();
            } else {
                editCodeSecret.setText("");
                editCodeSecret.setError("Code incorrect.");
                Toast.makeText(this, "Ce n'est pas le bon code. Réessaie ♡", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void lancerApplication() {
        Intent intent = new Intent(VerrouillageActivity.this, MainActivity.class);
        intent.putExtra("dejaVerrouille", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!modeModification) {
            // Ferme l'application si l'utilisatrice quitte l'écran de sécurité au démarrage
            finishAffinity();
        }
    }
}